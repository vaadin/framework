package com.vaadin.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
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
import com.vaadin.external.org.apache.commons.fileupload.portlet.PortletFileUpload;
import com.vaadin.ui.Window;

public abstract class AbstractApplicationPortlet extends GenericPortlet {

    // TODO Move some (all?) of the constants to a separate interface (shared with servlet)
    
    private static final String ERROR_NO_WINDOW_FOUND = "No window found. Did you remember to setMainWindow()?";

    static final String THEME_DIRECTORY_PATH = "VAADIN/themes/";

    private static final String WIDGETSET_DIRECTORY_PATH = "VAADIN/widgetsets/";

    private static final String DEFAULT_WIDGETSET = "com.vaadin.terminal.gwt.DefaultWidgetSet";

    private static final String DEFAULT_THEME_NAME = "reindeer";    
    
    private static final String URL_PARAMETER_REPAINT_ALL = "repaintAll";

    private static final String URL_PARAMETER_RESTART_APPLICATION = "restartApplication";

    private static final String URL_PARAMETER_CLOSE_APPLICATION = "closeApplication";

    private static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    // TODO Close application when portlet window is closed

    // TODO What happens when the portlet window is resized?

    private Properties applicationProperties;

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

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
        // TODO Check production mode
        // TODO Check cross site protection
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

    protected void handleRequest(PortletRequest request,
            PortletResponse response) throws PortletException, IOException {
        System.out.println("AbstractApplicationPortlet.handleRequest() " + System.currentTimeMillis());

        RequestType requestType = getRequestType(request);

        System.out.println("  RequestType: " + requestType);
        System.out.println("  WindowID: " + request.getWindowID());

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
        } catch (final Throwable e) {
            // TODO Handle exceptions
            e.printStackTrace();
        } finally {
            // Notifies transaction end
            if (application != null) {
                ((PortletApplicationContext2) application.getContext())
                        .endTransaction(application, request);
            }
        }
    }

    // TODO Vaadin resources cannot be loaded, try to load other resources using
    // the portlet context

    private void serveStaticResources(ResourceRequest request,
            ResourceResponse response) throws IOException, PortletException {
        final String resourceID = request.getResourceID();
        final PortletContext pc = getPortletContext();

        System.out.println("Trying to load resource [" + resourceID + "]");

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
        System.out.println("AbstractApplicationPortlet.processAction()");
        handleRequest(request, response);
    }

    @RenderMode(name = "VIEW")
    public void doRender(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        System.out.println("AbstractApplicationPortlet.render()");
        handleRequest(request, response);
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {
        System.out.println("AbstractApplicationPortlet.serveResource()");
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

        System.out.println("AbstractApplicationPortlet.writeAjaxPage()");

        response.setContentType("text/html");
        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getPortletOutputStream(), "UTF-8"));
        ;

        // TODO The widgetset URL is currently hard-corded for LifeRay

        String widgetsetURL = "/html/" + WIDGETSET_DIRECTORY_PATH + DEFAULT_WIDGETSET
                + "/" + DEFAULT_WIDGETSET + ".nocache.js?"
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
        
        //if (themeName != null) {
            // Custom theme's stylesheet, load only once, in different
            // script
            // tag to be dominate styles injected by widget
            // set
            page.write("<script type=\"text/javascript\">\n");
            page.write("//<![CDATA[\n");
            page.write("if(!vaadin.themesLoaded['" + DEFAULT_THEME_NAME + "']) {\n");
            page.write("var stylesheet = document.createElement('link');\n");
            page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
            page.write("stylesheet.setAttribute('type', 'text/css');\n");
            page.write("stylesheet.setAttribute('href', '" + themeURI
                    + "/styles.css');\n");
            page
                    .write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
            page.write("vaadin.themesLoaded['" + DEFAULT_THEME_NAME + "'] = true;\n}\n");
            page.write("//]]>\n</script>\n");
        //} 
        
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
}
