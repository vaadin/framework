package com.vaadin.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.xml.sax.SAXException;

import com.vaadin.Application;
import com.vaadin.Application.SystemMessages;
import com.vaadin.external.org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.vaadin.service.FileTypeResolver;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.ui.Window;

/**
 * Abstract implementation of the ApplicationServlet which handles all
 * communication between the client and the server.
 * 
 * It is possible to extend this class to provide own functionality but in most
 * cases this is unnecessary.
 * 
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 6.0
 */

@SuppressWarnings("serial")
public abstract class AbstractApplicationServlet extends HttpServlet {
    /**
     * Version number of this release. For example "5.0.0".
     */
    public static final String VERSION;
    /**
     * Major version number. For example 5 in 5.1.0.
     */
    public static final int VERSION_MAJOR;

    /**
     * Minor version number. For example 1 in 5.1.0.
     */
    public static final int VERSION_MINOR;

    /**
     * Builds number. For example 0-custom_tag in 5.0.0-custom_tag.
     */
    public static final String VERSION_BUILD;

    /* Initialize version numbers from string replaced by build-script. */
    static {
        if ("@VERSION@".equals("@" + "VERSION" + "@")) {
            VERSION = "5.9.9-INTERNAL-NONVERSIONED-DEBUG-BUILD";
        } else {
            VERSION = "@VERSION@";
        }
        final String[] digits = VERSION.split("\\.");
        VERSION_MAJOR = Integer.parseInt(digits[0]);
        VERSION_MINOR = Integer.parseInt(digits[1]);
        VERSION_BUILD = digits[2];
    }

    /**
     * If the attribute is present in the request, a html fragment will be
     * written instead of a whole page.
     */
    public static final String REQUEST_FRAGMENT = ApplicationServlet.class
            .getName()
            + ".fragment";
    /**
     * This request attribute forces widgetsets to be loaded from under the
     * specified base path; e.g shared widgetset for all portlets in a portal.
     */
    public static final String REQUEST_VAADIN_WIDGETSET_PATH = ApplicationServlet.class
            .getName()
            + ".widgetsetPath";
    /**
     * This request attribute forces widgetset used; e.g for portlets that can
     * not have different widgetsets.
     */
    public static final String REQUEST_WIDGETSET = ApplicationServlet.class
            .getName()
            + ".widgetset";
    /**
     * This request attribute is used to add styles to the main element. E.g
     * "height:500px" generates a style="height:500px" to the main element,
     * useful from some embedding situations (e.g portlet include.)
     */
    public static final String REQUEST_APPSTYLE = ApplicationServlet.class
            .getName()
            + ".style";

    private Properties applicationProperties;

    private static final String NOT_PRODUCTION_MODE_INFO = "=================================================================\nVaadin is running in DEBUG MODE.\nAdd productionMode=true to web.xml to disable debug features.\nTo show debug window, add ?debug to your application URL.\n=================================================================";

    private static final String WARNING_XSRF_PROTECTION_DISABLED = "===========================================================\nWARNING: Cross-site request forgery protection is disabled!\n===========================================================";

    private boolean productionMode = false;

    private static final String URL_PARAMETER_RESTART_APPLICATION = "restartApplication";
    private static final String URL_PARAMETER_CLOSE_APPLICATION = "closeApplication";
    private static final String URL_PARAMETER_REPAINT_ALL = "repaintAll";
    protected static final String URL_PARAMETER_THEME = "theme";

    private static final String SERVLET_PARAMETER_DEBUG = "Debug";
    private static final String SERVLET_PARAMETER_PRODUCTION_MODE = "productionMode";
    static final String SERVLET_PARAMETER_DISABLE_XSRF_PROTECTION = "disable-xsrf-protection";

    // Configurable parameter names
    private static final String PARAMETER_VAADIN_RESOURCES = "Resources";

    private static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    private static final int MAX_BUFFER_SIZE = 64 * 1024;

    private static final String RESOURCE_URI = "/RES/";

    private static final String AJAX_UIDL_URI = "/UIDL";

    static final String THEME_DIRECTORY_PATH = "VAADIN/themes/";

    private static final int DEFAULT_THEME_CACHETIME = 1000 * 60 * 60 * 24;

    static final String WIDGETSET_DIRECTORY_PATH = "VAADIN/widgetsets/";

    // Name of the default widget set, used if not specified in web.xml
    private static final String DEFAULT_WIDGETSET = "com.vaadin.terminal.gwt.DefaultWidgetSet";

    // Widget set parameter name
    private static final String PARAMETER_WIDGETSET = "widgetset";

    private static final String ERROR_NO_WINDOW_FOUND = "Application did not give any window, did you remember to setMainWindow()?";

    private static final String DEFAULT_THEME_NAME = "reindeer";

    private String resourcePath = null;

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

