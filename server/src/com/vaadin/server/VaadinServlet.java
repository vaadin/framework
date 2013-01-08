/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.server.AbstractCommunicationManager.Callback;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@SuppressWarnings("serial")
public class VaadinServlet extends HttpServlet implements Constants {

    private static class AbstractApplicationServletWrapper implements Callback {

        private final VaadinServlet servlet;

        public AbstractApplicationServletWrapper(VaadinServlet servlet) {
            this.servlet = servlet;
        }

        @Override
        public void criticalNotification(VaadinRequest request,
                VaadinResponse response, String cap, String msg,
                String details, String outOfSyncURL) throws IOException {
            servlet.criticalNotification((VaadinServletRequest) request,
                    ((VaadinServletResponse) response), cap, msg, details,
                    outOfSyncURL);
        }
    }

    // TODO Move some (all?) of the constants to a separate interface (shared
    // with portlet)

    private final String resourcePath = null;

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
        setCurrent(this);
        super.init(servletConfig);
        Properties initParameters = new Properties();

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

        DeploymentConfiguration deploymentConfiguration = createDeploymentConfiguration(initParameters);
        servletService = createServletService(deploymentConfiguration);
        // Sets current service even though there are no request and response
        servletService.setCurrentInstances(null, null);

        servletInitialized();

