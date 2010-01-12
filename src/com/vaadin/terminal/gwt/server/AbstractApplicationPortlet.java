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
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.util.PropsUtil;
import com.vaadin.Application;
import com.vaadin.Application.SystemMessages;
import com.vaadin.external.org.apache.commons.fileupload.portlet.PortletFileUpload;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.Terminal;
import com.vaadin.ui.Window;

/**
 * TODO Document me!
 * 
 * @author peholmst
 */
public abstract class AbstractApplicationPortlet extends GenericPortlet
        implements Constants {

    /**
     * This portlet parameter is used to add styles to the main element. E.g
     * "height:500px" generates a style="height:500px" to the main element.
     */
    public static final String PORTLET_PARAMETER_STYLE = "style";

    private static final String PORTAL_PARAMETER_VAADIN_THEME = "vaadin.theme";

    // TODO some parts could be shared with AbstractApplicationServlet

    // TODO Can we close the application when the portlet is removed? Do we know
    // when the portlet is removed?

    // TODO What happens when the portlet window is resized? Do we know when the
    // window is resized?

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

    /**
     * Return the URL from where static files, e.g. the widgetset and the theme,
     * are served. In a standard configuration the VAADIN folder inside the
     * returned folder is what is used for widgetsets and themes.
     * 
     * @param request
     * @return The location of static resources (inside which there should be a
     *         VAADIN directory). Does not end with a slash (/).
     */
    protected String getStaticFilesLocation(PortletRequest request) {
        // TODO allow overriding on portlet level?
        String staticFileLocation = getPortalProperty(
                Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH, request
                        .getPortalContext());
        if (staticFileLocation != null) {
            // remove trailing slash if any
            while (staticFileLocation.endsWith(".")) {
                staticFileLocation = staticFileLocation.substring(0,
                        staticFileLocation.length() - 1);
            }
            return staticFileLocation;
        } else {
            // default for Liferay
            return "/html";
        }
    }

    enum RequestType {
        FILE_UPLOAD, UIDL, RENDER, STATIC_FILE, APPLICATION_RESOURCE, DUMMY, EVENT, ACTION, UNKNOWN;
    }

    protected RequestType getRequestType(PortletRequest request) {
        if (request instanceof RenderRequest) {
            return RequestType.RENDER;
        } else if (request instanceof ResourceRequest) {
            if (isUIDLRequest((ResourceRequest) request)) {
                return RequestType.UIDL;
            } else if (isApplicationResourceRequest((ResourceRequest) request)) {
                return RequestType.APPLICATION_RESOURCE;
            } else if (isDummyRequest((ResourceRequest) request)) {
                return RequestType.DUMMY;
            } else {
                return RequestType.STATIC_FILE;
            }
        } else if (request instanceof ActionRequest) {
            if (isFileUploadRequest((ActionRequest) request)) {
                return RequestType.FILE_UPLOAD;
            } else {
                // action other than upload
                return RequestType.ACTION;
            }
        } else if (request instanceof EventRequest) {
            return RequestType.EVENT;
        }
        return RequestType.UNKNOWN;
    }

    private boolean isApplicationResourceRequest(ResourceRequest request) {
        return request.getResourceID() != null
                && request.getResourceID().startsWith("APP");
    }

    private boolean isUIDLRequest(ResourceRequest request) {
        return request.getResourceID() != null
                && request.getResourceID().equals("UIDL");
    }

    private boolean isDummyRequest(ResourceRequest request) {
        return request.getResourceID() != null
                && request.getResourceID().equals("DUMMY");
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
        RequestType requestType = getRequestType(request);

        if (requestType == RequestType.UNKNOWN) {
            System.err.println("Unknown request type");
        } else if (requestType == RequestType.DUMMY) {
            /*
             * This dummy page is used by action responses to redirect to, in
             * order to prevent the boot strap code from being rendered into
             * strange places such as iframes.
             */
            ((ResourceResponse) response).setContentType("text/html");
            final OutputStream out = ((ResourceResponse) response)
                    .getPortletOutputStream();
            final PrintWriter outWriter = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(out, "UTF-8")));
            outWriter.print("<html><body>dummy page</body></html>");
            outWriter.flush();
            out.close();
        } else if (requestType == RequestType.STATIC_FILE) {
            serveStaticResources((ResourceRequest) request,
                    (ResourceResponse) response);
        } else {
            Application application = null;
            boolean transactionStarted = false;
            boolean requestStarted = false;

            try {
                // TODO What about PARAM_UNLOADBURST & redirectToApplication??

                /* Find out which application this request is related to */
                application = findApplicationInstance(request, requestType);
                if (application == null) {
                    return;
                }

                /*
                 * Get or create an application context and an application
                 * manager for the session
                 */
                PortletApplicationContext2 applicationContext = PortletApplicationContext2
                        .getApplicationContext(request.getPortletSession());
                applicationContext.setResponse(response);

                PortletCommunicationManager applicationManager = applicationContext
                        .getApplicationManager(application);

                if (response instanceof RenderResponse
                        && applicationManager.dummyURL == null) {
                    /*
                     * The application manager needs an URL to the dummy page.
                     * See the PortletCommunicationManager.sendUploadResponse
                     * method for more information.
                     */
                    ResourceURL dummyURL = ((RenderResponse) response)
                            .createResourceURL();
                    dummyURL.setResourceID("DUMMY");
                    applicationManager.dummyURL = dummyURL.toString();
                }

                /* Update browser information from request */
                updateBrowserProperties(applicationContext.getBrowser(),
                        request);

                /*
                 * Call application requestStart before Application.init() is
                 * called (bypasses the limitation in TransactionListener)
                 */
                if (application instanceof PortletRequestListener) {
                    ((PortletRequestListener) application).onRequestStart(
                            request, response);
                    requestStarted = true;
                }

                /* Start the newly created application */
                startApplication(request, application, applicationContext);

                /*
                 * Transaction starts. Call transaction listeners. Transaction
                 * end is called in the finally block below.
                 */
                applicationContext.startTransaction(application, request);
                transactionStarted = true;

                /* Notify listeners */

                // TODO Should this happen before or after the transaction
                // starts?
                if (request instanceof RenderRequest) {
                    applicationContext.firePortletRenderRequest(application,
                            (RenderRequest) request, (RenderResponse) response);
                } else if (request instanceof ActionRequest) {
                    applicationContext.firePortletActionRequest(application,
                            (ActionRequest) request, (ActionResponse) response);
                } else if (request instanceof EventRequest) {
                    applicationContext.firePortletEventRequest(application,
                            (EventRequest) request, (EventResponse) response);
                } else if (request instanceof ResourceRequest) {
                    applicationContext.firePortletResourceRequest(application,
                            (ResourceRequest) request,
                            (ResourceResponse) response);
                }

                /* Handle the request */
                if (requestType == RequestType.FILE_UPLOAD) {
                    applicationManager.handleFileUpload(
                            (ActionRequest) request, (ActionResponse) response);
                    return;
                } else if (requestType == RequestType.UIDL) {
                    // Handles AJAX UIDL requests
                    applicationManager.handleUidlRequest(
                            (ResourceRequest) request,
                            (ResourceResponse) response, this);
                    return;
                } else {
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
                    Window window = getPortletWindow(request, application);
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

                    if (requestType == RequestType.APPLICATION_RESOURCE) {
                        handleURI(applicationManager, window,
                                (ResourceRequest) request,
                                (ResourceResponse) response);
                    } else if (requestType == RequestType.RENDER) {
                        writeAjaxPage((RenderRequest) request,
                                (RenderResponse) response, window, application);
                    } else if (requestType == RequestType.EVENT) {
                        // nothing to do, listeners do all the work
                    } else if (requestType == RequestType.ACTION) {
                        // nothing to do, listeners do all the work
                    } else {
                        throw new IllegalStateException(
                                "handleRequest() without anything to do - should never happen!");
                    }
                }
            } catch (final SessionExpiredException e) {
                // TODO Figure out a better way to deal with
                // SessionExpiredExceptions
                System.err.println("Session has expired");
                e.printStackTrace(System.err);
            } catch (final GeneralSecurityException e) {
                // TODO Figure out a better way to deal with
                // GeneralSecurityExceptions
                System.err
                        .println("General security exception, should never happen");
                e.printStackTrace(System.err);
            } catch (final Throwable e) {
                handleServiceException(request, response, application, e);
            } finally {
                // Notifies transaction end
                try {
                    if (transactionStarted) {
                        ((PortletApplicationContext2) application.getContext())
                                .endTransaction(application, request);
                    }
                } finally {
                    if (requestStarted) {
                        ((PortletRequestListener) application).onRequestEnd(
                                request, response);

                    }
                }
            }
        }
    }

    /**
     * Returns a window for a portlet mode. To define custom content for a
     * portlet mode, add (in the application) a window whose name matches the
     * portlet mode name. By default, the main window is returned.
     * 
     * Alternatively, a PortletListener can change the main window content.
     * 
     * @param request
     * @param application
     * @return Window to show in the portlet for the given portlet mode
     */
    protected Window getPortletWindow(PortletRequest request,
            Application application) {
        PortletMode mode = request.getPortletMode();
        Window window = application.getWindow(mode.toString());
        if (window != null) {
            return window;
        }
        // no specific window found
        return application.getMainWindow();
    }

    private void updateBrowserProperties(WebBrowser browser,
            PortletRequest request) {
        browser.updateBrowserProperties(request.getLocale(), null, request
                .isSecure(), request.getProperty("user-agent"), request
                .getParameter("sw"), request.getParameter("sh"));
    }

    @Override
    public void processEvent(EventRequest request, EventResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    private boolean handleURI(PortletCommunicationManager applicationManager,
            Window window, ResourceRequest request, ResourceResponse response)
            throws IOException {
        // Handles the URI
        DownloadStream download = applicationManager.handleURI(window, request,
                response, this);

        // A download request
        if (download != null) {
            // Client downloads an resource
            handleDownload(download, request, response);
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private void handleDownload(DownloadStream stream, ResourceRequest request,
            ResourceResponse response) throws IOException {

        if (stream.getParameter("Location") != null) {
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer
                    .toString(HttpServletResponse.SC_MOVED_TEMPORARILY));
            response.setProperty("Location", stream.getParameter("Location"));
            return;
        }

        // Download from given stream
        final InputStream data = stream.getStream();
        if (data != null) {

            // Sets content type
            response.setContentType(stream.getContentType());

            // Sets cache headers
            final long cacheTime = stream.getCacheTime();
            if (cacheTime <= 0) {
                response.setProperty("Cache-Control", "no-cache");
                response.setProperty("Pragma", "no-cache");
                response.setProperty("Expires", "0");
            } else {
                response.setProperty("Cache-Control", "max-age=" + cacheTime
                        / 1000);
                response.setProperty("Expires", "" + System.currentTimeMillis()
                        + cacheTime);
                // Required to apply caching in some Tomcats
                response.setProperty("Pragma", "cache");
            }

            // Copy download stream parameters directly
            // to HTTP headers.
            final Iterator i = stream.getParameterNames();
            if (i != null) {
                while (i.hasNext()) {
                    final String param = (String) i.next();
                    response.setProperty(param, stream.getParameter(param));
                }
            }

            // suggest local filename from DownloadStream if Content-Disposition
            // not explicitly set
            String contentDispositionValue = stream
                    .getParameter("Content-Disposition");
            if (contentDispositionValue == null) {
                contentDispositionValue = "filename=\"" + stream.getFileName()
                        + "\"";
                response.setProperty("Content-Disposition",
                        contentDispositionValue);
            }

            int bufferSize = stream.getBufferSize();
            if (bufferSize <= 0 || bufferSize > MAX_BUFFER_SIZE) {
                bufferSize = DEFAULT_BUFFER_SIZE;
            }
            final byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;

            final OutputStream out = response.getPortletOutputStream();

            while ((bytesRead = data.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
            out.close();
        }
    }

    private void serveStaticResources(ResourceRequest request,
            ResourceResponse response) throws IOException, PortletException {
        final String resourceID = request.getResourceID();
        final PortletContext pc = getPortletContext();

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
        handleRequest(request, response);
    }

    /**
     * Handles a request for the "view" (default) portlet mode. In Vaadin, the
     * basic portlet modes ("view", "edit" and "help") are handled identically,
     * and their behavior can be changed by registering windows in the
     * application with window names identical to the portlet mode names.
     * Alternatively, a PortletListener can change the application main window
     * contents.
     * 
     * To implement custom portlet modes, subclass the portlet class and
     * implement a method annotated with {@link RenderMode} for the custom mode,
     * calling {@link #handleRequest(PortletRequest, PortletResponse)} directly
     * from it.
     * 
     * Note that the portlet class in the portlet configuration needs to be
     * changed when overriding methods of this class.
     * 
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    @Override
    protected void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    /**
     * Handle a request for the "edit" portlet mode.
     * 
     * @see #doView(RenderRequest, RenderResponse)
     */
    @Override
    protected void doEdit(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    /**
     * Handle a request for the "help" portlet mode.
     * 
     * @see #doView(RenderRequest, RenderResponse)
     */
    @Override
    protected void doHelp(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {
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
            RequestType requestType) throws PortletException,
            SessionExpiredException, MalformedURLException {

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
            throw new SessionExpiredException();
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
            context.removeApplication(application);
        }
    }

    private Application createApplication(PortletRequest request)
            throws PortletException, MalformedURLException {
        Application newApplication = getNewApplication(request);
        final PortletApplicationContext2 context = PortletApplicationContext2
                .getApplicationContext(request.getPortletSession());
        context.addApplication(newApplication, request.getWindowID());
        return newApplication;
    }

    private Application getExistingApplication(PortletRequest request,
            boolean allowSessionCreation) throws MalformedURLException,
            SessionExpiredException {

        final PortletSession session = request
                .getPortletSession(allowSessionCreation);

        if (session == null) {
            throw new SessionExpiredException();
        }

        PortletApplicationContext2 context = PortletApplicationContext2
                .getApplicationContext(session);
        Application application = context.getApplicationForWindowId(request
                .getWindowID());
        if (application == null) {
            return null;
        }
        if (application.isRunning()) {
            return application;
        }
        // application found but not running
        PortletApplicationContext2.getApplicationContext(session)
                .removeApplication(application);

        return null;
    }

    protected String getWidgetsetURL(String widgetset, PortletRequest request) {
        return getStaticFilesLocation(request) + "/" + WIDGETSET_DIRECTORY_PATH
                + widgetset + "/" + widgetset + ".nocache.js?"
                + new Date().getTime();
    }

    protected String getThemeURI(String themeName, PortletRequest request) {
        return getStaticFilesLocation(request) + "/" + THEME_DIRECTORY_PATH
                + themeName;
    }

    protected void writeAjaxPage(RenderRequest request,
            RenderResponse response, Window window, Application application)
            throws IOException, MalformedURLException, PortletException {

        response.setContentType("text/html");
        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getPortletOutputStream(), "UTF-8"));

        // TODO check
        String requestWidgetset = getApplicationOrSystemProperty(
                PARAMETER_WIDGETSET, null);
        String sharedWidgetset = getPortalProperty(
                PORTAL_PARAMETER_VAADIN_WIDGETSET, request.getPortalContext());

        String widgetset;
        if (requestWidgetset != null) {
            widgetset = requestWidgetset;
        } else if (sharedWidgetset != null) {
            widgetset = sharedWidgetset;
        } else {
            widgetset = DEFAULT_WIDGETSET;
        }

        // TODO Currently, we can only load widgetsets and themes from the
        // portal

        String themeName = getThemeForWindow(request, window);

        String widgetsetURL = getWidgetsetURL(widgetset, request);
        String themeURI = getThemeURI(themeName, request);

        // fixed base theme to use - all portal pages with Vaadin
        // applications will load this exactly once
        String portalTheme = getPortalProperty(PORTAL_PARAMETER_VAADIN_THEME,
                request.getPortalContext());

        // Get system messages
        Application.SystemMessages systemMessages = null;
        try {
            systemMessages = getSystemMessages();
        } catch (SystemMessageException e) {
            // failing to get the system messages is always a problem
            throw new PortletException("Failed to obtain system messages!", e);
        }

        page.write("<script type=\"text/javascript\">\n");
        page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n "
                + "if(!vaadin) { var vaadin = {}} \n"
                + "vaadin.vaadinConfigurations = {};\n"
                + "if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }\n");
        if (!isProductionMode()) {
            page.write("vaadin.debug = true;\n");
        }
        page
                .write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                        + "style=\"width:0;height:0;border:0;overflow:"
                        + "hidden\" src=\"javascript:false\"></iframe>');\n");
        page.write("document.write(\"<script language='javascript' src='"
                + widgetsetURL + "'><\\/script>\");\n}\n");

        page.write("vaadin.vaadinConfigurations[\"" + request.getWindowID()
                + "\"] = {");

        /*
         * We need this in order to get uploads to work.
         */
        PortletURL appUri = response.createActionURL();

        page.write("appUri: '" + appUri.toString() + "', ");
        page.write("usePortletURLs: true, ");

        ResourceURL uidlUrlBase = response.createResourceURL();
        uidlUrlBase.setResourceID("UIDL");

        page.write("portletUidlURLBase: '" + uidlUrlBase.toString() + "', ");
        page.write("pathInfo: '', ");
        page.write("themeUri: '" + themeURI + "', ");
        page.write("versionInfo : {vaadinVersion:\"");
        page.write(AbstractApplicationServlet.VERSION);
        page.write("\",applicationVersion:\"");
        page.write(application.getVersion());
        page.write("\"},");
        if (systemMessages != null) {
            // Write the CommunicationError -message to client
            String caption = systemMessages.getCommunicationErrorCaption();
            if (caption != null) {
                caption = "\"" + caption + "\"";
            }
            String message = systemMessages.getCommunicationErrorMessage();
            if (message != null) {
                message = "\"" + message + "\"";
            }
            String url = systemMessages.getCommunicationErrorURL();
            if (url != null) {
                url = "\"" + url + "\"";
            }

            page.write("\"comErrMsg\": {" + "\"caption\":" + caption + ","
                    + "\"message\" : " + message + "," + "\"url\" : " + url
                    + "}");
        }
        page.write("};\n</script>\n");

        page.write("<script type=\"text/javascript\">\n");

        if (portalTheme == null) {
            portalTheme = DEFAULT_THEME_NAME;
        }

        page.write("if(!vaadin.themesLoaded['" + portalTheme + "']) {\n");
        page.write("var defaultStylesheet = document.createElement('link');\n");
        page.write("defaultStylesheet.setAttribute('rel', 'stylesheet');\n");
        page.write("defaultStylesheet.setAttribute('type', 'text/css');\n");
        page.write("defaultStylesheet.setAttribute('href', '"
                + getThemeURI(portalTheme, request) + "/styles.css');\n");
        page
                .write("document.getElementsByTagName('head')[0].appendChild(defaultStylesheet);\n");
        page.write("vaadin.themesLoaded['" + portalTheme + "'] = true;\n}\n");

        if (!portalTheme.equals(themeName)) {
            page.write("if(!vaadin.themesLoaded['" + themeName + "']) {\n");
            page.write("var stylesheet = document.createElement('link');\n");
            page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
            page.write("stylesheet.setAttribute('type', 'text/css');\n");
            page.write("stylesheet.setAttribute('href', '" + themeURI
                    + "/styles.css');\n");
            page
                    .write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
            page.write("vaadin.themesLoaded['" + themeName + "'] = true;\n}\n");
        }

        page.write("</script>\n");

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
        String themeClass = "v-theme-"
                + themeName.replaceAll("[^a-zA-Z0-9]", "");

        String classNames = "v-app v-app-loading " + themeClass + " "
                + appClass;

        String style = getApplicationProperty(PORTLET_PARAMETER_STYLE);
        String divStyle = "";
        if (style != null) {
            divStyle = "style=\"" + style + "\"";
        }
        page.write("<div id=\"" + request.getWindowID() + "\" class=\""
                + classNames + "\" " + divStyle + "></div>\n");

        page.close();
    }

    /**
     * Returns the theme for given request/window
     * 
     * @param request
     * @param window
     * @return
     */
    private String getThemeForWindow(PortletRequest request, Window window) {
        // Finds theme name
        String themeName;

        // theme defined for the window?
        themeName = window.getTheme();

        if (themeName == null) {
            // no, is the default theme defined by the portal?
            themeName = getPortalProperty(
                    Constants.PORTAL_PARAMETER_VAADIN_THEME, request
                            .getPortalContext());
        }

        if (themeName == null) {
            // no, using the default theme defined by Vaadin
            themeName = DEFAULT_THEME_NAME;
        }

        return themeName;
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

    private void handleServiceException(PortletRequest request,
            PortletResponse response, Application application, Throwable e)
            throws IOException, PortletException {
        // TODO Check that this error handler is working when running inside a
        // portlet

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

    private static String getPortalProperty(String name, PortalContext context) {
        boolean isLifeRay = context.getPortalInfo().toLowerCase().contains(
                "liferay");

        // TODO test on non-LifeRay platforms

        String value;
        if (isLifeRay) {
            value = getLifeRayPortalProperty(name);
        } else {
            value = context.getProperty(name);
        }

        return value;
    }

    private static String getLifeRayPortalProperty(String name) {
        String value;
        try {
            value = PropsUtil.get(name);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }

}
