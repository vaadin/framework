/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.VaadinServletConfiguration.InitParameterName;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.server.communication.ServletUIInitHandler;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.Version;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

@SuppressWarnings("serial")
public class VaadinServlet extends HttpServlet implements Constants {

    private class ScssCacheEntry implements Serializable {

        private final String css;
        private final List<String> sourceUris;
        private final long timestamp;
        private final String scssFileName;

        public ScssCacheEntry(String scssFileName, String css,
                List<String> sourceUris) {
            this.scssFileName = scssFileName;
            this.css = css;
            this.sourceUris = sourceUris;

            timestamp = getLastModified();
        }

        public ScssCacheEntry(JsonObject json) {
            css = json.getString("css");
            timestamp = Long.parseLong(json.getString("timestamp"));

            sourceUris = new ArrayList<>();

            JsonArray uris = json.getArray("uris");
            for (int i = 0; i < uris.length(); i++) {
                sourceUris.add(uris.getString(i));
            }

            // Not set for cache entries read from disk
            scssFileName = null;
        }

        public String asJson() {
            JsonArray uris = Json.createArray();
            for (String uri : sourceUris) {
                uris.set(uris.length(), uri);
            }

            JsonObject object = Json.createObject();
            object.put("version", Version.getFullVersion());
            object.put("timestamp", Long.toString(timestamp));
            object.put("uris", uris);
            object.put("css", css);

            return object.toJson();
        }

        public String getCss() {
            return css;
        }

        private long getLastModified() {
            long newest = 0;
            for (String uri : sourceUris) {
                File file = new File(uri);
                URL resource = getService().getClassLoader().getResource(uri);
                long lastModified = -1L;
                if (file.exists()) {
                    lastModified = file.lastModified();
                } else if (resource != null
                        && resource.getProtocol().equals("file")) {
                    try {
                        file = new File(resource.toURI());
                        if (file.exists()) {
                            lastModified = file.lastModified();
                        }
                    } catch (URISyntaxException e) {
                        getLogger().log(Level.WARNING,
                                "Could not resolve timestamp for " + resource,
                                e);
                    }
                }
                if (lastModified == -1L && resource == null) {
                    /*
                     * Ignore missing files found in the classpath, report
                     * problem and abort for other files.
                     */
                    getLogger().log(Level.WARNING,
                            "Could not resolve timestamp for {0}, Scss on the fly caching will be disabled",
                            uri);
                    // -1 means this cache entry will never be valid
                    return -1;
                }
                newest = Math.max(newest, lastModified);
            }

            return newest;
        }

        public boolean isStillValid() {
            if (timestamp == -1) {
                /*
                 * Don't ever bother checking anything if files used during the
                 * compilation were gone before the cache entry was created.
                 */
                return false;
            } else if (timestamp != getLastModified()) {
                /*
                 * Would in theory still be valid if the last modification is
                 * before the recorded timestamp, but that would still mean that
                 * something has changed since we last checked, so let's
                 * invalidate in that case as well to be on the safe side.
                 */
                return false;
            } else {
                return true;
            }
        }

        public String getScssFileName() {
            return scssFileName;
        }

    }

    private VaadinServletService servletService;

