/*
 * Copyright 2011 Vaadin Ltd.
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vaadin.Application;
import com.vaadin.Application.ApplicationStartEvent;
import com.vaadin.DefaultApplicationConfiguration;
import com.vaadin.server.AbstractCommunicationManager.Callback;
import com.vaadin.server.ServletPortletHelper.ApplicationClassException;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@SuppressWarnings("serial")
public class VaadinServlet extends HttpServlet implements Constants {

    public static class ServletDeploymentConfiguration extends
            AbstractDeploymentConfiguration {
        private final VaadinServlet servlet;

        public ServletDeploymentConfiguration(VaadinServlet servlet,
                ApplicationConfiguration applicationProperties) {
            super(applicationProperties);
            this.servlet = servlet;
        }

        protected VaadinServlet getServlet() {
            return servlet;
        }

        @Override
        public String getStaticFileLocation(WrappedRequest request) {
            HttpServletRequest servletRequest = WrappedHttpServletRequest
                    .cast(request);
            String staticFileLocation;
            // if property is defined in configurations, use that
            staticFileLocation = getApplicationConfiguration()
                    .getApplicationOrSystemProperty(PARAMETER_VAADIN_RESOURCES,
                            null);
            if (staticFileLocation != null) {
                return staticFileLocation;
            }

            // the last (but most common) option is to generate default location
            // from request

            // if context is specified add it to widgetsetUrl
            String ctxPath = servletRequest.getContextPath();

            // FIXME: ctxPath.length() == 0 condition is probably unnecessary
            // and
            // might even be wrong.

            if (ctxPath.length() == 0
                    && request
                            .getAttribute("javax.servlet.include.context_path") != null) {
                // include request (e.g portlet), get context path from
                // attribute
                ctxPath = (String) request
                        .getAttribute("javax.servlet.include.context_path");
            }

            // Remove heading and trailing slashes from the context path
            ctxPath = removeHeadingOrTrailing(ctxPath, "/");

            if (ctxPath.equals("")) {
                return "";
            } else {
                return "/" + ctxPath;
            }
        }

        @Override
        public String getConfiguredWidgetset(WrappedRequest request) {
            return getApplicationConfiguration()
                    .getApplicationOrSystemProperty(
                            VaadinServlet.PARAMETER_WIDGETSET,
                            VaadinServlet.DEFAULT_WIDGETSET);
        }

        @Override
        public String getConfiguredTheme(WrappedRequest request) {
            // Use the default
            return VaadinServlet.getDefaultTheme();
        }

        @Override
        public boolean isStandalone(WrappedRequest request) {
            return true;
        }

        @Override
        public String getMimeType(String resourceName) {
            return getServlet().getServletContext().getMimeType(resourceName);
        }

        @Override
        public SystemMessages getSystemMessages() {
            return ServletPortletHelper.DEFAULT_SYSTEM_MESSAGES;
        }

        @Override
        public File getBaseDirectory() {
            final String realPath = VaadinServlet.getResourcePath(
                    servlet.getServletContext(), "/");
            if (realPath == null) {
                return null;
            }
            return new File(realPath);
        }
    }

    private static class AbstractApplicationServletWrapper implements Callback {

        private final VaadinServlet servlet;

        public AbstractApplicationServletWrapper(VaadinServlet servlet) {
            this.servlet = servlet;
        }

        @Override
        public void criticalNotification(WrappedRequest request,
                WrappedResponse response, String cap, String msg,
                String details, String outOfSyncURL) throws IOException {
            servlet.criticalNotification(
                    WrappedHttpServletRequest.cast(request),
                    ((WrappedHttpServletResponse) response), cap, msg, details,
                    outOfSyncURL);
        }
    }

    // TODO Move some (all?) of the constants to a separate interface (shared
    // with portlet)

    private final String resourcePath = null;

    private DeploymentConfiguration deploymentConfiguration;

    private AddonContext addonContext;

    /**
     * Called by the servlet container to indicate to a servlet that the servlet
     * is being placed into service.
     * 
     * @param servletConfig
     *            the object containing the servlet's configuration and
     *            initialization parameters
     * @throws javax.servlet.ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    @Override
    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);
        Properties applicationProperties = new Properties();

        // Read default parameters from server.xml
        final ServletContext context = servletConfig.getServletContext();
        for (final Enumeration<String> e = context.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = e.nextElement();
            applicationProperties.setProperty(name,
                    context.getInitParameter(name));
        }

        // Override with application config from web.xml
        for (final Enumeration<String> e = servletConfig
                .getInitParameterNames(); e.hasMoreElements();) {
            final String name = e.nextElement();
            applicationProperties.setProperty(name,
                    servletConfig.getInitParameter(name));
        }

        ApplicationConfiguration applicationConfiguration = createApplicationConfiguration(applicationProperties);
        deploymentConfiguration = createDeploymentConfiguration(applicationConfiguration);

        addonContext = new AddonContext(deploymentConfiguration);
        addonContext.init();
    }

    protected ApplicationConfiguration createApplicationConfiguration(
            Properties applicationProperties) {
        return new DefaultApplicationConfiguration(getClass(),
                applicationProperties);
    }

    protected ServletDeploymentConfiguration createDeploymentConfiguration(
            ApplicationConfiguration applicationConfiguration) {
        return new ServletDeploymentConfiguration(this,
                applicationConfiguration);
    }

    @Override
    public void destroy() {
        super.destroy();

        addonContext.destroy();
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
        service(createWrappedRequest(request), createWrappedResponse(response));
    }

    private void service(WrappedHttpServletRequest request,
            WrappedHttpServletResponse response) throws ServletException,
            IOException {
        RequestTimer requestTimer = new RequestTimer();
        requestTimer.start();

        CurrentInstance.set(WrappedResponse.class, response);
        CurrentInstance.set(WrappedRequest.class, request);

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

        Application application = null;
        boolean requestStarted = false;
        boolean applicationRunning = false;

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
                    && getExistingApplication(request, false) == null) {
                redirectToApplication(request, response);
                return;
            }

            // Find out which application this request is related to
            application = findApplicationInstance(request, requestType);
            if (application == null) {
                return;
            }
            Application.setCurrent(application);

            /*
             * Get or create a WebApplicationContext and an ApplicationManager
             * for the session
             */
            ServletApplicationContext webApplicationContext = getApplicationContext(request
                    .getSession());
            CommunicationManager applicationManager = (CommunicationManager) webApplicationContext
                    .getApplicationManager();

            if (requestType == RequestType.CONNECTOR_RESOURCE) {
                applicationManager.serveConnectorResource(request, response);
                return;
            } else if (requestType == RequestType.HEARTBEAT) {
                applicationManager.handleHeartbeatRequest(request, response,
                        application);
                return;
            }

            /* Update browser information from the request */
            webApplicationContext.getBrowser().updateRequestDetails(request);

            /*
             * Call application requestStart before Application.init() is called
             * (bypasses the limitation in TransactionListener)
             */
            if (application instanceof HttpServletRequestListener) {
                ((HttpServletRequestListener) application).onRequestStart(
                        request, response);
                requestStarted = true;
            }

            // Start the application if it's newly created
            startApplication(request, application, webApplicationContext);
            applicationRunning = true;

            /* Handle the request */
            if (requestType == RequestType.FILE_UPLOAD) {
                // UI is resolved in communication manager
                applicationManager.handleFileUpload(application, request,
                        response);
                return;
            } else if (requestType == RequestType.UIDL) {
                UI uI = application.getUIForRequest(request);
                if (uI == null) {
                    throw new ServletException(ERROR_NO_UI_FOUND);
                }
                // Handles AJAX UIDL requests
                applicationManager.handleUidlRequest(request, response,
                        servletWrapper, uI);
                return;
            } else if (requestType == RequestType.BROWSER_DETAILS) {
                // Browser details - not related to a specific UI
                applicationManager.handleBrowserDetailsRequest(request,
                        response, application);
                return;
            }

            // Removes application if it has stopped (maybe by thread or
            // transactionlistener)
            if (!application.isRunning()) {
                endApplication(request, response, application);
                return;
            }

            if (applicationManager.handleApplicationRequest(request, response)) {
                return;
            }
            // TODO Should return 404 error here and not do anything more

        } catch (final SessionExpiredException e) {
            // Session has expired, notify user
            handleServiceSessionExpired(request, response);
        } catch (final GeneralSecurityException e) {
            handleServiceSecurityException(request, response);
        } catch (final Throwable e) {
            handleServiceException(request, response, application, e);
        } finally {

            if (applicationRunning) {
                application.closeInactiveUIs();
            }

            // Notifies transaction end
            try {
                if (requestStarted) {
                    ((HttpServletRequestListener) application).onRequestEnd(
                            request, response);
                }
            } finally {
                CurrentInstance.clearAll();

                HttpSession session = request.getSession(false);
                if (session != null) {
                    requestTimer.stop(getApplicationContext(session));
                }
            }

        }
    }

    private WrappedHttpServletResponse createWrappedResponse(
            HttpServletResponse response) {
        WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse(
                response, getDeploymentConfiguration());
        return wrappedResponse;
    }

    /**
     * Create a wrapped request for a http servlet request. This method can be
     * overridden if the wrapped request should have special properties.
     * 
     * @param request
     *            the original http servlet request
     * @return a wrapped request for the original request
     */
    protected WrappedHttpServletRequest createWrappedRequest(
            HttpServletRequest request) {
        return new WrappedHttpServletRequest(request,
                getDeploymentConfiguration());
    }

    /**
     * Gets a the deployment configuration for this servlet.
     * 
     * @return the deployment configuration
     */
    protected DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
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
            WrappedHttpServletRequest request,
            WrappedHttpServletResponse response) throws IOException {
        if (requestType == RequestType.UIDL && !isRepaintAll(request)) {
            // In all other but the first UIDL request a cookie should be
            // returned by the browser.
            // This can be removed if cookieless mode (#3228) is supported
            if (request.getRequestedSessionId() == null) {
                // User has cookies disabled
                SystemMessages systemMessages = getDeploymentConfiguration()
                        .getSystemMessages();
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
     * Send a notification to client's application. Used to notify client of
     * critical errors, session expiration and more. Server has no knowledge of
     * what application client refers to.
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
     */
    protected void criticalNotification(WrappedHttpServletRequest request,
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
     * Returns the application instance to be used for the request. If an
     * existing instance is not found a new one is created or null is returned
     * to indicate that the application is not available.
     * 
     * @param request
     * @param requestType
     * @return
     * @throws MalformedURLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ServletException
     * @throws SessionExpiredException
     */
    private Application findApplicationInstance(HttpServletRequest request,
            RequestType requestType) throws MalformedURLException,
            ServletException, SessionExpiredException {

        boolean requestCanCreateApplication = requestCanCreateApplication(
                request, requestType);

        /* Find an existing application for this request. */
        Application application = getExistingApplication(request,
                requestCanCreateApplication);

        if (application != null) {
            /*
             * There is an existing application. We can use this as long as the
             * user not specifically requested to close or restart it.
             */

            final boolean restartApplication = (request
                    .getParameter(URL_PARAMETER_RESTART_APPLICATION) != null);
            final boolean closeApplication = (request
                    .getParameter(URL_PARAMETER_CLOSE_APPLICATION) != null);

            if (restartApplication) {
                closeApplication(application, request.getSession(false));
                return createApplication(request);
            } else if (closeApplication) {
                closeApplication(application, request.getSession(false));
                return null;
            } else {
                return application;
            }
        }

        // No existing application was found

        if (requestCanCreateApplication) {
            /*
             * If the request is such that it should create a new application if
             * one as not found, we do that.
             */
            return createApplication(request);
        } else {
            /*
             * The application was not found and a new one should not be
             * created. Assume the session has expired.
             */
            throw new SessionExpiredException();
        }

    }

    /**
     * Check if the request should create an application if an existing
     * application is not found.
     * 
     * @param request
     * @param requestType
     * @return true if an application should be created, false otherwise
     */
    boolean requestCanCreateApplication(HttpServletRequest request,
            RequestType requestType) {
        if (requestType == RequestType.UIDL && isRepaintAll(request)) {
            /*
             * UIDL request contains valid repaintAll=1 event, the user probably
             * wants to initiate a new application through a custom index.html
             * without using the bootstrap page.
             */
            return true;

        } else if (requestType == RequestType.OTHER) {
            /*
             * I.e URIs that are not application resources or static (theme)
             * files.
             */
            return true;

        }

        return false;
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
     */
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
     * Creates a new application and registers it into WebApplicationContext
     * (aka session). This is not meant to be overridden. Override
     * getNewApplication to create the application instance in a custom way.
     * 
     * @param request
     * @return
     * @throws ServletException
     * @throws MalformedURLException
     */
    private Application createApplication(HttpServletRequest request)
            throws ServletException, MalformedURLException {
        Application newApplication = getNewApplication(request);

        final ServletApplicationContext context = getApplicationContext(request
                .getSession());
        context.setApplication(newApplication,
                createCommunicationManager(newApplication));

        return newApplication;
    }

    private void handleServiceException(WrappedHttpServletRequest request,
            WrappedHttpServletResponse response, Application application,
            Throwable e) throws IOException, ServletException {
        // if this was an UIDL request, response UIDL back to client
        if (getRequestType(request) == RequestType.UIDL) {
            SystemMessages ci = getDeploymentConfiguration()
                    .getSystemMessages();
            criticalNotification(request, response,
                    ci.getInternalErrorCaption(), ci.getInternalErrorMessage(),
                    null, ci.getInternalErrorURL());
            if (application != null) {
                application.getErrorHandler()
                        .terminalError(new RequestError(e));
            } else {
                throw new ServletException(e);
            }
        } else {
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
     */
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
     * Returns the default theme. Must never return null.
     * 
     * @return
     */
    public static String getDefaultTheme() {
        return DEFAULT_THEME_NAME;
    }

    void handleServiceSessionExpired(WrappedHttpServletRequest request,
            WrappedHttpServletResponse response) throws IOException,
            ServletException {

        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        try {
            SystemMessages ci = getDeploymentConfiguration()
                    .getSystemMessages();
            if (getRequestType(request) != RequestType.UIDL) {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                response.sendRedirect(ci.getSessionExpiredURL());
            } else {
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

            }
        } catch (SystemMessageException ee) {
            throw new ServletException(ee);
        }

    }

    private void handleServiceSecurityException(
            WrappedHttpServletRequest request,
            WrappedHttpServletResponse response) throws IOException,
            ServletException {
        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        try {
            SystemMessages ci = getDeploymentConfiguration()
                    .getSystemMessages();
            if (getRequestType(request) != RequestType.UIDL) {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                response.sendRedirect(ci.getCommunicationErrorURL());
            } else {
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
            }
        } catch (SystemMessageException ee) {
            throw new ServletException(ee);
        }

        log("Invalid security key received from " + request.getRemoteHost());
    }

    /**
     * Creates a new application for the given request.
     * 
     * @param request
     *            the HTTP request.
     * @return A new Application instance.
     * @throws ServletException
     */
    protected Application getNewApplication(HttpServletRequest request)
            throws ServletException {

        // Creates a new application instance
        try {
            Class<? extends Application> applicationClass = ServletPortletHelper
                    .getApplicationClass(getDeploymentConfiguration());

            final Application application = applicationClass.newInstance();
            application.addUIProvider(new DefaultUIProvider());

            return application;
        } catch (final IllegalAccessException e) {
            throw new ServletException("getNewApplication failed", e);
        } catch (final InstantiationException e) {
            throw new ServletException("getNewApplication failed", e);
        } catch (ApplicationClassException e) {
            throw new ServletException("getNewApplication failed", e);
        }
    }

    /**
     * Starts the application if it is not already running.
     * 
     * @param request
     * @param application
     * @param webApplicationContext
     * @throws ServletException
     * @throws MalformedURLException
     */
    private void startApplication(HttpServletRequest request,
            Application application,
            ServletApplicationContext webApplicationContext)
            throws ServletException, MalformedURLException {

        if (!application.isRunning()) {
            // Create application
            final URL applicationUrl = getApplicationUrl(request);

            // Initial locale comes from the request
            Locale locale = request.getLocale();
            application.setLocale(locale);
            application.start(new ApplicationStartEvent(applicationUrl,
                    getDeploymentConfiguration().getApplicationConfiguration(),
                    webApplicationContext));
            addonContext.fireApplicationStarted(application);
        }
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
        URL resourceUrl = sc.getResource(filename);
        if (resourceUrl == null) {
            // try if requested file is found from classloader

            // strip leading "/" otherwise stream from JAR wont work
            filename = filename.substring(1);
            resourceUrl = getDeploymentConfiguration().getClassLoader()
                    .getResource(filename);

            if (resourceUrl == null) {
                // cannot serve requested file
                getLogger()
                        .info("Requested resource ["
                                + filename
                                + "] not found from filesystem or through class loader."
                                + " Add widgetset and/or theme JAR to your classpath or add files to WebContent/VAADIN folder.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // security check: do not permit navigation out of the VAADIN
            // directory
            if (!isAllowedVAADINResourceUrl(request, resourceUrl)) {
                getLogger()
                        .info("Requested resource ["
                                + filename
                                + "] not accessible in the VAADIN directory or access to it is forbidden.");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
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
            int resourceCacheTime = getDeploymentConfiguration()
                    .getApplicationConfiguration().getResourceCacheTime();
            response.setHeader("Cache-Control",
                    "max-age= " + String.valueOf(resourceCacheTime));
        }

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
     */
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
                getLogger().info(
                        "Blocked attempt to access a JAR entry not starting with /VAADIN/: "
                                + resourceUrl);
                return false;
            }
            getLogger().fine(
                    "Accepted access to a JAR entry using a class loader: "
                            + resourceUrl);
            return true;
        } else {
            // Some servers such as GlassFish extract files from JARs (file:)
            // and e.g. JBoss 5+ use protocols vsf: and vfsfile: .

            // Check that the URL is in a VAADIN directory and does not contain
            // "/../"
            if (!resourceUrl.getPath().contains("/VAADIN/")
                    || resourceUrl.getPath().contains("/../")) {
                getLogger().info(
                        "Blocked attempt to access file: " + resourceUrl);
                return false;
            }
            getLogger().fine(
                    "Accepted access to a file using a class loader: "
                            + resourceUrl);
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

    protected enum RequestType {
        FILE_UPLOAD, BROWSER_DETAILS, UIDL, OTHER, STATIC_FILE, APPLICATION_RESOURCE, CONNECTOR_RESOURCE, HEARTBEAT;
    }

    protected RequestType getRequestType(WrappedHttpServletRequest request) {
        if (ServletPortletHelper.isFileUploadRequest(request)) {
            return RequestType.FILE_UPLOAD;
        } else if (ServletPortletHelper.isConnectorResourceRequest(request)) {
            return RequestType.CONNECTOR_RESOURCE;
        } else if (isBrowserDetailsRequest(request)) {
            return RequestType.BROWSER_DETAILS;
        } else if (ServletPortletHelper.isUIDLRequest(request)) {
            return RequestType.UIDL;
        } else if (isStaticResourceRequest(request)) {
            return RequestType.STATIC_FILE;
        } else if (ServletPortletHelper.isApplicationResourceRequest(request)) {
            return RequestType.APPLICATION_RESOURCE;
        } else if (ServletPortletHelper.isHeartbeatRequest(request)) {
            return RequestType.HEARTBEAT;
        }
        return RequestType.OTHER;

    }

    private static boolean isBrowserDetailsRequest(HttpServletRequest request) {
        return "POST".equals(request.getMethod())
                && request.getParameter("browserDetails") != null;
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
    private static String removeHeadingOrTrailing(String string, String what) {
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
     */
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
     * Gets the existing application for given request. Looks for application
     * instance for given request based on the requested URL.
     * 
     * @param request
     *            the HTTP request.
     * @param allowSessionCreation
     *            true if a session should be created if no session exists,
     *            false if no session should be created
     * @return Application instance, or null if the URL does not map to valid
     *         application.
     * @throws MalformedURLException
     *             if the application is denied access to the persistent data
     *             store represented by the given URL.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SessionExpiredException
     */
    protected Application getExistingApplication(HttpServletRequest request,
            boolean allowSessionCreation) throws MalformedURLException,
            SessionExpiredException {

        // Ensures that the session is still valid
        final HttpSession session = request.getSession(allowSessionCreation);
        if (session == null) {
            throw new SessionExpiredException();
        }

        ServletApplicationContext context = getApplicationContext(session);

        Application sessionApplication = context.getApplication();

        if (sessionApplication == null) {
            return null;
        }

        if (sessionApplication.isRunning()) {
            // Found a running application
            return sessionApplication;
        }
        // Application has stopped, so remove it before creating a new
        // application
        getApplicationContext(session).removeApplication();

        // Existing application not found
        return null;
    }

    /**
     * Ends the application.
     * 
     * @param request
     *            the HTTP request.
     * @param response
     *            the HTTP response to write to.
     * @param application
     *            the application to end.
     * @throws IOException
     *             if the writing failed due to input/output error.
     */
    private void endApplication(HttpServletRequest request,
            HttpServletResponse response, Application application)
            throws IOException {

        String logoutUrl = application.getLogoutURL();
        if (logoutUrl == null) {
            logoutUrl = application.getURL().toString();
        }

        final HttpSession session = request.getSession();
        if (session != null) {
            getApplicationContext(session).removeApplication();
        }

        response.sendRedirect(response.encodeRedirectURL(logoutUrl));
    }

    /**
     * Returns the path info; note that this _can_ be different than
     * request.getPathInfo(). Examples where this might be useful:
     * <ul>
     * <li>An application runner servlet that runs different Vaadin applications
     * based on an identifier.</li>
     * <li>Providing a REST interface in the context root, while serving a
     * Vaadin UI on a sub-URI using only one servlet (e.g. REST on
     * http://example.com/foo, UI on http://example.com/foo/vaadin)</li>
     * 
     * @param request
     * @return
     */
    protected String getRequestPathInfo(HttpServletRequest request) {
        return request.getPathInfo();
    }

    /**
     * Gets relative location of a theme resource.
     * 
     * @param theme
     *            the Theme name.
     * @param resource
     *            the Theme resource.
     * @return External URI specifying the resource
     */
    public String getResourceLocation(String theme, ThemeResource resource) {

        if (resourcePath == null) {
            return resource.getResourceId();
        }
        return resourcePath + theme + "/" + resource.getResourceId();
    }

    private boolean isRepaintAll(HttpServletRequest request) {
        return (request.getParameter(URL_PARAMETER_REPAINT_ALL) != null)
                && (request.getParameter(URL_PARAMETER_REPAINT_ALL).equals("1"));
    }

    private void closeApplication(Application application, HttpSession session) {
        if (application == null) {
            return;
        }

        application.close();
        if (session != null) {
            ServletApplicationContext context = getApplicationContext(session);
            context.removeApplication();
        }
    }

    /**
     * 
     * Gets the application context from an HttpSession. If no context is
     * currently stored in a session a new context is created and stored in the
     * session.
     * 
     * @param session
     *            the HTTP session.
     * @return the application context for HttpSession.
     */
    protected ServletApplicationContext getApplicationContext(
            HttpSession session) {
        /*
         * TODO the ApplicationContext.getApplicationContext() should be removed
         * and logic moved here. Now overriding context type is possible, but
         * the whole creation logic should be here. MT 1101
         */
        return ServletApplicationContext.getApplicationContext(session);
    }

    public class RequestError implements Terminal.ErrorEvent, Serializable {

        private final Throwable throwable;

        public RequestError(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public Throwable getThrowable() {
            return throwable;
        }

    }

    /**
     * Override this method if you need to use a specialized communicaiton
     * mananger implementation.
     * 
     * @deprecated Instead of overriding this method, override
     *             {@link ServletApplicationContext} implementation via
     *             {@link VaadinServlet#getApplicationContext(HttpSession)}
     *             method and in that customized implementation return your
     *             CommunicationManager in
     *             {@link ServletApplicationContext#getApplicationManager(Application, VaadinServlet)}
     *             method.
     * 
     * @param application
     * @return
     */
    @Deprecated
    public CommunicationManager createCommunicationManager(
            Application application) {
        return new CommunicationManager(application);
    }

    /**
     * Escapes characters to html entities. An exception is made for some
     * "safe characters" to keep the text somewhat readable.
     * 
     * @param unsafe
     * @return a safe string to be added inside an html tag
     */
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
