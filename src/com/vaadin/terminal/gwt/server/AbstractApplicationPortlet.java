/*
@VaadinApache2LicenseForJavaFiles@
 */
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
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.util.PortalClassInvoker;
import com.liferay.portal.kernel.util.PropsUtil;
import com.vaadin.Application;
import com.vaadin.Application.ApplicationStartEvent;
import com.vaadin.Application.SystemMessages;
import com.vaadin.RootRequiresMoreInformationException;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.terminal.gwt.server.AbstractCommunicationManager.Callback;
import com.vaadin.ui.Root;

/**
 * Portlet 2.0 base class. This replaces the servlet in servlet/portlet 1.0
 * deployments and handles various portlet requests from the browser.
 * 
 * TODO Document me!
 * 
 * @author peholmst
 */
public abstract class AbstractApplicationPortlet extends GenericPortlet
        implements Constants {

    private static final Logger logger = Logger
            .getLogger(AbstractApplicationPortlet.class.getName());

    private static class WrappedHttpAndPortletRequest extends
            WrappedPortletRequest {

        public WrappedHttpAndPortletRequest(PortletRequest request,
                HttpServletRequest originalRequest,
                DeploymentConfiguration deploymentConfiguration) {
            super(request, deploymentConfiguration);
            this.originalRequest = originalRequest;
        }

        private final HttpServletRequest originalRequest;

        @Override
        public String getParameter(String name) {
            String parameter = super.getParameter(name);
            if (parameter == null) {
                parameter = originalRequest.getParameter(name);
            }
            return parameter;
        }

        @Override
        public String getRemoteAddr() {
            return originalRequest.getRemoteAddr();
        }

        @Override
        public String getHeader(String name) {
            String header = super.getHeader(name);
            if (header == null) {
                header = originalRequest.getHeader(name);
            }
            return header;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> parameterMap = super.getParameterMap();
            if (parameterMap == null) {
                parameterMap = originalRequest.getParameterMap();
            }
            return parameterMap;
        }
    }

    private static class WrappedGateinRequest extends
            WrappedHttpAndPortletRequest {
        public WrappedGateinRequest(PortletRequest request,
                DeploymentConfiguration deploymentConfiguration) {
            super(request, getOriginalRequest(request), deploymentConfiguration);
        }

        private static final HttpServletRequest getOriginalRequest(
                PortletRequest request) {
            try {
                Method getRealReq = request.getClass().getMethod(
                        "getRealRequest");
                HttpServletRequestWrapper origRequest = (HttpServletRequestWrapper) getRealReq
                        .invoke(request);
                return origRequest;
            } catch (Exception e) {
                throw new IllegalStateException("GateIn request not detected",
                        e);
            }
        }
    }

    private static class WrappedLiferayRequest extends
            WrappedHttpAndPortletRequest {

        public WrappedLiferayRequest(PortletRequest request,
                DeploymentConfiguration deploymentConfiguration) {
            super(request, getOriginalRequest(request), deploymentConfiguration);
        }

        @Override
        public String getPortalProperty(String name) {
            return PropsUtil.get(name);
        }

        private static HttpServletRequest getOriginalRequest(
                PortletRequest request) {
            try {
                // httpRequest = PortalUtil.getHttpServletRequest(request);
                HttpServletRequest httpRequest = (HttpServletRequest) PortalClassInvoker
                        .invoke("com.liferay.portal.util.PortalUtil",
                                "getHttpServletRequest", request);

                // httpRequest =
                // PortalUtil.getOriginalServletRequest(httpRequest);
                httpRequest = (HttpServletRequest) PortalClassInvoker.invoke(
                        "com.liferay.portal.util.PortalUtil",
                        "getOriginalServletRequest", httpRequest);
                return httpRequest;
            } catch (Exception e) {
                throw new IllegalStateException("Liferay request not detected",
                        e);
            }
        }

    }

    private static class AbstractApplicationPortletWrapper implements Callback {

        private final AbstractApplicationPortlet portlet;

        public AbstractApplicationPortletWrapper(
                AbstractApplicationPortlet portlet) {
            this.portlet = portlet;
        }

        public void criticalNotification(WrappedRequest request,
                WrappedResponse response, String cap, String msg,
                String details, String outOfSyncURL) throws IOException {
            PortletRequest portletRequest = WrappedPortletRequest.cast(request)
                    .getPortletRequest();
            PortletResponse portletResponse = ((WrappedPortletResponse) response)
                    .getPortletResponse();
            portlet.criticalNotification(portletRequest,
                    (MimeResponse) portletResponse, cap, msg, details,
                    outOfSyncURL);
        }
    }

    /**
     * This portlet parameter is used to add styles to the main element. E.g
     * "height:500px" generates a style="height:500px" to the main element.
     */
    public static final String PORTLET_PARAMETER_STYLE = "style";

    /**
     * This portal parameter is used to define the name of the Vaadin theme that
     * is used for all Vaadin applications in the portal.
     */
    public static final String PORTAL_PARAMETER_VAADIN_THEME = "vaadin.theme";

    // TODO some parts could be shared with AbstractApplicationServlet

    // TODO Can we close the application when the portlet is removed? Do we know
    // when the portlet is removed?

    private Properties applicationProperties;

    private boolean productionMode = false;

    private DeploymentConfiguration deploymentConfiguration = new DeploymentConfiguration() {
        public String getConfiguredWidgetset(WrappedRequest request) {

            String widgetset = getApplicationOrSystemProperty(
                    PARAMETER_WIDGETSET, null);

            if (widgetset == null) {
                // If no widgetset defined for the application, check the portal
                // property
                widgetset = WrappedPortletRequest.cast(request)
                        .getPortalProperty(PORTAL_PARAMETER_VAADIN_WIDGETSET);
            }

            if (widgetset == null) {
                // If no widgetset defined for the portal, use the default
                widgetset = DEFAULT_WIDGETSET;
            }

            return widgetset;
        }

        public String getConfiguredTheme(WrappedRequest request) {

            // is the default theme defined by the portal?
            String themeName = WrappedPortletRequest.cast(request)
                    .getPortalProperty(Constants.PORTAL_PARAMETER_VAADIN_THEME);

            if (themeName == null) {
                // no, using the default theme defined by Vaadin
                themeName = DEFAULT_THEME_NAME;
            }

            return themeName;
        }

        public String getApplicationOrSystemProperty(String propertyName,
                String defaultValue) {
            return AbstractApplicationPortlet.this
                    .getApplicationOrSystemProperty(propertyName, defaultValue);
        }

        public boolean isStandalone(WrappedRequest request) {
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.vaadin.terminal.DeploymentConfiguration#getStaticFileLocation
         * (com.vaadin.terminal.WrappedRequest)
         * 
         * Return the URL from where static files, e.g. the widgetset and the
         * theme, are served. In a standard configuration the VAADIN folder
         * inside the returned folder is what is used for widgetsets and themes.
         * 
         * @return The location of static resources (inside which there should
         * be a VAADIN directory). Does not end with a slash (/).
         */
        public String getStaticFileLocation(WrappedRequest request) {
            String staticFileLocation = WrappedPortletRequest.cast(request)
                    .getPortalProperty(
                            Constants.PORTAL_PARAMETER_VAADIN_RESOURCE_PATH);
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

        public ClassLoader getClassLoader() {
            // Custom class loaders not currently supported in portlets (see
            // #8574)
            return null;
        }
    };

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        applicationProperties = new Properties();

        // Read default parameters from the context
        final PortletContext context = config.getPortletContext();
        for (final Enumeration<String> e = context.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = e.nextElement();
            applicationProperties.setProperty(name,
                    context.getInitParameter(name));
        }

        // Override with application settings from portlet.xml
        for (final Enumeration<String> e = config.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = e.nextElement();
            applicationProperties.setProperty(name,
                    config.getInitParameter(name));
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
            logger.warning(WARNING_XSRF_PROTECTION_DISABLED);
        }
    }

    /**
     * Checks that the version reported by the client (widgetset) matches that
     * of the server.
     * 
     * @param request
     */
    private void checkWidgetsetVersion(WrappedRequest request) {
        if (!AbstractApplicationServlet.VERSION.equals(request
                .getParameter("wsver"))) {
            logger.warning(String.format(WIDGETSET_MISMATCH_INFO,
                    AbstractApplicationServlet.VERSION,
                    request.getParameter("wsver")));
        }
    }

    private void checkProductionMode() {
        // TODO Identical code in AbstractApplicationServlet -> refactor
        // Check if the application is in production mode.
        // We are in production mode if productionMode=true
        if (getApplicationOrSystemProperty(SERVLET_PARAMETER_PRODUCTION_MODE,
                "false").equals("true")) {
            productionMode = true;
        }

        if (!productionMode) {
            /* Print an information/warning message about running in debug mode */
            // TODO Maybe we need a different message for portlets?
            logger.warning(NOT_PRODUCTION_MODE_INFO);
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
            pkgName = new String(className.toCharArray(), 0,
                    className.lastIndexOf('.'));
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
    protected String getApplicationOrSystemProperty(String parameterName,
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

    protected enum RequestType {
        FILE_UPLOAD, UIDL, RENDER, STATIC_FILE, APPLICATION_RESOURCE, DUMMY, EVENT, ACTION, UNKNOWN, BROWSER_DETAILS;
    }

    protected RequestType getRequestType(PortletRequest request) {
        if (request instanceof RenderRequest) {
            return RequestType.RENDER;
        } else if (request instanceof ResourceRequest) {
            ResourceRequest resourceRequest = (ResourceRequest) request;
            if (isUIDLRequest(resourceRequest)) {
                return RequestType.UIDL;
            } else if (isBrowserDetailsRequeset(resourceRequest)) {
                return RequestType.BROWSER_DETAILS;
            } else if (isFileUploadRequest(resourceRequest)) {
                return RequestType.FILE_UPLOAD;
            } else if (isApplicationResourceRequest(resourceRequest)) {
                return RequestType.APPLICATION_RESOURCE;
            } else if (isDummyRequest(resourceRequest)) {
                return RequestType.DUMMY;
            } else {
                return RequestType.STATIC_FILE;
            }
        } else if (request instanceof ActionRequest) {
            return RequestType.ACTION;
        } else if (request instanceof EventRequest) {
            return RequestType.EVENT;
        }
        return RequestType.UNKNOWN;
    }

    private boolean isBrowserDetailsRequeset(ResourceRequest request) {
        return request.getResourceID() != null
                && request.getResourceID().equals("browserDetails");
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

    private boolean isFileUploadRequest(ResourceRequest request) {
        return "UPLOAD".equals(request.getResourceID());
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
        AbstractApplicationPortletWrapper portletWrapper = new AbstractApplicationPortletWrapper(
                this);

        WrappedPortletRequest wrappedRequest;

        String portalInfo = request.getPortalContext().getPortalInfo()
                .toLowerCase();
        if (portalInfo.contains("liferay")) {
            wrappedRequest = new WrappedLiferayRequest(request,
                    getDeploymentConfiguration());
        } else if (portalInfo.contains("gatein")) {
            wrappedRequest = new WrappedGateinRequest(request,
                    getDeploymentConfiguration());
        } else {
            wrappedRequest = new WrappedPortletRequest(request,
                    getDeploymentConfiguration());
        }

        WrappedPortletResponse wrappedResponse = new WrappedPortletResponse(
                response, getDeploymentConfiguration());

        RequestType requestType = getRequestType(request);

        if (requestType == RequestType.UNKNOWN) {
            handleUnknownRequest(request, response);
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
            outWriter.close();
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
                application = findApplicationInstance(wrappedRequest,
                        requestType);
                if (application == null) {
                    return;
                }
                Application.setCurrentApplication(application);

                /*
                 * Get or create an application context and an application
                 * manager for the session
                 */
                PortletApplicationContext2 applicationContext = getApplicationContext(request
                        .getPortletSession());
                applicationContext.setResponse(response);
                applicationContext.setPortletConfig(getPortletConfig());

                PortletCommunicationManager applicationManager = applicationContext
                        .getApplicationManager(application);

                /* Update browser information from request */
                applicationContext.getBrowser().updateRequestDetails(
                        wrappedRequest);

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

                // Finds the window within the application
                Root root = null;
                synchronized (application) {
                    if (application.isRunning()) {
                        switch (requestType) {
                        case RENDER:
                            try {
                                root = application
                                        .getRootForRequest(wrappedRequest);
                            } catch (RootRequiresMoreInformationException e) {
                                // Ignore problem and continue without root
                            }
                            break;
                        case BROWSER_DETAILS:
                            // Should not try to find a root here as the
                            // combined request details might change the root
                            break;
                        case FILE_UPLOAD:
                            // no window
                            break;
                        case APPLICATION_RESOURCE:
                            // use main window - should not need any window
                            // root = application.getRoot();
                            break;
                        default:
                            root = application
                                    .getRootForRequest(wrappedRequest);
                        }
                        // if window not found, not a problem - use null
                    }
                }

                // TODO Should this happen before or after the transaction
                // starts?
                if (request instanceof RenderRequest) {
                    applicationContext.firePortletRenderRequest(application,
                            root, (RenderRequest) request,
                            (RenderResponse) response);
                } else if (request instanceof ActionRequest) {
                    applicationContext.firePortletActionRequest(application,
                            root, (ActionRequest) request,
                            (ActionResponse) response);
                } else if (request instanceof EventRequest) {
                    applicationContext.firePortletEventRequest(application,
                            root, (EventRequest) request,
                            (EventResponse) response);
                } else if (request instanceof ResourceRequest) {
                    applicationContext.firePortletResourceRequest(application,
                            root, (ResourceRequest) request,
                            (ResourceResponse) response);
                }

                /* Handle the request */
                if (requestType == RequestType.FILE_UPLOAD) {
                    applicationManager.handleFileUpload(wrappedRequest,
                            wrappedResponse);
                    return;
                } else if (requestType == RequestType.BROWSER_DETAILS) {
                    applicationManager.handleBrowserDetailsRequest(
                            wrappedRequest, wrappedResponse, application);
                    return;
                } else if (requestType == RequestType.UIDL) {
                    // Handles AJAX UIDL requests
                    if (isRepaintAll(request)) {
                        // warn if versions do not match
                        checkWidgetsetVersion(wrappedRequest);
                    }
                    applicationManager.handleUidlRequest(wrappedRequest,
                            wrappedResponse, portletWrapper, root);
                    return;
                } else {
                    /*
                     * Removes the application if it has stopped
                     */
                    if (!application.isRunning()) {
                        endApplication(request, response, application);
                        return;
                    }

                    handleOtherRequest(wrappedRequest, wrappedResponse,
                            requestType, application, applicationContext,
                            applicationManager);
                }
            } catch (final SessionExpiredException e) {
                // TODO Figure out a better way to deal with
                // SessionExpiredExceptions
                logger.finest("A user session has expired");
            } catch (final GeneralSecurityException e) {
                // TODO Figure out a better way to deal with
                // GeneralSecurityExceptions
                logger.fine("General security exception, the security key was probably incorrect.");
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
                    try {
                        if (requestStarted) {
                            ((PortletRequestListener) application)
                                    .onRequestEnd(request, response);

                        }
                    } finally {
                        Root.setCurrentRoot(null);
                        Application.setCurrentApplication(null);
                    }
                }
            }
        }
    }

    private DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    private void handleUnknownRequest(PortletRequest request,
            PortletResponse response) {
        logger.warning("Unknown request type");
    }

    /**
     * Handle a portlet request that is not for static files, UIDL or upload.
     * Also render requests are handled here.
     * 
     * This method is called after starting the application and calling portlet
     * and transaction listeners.
     * 
     * @param request
     * @param response
     * @param requestType
     * @param application
     * @param applicationContext
     * @param applicationManager
     * @throws PortletException
     * @throws IOException
     * @throws MalformedURLException
     */
    private void handleOtherRequest(WrappedPortletRequest request,
            WrappedResponse response, RequestType requestType,
            Application application,
            PortletApplicationContext2 applicationContext,
            PortletCommunicationManager applicationManager)
            throws PortletException, IOException, MalformedURLException {
        if (requestType == RequestType.APPLICATION_RESOURCE
                || requestType == RequestType.RENDER) {
            if (!applicationManager.handleApplicationRequest(request, response)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Not found");
            }
        } else if (requestType == RequestType.EVENT) {
            // nothing to do, listeners do all the work
        } else if (requestType == RequestType.ACTION) {
            // nothing to do, listeners do all the work
        } else {
            throw new IllegalStateException(
                    "handleRequest() without anything to do - should never happen!");
        }
    }

    @Override
    public void processEvent(EventRequest request, EventResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
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
            logger.info("Requested resource [" + resourceID
                    + "] could not be found");
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE,
                    Integer.toString(HttpServletResponse.SC_NOT_FOUND));
        }
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doDispatch(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        try {
            // try to let super handle - it'll call methods annotated for
            // handling, the default doXYZ(), or throw if a handler for the mode
            // is not found
            super.doDispatch(request, response);

        } catch (PortletException e) {
            if (e.getCause() == null) {
                // No cause interpreted as 'unknown mode' - pass that trough
                // so that the application can handle
                handleRequest(request, response);

            } else {
                // Something else failed, pass on
                throw e;
            }
        }
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
            // In most cases the first request is a render request that renders
            // the HTML fragment. This should create an application instance.
            return true;
        } else if (requestType == RequestType.EVENT) {
            // A portlet can also be sent an event even though it has not been
            // rendered, e.g. portlet on one page sends an event to a portlet on
            // another page and then moves the user to that page.
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
            application.start(new ApplicationStartEvent(null,
                    applicationProperties, context, isProductionMode()));
        }
    }

    private void endApplication(PortletRequest request,
            PortletResponse response, Application application)
            throws IOException {
        final PortletSession session = request.getPortletSession();
        if (session != null) {
            getApplicationContext(session).removeApplication(application);
        }
        // Do not send any redirects when running inside a portlet.
    }

    private Application findApplicationInstance(
            WrappedPortletRequest wrappedRequest, RequestType requestType)
            throws PortletException, SessionExpiredException,
            MalformedURLException {
        PortletRequest request = wrappedRequest.getPortletRequest();

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

            final boolean restartApplication = (wrappedRequest
                    .getParameter(URL_PARAMETER_RESTART_APPLICATION) != null);
            final boolean closeApplication = (wrappedRequest
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
            PortletApplicationContext2 context = getApplicationContext(session);
            context.removeApplication(application);
        }
    }

    private Application createApplication(PortletRequest request)
            throws PortletException, MalformedURLException {
        Application newApplication = getNewApplication(request);
        final PortletApplicationContext2 context = getApplicationContext(request
                .getPortletSession());
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

        PortletApplicationContext2 context = getApplicationContext(session);
        Application application = context.getApplicationForWindowId(request
                .getWindowID());
        if (application == null) {
            return null;
        }
        if (application.isRunning()) {
            return application;
        }
        // application found but not running
        context.removeApplication(application);

        return null;
    }

    protected abstract Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException;

    protected Application getNewApplication(PortletRequest request)
            throws PortletException {
        try {
            final Application application = getApplicationClass().newInstance();
            application.setRootPreserved(true);
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
            criticalNotification(request, (ResourceResponse) response,
                    ci.getInternalErrorCaption(), ci.getInternalErrorMessage(),
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
        outWriter.close();
    }

    /**
     * 
     * Gets the application context for a PortletSession. If no context is
     * currently stored in a session a new context is created and stored in the
     * session.
     * 
     * @param portletSession
     *            the portlet session.
     * @return the application context for the session.
     */
    protected PortletApplicationContext2 getApplicationContext(
            PortletSession portletSession) {
        return PortletApplicationContext2.getApplicationContext(portletSession);
    }

}