    /**
     * Called by the servlet container to indicate to a servlet that the servlet
     * is being placed into service.
     *
     * @param servletConfig
     *            the object containing the servlet's configuration and
     *            initialization parameters
     * @throws ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    @Override
    public void init(javax.servlet.ServletConfig servletConfig)
            throws ServletException {
        CurrentInstance.clearAll();
        super.init(servletConfig);
        Properties initParameters = new Properties();

        readUiFromEnclosingClass(initParameters);

        readConfigurationAnnotation(initParameters);

        // Read default parameters from server.xml
        final ServletContext context = servletConfig.getServletContext();
        for (final Enumeration<String> e = context.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = e.nextElement();
            initParameters.setProperty(name, context.getInitParameter(name));
        }

        // Override with application config from web.xml
        for (final Enumeration<String> e = servletConfig
                .getInitParameterNames(); e.hasMoreElements();) {
            final String name = e.nextElement();
            initParameters.setProperty(name,
                    servletConfig.getInitParameter(name));
        }

        DeploymentConfiguration deploymentConfiguration = createDeploymentConfiguration(
                initParameters);
        try {
            servletService = createServletService(deploymentConfiguration);
        } catch (ServiceException e) {
            throw new ServletException("Could not initialize VaadinServlet", e);
        }
        // Sets current service even though there are no request and response
        servletService.setCurrentInstances(null, null);

        servletInitialized();

        CurrentInstance.clearAll();
    }

    private void readUiFromEnclosingClass(Properties initParameters) {
        Class<?> enclosingClass = getClass().getEnclosingClass();

        if (enclosingClass != null
                && UI.class.isAssignableFrom(enclosingClass)) {
            initParameters.put(VaadinSession.UI_PARAMETER,
                    enclosingClass.getName());
        }
    }

    private void readConfigurationAnnotation(Properties initParameters)
            throws ServletException {
        VaadinServletConfiguration configAnnotation = UIProvider
                .getAnnotationFor(getClass(), VaadinServletConfiguration.class);
        if (configAnnotation != null) {
            Method[] methods = VaadinServletConfiguration.class
                    .getDeclaredMethods();
            for (Method method : methods) {
                InitParameterName name = method
                        .getAnnotation(InitParameterName.class);
                assert name != null : "All methods declared in VaadinServletConfiguration should have a @InitParameterName annotation";

                try {
                    Object value = method.invoke(configAnnotation);

                    String stringValue;
                    if (value instanceof Class<?>) {
                        stringValue = ((Class<?>) value).getName();
                    } else {
                        stringValue = value.toString();
                    }

                    if (VaadinServlet.PARAMETER_WIDGETSET.equals(name.value())
                            && method.getDefaultValue().equals(stringValue)) {
                        // Do not set the widgetset to anything so that the
                        // framework can fallback to the default. Setting
                        // anything to the init parameter will force that into
                        // use and e.g. AppWidgetset will not be used even
                        // though it is found.
                        continue;
                    }

                    initParameters.setProperty(name.value(), stringValue);
                } catch (Exception e) {
                    // This should never happen
                    throw new ServletException(
                            "Could not read @VaadinServletConfiguration value "
                                    + method.getName(),
                            e);
                }
            }
        }
    }

    protected void servletInitialized() throws ServletException {
        // Empty by default
    }

    /**
     * Gets the currently used Vaadin servlet. The current servlet is
     * automatically defined when initializing the servlet and when processing
     * requests to the server (see {@link ThreadLocal}) and in
     * {@link VaadinSession#access(Runnable)} and {@link UI#access(Runnable)}.
     * In other cases, (e.g. from background threads), the current servlet is
     * not automatically defined.
     * <p>
     * The current servlet is derived from the current service using
     * {@link VaadinService#getCurrent()}
     *
     * @return the current Vaadin servlet instance if available, otherwise
     *         <code>null</code>
     *
     * @since 7.0
     */
    public static VaadinServlet getCurrent() {
        VaadinService vaadinService = CurrentInstance.get(VaadinService.class);
        if (vaadinService instanceof VaadinServletService) {
            VaadinServletService vss = (VaadinServletService) vaadinService;
            return vss.getServlet();
        } else {
            return null;
        }
    }