        CurrentInstance.clearAll();
    }

    protected void servletInitialized() throws ServletException {
        // Empty by default
    }

    /**
     * Gets the currently used Vaadin servlet. The current servlet is
     * automatically defined when initializing the servlet and when processing
     * requests to the server and in threads started at a point when the current
     * servlet is defined (see {@link InheritableThreadLocal}). In other cases,
     * (e.g. from background threads started in some other way), the current
     * servlet is not automatically defined.
     * 
     * @return the current Vaadin servlet instance if available, otherwise
     *         <code>null</code>
     * 
     * @see #setCurrent(VaadinServlet)
     * 
     * @since 7.0
     */
    public static VaadinServlet getCurrent() {
        return CurrentInstance.get(VaadinServlet.class);
    }

    /**
     * Sets the current Vaadin servlet. This method is used by the framework to
     * set the current servlet whenever a new request is processed and it is
     * cleared when the request has been processed.
     * <p>
     * The application developer can also use this method to define the current
     * servlet outside the normal request handling, e.g. when initiating custom
     * background threads.
     * </p>
     * 
     * @param servlet
     *            the Vaadin servlet to register as the current servlet
     * 
     * @see #getCurrent()
     * @see InheritableThreadLocal
     */
    public static void setCurrent(VaadinServlet servlet) {
        CurrentInstance.setInheritable(VaadinServlet.class, servlet);
    }

    protected DeploymentConfiguration createDeploymentConfiguration(
            Properties initParameters) {
        return new DefaultDeploymentConfiguration(getClass(), initParameters);
    }

    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration) {
        return new VaadinServletService(this, deploymentConfiguration);
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
        setCurrent(this);
        service(createVaadinRequest(request), createVaadinResponse(response));
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
        if ("/".equals(request.getPathInfo())
                && "".equals(request.getServletPath())
                && !request.getRequestURI().endsWith("/")) {
            /*
             * Path info is for the root but request URI doesn't end with a
             * slash -> redirect to the same URI but with an ending slash.
             */
            String location = request.getRequestURI() + "/";
            String queryString = request.getQueryString();
            if (queryString != null) {
                location += '?' + queryString;
            }
            response.sendRedirect(location);
            return true;
        } else {
            return false;
        }
    }

    private void service(VaadinServletRequest request,
            VaadinServletResponse response) throws ServletException,
            IOException {
        RequestTimer requestTimer = new RequestTimer();
        requestTimer.start();

        getService().setCurrentInstances(request, response);

        AbstractApplicationServletWrapper servletWrapper = new AbstractApplicationServletWrapper(
                this);

        RequestType requestType = getRequestType(request);
        if (!ensureCookiesEnabled(requestType, request, response)) {
            return;
        }

        if (requestType == RequestType.STATIC_FILE) {
            serveStaticResources(request, response);
            return;
        }

        VaadinSession vaadinSession = null;

        try {
            // If a duplicate "close application" URL is received for an
            // application that is not open, redirect to the application's main
            // page.
            // This is needed as e.g. Spring Security remembers the last
            // URL from the application, which is the logout URL, and repeats
            // it.
            // We can tell apart a real onunload request from a repeated one
            // based on the real one having content (at least the UIDL security
            // key).
            if (requestType == RequestType.UIDL
                    && request.getParameterMap().containsKey(
                            ApplicationConstants.PARAM_UNLOADBURST)
                    && request.getContentLength() < 1
                    && getService().getExistingSession(request, false) == null) {
                redirectToApplication(request, response);
                return;
            }

            // Find out the service session this request is related to
            vaadinSession = getService().findVaadinSession(request);
            if (vaadinSession == null) {
                return;
            }

            CommunicationManager communicationManager = (CommunicationManager) vaadinSession
                    .getCommunicationManager();

            if (requestType == RequestType.PUBLISHED_FILE) {
                communicationManager.servePublishedFile(request, response);
                return;
            } else if (requestType == RequestType.HEARTBEAT) {
                communicationManager.handleHeartbeatRequest(request, response,
                        vaadinSession);
                return;
            }

            /* Update browser information from the request */
            vaadinSession.getBrowser().updateRequestDetails(request);

            /* Handle the request */
            if (requestType == RequestType.FILE_UPLOAD) {
                // UI is resolved in communication manager
                communicationManager.handleFileUpload(vaadinSession, request,
                        response);
                return;
            } else if (requestType == RequestType.UIDL) {
                UI uI = getService().findUI(request);
                if (uI == null) {
                    throw new ServletException(ERROR_NO_UI_FOUND);
                }
                // Handles AJAX UIDL requests
                communicationManager.handleUidlRequest(request, response,
                        servletWrapper, uI);

                // Ensure that the browser does not cache UIDL responses.
                // iOS 6 Safari requires this (#9732)
                response.setHeader("Cache-Control", "no-cache");

                return;
            } else if (requestType == RequestType.BROWSER_DETAILS) {
                // Browser details - not related to a specific UI
                communicationManager.handleBrowserDetailsRequest(request,
                        response, vaadinSession);
                return;
            }

            if (communicationManager.handleOtherRequest(request, response)) {
                return;
            }

            // Request not handled by any RequestHandler -> 404
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

        } catch (final SessionExpiredException e) {
            // Session has expired, notify user
            handleServiceSessionExpired(request, response);
        } catch (final GeneralSecurityException e) {
            handleServiceSecurityException(request, response);
        } catch (final Throwable e) {
            handleServiceException(request, response, vaadinSession, e);
        } finally {
            if (vaadinSession != null) {
                getService().cleanupSession(vaadinSession);
                requestTimer.stop(vaadinSession);
            }
            CurrentInstance.clearAll();
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
     * @param requestType
     *            Type of the request as returned by
     *            {@link #getRequestType(HttpServletRequest)}
     * @param request
     *            The request from the browser
     * @param response
     *            The response to which an error can be written
     * @return false if cookies are disabled, true otherwise
     * @throws IOException
     */
    private boolean ensureCookiesEnabled(RequestType requestType,
            VaadinServletRequest request, VaadinServletResponse response)
            throws IOException {
        if (requestType == RequestType.UIDL) {
            // In all other but the first UIDL request a cookie should be
            // returned by the browser.
            // This can be removed if cookieless mode (#3228) is supported
            if (request.getRequestedSessionId() == null) {
                // User has cookies disabled
                SystemMessages systemMessages = getService().getSystemMessages(
                        ServletPortletHelper.findLocale(null, null, request),
                        request);
                criticalNotification(request, response,
                        systemMessages.getCookiesDisabledCaption(),
                        systemMessages.getCookiesDisabledMessage(), null,
                        systemMessages.getCookiesDisabledURL());
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
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected void criticalNotification(VaadinServletRequest request,
            HttpServletResponse response, String caption, String message,
            String details, String url) throws IOException {

        if (ServletPortletHelper.isUIDLRequest(request)) {

            if (caption != null) {
                caption = "\"" + JsonPaintTarget.escapeJSON(caption) + "\"";
            }
            if (details != null) {
                if (message == null) {
                    message = details;
                } else {
                    message += "<br/><br/>" + details;
                }
            }

            if (message != null) {
                message = "\"" + JsonPaintTarget.escapeJSON(message) + "\"";
            }
            if (url != null) {
                url = "\"" + JsonPaintTarget.escapeJSON(url) + "\"";
            }

            String output = "for(;;);[{\"changes\":[], \"meta\" : {"
                    + "\"appError\": {" + "\"caption\":" + caption + ","
                    + "\"message\" : " + message + "," + "\"url\" : " + url
                    + "}}, \"resources\": {}, \"locales\":[]}]";
            writeResponse(response, "application/json; charset=UTF-8", output);
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
            writeResponse(response, "text/html; charset=UTF-8", output);

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
    private void writeResponse(HttpServletResponse response,
            String contentType, String output) throws IOException {
        response.setContentType(contentType);
        final ServletOutputStream out = response.getOutputStream();
        // Set the response type
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print(output);
        outWriter.flush();
        outWriter.close();
        out.flush();

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

    private void handleServiceException(VaadinServletRequest request,
            VaadinServletResponse response, VaadinSession vaadinSession,
            Throwable e) throws IOException, ServletException {
        ErrorHandler errorHandler = ErrorEvent.findErrorHandler(vaadinSession);

        // if this was an UIDL request, response UIDL back to client
        if (getRequestType(request) == RequestType.UIDL) {
            SystemMessages ci = getService().getSystemMessages(
                    ServletPortletHelper.findLocale(null, vaadinSession,
                            request), request);
            criticalNotification(request, response,
                    ci.getInternalErrorCaption(), ci.getInternalErrorMessage(),
                    null, ci.getInternalErrorURL());
            if (errorHandler != null) {
                errorHandler.error(new ErrorEvent(e));
            }
        } else {
            if (errorHandler != null) {
                errorHandler.error(new ErrorEvent(e));
            }

            // Re-throw other exceptions
            throw new ServletException(e);
        }

    }

    /**
     * A helper method to strip away characters that might somehow be used for
     * XSS attacs. Leaves at least alphanumeric characters intact. Also removes
     * eg. ( and ), so values should be safe in javascript too.
     * 
     * @param themeName
     * @return
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected static String stripSpecialChars(String themeName) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = themeName.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (!CHAR_BLACKLIST.contains(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static final Collection<Character> CHAR_BLACKLIST = new HashSet<Character>(
            Arrays.asList(new Character[] { '&', '"', '\'', '<', '>', '(', ')',
                    ';' }));

    /**
     * Mutex for preventing to scss compilations to take place simultaneously.
     * This is a workaround needed as the scss compiler currently is not thread
     * safe (#10292).
     */
    private static final Object SCSS_MUTEX = new Object();

    /**
     * Returns the default theme. Must never return null.
     * 
     * @return
     */
    public static String getDefaultTheme() {
        return DEFAULT_THEME_NAME;
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    void handleServiceSessionExpired(VaadinServletRequest request,
            VaadinServletResponse response) throws IOException,
            ServletException {

        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        try {
            SystemMessages ci = getService().getSystemMessages(
                    ServletPortletHelper.findLocale(null, null, request),
                    request);
            RequestType requestType = getRequestType(request);
            if (requestType == RequestType.UIDL) {
                /*
                 * Invalidate session (weird to have session if we're saying
                 * that it's expired, and worse: portal integration will fail
                 * since the session is not created by the portal.
                 * 
                 * Session must be invalidated before criticalNotification as it
                 * commits the response.
                 */
                request.getSession().invalidate();

                // send uidl redirect
                criticalNotification(request, response,
                        ci.getSessionExpiredCaption(),
                        ci.getSessionExpiredMessage(), null,
                        ci.getSessionExpiredURL());

            } else if (requestType == RequestType.HEARTBEAT) {
                response.sendError(HttpServletResponse.SC_GONE,
                        "Session expired");
            } else {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                response.sendRedirect(ci.getSessionExpiredURL());
            }
        } catch (SystemMessageException ee) {
            throw new ServletException(ee);
        }

    }

    private void handleServiceSecurityException(VaadinServletRequest request,
            VaadinServletResponse response) throws IOException,
            ServletException {
        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        try {
            /*
             * We might have a UI, but we don't want to leak any information in
             * this case so just use the info provided in the request.
             */
            SystemMessages ci = getService().getSystemMessages(
                    request.getLocale(), request);
            RequestType requestType = getRequestType(request);
            if (requestType == RequestType.UIDL) {
                // send uidl redirect
                criticalNotification(request, response,
                        ci.getCommunicationErrorCaption(),
                        ci.getCommunicationErrorMessage(),
                        INVALID_SECURITY_KEY_MSG, ci.getCommunicationErrorURL());
                /*
                 * Invalidate session. Portal integration will fail otherwise
                 * since the session is not created by the portal.
                 */
                request.getSession().invalidate();

            } else if (requestType == RequestType.HEARTBEAT) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Forbidden");
            } else {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                response.sendRedirect(ci.getCommunicationErrorURL());
            }
        } catch (SystemMessageException ee) {
            throw new ServletException(ee);
        }

        log("Invalid security key received from " + request.getRemoteHost());
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

        // FIXME What does 10 refer to?
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 10) {
            return false;
        }

        if ((request.getContextPath() != null)
                && (request.getRequestURI().startsWith("/VAADIN/"))) {
            serveStaticResourcesInVAADIN(request.getRequestURI(), request,
                    response);
            return true;
        } else if (request.getRequestURI().startsWith(
                request.getContextPath() + "/VAADIN/")) {
            serveStaticResourcesInVAADIN(
                    request.getRequestURI().substring(
                            request.getContextPath().length()), request,
                    response);
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
        URL resourceUrl = findResourceURL(filename, sc);

        if (resourceUrl == null) {
            // File not found, if this was a css request we still look for a
            // scss file with the same name
            if (serveOnTheFlyCompiledScss(filename, request, response, sc)) {
                return;
            } else {
                // cannot serve requested file
                getLogger()
                        .log(Level.INFO,
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
            getLogger()
                    .log(Level.INFO,
                            "Requested resource [{0}] not accessible in the VAADIN directory or access to it is forbidden.",
                            filename);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Find the modification timestamp
        long lastModifiedTime = 0;
        URLConnection connection = null;
        try {
            connection = resourceUrl.openConnection();
            lastModifiedTime = connection.getLastModified();
            // Remove milliseconds to avoid comparison problems (milliseconds
            // are not returned by the browser in the "If-Modified-Since"
            // header).
            lastModifiedTime = lastModifiedTime - lastModifiedTime % 1000;

            if (browserHasNewestVersion(request, lastModifiedTime)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        } catch (Exception e) {
            // Failed to find out last modified timestamp. Continue without it.
            getLogger()
                    .log(Level.FINEST,
                            "Failed to find out last modified timestamp. Continuing without it.",
                            e);
        } finally {
            if (connection instanceof URLConnection) {
                try {
                    // Explicitly close the input stream to prevent it
                    // from remaining hanging
                    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4257700
                    InputStream is = connection.getInputStream();
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    getLogger().log(Level.INFO,
                            "Error closing URLConnection input stream", e);
                }
            }
        }

        // Set type mime type if we can determine it based on the filename
        final String mimetype = sc.getMimeType(filename);
        if (mimetype != null) {
            response.setContentType(mimetype);
        }

        // Provide modification timestamp to the browser if it is known.
        if (lastModifiedTime > 0) {
            response.setDateHeader("Last-Modified", lastModifiedTime);
            /*
             * The browser is allowed to cache for 1 hour without checking if
             * the file has changed. This forces browsers to fetch a new version
             * when the Vaadin version is updated. This will cause more requests
             * to the servlet than without this but for high volume sites the
             * static files should never be served through the servlet. The
             * cache timeout can be configured by setting the resourceCacheTime
             * parameter in web.xml
             */
            int resourceCacheTime = getService().getDeploymentConfiguration()
                    .getResourceCacheTime();
            String cacheControl = "max-age="
                    + String.valueOf(resourceCacheTime);
            if (filename.contains("nocache")) {
                cacheControl = "public, max-age=0, must-revalidate";
            }
            response.setHeader("Cache-Control", cacheControl);
        }

        writeStaticResourceResponse(request, response, resourceUrl);
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
        // Write the resource to the client.
        final OutputStream os = response.getOutputStream();
        final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
        int bytes;
        InputStream is = resourceUrl.openStream();
        while ((bytes = is.read(buffer)) >= 0) {
            os.write(buffer, 0, bytes);
        }
        is.close();
    }

    private URL findResourceURL(String filename, ServletContext sc)
            throws MalformedURLException {
        URL resourceUrl = sc.getResource(filename);
        if (resourceUrl == null) {
            // try if requested file is found from classloader

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
        if (getService().getDeploymentConfiguration().isProductionMode()) {
            // This is not meant for production mode.
            return false;
        }

        if (!filename.endsWith(".css")) {
            return false;
        }

        String scssFilename = filename.substring(0, filename.length() - 4)
                + ".scss";
        URL scssUrl = findResourceURL(scssFilename, sc);
        if (scssUrl == null) {
            // Is a css request but no scss file was found
            return false;
        }
        // security check: do not permit navigation out of the VAADIN
        // directory
        if (!isAllowedVAADINResourceUrl(request, scssUrl)) {
            getLogger()
                    .log(Level.INFO,
                            "Requested resource [{0}] not accessible in the VAADIN directory or access to it is forbidden.",
                            filename);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            // Handled, return true so no further processing is done
            return true;
        }
        synchronized (SCSS_MUTEX) {
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
                getLogger()
                        .log(Level.WARNING,
                                "Scss file {0} exists but ScssStylesheet was not able to find it",
                                scssFilename);
                return false;
            }
            try {
                getLogger().log(Level.FINE, "Compiling {0} for request to {1}",
                        new Object[] { realFilename, filename });
                scss.compile();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            // This is for development mode only so instruct the browser to
            // never
            // cache it
            response.setHeader("Cache-Control", "no-cache");
            final String mimetype = getService().getMimeType(filename);
            writeResponse(response, mimetype, scss.toString());

            return true;
        }
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
        if ("jar".equals(resourceUrl.getProtocol())) {
            // This branch is used for accessing resources directly from the
            // Vaadin JAR in development environments and in similar cases.

            // Inside a JAR, a ".." would mean a real directory named ".." so
            // using it in paths should just result in the file not being found.
            // However, performing a check in case some servers or class loaders
            // try to normalize the path by collapsing ".." before the class
            // loader sees it.

            if (!resourceUrl.getPath().contains("!/VAADIN/")) {
                getLogger()
                        .log(Level.INFO,
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
            if (!resourceUrl.getPath().contains("/VAADIN/")
                    || resourceUrl.getPath().contains("/../")) {
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
     * @since 7.0.0
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected enum RequestType {
        FILE_UPLOAD, BROWSER_DETAILS, UIDL, OTHER, STATIC_FILE, APP, PUBLISHED_FILE, HEARTBEAT;
    }

    /**
     * @param request
     * @return
     * 
     * @deprecated As of 7.0. Will likely change or be removed in a future
     *             version
     */
    @Deprecated
    protected RequestType getRequestType(VaadinServletRequest request) {
        if (ServletPortletHelper.isFileUploadRequest(request)) {
            return RequestType.FILE_UPLOAD;
        } else if (ServletPortletHelper.isPublishedFileRequest(request)) {
            return RequestType.PUBLISHED_FILE;
        } else if (isBrowserDetailsRequest(request)) {
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

    private static boolean isBrowserDetailsRequest(HttpServletRequest request) {
        return "POST".equals(request.getMethod())
                && request.getParameter("v-browserDetails") != null;
    }

    private boolean isStaticResourceRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 10) {
            return false;
        }

        if ((request.getContextPath() != null)
                && (request.getRequestURI().startsWith("/VAADIN/"))) {
            return true;
        } else if (request.getRequestURI().startsWith(
                request.getContextPath() + "/VAADIN/")) {
            return true;
        }

        return false;
    }

    private boolean isOnUnloadRequest(HttpServletRequest request) {
        return request.getParameter(ApplicationConstants.PARAM_UNLOADBURST) != null;
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
     * Write a redirect response to the main page of the application.
     * 
     * @param request
     * @param response
     * @throws IOException
     *             if sending the redirect fails due to an input/output error or
     *             a bad application URL
     */
    private void redirectToApplication(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String applicationUrl = getApplicationUrl(request).toExternalForm();
        response.sendRedirect(response.encodeRedirectURL(applicationUrl));
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
        final URL reqURL = new URL(
                (request.isSecure() ? "https://" : "http://")
                        + request.getServerName()
                        + ((request.isSecure() && request.getServerPort() == 443)
                                || (!request.isSecure() && request
                                        .getServerPort() == 80) ? "" : ":"
                                + request.getServerPort())
                        + request.getRequestURI());
        String servletPath = "";
        if (request.getAttribute("javax.servlet.include.servlet_path") != null) {
            // this is an include request
            servletPath = request.getAttribute(
                    "javax.servlet.include.context_path").toString()
                    + request
                            .getAttribute("javax.servlet.include.servlet_path");

        } else {
            servletPath = request.getContextPath() + request.getServletPath();
        }

        if (servletPath.length() == 0
                || servletPath.charAt(servletPath.length() - 1) != '/') {
            servletPath = servletPath + "/";
        }
        URL u = new URL(reqURL, servletPath);
        return u;
    }

    /**
     * Escapes characters to html entities. An exception is made for some
     * "safe characters" to keep the text somewhat readable.
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
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
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
