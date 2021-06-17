/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import static java.nio.charset.StandardCharsets.UTF_8;

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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
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

    // Mapped uri is for the jar file
    static final Map<URI, Integer> openFileSystems = new HashMap<>();
    private static final Object fileSystemLock = new Object();

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
        try {
            servletService = createServletService();
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

    /**
     * Creates a deployment configuration to be used for the creation of a
     * {@link VaadinService}. Intended to be used by dependency injection
     * frameworks.
     *
     * @return the created deployment configuration
     *
     * @throws ServletException
     *             if construction of the {@link Properties} for
     *             {@link #createDeploymentConfiguration(Properties)} fails
     *
     * @since 8.2
     */
    protected DeploymentConfiguration createDeploymentConfiguration()
            throws ServletException {
        Properties initParameters = new Properties();

        readUiFromEnclosingClass(initParameters);

        readConfigurationAnnotation(initParameters);

        // Read default parameters from server.xml
        final ServletContext context = getServletConfig().getServletContext();
        for (final Enumeration<String> e = context.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = e.nextElement();
            initParameters.setProperty(name, context.getInitParameter(name));
        }

        // Override with application config from web.xml
        for (final Enumeration<String> e = getServletConfig()
                .getInitParameterNames(); e.hasMoreElements();) {
            final String name = e.nextElement();
            initParameters.setProperty(name,
                    getServletConfig().getInitParameter(name));
        }

        return createDeploymentConfiguration(initParameters);
    }

    /**
     * Creates a deployment configuration to be used for the creation of a
     * {@link VaadinService}. Override this if you want to override certain
     * properties.
     *
     * @param initParameters
     *            the context-param and init-param values as properties
     * @return the created deployment configuration
     *
     * @since 7.0.0
     */
    protected DeploymentConfiguration createDeploymentConfiguration(
            Properties initParameters) {
        return new DefaultDeploymentConfiguration(getClass(), initParameters);
    }

    /**
     * Creates a vaadin servlet service. This method functions as a layer of
     * indirection between {@link #init(ServletConfig)} and
     * {@link #createServletService(DeploymentConfiguration)} so dependency
     * injection frameworks can call {@link #createDeploymentConfiguration()}
     * when creating a vaadin servlet service lazily.
     *
     * @return the created vaadin servlet service
     *
     * @throws ServletException
     *             if creating a deployment configuration fails
     * @throws ServiceException
     *             if creating the vaadin servlet service fails
     *
     * @since 8.2
     */
    protected VaadinServletService createServletService()
            throws ServletException, ServiceException {
        return createServletService(createDeploymentConfiguration());
    }

    /**
     * Creates a vaadin servlet service.
     *
     * @param deploymentConfiguration
     *            the deployment configuration to be used
     *
     * @return the created vaadin servlet service
     *
     * @throws ServiceException
     *             if creating the vaadin servlet service fails
     *
     * @since 7.0.0
     */
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
                && request.getServletPath().isEmpty()
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
     * Creates a Vaadin request for a http servlet request. This method can be
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
                getService().writeUncachedStringResponse(response,
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
            getService().writeUncachedStringResponse(response,
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
            getService().writeUncachedStringResponse(response,
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
                        new OutputStreamWriter(out, UTF_8)))) {
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
     * Pattern for matching request paths that start with /VAADIN/, multiple
     * slashes allowed on either side.
     */
    private static Pattern staticFileRequestPathPatternVaadin = Pattern
            .compile("^/+VAADIN/.*");

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
     *            The request
     * @param response
     *            The response
     * @return {@code true} if a file was served and the request has been
     *         handled; {@code false} otherwise.
     * @throws IOException
     * @throws ServletException
     *
     * @since 8.5
     */
    protected boolean serveStaticResources(HttpServletRequest request,
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
     *            The request
     * @param response
     *            The response
     * @throws IOException
     * @throws ServletException
     *
     * @since 8.5
     */
    protected void serveStaticResourcesInVAADIN(String filename,
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
        // directory or into any directory rather than a file
        if (!isAllowedVAADINResourceUrl(request, resourceUrl)) {
            getLogger().log(Level.INFO,
                    "Requested resource [{0}] is a directory, "
                            + "is not within the VAADIN directory, "
                            + "or access to it is forbidden.",
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
     *            the filename
     * @return cache lifetime for the given filename in seconds
     */
    protected int getCacheTime(String filename) {
        // GWT conventions:
        // - files containing .nocache. will not be cached.
        // - files containing .cache. will be cached for one year.
        // https://developers.google.com/web-toolkit/doc/latest/DevGuideCompilingAndDebugging#perfect_caching

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
     * <p>
     * WARNING: note that this should not be used for a {@code resourceUrl} that
     * represents a directory! For security reasons, the directory contents
     * should not be ever written into the {@code response}, and the
     * implementation which is used for setting the content length relies on
     * {@link URLConnection#getContentLength()} method which returns incorrect
     * values for directories.
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
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
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
    public URL findResourceURL(String filename) throws IOException {
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
                    "Requested resource [{0}] is a directory, "
                            + "is not within the VAADIN directory, "
                            + "or access to it is forbidden.",
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
     * resource in the directory VAADIN. Directories do not count as valid
     * resources.
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
        if (resourceUrl == null || resourceIsDirectory(resourceUrl)) {
            return false;
        }
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

    private boolean resourceIsDirectory(URL resource) {
        if (resource.getPath().endsWith("/")) {
            return true;
        }
        URI resourceURI = null;
        try {
            resourceURI = resource.toURI();
        } catch (URISyntaxException e) {
            getLogger().log(Level.FINE,
                    "Syntax error in uri from getStaticResource", e);
            // Return false as we couldn't determine if the resource is a
            // directory.
            return false;
        }

        if ("jar".equals(resource.getProtocol())) {
            // Get the file path in jar
            final String pathInJar = resource.getPath()
                    .substring(resource.getPath().lastIndexOf("!") + 1);
            try {
                FileSystem fileSystem = getFileSystem(resourceURI);
                // Get the file path inside the jar.
                final Path path = fileSystem.getPath(pathInJar);

                return Files.isDirectory(path);
            } catch (IOException e) {
                getLogger().log(Level.FINE, "failed to read zip file", e);
            } finally {
                closeFileSystem(resourceURI);
            }
        }

        // If not a jar check if a file path directory.
        return "file".equals(resource.getProtocol())
                && Files.isDirectory(Paths.get(resourceURI));
    }

    /**
     * Get the file URI for the resource jar file. Returns give URI if
     * URI.scheme is not of type jar.
     *
     * The URI for a file inside a jar is composed as
     * 'jar:file://...pathToJar.../jarFile.jar!/pathToFile'
     *
     * the first step strips away the initial scheme 'jar:' leaving us with
     * 'file://...pathToJar.../jarFile.jar!/pathToFile' from which we remove the
     * inside jar path giving the end result
     * 'file://...pathToJar.../jarFile.jar'
     *
     * @param resourceURI
     *            resource URI to get file URI for
     * @return file URI for resource jar or given resource if not a jar schemed
     *         URI
     */
    private URI getFileURI(URI resourceURI) {
        if (!"jar".equals(resourceURI.getScheme())) {
            return resourceURI;
        }
        try {
            String scheme = resourceURI.getRawSchemeSpecificPart();
            int jarPartIndex = scheme.indexOf("!/");
            if (jarPartIndex != -1) {
                scheme = scheme.substring(0, jarPartIndex);
            }
            return new URI(scheme);
        } catch (URISyntaxException syntaxException) {
            throw new IllegalArgumentException(syntaxException.getMessage(),
                    syntaxException);
        }
    }

    // Package protected for feature verification purpose
    FileSystem getFileSystem(URI resourceURI) throws IOException {
        synchronized (fileSystemLock) {
            URI fileURI = getFileURI(resourceURI);

            if (openFileSystems.computeIfPresent(fileURI,
                    (key, value) -> value + 1) != null) {
                // Get filesystem is for the file to get the correct provider
                return FileSystems.getFileSystem(resourceURI);
            }
            // Opened filesystem is for the file to get the correct provider
            FileSystem fileSystem = FileSystems.newFileSystem(resourceURI,
                    Collections.emptyMap());
            openFileSystems.put(fileURI, 1);
            return fileSystem;
        }
    }

    // Package protected for feature verification purpose
    void closeFileSystem(URI resourceURI) {
        synchronized (fileSystemLock) {
            try {
                URI fileURI = getFileURI(resourceURI);
                final Integer locks = openFileSystems.computeIfPresent(fileURI,
                        (key, value) -> value - 1);
                if (locks != null && locks == 0) {
                    openFileSystems.remove(fileURI);
                    // Get filesystem is for the file to get the correct
                    // provider
                    FileSystems.getFileSystem(resourceURI).close();
                }
            } catch (IOException ioe) {
                getLogger().log(Level.SEVERE,
                        "Failed to close FileSystem for '{}'", resourceURI);
                getLogger().log(Level.INFO, "Exception closing FileSystem",
                        ioe);
            }
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
     * <p>
     * NOTE: This method does not check whether the requested resource is a
     * directory and as such not a valid VAADIN resource.
     *
     * @param request
     *            HTTP request
     * @return relative servlet path or null if the request path does not
     *         contain "/VAADIN/" or the request has no path info
     * @since 8.0
     */
    protected String getStaticFilePath(HttpServletRequest request) {
        if (request.getPathInfo() == null) {
            return null;
        }
        String decodedPath = null;
        String contextPath = null;
        try {
            // pathInfo should be already decoded, but some containers do not
            // decode it, hence we use getRequestURI instead.
            decodedPath = URLDecoder.decode(request.getRequestURI(),
                    StandardCharsets.UTF_8.name());
            contextPath = URLDecoder.decode(request.getContextPath(),
                    StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("An error occurred during decoding URL.",
                    e);
        }
        // Possible context path needs to be removed
        String filePath = decodedPath.substring(contextPath.length());
        String servletPath = request.getServletPath();
        // Possible servlet path needs to be removed
        if (!servletPath.isEmpty() && !servletPath.equals("/VAADIN")
                && filePath.startsWith(servletPath)) {
            filePath = filePath.substring(servletPath.length());
        }
        // Servlet mapped as /* serves at /VAADIN
        // Servlet mapped as /foo/bar/* serves at /foo/bar/VAADIN

        // Matches request paths /VAADIN/*, //VAADIN/* etc.
        if (staticFileRequestPathPatternVaadin.matcher(filePath).matches()) {
            // Remove any extra slashes from the beginning,
            // later occurrences don't interfere
            while (filePath.startsWith("//")) {
                filePath = filePath.substring(1);
            }
            return filePath;
        }

        String servletPrefixedPath = servletPath + filePath;
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
                                ? ""
                                : ":" + request.getServerPort())
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

        if (servletPath.isEmpty()
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
        if (getService() != null) {
            getService().destroy();
        }
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
                safe.append(';');
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