    protected DeploymentConfiguration createDeploymentConfiguration(
            Properties initParameters) {
        return new DefaultDeploymentConfiguration(getClass(), initParameters);
    }

    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        VaadinServletService service = new VaadinServletService(this,
                deploymentConfiguration);
        service.init();
        return service;
    }

    /**
     * Receives standard HTTP requests from the public service method and
     * dispatches them.
     *
     * @param request
     *            the object that contains the request the client made of the
     *            servlet.
     * @param response
     *            the object that contains the response the servlet returns to
     *            the client.
     * @throws ServletException
     *             if an input or output error occurs while the servlet is
     *             handling the TRACE request.
     * @throws IOException
     *             if the request for the TRACE cannot be handled.
     */

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // Handle context root request without trailing slash, see #9921
        if (handleContextRootWithoutSlash(request, response)) {
            return;
        }
        CurrentInstance.clearAll();

        VaadinServletRequest vaadinRequest = createVaadinRequest(request);
        VaadinServletResponse vaadinResponse = createVaadinResponse(response);
        if (!ensureCookiesEnabled(vaadinRequest, vaadinResponse)) {
            return;
        }

        if (isStaticResourceRequest(vaadinRequest)) {
            // Define current servlet and service, but no request and response
            getService().setCurrentInstances(null, null);
            try {
                serveStaticResources(vaadinRequest, vaadinResponse);
                return;
            } finally {
                CurrentInstance.clearAll();
            }
        }
        try {
            getService().handleRequest(vaadinRequest, vaadinResponse);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }

    }

    /**
     * Invoked for every request to this servlet to potentially send a redirect
     * to avoid problems with requests to the context root with no trailing
     * slash.
     *
     * @param request
     *            the processed request
     * @param response
     *            the processed response
     * @return <code>true</code> if a redirect has been sent and the request
     *         should not be processed further; <code>false</code> if the
     *         request should be processed as usual
     * @throws IOException
     *             If an input or output exception occurs
     */
    protected boolean handleContextRootWithoutSlash(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // Query parameters like "?a=b" are handled by the servlet container but
        // path parameter (e.g. ;jsessionid=) needs to be handled here
        String location = request.getRequestURI();

        String lastPathParameter = getLastPathParameter(location);
        location = location.substring(0,
                location.length() - lastPathParameter.length());

        if ((request.getPathInfo() == null || "/".equals(request.getPathInfo()))
                && "".equals(request.getServletPath())
                && !location.endsWith("/")) {
            /*
             * Path info is for the root but request URI doesn't end with a
             * slash -> redirect to the same URI but with an ending slash.
             */
            location = location + "/" + lastPathParameter;
            String queryString = request.getQueryString();
            if (queryString != null) {
                // Prevent HTTP Response splitting in case the server doesn't
                queryString = queryString.replaceAll("[\\r\\n]", "");
                location += '?' + queryString;
            }
            response.sendRedirect(location);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finds any path parameter added to the last part of the uri. A path
     * parameter is any string separated by ";" from the path and ends in / or
     * at the end of the string.
     * <p>
     * For example the uri http://myhost.com/foo;a=1/bar;b=1 contains two path
     * parameters, {@literal a=1} related to {@literal /foo} and {@literal b=1}
     * related to /bar.
     * <p>
     * For http://myhost.com/foo;a=1/bar;b=1 this method will return ;b=1
     *
     * @since 7.2
     * @param uri
     *            a URI
     * @return the last path parameter of the uri including the semicolon or an
     *         empty string. Never null.
     */
    protected static String getLastPathParameter(String uri) {
        int lastPathStart = uri.lastIndexOf('/');
        if (lastPathStart == -1) {
            return "";
        }

        int semicolonPos = uri.indexOf(';', lastPathStart);
        if (semicolonPos < 0) {
            // No path parameter for the last part
            return "";
        } else {
            // This includes the semicolon.
            String semicolonString = uri.substring(semicolonPos);
            return semicolonString;
        }
    }

    private VaadinServletResponse createVaadinResponse(
            HttpServletResponse response) {
        return new VaadinServletResponse(response, getService());
    }

    /**
     * Create a Vaadin request for a http servlet request. This method can be
     * overridden if the Vaadin request should have special properties.
     *
     * @param request
     *            the original http servlet request
     * @return a Vaadin request for the original request
     */
    protected VaadinServletRequest createVaadinRequest(
            HttpServletRequest request) {
        return new VaadinServletRequest(request, getService());
    }

    /**
     * Gets a the vaadin service for this servlet.
     *
     * @return the vaadin service
     */
    protected VaadinServletService getService() {
        return servletService;
    }

    /**
     * Check that cookie support is enabled in the browser. Only checks UIDL
     * requests.
     *
     * @param request
     *            The request from the browser
     * @param response
     *            The response to which an error can be written
     * @return false if cookies are disabled, true otherwise
     * @throws IOException
     */
    private boolean ensureCookiesEnabled(VaadinServletRequest request,
            VaadinServletResponse response) throws IOException {
        if (ServletPortletHelper.isUIDLRequest(request)) {
            // In all other but the first UIDL request a cookie should be
            // returned by the browser.
            // This can be removed if cookieless mode (#3228) is supported
            if (request.getRequestedSessionId() == null) {
                // User has cookies disabled
                SystemMessages systemMessages = getService().getSystemMessages(
                        ServletPortletHelper.findLocale(null, null, request),
                        request);
                getService().writeStringResponse(response,
                        JsonConstants.JSON_CONTENT_TYPE,
                        VaadinService.createCriticalNotificationJSON(
                                systemMessages.getCookiesDisabledCaption(),
                                systemMessages.getCookiesDisabledMessage(),
                                null, systemMessages.getCookiesDisabledURL()));
                return false;
            }
        }
        return true;
    }

    /**
     * Send a notification to client-side widgetset. Used to notify client of
     * critical errors, session expiration and more. Server has no knowledge of
     * what UI client refers to.
     *
     * @param request
     *            the HTTP request instance.
     * @param response
     *            the HTTP response to write to.
     * @param caption
     *            the notification caption
     * @param message
     *            to notification body
     * @param details
     *            a detail message to show in addition to the message. Currently
     *            shown directly below the message but could be hidden behind a
     *            details drop down in the future. Mainly used to give
     *            additional information not necessarily useful to the end user.
     * @param url
     *            url to load when the message is dismissed. Null will reload
     *            the current page.
     * @throws IOException
     *             if the writing failed due to input/output error.
     *
     * @deprecated As of 7.0. This method is retained only for backwards
     *             compatibility and for GAEVaadinServlet.
     */
    @Deprecated
    protected void criticalNotification(VaadinServletRequest request,
            VaadinServletResponse response, String caption, String message,
            String details, String url) throws IOException {

        if (ServletPortletHelper.isUIDLRequest(request)) {
            String output = VaadinService.createCriticalNotificationJSON(
                    caption, message, details, url);
            getService().writeStringResponse(response,
                    JsonConstants.JSON_CONTENT_TYPE, output);
        } else {
            // Create an HTML reponse with the error
            String output = "";

            if (url != null) {
                output += "<a href=\"" + url + "\">";
            }
            if (caption != null) {
                output += "<b>" + caption + "</b><br/>";
            }
            if (message != null) {
                output += message;
                output += "<br/><br/>";
            }

            if (details != null) {
                output += details;
                output += "<br/><br/>";
            }
            if (url != null) {
                output += "</a>";
            }
            getService().writeStringResponse(response,
                    ApplicationConstants.CONTENT_TYPE_TEXT_HTML_UTF_8, output);
        }
    }

    /**
     * Writes the response in {@code output} using the contentType given in
     * {@code contentType} to the provided {@link HttpServletResponse}
     *
     * @param response
     * @param contentType
     * @param output
     *            Output to write (UTF-8 encoded)
     * @throws IOException
     */
    private void writeResponse(HttpServletResponse response, String contentType,
            String output) throws IOException {
        response.setContentType(contentType);
        final OutputStream out = response.getOutputStream();
        try ( // Set the response type
                PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8")))) {
            outWriter.print(output);
            outWriter.flush();
        }
    }

    /**
     * Gets resource path using different implementations. Required to
     * supporting different servlet container implementations (application
     * servers).
     *
     * @param servletContext
     * @param path
     *            the resource path.
     * @return the resource path.
     *
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected static String getResourcePath(ServletContext servletContext,
            String path) {
        String resultPath = null;
        resultPath = servletContext.getRealPath(path);
        if (resultPath != null) {
            return resultPath;
        } else {
            try {
                final URL url = servletContext.getResource(path);
                resultPath = url.getFile();
            } catch (final Exception e) {
                // FIXME: Handle exception
                getLogger().log(Level.INFO,
                        "Could not find resource path " + path, e);
            }
        }
        return resultPath;
    }

    /**
     * A helper method to strip away characters that might somehow be used for
     * XSS attacks. Leaves at least alphanumeric characters intact. Also removes
     * e.g. '(' and ')', so values should be safe in javascript too.
     *
     * @param themeName
     * @return
     *
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public static String stripSpecialChars(String themeName) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = themeName.toCharArray();
        for (char c : charArray) {
            if (!CHAR_BLACKLIST.contains(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static final Collection<Character> CHAR_BLACKLIST = new HashSet<>(
            Arrays.asList(new Character[] { '&', '"', '\'', '<', '>', '(', ')',
                    ';' }));

    /**
     * Mutex for preventing to scss compilations to take place simultaneously.
     * This is a workaround needed as the scss compiler currently is not thread
     * safe (#10292).
     * <p>
     * In addition, this is also used to protect the cached compilation results.
     */
    private static final Object SCSS_MUTEX = new Object();

    /**
     * Global cache of scss compilation results. This map is protected from
     * concurrent access by {@link #SCSS_MUTEX}.
     */
    private final Map<String, ScssCacheEntry> scssCache = new HashMap<>();

    /**
     * Keeps track of whether a warning about not being able to persist cache
     * files has already been printed. The flag is protected from concurrent
     * access by {@link #SCSS_MUTEX}.
     */
    private static boolean scssCompileWarWarningEmitted = false;

    /**
     * Returns the default theme. Must never return null.
     *
     * @return
     */
    public static String getDefaultTheme() {
        return DEFAULT_THEME_NAME;
    }

    /**
     * Check if this is a request for a static resource and, if it is, serve the
     * resource to the client.
     *
     * @param request
     * @param response
     * @return true if a file was served and the request has been handled, false
     *         otherwise.
     * @throws IOException
     * @throws ServletException
     */
    private boolean serveStaticResources(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        String filePath = getStaticFilePath(request);
        if (filePath != null) {
            serveStaticResourcesInVAADIN(filePath, request, response);
            return true;
        }

        return false;
    }

    /**
     * Serve resources from VAADIN directory.
     *
     * @param filename
     *            The filename to serve. Should always start with /VAADIN/.
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void serveStaticResourcesInVAADIN(String filename,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        final ServletContext sc = getServletContext();
        URL resourceUrl = findResourceURL(filename);

        if (resourceUrl == null) {
            // File not found, if this was a css request we still look for a
            // scss file with the same name
            if (serveOnTheFlyCompiledScss(filename, request, response, sc)) {
                return;
            } else {
                // cannot serve requested file
                getLogger().log(Level.INFO,
                        "Requested resource [{0}] not found from filesystem or through class loader."
                                + " Add widgetset and/or theme JAR to your classpath or add files to WebContent/VAADIN folder.",
                        filename);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            return;
        }

        // security check: do not permit navigation out of the VAADIN
        // directory
        if (!isAllowedVAADINResourceUrl(request, resourceUrl)) {
            getLogger().log(Level.INFO,
                    "Requested resource [{0}] not accessible in the VAADIN directory or access to it is forbidden.",
                    filename);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String cacheControl = "public, max-age=0, must-revalidate";
        int resourceCacheTime = getCacheTime(filename);
        if (resourceCacheTime > 0) {
            cacheControl = "max-age=" + String.valueOf(resourceCacheTime);
        }
        response.setHeader("Cache-Control", cacheControl);
        response.setDateHeader("Expires",
                System.currentTimeMillis() + resourceCacheTime * 1000);

        // Find the modification timestamp
        long lastModifiedTime = 0;
        URLConnection connection = null;
        try {
            connection = resourceUrl.openConnection();
            lastModifiedTime = connection.getLastModified();
            // Remove milliseconds to avoid comparison problems (milliseconds
            // are not returned by the browser in the "If-Modified-Since"
            // header).
            lastModifiedTime -= lastModifiedTime % 1000;
            response.setDateHeader("Last-Modified", lastModifiedTime);

            if (browserHasNewestVersion(request, lastModifiedTime)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        } catch (Exception e) {
            // Failed to find out last modified timestamp. Continue without it.
            getLogger().log(Level.FINEST,
                    "Failed to find out last modified timestamp. Continuing without it.",
                    e);
        } finally {
            try {
                // Explicitly close the input stream to prevent it
                // from remaining hanging
                // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4257700
                InputStream is = connection.getInputStream();
                if (is != null) {
                    is.close();
                }
            } catch (FileNotFoundException e) {
                // Not logging when the file does not exist.
            } catch (IOException e) {
                getLogger().log(Level.INFO,
                        "Error closing URLConnection input stream", e);
            }
        }

        // Set type mime type if we can determine it based on the filename
        final String mimetype = sc.getMimeType(filename);
        if (mimetype != null) {
            response.setContentType(mimetype);
        }

        writeStaticResourceResponse(request, response, resourceUrl);
    }

    /**
     * Calculates the cache lifetime for the given filename in seconds. By
     * default filenames containing ".nocache." return 0, filenames containing
     * ".cache." return one year, all other return the value defined in the
     * web.xml using resourceCacheTime (defaults to 1 hour).
     *
     * @param filename
     * @return cache lifetime for the given filename in seconds
     */
    protected int getCacheTime(String filename) {
        /*
         * GWT conventions:
         *
         * - files containing .nocache. will not be cached.
         *
         * - files containing .cache. will be cached for one year.
         *
         * https://developers.google.com/web-toolkit/doc/latest/
         * DevGuideCompilingAndDebugging#perfect_caching
         */
        if (filename.contains(".nocache.")) {
            return 0;
        }
        if (filename.contains(".cache.")) {
            return 60 * 60 * 24 * 365;
        }
        /*
         * For all other files, the browser is allowed to cache for 1 hour
         * without checking if the file has changed. This forces browsers to
         * fetch a new version when the Vaadin version is updated. This will
         * cause more requests to the servlet than without this but for high
         * volume sites the static files should never be served through the
         * servlet.
         */
        return getService().getDeploymentConfiguration().getResourceCacheTime();
    }

    /**
     * Writes the contents of the given resourceUrl in the response. Can be
     * overridden to add/modify response headers and similar.
     *
     * @param request
     *            The request for the resource
     * @param response
     *            The response
     * @param resourceUrl
     *            The url to send
     * @throws IOException
     */
    protected void writeStaticResourceResponse(HttpServletRequest request,
            HttpServletResponse response, URL resourceUrl) throws IOException {

        URLConnection connection = null;
        InputStream is = null;
        String urlStr = resourceUrl.toExternalForm();

        if (allowServePrecompressedResource(request, urlStr)) {
            // try to serve a precompressed version if available
            try {
                connection = new URL(urlStr + ".gz").openConnection();
                is = connection.getInputStream();
                // set gzip headers
                response.setHeader("Content-Encoding", "gzip");
            } catch (IOException e) {
                // NOP: will be still tried with non gzipped version
            } catch (Exception e) {
                getLogger().log(Level.FINE,
                        "Unexpected exception looking for gzipped version of resource "
                                + urlStr,
                        e);
            }
        }
        if (is == null) {
            // precompressed resource not available, get non compressed
            connection = resourceUrl.openConnection();
            try {
                is = connection.getInputStream();
            } catch (FileNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        try {
            int length = connection.getContentLength();
            if (length >= 0) {
                response.setContentLength(length);
            }
        } catch (Throwable e) {
            // This can be ignored, content length header is not required.
            // Need to close the input stream because of
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4257700 to
            // prevent it from hanging, but that is done below.
        }

        try {
            streamContent(response, is);
        } finally {
            is.close();
        }
    }

    /**
     * Returns whether this servlet should attempt to serve a precompressed
     * version of the given static resource. If this method returns true, the
     * suffix {@code .gz} is appended to the URL and the corresponding resource
     * is served if it exists. It is assumed that the compression method used is
     * gzip. If this method returns false or a compressed version is not found,
     * the original URL is used.
     *
     * The base implementation of this method returns true if and only if the
     * request indicates that the client accepts gzip compressed responses and
     * the filename extension of the requested resource is .js, .css, or .html.
     *
     * @since 7.5.0
     *
     * @param request
     *            the request for the resource
     * @param url
     *            the URL of the requested resource
     * @return true if the servlet should attempt to serve a precompressed
     *         version of the resource, false otherwise
     */
    protected boolean allowServePrecompressedResource(
            HttpServletRequest request, String url) {
        String accept = request.getHeader("Accept-Encoding");
        return accept != null && accept.contains("gzip") && (url.endsWith(".js")
                || url.endsWith(".css") || url.endsWith(".html"));
    }

    private void streamContent(HttpServletResponse response, InputStream is)
            throws IOException {
        final OutputStream os = response.getOutputStream();
        final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
        int bytes;
        while ((bytes = is.read(buffer)) >= 0) {
            os.write(buffer, 0, bytes);
        }
    }

    /**
     * Finds the given resource from the web content folder or using the class
     * loader.
     *
     * @since 7.7
     * @param filename
     *            The file to find, starting with a "/"
     * @return The URL to the given file, or null if the file was not found
     * @throws IOException
     *             if there was a problem while locating the file
     */
    protected URL findResourceURL(String filename) throws IOException {
        URL resourceUrl = getServletContext().getResource(filename);
        if (resourceUrl == null) {
            // try if requested file is found from class loader

            // strip leading "/" otherwise stream from JAR wont work
            if (filename.startsWith("/")) {
                filename = filename.substring(1);
            }

            resourceUrl = getService().getClassLoader().getResource(filename);
        }
        return resourceUrl;
    }

    private boolean serveOnTheFlyCompiledScss(String filename,
            HttpServletRequest request, HttpServletResponse response,
            ServletContext sc) throws IOException {
        if (!filename.endsWith(".css")) {
            return false;
        }

        String scssFilename = filename.substring(0, filename.length() - 4)
                + ".scss";
        URL scssUrl = findResourceURL(scssFilename);
        if (scssUrl == null) {
            // Is a css request but no scss file was found
            return false;
        }
        // security check: do not permit navigation out of the VAADIN
        // directory
        if (!isAllowedVAADINResourceUrl(request, scssUrl)) {
            getLogger().log(Level.INFO,
                    "Requested resource [{0}] not accessible in the VAADIN directory or access to it is forbidden.",
                    filename);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            // Handled, return true so no further processing is done
            return true;
        }
        if (getService().getDeploymentConfiguration().isProductionMode()) {
            // This is not meant for production mode.
            getLogger().log(Level.INFO,
                    "Request for {0} not handled by sass compiler while in production mode",
                    filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            // Handled, return true so no further processing is done
            return true;
        }

        synchronized (SCSS_MUTEX) {
            ScssCacheEntry cacheEntry = scssCache.get(scssFilename);

            if (cacheEntry == null) {
                try {
                    cacheEntry = loadPersistedScssCache(scssFilename, sc);
                } catch (Exception e) {
                    getLogger().log(Level.WARNING,
                            "Could not read persisted scss cache", e);
                }
            }

            if (cacheEntry == null || !cacheEntry.isStillValid()) {
                cacheEntry = compileScssOnTheFly(filename, scssFilename, sc);
                persistCacheEntry(cacheEntry);
            }
            scssCache.put(scssFilename, cacheEntry);

            if (cacheEntry == null) {
                // compilation did not produce any result, but logged a message
                return false;
            }

            // This is for development mode only so instruct the browser to
            // never cache it
            response.setHeader("Cache-Control", "no-cache");
            final String mimetype = getService().getMimeType(filename);
            writeResponse(response, mimetype, cacheEntry.getCss());

            return true;
        }
    }

    private ScssCacheEntry loadPersistedScssCache(String scssFilename,
            ServletContext sc) throws IOException {
        String realFilename = sc.getRealPath(scssFilename);

        File scssCacheFile = getScssCacheFile(new File(realFilename));
        if (!scssCacheFile.exists()) {
            return null;
        }

        String jsonString = readFile(scssCacheFile, StandardCharsets.UTF_8);

        JsonObject entryJson = Json.parse(jsonString);

        String cacheVersion = entryJson.getString("version");
        if (!Version.getFullVersion().equals(cacheVersion)) {
            // Compiled for some other Vaadin version, discard cache
            scssCacheFile.delete();
            return null;
        }

        return new ScssCacheEntry(entryJson);
    }

    private ScssCacheEntry compileScssOnTheFly(String filename,
            String scssFilename, ServletContext sc) throws IOException {
        String realFilename = sc.getRealPath(scssFilename);
        ScssStylesheet scss = ScssStylesheet.get(realFilename);
        if (scss == null) {
            // Not a file in the file system (WebContent directory). Use the
            // identifier directly (VAADIN/themes/.../styles.css) so
            // ScssStylesheet will try using the class loader.
            if (scssFilename.startsWith("/")) {
                scssFilename = scssFilename.substring(1);
            }

            scss = ScssStylesheet.get(scssFilename);
        }

        if (scss == null) {
            getLogger().log(Level.WARNING,
                    "Scss file {0} exists but ScssStylesheet was not able to find it",
                    scssFilename);
            return null;
        }
        try {
            getLogger().log(Level.FINE, "Compiling {0} for request to {1}",
                    new Object[] { realFilename, filename });
            scss.compile();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Scss compilation failed", e);
            return null;
        }

        return new ScssCacheEntry(realFilename, scss.printState(),
                scss.getSourceUris());
    }

    /**
     * Check whether a URL obtained from a classloader refers to a valid static
     * resource in the directory VAADIN.
     *
     * Warning: Overriding of this method is not recommended, but is possible to
     * support non-default classloaders or servers that may produce URLs
     * different from the normal ones. The method prototype may change in the
     * future. Care should be taken not to expose class files or other resources
     * outside the VAADIN directory if the method is overridden.
     *
     * @param request
     * @param resourceUrl
     * @return
     *
     * @since 6.6.7
     *
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected boolean isAllowedVAADINResourceUrl(HttpServletRequest request,
            URL resourceUrl) {
        String resourcePath = resourceUrl.getPath();
        if ("jar".equals(resourceUrl.getProtocol())) {
            // This branch is used for accessing resources directly from the
            // Vaadin JAR in development environments and in similar cases.

            // Inside a JAR, a ".." would mean a real directory named ".." so
            // using it in paths should just result in the file not being found.
            // However, performing a check in case some servers or class loaders
            // try to normalize the path by collapsing ".." before the class
            // loader sees it.
            if (!resourcePath.contains("!/VAADIN/")
                    && !resourcePath.contains("!/META-INF/resources/VAADIN/")) {
                getLogger().log(Level.INFO,
                        "Blocked attempt to access a JAR entry not starting with /VAADIN/: {0}",
                        resourceUrl);
                return false;
            }
            getLogger().log(Level.FINE,
                    "Accepted access to a JAR entry using a class loader: {0}",
                    resourceUrl);
            return true;
        } else {
            // Some servers such as GlassFish extract files from JARs (file:)
            // and e.g. JBoss 5+ use protocols vsf: and vfsfile: .

            // Check that the URL is in a VAADIN directory and does not contain
            // "/../"
            if (!resourcePath.contains("/VAADIN/")
                    || resourcePath.contains("/../")) {
                getLogger().log(Level.INFO,
                        "Blocked attempt to access file: {0}", resourceUrl);
                return false;
            }
            getLogger().log(Level.FINE,
                    "Accepted access to a file using a class loader: {0}",
                    resourceUrl);
            return true;
        }
    }

    /**
     * Checks if the browser has an up to date cached version of requested
     * resource. Currently the check is performed using the "If-Modified-Since"
     * header. Could be expanded if needed.
     *
     * @param request
     *            The HttpServletRequest from the browser.
     * @param resourceLastModifiedTimestamp
     *            The timestamp when the resource was last modified. 0 if the
     *            last modification time is unknown.
     * @return true if the If-Modified-Since header tells the cached version in
     *         the browser is up to date, false otherwise
     */
    private boolean browserHasNewestVersion(HttpServletRequest request,
            long resourceLastModifiedTimestamp) {
        if (resourceLastModifiedTimestamp < 1) {
            // We do not know when it was modified so the browser cannot have an
            // up-to-date version
            return false;
        }
        /*
         * The browser can request the resource conditionally using an
         * If-Modified-Since header. Check this against the last modification
         * time.
         */
        try {
            // If-Modified-Since represents the timestamp of the version cached
            // in the browser
            long headerIfModifiedSince = request
                    .getDateHeader("If-Modified-Since");

            if (headerIfModifiedSince >= resourceLastModifiedTimestamp) {
                // Browser has this an up-to-date version of the resource
                return true;
            }
        } catch (Exception e) {
            // Failed to parse header. Fail silently - the browser does not have
            // an up-to-date version in its cache.
        }
        return false;
    }

    /**
     *
     * @author Vaadin Ltd
     * @since 7.0
     *
     * @deprecated As of 7.0. This is no longer used and only provided for
     *             backwards compatibility. Each {@link RequestHandler} can
     *             individually decide whether it wants to handle a request or
     *             not.
     */
    @Deprecated
    protected enum RequestType {
        FILE_UPLOAD, BROWSER_DETAILS, UIDL, OTHER, STATIC_FILE, APP, PUBLISHED_FILE, HEARTBEAT;
    }

    /**
     * @param request
     * @return
     *
     * @deprecated As of 7.0. This is no longer used and only provided for
     *             backwards compatibility. Each {@link RequestHandler} can
     *             individually decide whether it wants to handle a request or
     *             not.
     */
    @Deprecated
    protected RequestType getRequestType(VaadinServletRequest request) {
        if (ServletPortletHelper.isFileUploadRequest(request)) {
            return RequestType.FILE_UPLOAD;
        } else if (ServletPortletHelper.isPublishedFileRequest(request)) {
            return RequestType.PUBLISHED_FILE;
        } else if (ServletUIInitHandler.isUIInitRequest(request)) {
            return RequestType.BROWSER_DETAILS;
        } else if (ServletPortletHelper.isUIDLRequest(request)) {
            return RequestType.UIDL;
        } else if (isStaticResourceRequest(request)) {
            return RequestType.STATIC_FILE;
        } else if (ServletPortletHelper.isAppRequest(request)) {
            return RequestType.APP;
        } else if (ServletPortletHelper.isHeartbeatRequest(request)) {
            return RequestType.HEARTBEAT;
        }
        return RequestType.OTHER;

    }

    protected boolean isStaticResourceRequest(HttpServletRequest request) {
        return getStaticFilePath(request) != null;
    }

    /**
     * Returns the relative path at which static files are served for a request
     * (if any).
     * 
     * @param request
     *            HTTP request
     * @return relative servlet path or null if the request path does not
     *         contain "/VAADIN/" or the request has no path info
     * @since 8.0
     */
    protected String getStaticFilePath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            return null;
        }
        // Servlet mapped as /* serves at /VAADIN
        // Servlet mapped as /foo/bar/* serves at /foo/bar/VAADIN
        if (pathInfo.startsWith("/VAADIN/")) {
            return pathInfo;
        }
        String servletPrefixedPath = request.getServletPath() + pathInfo;
        // Servlet mapped as /VAADIN/*
        if (servletPrefixedPath.startsWith("/VAADIN/")) {
            return servletPrefixedPath;
        }
        return null;
    }

    /**
     * Remove any heading or trailing "what" from the "string".
     *
     * @param string
     * @param what
     * @return
     */
    static String removeHeadingOrTrailing(String string, String what) {
        while (string.startsWith(what)) {
            string = string.substring(1);
        }

        while (string.endsWith(what)) {
            string = string.substring(0, string.length() - 1);
        }

        return string;
    }

    /**
     * Gets the current application URL from request.
     *
     * @param request
     *            the HTTP request.
     * @throws MalformedURLException
     *             if the application is denied access to the persistent data
     *             store represented by the given URL.
     *
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected URL getApplicationUrl(HttpServletRequest request)
            throws MalformedURLException {
        final URL reqURL = new URL((request.isSecure() ? "https://" : "http://")
                + request.getServerName()
                + (request.isSecure() && request.getServerPort() == 443
                        || !request.isSecure() && request.getServerPort() == 80
                                ? "" : ":" + request.getServerPort())
                + request.getRequestURI());
        String servletPath = "";
        if (request
                .getAttribute("javax.servlet.include.servlet_path") != null) {
            // this is an include request
            servletPath = request
                    .getAttribute("javax.servlet.include.context_path")
                    .toString()
                    + request
                            .getAttribute("javax.servlet.include.servlet_path");

        } else {
            servletPath = request.getContextPath() + request.getServletPath();
        }

        if (servletPath.length() == 0
                || servletPath.charAt(servletPath.length() - 1) != '/') {
            servletPath += "/";
        }
        URL u = new URL(reqURL, servletPath);
        return u;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        getService().destroy();
    }

    private static void persistCacheEntry(ScssCacheEntry cacheEntry) {
        String scssFileName = cacheEntry.getScssFileName();
        if (scssFileName == null) {
            if (!scssCompileWarWarningEmitted) {
                getLogger().warning(
                        "Could not persist scss cache because no real file was found for the compiled scss file. "
                                + "This might happen e.g. if serving the scss file directly from a .war file.");
                scssCompileWarWarningEmitted = true;
            }
            return;
        }

        File scssFile = new File(scssFileName);
        File cacheFile = getScssCacheFile(scssFile);

        String cacheEntryJsonString = cacheEntry.asJson();

        try {
            writeFile(cacheEntryJsonString, cacheFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            getLogger().log(Level.WARNING,
                    "Error persisting scss cache " + cacheFile, e);
        }
    }

    private static String readFile(File file, Charset charset)
            throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            // no point in reading files over 2GB to a String
            byte[] b = new byte[(int) file.length()];
            int len = b.length;
            int total = 0;

            while (total < len) {
                int result = in.read(b, total, len - total);
                if (result == -1) {
                    break;
                }
                total += result;
            }
            return new String(b, charset);
        }
    }

    private static void writeFile(String content, File file, Charset charset)
            throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(charset));
        }
    }

    private static File getScssCacheFile(File scssFile) {
        return new File(scssFile.getParentFile(),
                scssFile.getName() + ".cache");
    }

    /**
     * Escapes characters to html entities. An exception is made for some "safe
     * characters" to keep the text somewhat readable.
     *
     * @param unsafe
     * @return a safe string to be added inside an html tag
     *
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    public static final String safeEscapeForHtml(String unsafe) {
        if (null == unsafe) {
            return null;
        }
        StringBuilder safe = new StringBuilder();
        char[] charArray = unsafe.toCharArray();
        for (char c : charArray) {
            if (isSafe(c)) {
                safe.append(c);
            } else {
                safe.append("&#");
                safe.append((int) c);
                safe.append(";");
            }
        }

        return safe.toString();
    }

    private static boolean isSafe(char c) {
        return //
        c > 47 && c < 58 || // alphanum
                c > 64 && c < 91 || // A-Z
                c > 96 && c < 123 // a-z
        ;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinServlet.class.getName());
    }

}
