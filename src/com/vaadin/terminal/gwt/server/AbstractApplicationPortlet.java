package com.vaadin.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.Application.SystemMessages;
import com.vaadin.external.org.apache.commons.fileupload.portlet.PortletFileUpload;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.ui.Window;

/**
 * TODO Document me!
 * 
 * @author peholmst
 */
public abstract class AbstractApplicationPortlet extends GenericPortlet
        implements Constants {

    /*
     * TODO Big parts of this class are directly copy-pasted from
     * AbstractApplicationServlet. On the long term, it would probably be wise
     * to try to integrate the common parts into a shared super class.
     */

    // TODO Close application when portlet window is closed

    // TODO What happens when the portlet window is resized?

    private Properties applicationProperties;

    private boolean productionMode = false;

    @SuppressWarnings("unchecked")
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        // Stores the application parameters into Properties object
        applicationProperties = new Properties();
        for (final Enumeration e = config.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = (String) e.nextElement();
            applicationProperties.setProperty(name, config
                    .getInitParameter(name));
        }

        // Overrides with server.xml parameters
        final PortletContext context = config.getPortletContext();
        for (final Enumeration e = context.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = (String) e.nextElement();
            applicationProperties.setProperty(name, context
                    .getInitParameter(name));
        }
        checkProductionMode();
        checkCrossSiteProtection();
    }

    private void checkCrossSiteProtection() {
        if (getApplicationOrSystemProperty(
                SERVLET_PARAMETER_DISABLE_XSRF_PROTECTION, "false").equals(
                "true")) {
            /*
             * Print an information/warning message about running with xsrf
             * protection disabled
             */
            System.err.println(WARNING_XSRF_PROTECTION_DISABLED);
        }
    }

    private void checkProductionMode() {
        // Check if the application is in production mode.
        // We are in production mode if Debug=false or productionMode=true
        if (getApplicationOrSystemProperty(SERVLET_PARAMETER_DEBUG, "true")
                .equals("false")) {
            // "Debug=true" is the old way and should no longer be used
            productionMode = true;
        } else if (getApplicationOrSystemProperty(
                SERVLET_PARAMETER_PRODUCTION_MODE, "false").equals("true")) {
            // "productionMode=true" is the real way to do it
            productionMode = true;
        }

        if (!productionMode) {
            /* Print an information/warning message about running in debug mode */
            // TODO Maybe we need a different message for portlets?
            System.err.println(NOT_PRODUCTION_MODE_INFO);
        }
    }

    /**
     * Gets an application property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @return String value or null if not found
     */
    protected String getApplicationProperty(String parameterName) {

        String val = applicationProperties.getProperty(parameterName);
        if (val != null) {
            return val;
        }

        // Try lower case application properties for backward compatibility with
        // 3.0.2 and earlier
        val = applicationProperties.getProperty(parameterName.toLowerCase());

        return val;
    }

    /**
     * Gets an system property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @return String value or null if not found
     */
    protected String getSystemProperty(String parameterName) {
        String val = null;

        String pkgName;
        final Package pkg = getClass().getPackage();
        if (pkg != null) {
            pkgName = pkg.getName();
        } else {
            final String className = getClass().getName();
            pkgName = new String(className.toCharArray(), 0, className
                    .lastIndexOf('.'));
        }
        val = System.getProperty(pkgName + "." + parameterName);
        if (val != null) {
            return val;
        }

        // Try lowercased system properties
        val = System.getProperty(pkgName + "." + parameterName.toLowerCase());
        return val;
    }

    /**
     * Gets an application or system property value.
     * 
     * @param parameterName
     *            the Name or the parameter.
     * @param defaultValue
     *            the Default to be used.
     * @return String value or default if not found
     */
    private String getApplicationOrSystemProperty(String parameterName,
            String defaultValue) {

        String val = null;

        // Try application properties
        val = getApplicationProperty(parameterName);
        if (val != null) {
            return val;
        }

        // Try system properties
        val = getSystemProperty(parameterName);
        if (val != null) {
            return val;
        }

        return defaultValue;
    }

    enum RequestType {
        FILE_UPLOAD, UIDL, RENDER, STATIC_FILE, UNKNOWN;
    }

    protected RequestType getRequestType(PortletRequest request) {
        if (request instanceof RenderRequest) {
            return RequestType.RENDER;
        } else if (request instanceof ResourceRequest) {
            if (isUIDLRequest((ResourceRequest) request)) {
                return RequestType.UIDL;
            } else if (isStaticResourceRequest((ResourceRequest) request)) {
                return RequestType.STATIC_FILE;
            }
        } else if (request instanceof ActionRequest) {
            if (isFileUploadRequest((ActionRequest) request)) {
                return RequestType.FILE_UPLOAD;
            }
        }
        return RequestType.UNKNOWN;
    }

    private boolean isStaticResourceRequest(ResourceRequest request) {
        String resourceID = request.getResourceID();
        if (resourceID != null && !resourceID.startsWith("/VAADIN/")) {
            return true;
        }
        return false;
    }

    private boolean isUIDLRequest(ResourceRequest request) {
        return request.getResourceID() != null
                && request.getResourceID().equals("UIDL");
    }

    private boolean isFileUploadRequest(ActionRequest request) {
        return PortletFileUpload.isMultipartContent(request);
    }

    /**
     * Returns true if the servlet is running in production mode. Production
     * mode disables all debug facilities.
     * 
     * @return true if in production mode, false if in debug mode
     */
    public boolean isProductionMode() {
        return productionMode;
    }

    protected void handleRequest(PortletRequest request,
            PortletResponse response) throws PortletException, IOException {
        // System.out.println("AbstractApplicationPortlet.handleRequest() " +
        // System.currentTimeMillis());

        RequestType requestType = getRequestType(request);

        // System.out.println("  RequestType: " + requestType);
        // System.out.println("  WindowID: " + request.getWindowID());

        if (requestType == RequestType.UNKNOWN) {
            System.out.println("Unknown request type");
            return;
        } else if (requestType == RequestType.STATIC_FILE) {
            serveStaticResources((ResourceRequest) request,
                    (ResourceResponse) response);
            return;
        }

        Application application = null;

        try {
            /* Find out which application this request is related to */
            application = findApplicationInstance(request, requestType);
            if (application == null) {
                return;
            }

            /*
             * Get or create an application context and an application manager
             * for the session
             */
            PortletApplicationContext2 applicationContext = PortletApplicationContext2
                    .getApplicationContext(request.getPortletSession());
            PortletCommunicationManager applicationManager = applicationContext
                    .getApplicationManager(application);

            /* Update browser information from request */
            applicationContext.getBrowser().updateBrowserProperties(request);

            /* Start the newly created application */
            startApplication(request, application, applicationContext);

            /*
             * Transaction starts. Call transaction listeners. Transaction end
             * is called in the finally block below.
             */
            applicationContext.startTransaction(application, request);

            /* Handle the request */
            if (requestType == RequestType.FILE_UPLOAD) {
                applicationManager.handleFileUpload((ActionRequest) request,
                        (ActionResponse) response);
                return;
            } else if (requestType == RequestType.UIDL) {
                // Handles AJAX UIDL requests
                applicationManager.handleUidlRequest((ResourceRequest) request,
                        (ResourceResponse) response, this);
                return;
            } else if (requestType == RequestType.RENDER) {
                /*
                 * Removes the application if it has stopped
                 */
                if (!application.isRunning()) {
                    endApplication(request, response, application);
                    return;
                }

                /*
                 * Always use the main window when running inside a portlet.
                 */
                Window window = application.getMainWindow();
                if (window == null) {
                    throw new PortletException(ERROR_NO_WINDOW_FOUND);
                }

                /*
                 * Sets terminal type for the window, if not already set
                 */
                if (window.getTerminal() == null) {
                    window.setTerminal(applicationContext.getBrowser());
                }

                /*
                 * Handle parameters
                 */
                final Map<String, String[]> parameters = request
                        .getParameterMap();
                if (window != null && parameters != null) {
                    window.handleParameters(parameters);
                }

                /*
                 * Send initial AJAX page that kickstarts the Vaadin application
                 */
                writeAjaxPage((RenderRequest) request,
                        (RenderResponse) response, window, application);
            }
        } catch (final SessionExpired e) {
            // Session has expired, notify user
            handleServiceSessionExpired(request, response);
        } catch (final GeneralSecurityException e) {
            handleServiceSecurityException(request, response);
        } catch (final Throwable e) {
            handleServiceException(request, response, application, e);
        } finally {
            // Notifies transaction end
            if (application != null) {
                ((PortletApplicationContext2) application.getContext())
                        .endTransaction(application, request);
            }
        }
    }

    private void serveStaticResources(ResourceRequest request,
            ResourceResponse response) throws IOException, PortletException {
        final String resourceID = request.getResourceID();
        final PortletContext pc = getPortletContext();

//        System.out.println("Trying to load resource [" + resourceID + "]");

        InputStream is = pc.getResourceAsStream(resourceID);
        if (is != null) {
            final String mimetype = pc.getMimeType(resourceID);
            if (mimetype != null) {
                response.setContentType(mimetype);
            }
            final OutputStream os = response.getPortletOutputStream();
            final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
            int bytes;
            while ((bytes = is.read(buffer)) >= 0) {
                os.write(buffer, 0, bytes);
            }
        } else {
            System.err.println("Requested resource [" + resourceID
                    + "] could not be found");
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer
                    .toString(HttpServletResponse.SC_NOT_FOUND));
        }
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        // System.out.println("AbstractApplicationPortlet.processAction()");
        handleRequest(request, response);
    }

    @RenderMode(name = "VIEW")
    public void doRender(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        // System.out.println("AbstractApplicationPortlet.render()");
        handleRequest(request, response);
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {
        // System.out.println("AbstractApplicationPortlet.serveResource()");
        handleRequest(request, response);
    }

    boolean requestCanCreateApplication(PortletRequest request,
            RequestType requestType) {
        if (requestType == RequestType.UIDL && isRepaintAll(request)) {
            return true;
        } else if (requestType == RequestType.RENDER) {
            return true;
        }
        return false;
    }

    private boolean isRepaintAll(PortletRequest request) {
        return (request.getParameter(URL_PARAMETER_REPAINT_ALL) != null)
                && (request.getParameter(URL_PARAMETER_REPAINT_ALL).equals("1"));
    }

    private void startApplication(PortletRequest request,
            Application application, PortletApplicationContext2 context)
            throws PortletException, MalformedURLException {
        if (!application.isRunning()) {
            Locale locale = request.getLocale();
            application.setLocale(locale);
            // No application URL when running inside a portlet
            application.start(null, applicationProperties, context);
        }
    }

    private void endApplication(PortletRequest request,
            PortletResponse response, Application application)
            throws IOException {
        final PortletSession session = request.getPortletSession();
        if (session != null) {
            PortletApplicationContext2.getApplicationContext(session)
                    .removeApplication(application);
        }
        // Do not send any redirects when running inside a portlet.
    }

    private Application findApplicationInstance(PortletRequest request,
            RequestType requestType) throws PortletException, SessionExpired,
            MalformedURLException {

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
                closeApplication(application, request.getPortletSession(false));
                return createApplication(request);
            } else if (closeApplication) {
                closeApplication(application, request.getPortletSession(false));
                return null;
            } else {
                return application;
            }
        }

        // No existing application was found

        if (requestCanCreateApplication) {
            return createApplication(request);
        } else {
            throw new SessionExpired();
        }
    }

    private void closeApplication(Application application,
            PortletSession session) {
        if (application == null) {
            return;
        }

        application.close();
        if (session != null) {
            PortletApplicationContext2 context = PortletApplicationContext2
                    .getApplicationContext(session);
            context.applicationToAjaxAppMgrMap.remove(application);
            context.removeApplication(application);
        }
    }

    private Application createApplication(PortletRequest request)
            throws PortletException, MalformedURLException {
        Application newApplication = getNewApplication(request);
        newApplication.setPortletWindowId(request.getWindowID());
        final PortletApplicationContext2 context = PortletApplicationContext2
                .getApplicationContext(request.getPortletSession());
        context.addApplication(newApplication);
        return newApplication;
    }

    private Application getExistingApplication(PortletRequest request,
            boolean allowSessionCreation) throws MalformedURLException,
            SessionExpired {

        final PortletSession session = request
                .getPortletSession(allowSessionCreation);
        if (session == null) {
            throw new SessionExpired();
        }

        PortletApplicationContext2 context = PortletApplicationContext2
                .getApplicationContext(session);

        final Collection<Application> applications = context.getApplications();
        for (final Iterator<Application> i = applications.iterator(); i
                .hasNext();) {
            final Application sessionApplication = i.next();
            if (request.getWindowID().equals(
                    sessionApplication.getPortletWindowId())) {
                if (sessionApplication.isRunning()) {
                    return sessionApplication;
                }
                PortletApplicationContext2.getApplicationContext(session)
                        .removeApplication(sessionApplication);
                break;
            }
        }

        return null;
    }

    protected void writeAjaxPage(RenderRequest request,
            RenderResponse response, Window window, Application application)
            throws IOException, MalformedURLException, PortletException {

//        System.out.println("AbstractApplicationPortlet.writeAjaxPage()");

        response.setContentType("text/html");
        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getPortletOutputStream(), "UTF-8"));

        // TODO The widgetset URL is currently hard-corded for LifeRay

        String widgetsetURL = "/html/" + WIDGETSET_DIRECTORY_PATH
                + DEFAULT_WIDGETSET + "/" + DEFAULT_WIDGETSET + ".nocache.js?"
                + new Date().getTime();

        String themeURI = "/html/" + THEME_DIRECTORY_PATH + DEFAULT_THEME_NAME;

        page.write("<script type=\"text/javascript\">\n");
        page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n "
                + "if(!vaadin) { var vaadin = {}} \n"
                + "vaadin.vaadinConfigurations = {};\n"
                + "if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }\n");
        // TODO Add support for production mode
        page.write("vaadin.debug = true;\n");
        page
                .write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                        + "style=\"width:0;height:0;border:0;overflow:"
                        + "hidden\" src=\"javascript:false\"></iframe>');\n");
        page.write("document.write(\"<script language='javascript' src='"
                + widgetsetURL + "'><\\/script>\");\n}\n");

        page.write("vaadin.vaadinConfigurations[\"" + request.getWindowID()
                + "\"] = {");
        page.write("appUri: '', ");
        // page.write("appId: '', ");
        page.write("usePortletURLs: true, ");

        ResourceURL uidlUrlBase = response.createResourceURL();
        uidlUrlBase.setResourceID("UIDL");

        page.write("portletUidlURLBase: '" + uidlUrlBase.toString() + "', ");
        page.write("pathInfo: '', ");
        page.write("themeUri: '" + themeURI + "', ");
        page.write("versionInfo : {vaadinVersion:\"");
        // page.write(VERSION);
        page.write("UNVERSIONED"); // TODO Fix this
        page.write("\",applicationVersion:\"");
        page.write(application.getVersion());
        page.write("\"},");
        // TODO Add system messages
        page.write("};\n</script>\n");

        // if (themeName != null) {
        // Custom theme's stylesheet, load only once, in different
        // script
        // tag to be dominate styles injected by widget
        // set
        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");
        page
                .write("if(!vaadin.themesLoaded['" + DEFAULT_THEME_NAME
                        + "']) {\n");
        page.write("var stylesheet = document.createElement('link');\n");
        page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
        page.write("stylesheet.setAttribute('type', 'text/css');\n");
        page.write("stylesheet.setAttribute('href', '" + themeURI
                + "/styles.css');\n");
        page
                .write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
        page.write("vaadin.themesLoaded['" + DEFAULT_THEME_NAME
                + "'] = true;\n}\n");
        page.write("//]]>\n</script>\n");
        // }

        // TODO Warn if widgetset has not been loaded after 15 seconds

        /*- Add classnames;
         *      .v-app
         *      .v-app-loading
         *      .v-app-<simpleName for app class>
         *      .v-theme-<themeName, remove non-alphanum>
         */

        String appClass = "v-app-";
        try {
            appClass += getApplicationClass().getSimpleName();
        } catch (ClassNotFoundException e) {
            appClass += "unknown";
            e.printStackTrace();
        }
        // TODO Add support for flexible theme names
        String themeClass = "v-theme-"
                + DEFAULT_THEME_NAME.replaceAll("[^a-zA-Z0-9]", "");

        String classNames = "v-app v-app-loading " + themeClass + " "
                + appClass;

        page.write("<div id=\"" + request.getWindowID() + "\" class=\""
                + classNames + "\"></div>\n");

        page.close();
    }

    protected abstract Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException;

    protected Application getNewApplication(PortletRequest request)
            throws PortletException {
        try {
            final Application application = getApplicationClass().newInstance();
            return application;
        } catch (final IllegalAccessException e) {
            throw new PortletException("getNewApplication failed", e);
        } catch (final InstantiationException e) {
            throw new PortletException("getNewApplication failed", e);
        } catch (final ClassNotFoundException e) {
            throw new PortletException("getNewApplication failed", e);
        }
    }

    protected ClassLoader getClassLoader() throws PortletException {
        // TODO Add support for custom class loader
        return getClass().getClassLoader();
    }

    private boolean isOnUnloadRequest(PortletRequest request) {
        return request.getParameter(ApplicationConnection.PARAM_UNLOADBURST) != null;
    }

    /**
     * Get system messages from the current application class
     * 
     * @return
     */
    protected SystemMessages getSystemMessages() {
        try {
            Class<? extends Application> appCls = getApplicationClass();
            Method m = appCls.getMethod("getSystemMessages", (Class[]) null);
            return (Application.SystemMessages) m.invoke(null, (Object[]) null);
        } catch (ClassNotFoundException e) {
            // This should never happen
            throw new SystemMessageException(e);
        } catch (SecurityException e) {
            throw new SystemMessageException(
                    "Application.getSystemMessage() should be static public", e);
        } catch (NoSuchMethodException e) {
            // This is completely ok and should be silently ignored
        } catch (IllegalArgumentException e) {
            // This should never happen
            throw new SystemMessageException(e);
        } catch (IllegalAccessException e) {
            throw new SystemMessageException(
                    "Application.getSystemMessage() should be static public", e);
        } catch (InvocationTargetException e) {
            // This should never happen
            throw new SystemMessageException(e);
        }
        return Application.getSystemMessages();
    }

    void handleServiceSessionExpired(PortletRequest request,
            PortletResponse response) throws IOException, PortletException {

        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        try {
            Application.SystemMessages ci = getSystemMessages();
            if (getRequestType(request) != RequestType.UIDL) {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                if (response instanceof ActionResponse) {
                    ((ActionResponse) response).sendRedirect(ci
                            .getSessionExpiredURL());
                } else {
                    // TODO What to do if we are e.g. rendering?
                }
            } else {
                /*
                 * Session must be invalidated before criticalNotification as it
                 * commits the response.
                 */
                request.getPortletSession().invalidate();

                // send uidl redirect
                criticalNotification(request, (ResourceResponse) response, ci
                        .getSessionExpiredCaption(), ci
                        .getSessionExpiredMessage(), null, ci
                        .getSessionExpiredURL());

            }
        } catch (SystemMessageException ee) {
            throw new PortletException(ee);
        }

    }

    private void handleServiceSecurityException(PortletRequest request,
            PortletResponse response) throws IOException, PortletException {
        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        try {
            Application.SystemMessages ci = getSystemMessages();
            if (getRequestType(request) != RequestType.UIDL) {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                if (response instanceof ActionResponse) {
                    ((ActionResponse) response).sendRedirect(ci
                            .getCommunicationErrorURL());
                } else {
                    // TODO What to do if we are e.g. rendering?
                }
            } else {
                // send uidl redirect
                criticalNotification(request, (ResourceResponse) response, ci
                        .getCommunicationErrorCaption(), ci
                        .getCommunicationErrorMessage(),
                        INVALID_SECURITY_KEY_MSG, ci.getCommunicationErrorURL());
                /*
                 * Invalidate session. Portal integration will fail otherwise
                 * since the session is not created by the portal.
                 */
                request.getPortletSession().invalidate();
            }
        } catch (SystemMessageException ee) {
            throw new PortletException(ee);
        }
    }

    private void handleServiceException(PortletRequest request,
            PortletResponse response, Application application, Throwable e)
            throws IOException, PortletException {
        // if this was an UIDL request, response UIDL back to client
        if (getRequestType(request) == RequestType.UIDL) {
            Application.SystemMessages ci = getSystemMessages();
            criticalNotification(request, (ResourceResponse) response, ci
                    .getInternalErrorCaption(), ci.getInternalErrorMessage(),
                    null, ci.getInternalErrorURL());
            if (application != null) {
                application.getErrorHandler()
                        .terminalError(new RequestError(e));
            } else {
                throw new PortletException(e);
            }
        } else {
            // Re-throw other exceptions
            throw new PortletException(e);
        }

    }

    @SuppressWarnings("serial")
    public class RequestError implements Terminal.ErrorEvent, Serializable {

        private final Throwable throwable;

        public RequestError(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

    }

    /**
     * Send notification to client's application. Used to notify client of
     * critical errors and session expiration due to long inactivity. Server has
     * no knowledge of what application client refers to.
     * 
     * @param request
     *            the Portlet request instance.
     * @param response
     *            the Portlet response to write to.
     * @param caption
     *            for the notification
     * @param message
     *            for the notification
     * @param details
     *            a detail message to show in addition to the passed message.
     *            Currently shown directly but could be hidden behind a details
     *            drop down.
     * @param url
     *            url to load after message, null for current page
     * @throws IOException
     *             if the writing failed due to input/output error.
     */
    void criticalNotification(PortletRequest request, MimeResponse response,
            String caption, String message, String details, String url)
            throws IOException {

        // clients JS app is still running, but server application either
        // no longer exists or it might fail to perform reasonably.
        // send a notification to client's application and link how
        // to "restart" application.

        if (caption != null) {
            caption = "\"" + caption + "\"";
        }
        if (details != null) {
            if (message == null) {
                message = details;
            } else {
                message += "<br/><br/>" + details;
            }
        }
        if (message != null) {
            message = "\"" + message + "\"";
        }
        if (url != null) {
            url = "\"" + url + "\"";
        }

        // Set the response type
        response.setContentType("application/json; charset=UTF-8");
        final OutputStream out = response.getPortletOutputStream();
        final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(out, "UTF-8")));
        outWriter.print("for(;;);[{\"changes\":[], \"meta\" : {"
                + "\"appError\": {" + "\"caption\":" + caption + ","
                + "\"message\" : " + message + "," + "\"url\" : " + url
                + "}}, \"resources\": {}, \"locales\":[]}]");
        outWriter.flush();
        outWriter.close();
        out.flush();
    }

}