        // Stores the application parameters into Properties object
        applicationProperties = new Properties();
        for (final Enumeration e = servletConfig.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = (String) e.nextElement();
            applicationProperties.setProperty(name, servletConfig
                    .getInitParameter(name));
        }

        // Overrides with server.xml parameters
        final ServletContext context = servletConfig.getServletContext();
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
     * Returns true if the servlet is running in production mode. Production
     * mode disables all debug facilities.
     * 
     * @return true if in production mode, false if in debug mode
     */
    public boolean isProductionMode() {
        return productionMode;
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

        // check if we should serve static files (widgetsets, themes)
        if (serveStaticResources(request, response)) {
            return;
        }

        Application application = null;
        RequestType requestType = getRequestType(request);

        try {
            // Find out which application this request is related to
            application = findApplicationInstance(request, requestType);
            if (application == null) {
                return;
            }

            /*
             * Get or create a WebApplicationContext and an ApplicationManager
             * for the session
             */
            WebApplicationContext webApplicationContext = WebApplicationContext
                    .getApplicationContext(request.getSession());
            CommunicationManager applicationManager = webApplicationContext
                    .getApplicationManager(application, this);

            /* Update browser information from the request */
            webApplicationContext.getBrowser().updateBrowserProperties(request);

            /*
             * Transaction starts. Call transaction listeners. Transaction end
             * is called in the finally block below.
             */
            webApplicationContext.startTransaction(application, request);

            // TODO Add screen height and width to the GWT client

            /* Handle the request */
            if (requestType == RequestType.FILE_UPLOAD) {
                applicationManager.handleFileUpload(request, response);
                return;
            } else if (requestType == RequestType.UIDL) {
                // Handles AJAX UIDL requests
                applicationManager.handleUidlRequest(request, response, this);
                return;
            }

            // Removes application if it has stopped
            if (!application.isRunning()) {
                // FIXME How can this be reached?
                endApplication(request, response, application);
                return;
            }

            // Finds the window within the application
            Window window = getApplicationWindow(request, application);
            if (window == null) {
                throw new ServletException(ERROR_NO_WINDOW_FOUND);
            }

            // Sets terminal type for the window, if not already set
            if (window.getTerminal() == null) {
                window.setTerminal(webApplicationContext.getBrowser());
            }

            // Handle parameters
            final Map parameters = request.getParameterMap();
            if (window != null && parameters != null) {
                window.handleParameters(parameters);
            }

            /*
             * Call the URI handlers and if this turns out to be a download
             * request, send the file to the client
             */
            if (handleURI(applicationManager, window, request, response)) {
                return;
            }

            String themeName = getThemeForWindow(request, window);

            // Handles theme resource requests
            if (handleResourceRequest(request, response, themeName)) {
                return;
            }

            // Send initial AJAX page that kickstarts a Vaadin application
            writeAjaxPage(request, response, window, themeName, application);

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
                ((WebApplicationContext) application.getContext())
                        .endTransaction(application, request);
            }

            // Work-around for GAE session problem. Explicitly touch session so
            // it is re-serialized.
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("sessionUpdated", new Date().getTime());
            }
        }
    }

    protected ClassLoader getClassLoader() throws ServletException {
        // Gets custom class loader
        final String classLoaderName = getApplicationOrSystemProperty(
                "ClassLoader", null);
        ClassLoader classLoader;
        if (classLoaderName == null) {
            classLoader = getClass().getClassLoader();
        } else {
            try {
                final Class<?> classLoaderClass = getClass().getClassLoader()
                        .loadClass(classLoaderName);
                final Constructor<?> c = classLoaderClass
                        .getConstructor(new Class[] { ClassLoader.class });
                classLoader = (ClassLoader) c
                        .newInstance(new Object[] { getClass().getClassLoader() });
            } catch (final Exception e) {
                throw new ServletException(
                        "Could not find specified class loader: "
                                + classLoaderName, e);
            }
        }
        return classLoader;
    }

    /**
     * Send notification to client's application. Used to notify client of
     * critical errors and session expiration due to long inactivity. Server has
     * no knowledge of what application client refers to.
     * 
     * @param request
     *            the HTTP request instance.
     * @param response
     *            the HTTP response to write to.
     * @param caption
     *            for the notification
     * @param message
     *            for the notification
     * @param url
     *            url to load after message, null for current page
     * @throws IOException
     *             if the writing failed due to input/output error.
     */
    void criticalNotification(HttpServletRequest request,
            HttpServletResponse response, String caption, String message,
            String url) throws IOException {

        // clients JS app is still running, but server application either
        // no longer exists or it might fail to perform reasonably.
        // send a notification to client's application and link how
        // to "restart" application.

        if (caption != null) {
            caption = "\"" + caption + "\"";
        }
        if (message != null) {
            message = "\"" + message + "\"";
        }
        if (url != null) {
            url = "\"" + url + "\"";
        }

        // Set the response type
        response.setContentType("application/json; charset=UTF-8");
        final ServletOutputStream out = response.getOutputStream();
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

    /**
     * Returns the application instance to be used for the request. If an
     * existing instance is not found a new one is created or null is returned
     * to indicate that the application is not available.
     * 
     * @param request
     * @param requestType
     * @return
     * @throws MalformedURLException
     * @throws SAXException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ServletException
     * @throws SessionExpired
     */
    private Application findApplicationInstance(HttpServletRequest request,
            RequestType requestType) throws MalformedURLException,
            ServletException, SessionExpired {

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
            throw new SessionExpired();
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
    private boolean requestCanCreateApplication(HttpServletRequest request,
            RequestType requestType) {
        if (requestType == RequestType.UIDL && isRepaintAll(request)) {
            /*
             * UIDL request contains valid repaintAll=1 event, the user probably
             * wants to initiate a new application through a custom index.html
             * without using writeAjaxPage.
             */
            return true;

        } else if (requestType == RequestType.OTHER) {
            /*
             * TODO Should any URL request really create a new application
             * instance if none was found?
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
                e.printStackTrace();
            }
        }
        return resultPath;
    }

    /**
     * Handles the requested URI. An application can add handlers to do special
     * processing, when a certain URI is requested. The handlers are invoked
     * before any windows URIs are processed and if a DownloadStream is returned
     * it is sent to the client.
     * 
     * @param stream
     *            the download stream.
     * 
     * @param request
     *            the HTTP request instance.
     * @param response
     *            the HTTP response to write to.
     * @throws IOException
     * 
     * @see com.vaadin.terminal.URIHandler
     */
    private void handleDownload(DownloadStream stream,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (stream.getParameter("Location") != null) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.addHeader("Location", stream.getParameter("Location"));
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
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
            } else {
                response.setHeader("Cache-Control", "max-age=" + cacheTime
                        / 1000);
                response.setDateHeader("Expires", System.currentTimeMillis()
                        + cacheTime);
                response.setHeader("Pragma", "cache"); // Required to apply
                // caching in some
                // Tomcats
            }

            // Copy download stream parameters directly
            // to HTTP headers.
            final Iterator i = stream.getParameterNames();
            if (i != null) {
                while (i.hasNext()) {
                    final String param = (String) i.next();
                    response.setHeader(param, stream.getParameter(param));
                }
            }

            // suggest local filename from DownloadStream if Content-Disposition
            // not explicitly set
            String contentDispositionValue = stream
                    .getParameter("Content-Disposition");
            if (contentDispositionValue == null) {
                contentDispositionValue = "filename=\"" + stream.getFileName()
                        + "\"";
                response.setHeader("Content-Disposition",
                        contentDispositionValue);
            }

            int bufferSize = stream.getBufferSize();
            if (bufferSize <= 0 || bufferSize > MAX_BUFFER_SIZE) {
                bufferSize = DEFAULT_BUFFER_SIZE;
            }
            final byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;

            final OutputStream out = response.getOutputStream();

            while ((bytesRead = data.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
            out.close();

        }

    }

    /**
     * Creates and starts a new application. This is not meant to be overridden.
     * Override getNewApplication to create the application instance in a custom
     * way.
     * 
     * @param request
     * @return
     * @throws ServletException
     * @throws MalformedURLException
     */
    private Application createApplication(HttpServletRequest request)
            throws ServletException, MalformedURLException {
        Application newApplication = getNewApplication(request);

        // Create application
        final URL applicationUrl = getApplicationUrl(request);

        // Initial locale comes from the request
        Locale locale = request.getLocale();
        HttpSession session = request.getSession();

        // Start the newly created application
        startApplication(session, newApplication, applicationUrl, locale);

        return newApplication;
    }

    private void handleServiceException(HttpServletRequest request,
            HttpServletResponse response, Application application, Throwable e)
            throws IOException, ServletException {
        // if this was an UIDL request, response UIDL back to client
        if (getRequestType(request) == RequestType.UIDL) {
            Application.SystemMessages ci = getSystemMessages();
            criticalNotification(request, response, ci
                    .getInternalErrorCaption(), ci.getInternalErrorMessage(),
                    ci.getInternalErrorURL());
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

    private String getThemeForWindow(HttpServletRequest request, Window window) {
        // Finds theme name
        String themeName = window.getTheme();
        if (request.getParameter(URL_PARAMETER_THEME) != null) {
            themeName = request.getParameter(URL_PARAMETER_THEME);
        }

        if (themeName == null) {
            themeName = getDefaultTheme();
        }

        return themeName;
    }

    /**
     * Returns the default theme. Must never return null.
     * 
     * @return
     */
    public static String getDefaultTheme() {
        return DEFAULT_THEME_NAME;
    }

    /**
     * Calls URI handlers for the request. If an URI handler returns a
     * DownloadStream the stream is passed to the client for downloading.
     * 
     * @param applicationManager
     * @param window
     * @param request
     * @param response
     * @return true if an DownloadStream was sent to the client, false otherwise
     * @throws IOException
     */
    private boolean handleURI(CommunicationManager applicationManager,
            Window window, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // Handles the URI
        DownloadStream download = applicationManager.handleURI(window, request,
                response);

        // A download request
        if (download != null) {
            // Client downloads an resource
            handleDownload(download, request, response);
            return true;
        }

        return false;
    }

    private void handleServiceSessionExpired(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

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
                response.sendRedirect(ci.getSessionExpiredURL());
            } else {
                // send uidl redirect
                criticalNotification(request, response, ci
                        .getSessionExpiredCaption(), ci
                        .getSessionExpiredMessage(), ci.getSessionExpiredURL());
                /*
                 * Invalidate session (weird to have session if we're saying
                 * that it's expired, and worse: portal integration will fail
                 * since the session is not created by the portal.
                 */
                request.getSession().invalidate();
            }
        } catch (SystemMessageException ee) {
            throw new ServletException(ee);
        }

    }

    private void handleServiceSecurityException(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        if (isOnUnloadRequest(request)) {
            /*
             * Request was an unload request (e.g. window close event) and the
             * client expects no response if it fails.
             */
            return;
        }

        // TODO handle differently?
        // Invalid security key, show session expired message for now.
        handleServiceSessionExpired(request, response);
    }

    /**
     * Creates a new application for the given request.
     * 
     * @param request
     *            the HTTP request.
     * @return A new Application instance.
     * @throws ServletException
     */
    protected abstract Application getNewApplication(HttpServletRequest request)
            throws ServletException;

    /**
     * Starts the application if it is not already running. Ensures the
     * application is added to the WebApplicationContext.
     * 
     * @param session
     * @param application
     * @param applicationUrl
     * @param locale
     * @throws ServletException
     */
    private void startApplication(HttpSession session, Application application,
            URL applicationUrl, Locale locale) throws ServletException {
        if (application == null) {
            throw new ServletException(
                    "Application is null and can't be started");
        }

        if (!application.isRunning()) {
            final WebApplicationContext context = WebApplicationContext
                    .getApplicationContext(session);
            // final URL applicationUrl = getApplicationUrl(request);
            context.addApplication(application);
            application.setLocale(locale);
            application.start(applicationUrl, applicationProperties, context);
        }
    }

    /**
     * Check if this is a request for a static resource and, if it is, serve the
     * resource to the client. Returns true if a file was served and the request
     * has been handled, false otherwise.
     * 
     * @param request
     * @param response
     * @return
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
            serveStaticResourcesInVAADIN(request.getRequestURI(), response);
            return true;
        } else if (request.getRequestURI().startsWith(
                request.getContextPath() + "/VAADIN/")) {
            serveStaticResourcesInVAADIN(request.getRequestURI().substring(
                    request.getContextPath().length()), response);
            return true;
        }

        return false;
    }

    /**
     * Serve resources from VAADIN directory.
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void serveStaticResourcesInVAADIN(String filename,
            HttpServletResponse response) throws IOException, ServletException {

        final ServletContext sc = getServletContext();
        InputStream is = sc.getResourceAsStream(filename);
        if (is == null) {
            // try if requested file is found from classloader

            // strip leading "/" otherwise stream from JAR wont work
            filename = filename.substring(1);
            is = getClassLoader().getResourceAsStream(filename);

            if (is == null) {
                // cannot serve requested file
                System.err
                        .println("Requested resource ["
                                + filename
                                + "] not found from filesystem or through class loader."
                                + " Add widgetset and/or theme JAR to your classpath or add files to WebContent/VAADIN folder.");
                response.setStatus(404);
                return;
            }
        }
        final String mimetype = sc.getMimeType(filename);
        if (mimetype != null) {
            response.setContentType(mimetype);
        }
        final OutputStream os = response.getOutputStream();
        final byte buffer[] = new byte[20000];
        int bytes;
        while ((bytes = is.read(buffer)) >= 0) {
            os.write(buffer, 0, bytes);
        }
    }

    private enum RequestType {
        FILE_UPLOAD, UIDL, OTHER;
    }

    private RequestType getRequestType(HttpServletRequest request) {
        if (isFileUploadRequest(request)) {
            return RequestType.FILE_UPLOAD;
        } else if (isUIDLRequest(request)) {
            return RequestType.UIDL;
        }

        return RequestType.OTHER;
    }

    private boolean isUIDLRequest(HttpServletRequest request) {
        String pathInfo = getRequestPathInfo(request);

        if (pathInfo == null) {
            return false;
        }

        String compare = AJAX_UIDL_URI;

        if (pathInfo.startsWith(compare + "/") || pathInfo.endsWith(compare)) {
            return true;
        }

        return false;
    }

    private boolean isFileUploadRequest(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    private boolean isOnUnloadRequest(HttpServletRequest request) {
        return request.getParameter(ApplicationConnection.PARAM_UNLOADBURST) != null;
    }

    /**
     * Get system messages from the current application class
     * 
     * @return
     */
    protected SystemMessages getSystemMessages() {
        try {
            Class appCls = getApplicationClass();
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

    protected abstract Class getApplicationClass()
            throws ClassNotFoundException;

    /**
     * Return the URL from where static files, e.g. the widgetset and the theme,
     * are served. In a standard configuration the VAADIN folder inside the
     * returned folder is what is used for widgetsets and themes.
     * 
     * The returned folder is usually the same as the context path and
     * independent of the application.
     * 
     * @param request
     * @return The location of static resources (should contain the VAADIN
     *         directory). Never ends with a slash (/).
     * @throws MalformedURLException
     */
    String getStaticFilesLocation(HttpServletRequest request) {
        // if context is specified add it to widgetsetUrl
        String ctxPath = request.getContextPath();

        // FIXME: ctxPath.length() == 0 condition is probably unnecessary and
        // might even be wrong.

        if (ctxPath.length() == 0
                && request.getAttribute("javax.servlet.include.context_path") != null) {
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
     * 
     * @param request
     *            the HTTP request.
     * @param response
     *            the HTTP response to write to.
     * @param out
     * @param unhandledParameters
     * @param window
     * @param terminalType
     * @param theme
     * @throws IOException
     *             if the writing failed due to input/output error.
     * @throws MalformedURLException
     *             if the application is denied access the persistent data store
     *             represented by the given URL.
     */
    private void writeAjaxPage(HttpServletRequest request,
            HttpServletResponse response, Window window, String themeName,
            Application application) throws IOException, MalformedURLException,
            ServletException {

        // e.g portlets only want a html fragment
        boolean fragment = (request.getAttribute(REQUEST_FRAGMENT) != null);
        if (fragment) {
            request.setAttribute(Application.class.getName(), application);
        }

        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getOutputStream()));
        String pathInfo = getRequestPathInfo(request);
        if (pathInfo == null) {
            pathInfo = "/";
        }

        String title = ((window.getCaption() == null) ? "Vaadin 6" : window
                .getCaption());

        String widgetset = null;
        // request widgetset takes precedence (e.g portlet include)
        Object reqParam = request.getAttribute(REQUEST_WIDGETSET);
        try {
            widgetset = (String) reqParam;
        } catch (Exception e) {
            // FIXME: Handle exception
            System.err.println("Warning: request param '" + REQUEST_WIDGETSET
                    + "' could not be used (is not a String)" + e);
        }

        // TODO: Any reason this could not use
        // getApplicationOrSystemProperty with DEFAULT_WIDGETSET as default
        // value ?
        if (widgetset == null) {
            widgetset = getApplicationProperty(PARAMETER_WIDGETSET);
        }
        if (widgetset == null) {
            widgetset = DEFAULT_WIDGETSET;
        }

        /* Fetch relative url to application */
        // don't use server and port in uri. It may cause problems with some
        // virtual server configurations which lose the server name
        String appUrl = getApplicationUrl(request).getPath();
        if (appUrl.endsWith("/")) {
            appUrl = appUrl.substring(0, appUrl.length() - 1);
        }

        final String staticFilesLocation = getStaticFilesLocation(request);

        final String staticFilePath = getApplicationOrSystemProperty(
                PARAMETER_VAADIN_RESOURCES, staticFilesLocation);

        reqParam = request.getAttribute(REQUEST_VAADIN_WIDGETSET_PATH);
        final String widgetsetFilePath = reqParam instanceof String ? (String) reqParam
                : staticFilePath;

        // Default theme does not use theme URI
        String themeUri = null;
        if (themeName != null) {
            // Using custom theme
            themeUri = staticFilePath + "/" + THEME_DIRECTORY_PATH + themeName;
        }

        if (!fragment) {
            // Window renders are not cacheable
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("text/html; charset=UTF-8");

            // write html header
            page.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD "
                    + "XHTML 1.0 Transitional//EN\" "
                    + "\"http://www.w3.org/TR/xhtml1/"
                    + "DTD/xhtml1-transitional.dtd\">\n");

            page.write("<html xmlns=\"http://www.w3.org/1999/xhtml\""
                    + ">\n<head>\n");
            page
                    .write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");
            page
                    .write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=7\" />\n");
            page.write("<style type=\"text/css\">"
                    + "html, body {height:100%;}</style>");

            // Add favicon links
            page
                    .write("<link rel=\"shortcut icon\" type=\"image/vnd.microsoft.icon\" href=\""
                            + themeUri + "/favicon.ico\" />");
            page
                    .write("<link rel=\"icon\" type=\"image/vnd.microsoft.icon\" href=\""
                            + themeUri + "/favicon.ico\" />");

            page.write("<title>" + title + "</title>");

            page
                    .write("\n</head>\n<body scroll=\"auto\" class=\"v-generated-body\">\n");
        }

        String appId = appUrl;
        if ("".equals(appUrl)) {
            appId = "ROOT";
        }
        appId = appId.replaceAll("[^a-zA-Z0-9]", "");
        // Add hashCode to the end, so that it is still (sort of) predictable,
        // but indicates that it should not be used in CSS and such:
        int hashCode = appId.hashCode();
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        appId = appId + "-" + hashCode;

        // Get system messages
        Application.SystemMessages systemMessages = null;
        try {
            systemMessages = getSystemMessages();
        } catch (SystemMessageException e) {
            // failing to get the system messages is always a problem
            throw new ServletException("CommunicationError!", e);
        }

        if (isGecko17(request)) {
            // special start page for gecko 1.7 versions. Firefox 1.0 is not
            // supported, but the hack is make it possible to use linux and
            // hosted mode browser for debugging. Note that due this hack,
            // debugging gwt code in portals with linux will be problematic if
            // there are multiple Vaadin portlets visible at the same time.
            // TODO remove this when hosted mode on linux gets newer gecko

            page.write("<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                    + "style=\"width:0;height:0;border:0;overflow:"
                    + "hidden\" src=\"javascript:false\"></iframe>\n");
            page.write("<script language='javascript' src='"
                    + widgetsetFilePath + "/" + WIDGETSET_DIRECTORY_PATH
                    + widgetset + "/" + widgetset + ".nocache.js?"
                    + new Date().getTime() + "'></script>\n");
            page.write("<script type=\"text/javascript\">\n");
            page.write("//<![CDATA[\n");
            page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n "
                    + "if(!vaadin) { var vaadin = {}} \n"
                    + "vaadin.vaadinConfigurations = {};\n"
                    + "vaadin.themesLoaded = {}};\n");

            if (!isProductionMode()) {
                page.write("vaadin.debug = true;\n");
            }

            page.write("vaadin.vaadinConfigurations[\"" + appId + "\"] = {");
            page.write("appUri:'" + appUrl + "', ");
            page.write("pathInfo: '" + pathInfo + "', ");
            if (window != application.getMainWindow()) {
                page.write("windowName: '" + window.getName() + "', ");
            }
            page.write("themeUri:");
            page.write(themeUri != null ? "'" + themeUri + "'" : "null");
            page.write(", versionInfo : {vaadinVersion:\"");
            page.write(VERSION);
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
            page.write("};\n//]]>\n</script>\n");

            if (themeName != null) {
                // Custom theme's stylesheet, load only once, in different
                // script
                // tag to be dominate styles injected by widget
                // set
                page.write("<script type=\"text/javascript\">\n");
                page.write("//<![CDATA[\n");
                page.write("if(!vaadin.themesLoaded['" + themeName + "']) {\n");
                page
                        .write("var stylesheet = document.createElement('link');\n");
                page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
                page.write("stylesheet.setAttribute('type', 'text/css');\n");
                page.write("stylesheet.setAttribute('href', '" + themeUri
                        + "/styles.css');\n");
                page
                        .write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
                page.write("vaadin.themesLoaded['" + themeName
                        + "'] = true;\n}\n");
                page.write("//]]>\n</script>\n");
            }

        } else {
            page.write("<script type=\"text/javascript\">\n");
            page.write("//<![CDATA[\n");
            page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n "
                    + "if(!vaadin) { var vaadin = {}} \n"
                    + "vaadin.vaadinConfigurations = {};\n"
                    + "vaadin.themesLoaded = {};\n");
            if (!isProductionMode()) {
                page.write("vaadin.debug = true;\n");
            }
            page
                    .write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                            + "style=\"width:0;height:0;border:0;overflow:"
                            + "hidden\" src=\"javascript:false\"></iframe>');\n");
            page.write("document.write(\"<script language='javascript' src='"
                    + widgetsetFilePath + "/" + WIDGETSET_DIRECTORY_PATH
                    + widgetset + "/" + widgetset + ".nocache.js?"
                    + new Date().getTime() + "'><\\/script>\");\n}\n");

            page.write("vaadin.vaadinConfigurations[\"" + appId + "\"] = {");
            page.write("appUri:'" + appUrl + "', ");
            page.write("pathInfo: '" + pathInfo + "', ");
            if (window != application.getMainWindow()) {
                page.write("windowName: '" + window.getName() + "', ");
            }
            page.write("themeUri:");
            page.write(themeUri != null ? "'" + themeUri + "'" : "null");
            page.write(", versionInfo : {vaadinVersion:\"");
            page.write(VERSION);
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
            page.write("};\n//]]>\n</script>\n");

            if (themeName != null) {
                // Custom theme's stylesheet, load only once, in different
                // script
                // tag to be dominate styles injected by widget
                // set
                page.write("<script type=\"text/javascript\">\n");
                page.write("//<![CDATA[\n");
                page.write("if(!vaadin.themesLoaded['" + themeName + "']) {\n");
                page
                        .write("var stylesheet = document.createElement('link');\n");
                page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
                page.write("stylesheet.setAttribute('type', 'text/css');\n");
                page.write("stylesheet.setAttribute('href', '" + themeUri
                        + "/styles.css');\n");
                page
                        .write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
                page.write("vaadin.themesLoaded['" + themeName
                        + "'] = true;\n}\n");
                page.write("//]]>\n</script>\n");
            }
        }

        // Warn if the widgetset has not been loaded after 15 seconds on
        // inactivity
        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");
        page.write("setTimeout('if (typeof " + widgetset.replace('.', '_')
                + " == \"undefined\") {alert(\"Failed to load the widgetset: "
                + widgetsetFilePath + "/" + WIDGETSET_DIRECTORY_PATH
                + widgetset + "/" + widgetset + ".nocache.js\")};',15000);\n"
                + "//]]>\n</script>\n");

        String style = null;
        reqParam = request.getAttribute(REQUEST_APPSTYLE);
        if (reqParam != null) {
            style = "style=\"" + reqParam + "\"";
        }
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
        String themeClass = "";
        if (themeName != null) {
            themeClass = "v-theme-" + themeName.replaceAll("[^a-zA-Z0-9]", "");
        }

        page.write("<div id=\"" + appId + "\" class=\"v-app v-app-loading "
                + themeClass + " " + appClass + "\" "
                + (style != null ? style : "") + "></div>\n");

        if (!fragment) {
            page.write("<noscript>" + getNoScriptMessage() + "</noscript>");
            page.write("</body>\n</html>\n");
        }
        page.close();

    }

    /**
     * Returns a message printed for browsers without scripting support or if
     * browsers scripting support is disabled.
     */
    protected String getNoScriptMessage() {
        return "You have to enable javascript in your browser to use an application built with Vaadin.";
    }

    private boolean isGecko17(HttpServletRequest request) {
        final WebBrowser browser = WebApplicationContext.getApplicationContext(
                request.getSession()).getBrowser();
        if (browser != null && browser.getBrowserApplication() != null) {
            if (browser.getBrowserApplication().indexOf("rv:1.7.") > 0
                    && browser.getBrowserApplication().indexOf("Gecko") > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles theme resource file requests. Resources supplied with the themes
     * are provided by the WebAdapterServlet.
     * 
     * @param request
     *            the HTTP request.
     * @param response
     *            the HTTP response.
     * @return boolean <code>true</code> if the request was handled and further
     *         processing should be suppressed, <code>false</code> otherwise.
     * @throws ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    private boolean handleResourceRequest(HttpServletRequest request,
            HttpServletResponse response, String themeName)
            throws ServletException {

        // If the resource path is unassigned, initialize it
        if (resourcePath == null) {
            resourcePath = request.getContextPath() + request.getServletPath()
                    + RESOURCE_URI;
            // WebSphere Application Server related fix
            resourcePath = resourcePath.replaceAll("//", "/");
        }

        String resourceId = request.getPathInfo();

        // Checks if this really is a resource request
        if (resourceId == null || !resourceId.startsWith(RESOURCE_URI)) {
            return false;
        }

        // Checks the resource type
        resourceId = resourceId.substring(RESOURCE_URI.length());
        InputStream data = null;

        // Gets theme resources
        try {
            data = getServletContext().getResourceAsStream(
                    THEME_DIRECTORY_PATH + themeName + "/" + resourceId);
        } catch (final Exception e) {
            // FIXME: Handle exception
            e.printStackTrace();
            data = null;
        }

        // Writes the response
        try {
            if (data != null) {
                response.setContentType(FileTypeResolver
                        .getMIMEType(resourceId));

                // Use default cache time for theme resources
                response.setHeader("Cache-Control", "max-age="
                        + DEFAULT_THEME_CACHETIME / 1000);
                response.setDateHeader("Expires", System.currentTimeMillis()
                        + DEFAULT_THEME_CACHETIME);
                response.setHeader("Pragma", "cache"); // Required to apply
                // caching in some
                // Tomcats

                // Writes the data to client
                final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int bytesRead = 0;
                final OutputStream out = response.getOutputStream();
                while ((bytesRead = data.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                data.close();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (final java.io.IOException e) {
            // FIXME: Handle exception
            System.err.println("Resource transfer failed:  "
                    + request.getRequestURI() + ". (" + e.getMessage() + ")");
        }

        return true;
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
    URL getApplicationUrl(HttpServletRequest request)
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
        System.out.println(request.getPathInfo());
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
     * @throws SAXException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SessionExpired
     */
    private Application getExistingApplication(HttpServletRequest request,
            boolean allowSessionCreation) throws MalformedURLException,
            SessionExpired {

        // Ensures that the session is still valid
        final HttpSession session = request.getSession(allowSessionCreation);
        if (session == null) {
            throw new SessionExpired();
        }

        WebApplicationContext context = WebApplicationContext
                .getApplicationContext(session);

        // Gets application list for the session.
        final Collection<Application> applications = context.getApplications();

        // Search for the application (using the application URI) from the list
        for (final Iterator<Application> i = applications.iterator(); i
                .hasNext();) {
            final Application sessionApplication = i.next();
            final String sessionApplicationPath = sessionApplication.getURL()
                    .getPath();
            String requestApplicationPath = getApplicationUrl(request)
                    .getPath();

            if (requestApplicationPath.equals(sessionApplicationPath)) {
                // Found a running application
                if (sessionApplication.isRunning()) {
                    return sessionApplication;
                }
                // Application has stopped, so remove it before creating a new
                // application
                WebApplicationContext.getApplicationContext(session)
                        .removeApplication(sessionApplication);
                break;
            }
        }

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
            WebApplicationContext.getApplicationContext(session)
                    .removeApplication(application);
        }

        response.sendRedirect(response.encodeRedirectURL(logoutUrl));
    }

    /**
     * Gets the existing application or create a new one. Get a window within an
     * application based on the requested URI.
     * 
     * @param request
     *            the HTTP Request.
     * @param application
     *            the Application to query for window.
     * @return Window matching the given URI or null if not found.
     * @throws ServletException
     *             if an exception has occurred that interferes with the
     *             servlet's normal operation.
     */
    private Window getApplicationWindow(HttpServletRequest request,
            Application application) throws ServletException {

        Window window = null;

        // Finds the window where the request is handled
        String path = getRequestPathInfo(request);

        // Main window as the URI is empty
        if (path == null || path.length() == 0 || path.equals("/")
                || path.startsWith("/APP/")) {
            window = application.getMainWindow();
        } else {
            String windowName = null;
            if (path.charAt(0) == '/') {
                path = path.substring(1);
            }
            final int index = path.indexOf('/');
            if (index < 0) {
                windowName = path;
                path = "";
            } else {
                windowName = path.substring(0, index);
                path = path.substring(index + 1);
            }
            window = application.getWindow(windowName);

            if (window == null) {
                // By default, we use main window
                window = application.getMainWindow();
            } else if (!window.isVisible()) {
                // Implicitly painting without actually invoking paint()
                window.requestRepaintRequests();

                // If the window is invisible send a blank page
                return null;
            }
        }

        return window;
    }

    String getRequestPathInfo(HttpServletRequest request) {
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
            WebApplicationContext context = WebApplicationContext
                    .getApplicationContext(session);
            context.applicationToAjaxAppMgrMap.remove(application);
            // FIXME: Move to WebApplicationContext
            context.removeApplication(application);
        }
    }

    /**
     * Implementation of ParameterHandler.ErrorEvent interface.
     */
    public class ParameterHandlerErrorImpl implements
            ParameterHandler.ErrorEvent, Serializable {

        private ParameterHandler owner;

        private Throwable throwable;

        /**
         * Gets the contained throwable.
         * 
         * @see com.vaadin.terminal.Terminal.ErrorEvent#getThrowable()
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Gets the source ParameterHandler.
         * 
         * @see com.vaadin.terminal.ParameterHandler.ErrorEvent#getParameterHandler()
         */
        public ParameterHandler getParameterHandler() {
            return owner;
        }

    }

    /**
     * Implementation of URIHandler.ErrorEvent interface.
     */
    public class URIHandlerErrorImpl implements URIHandler.ErrorEvent,
            Serializable {

        private final URIHandler owner;

        private final Throwable throwable;

        /**
         * 
         * @param owner
         * @param throwable
         */
        private URIHandlerErrorImpl(URIHandler owner, Throwable throwable) {
            this.owner = owner;
            this.throwable = throwable;
        }

        /**
         * Gets the contained throwable.
         * 
         * @see com.vaadin.terminal.Terminal.ErrorEvent#getThrowable()
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Gets the source URIHandler.
         * 
         * @see com.vaadin.terminal.URIHandler.ErrorEvent#getURIHandler()
         */
        public URIHandler getURIHandler() {
            return owner;
        }
    }

    public class RequestError implements Terminal.ErrorEvent, Serializable {

        private final Throwable throwable;

        public RequestError(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

    }
}
