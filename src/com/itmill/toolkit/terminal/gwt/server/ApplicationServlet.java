/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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

import com.itmill.toolkit.Application;
import com.itmill.toolkit.external.org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.itmill.toolkit.service.FileTypeResolver;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.ui.Window;

/**
 * This servlet connects IT Mill Toolkit Application to Web.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */

public class ApplicationServlet extends HttpServlet {

    private static final long serialVersionUID = -4937882979845826574L;

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

    // Configurable parameter names
    private static final String PARAMETER_DEBUG = "Debug";

    private static final String PARAMETER_ITMILL_RESOURCES = "Resources";

    private static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    private static final int MAX_BUFFER_SIZE = 64 * 1024;

    // TODO This is session specific not servlet wide data. No need to store
    // this here, move it to Session from where it can be queried when required
    protected static HashMap applicationToAjaxAppMgrMap = new HashMap();

    private static final String RESOURCE_URI = "/RES/";

    private static final String AJAX_UIDL_URI = "/UIDL";

    static final String THEME_DIRECTORY_PATH = "ITMILL/themes/";

    private static final int DEFAULT_THEME_CACHETIME = 1000 * 60 * 60 * 24;

    static final String WIDGETSET_DIRECTORY_PATH = "ITMILL/widgetsets/";

    // Name of the default widget set, used if not specified in web.xml
    private static final String DEFAULT_WIDGETSET = "com.itmill.toolkit.terminal.gwt.DefaultWidgetSet";

    // Widget set parameter name
    private static final String PARAMETER_WIDGETSET = "widgetset";

    // Private fields
    private Class applicationClass;

    private Properties applicationProperties;

    private String resourcePath = null;

    private String debugMode = "";

    // Is this servlet application runner
    private boolean isApplicationRunnerServlet = false;

    // If servlet is application runner, store request's classname
    private String applicationRunnerClassname = null;

    private ClassLoader classLoader;

    private boolean testingToolsActive = false;

    private String testingToolsServerUri = null;

    /**
     * Called by the servlet container to indicate to a servlet that the servlet
     * is being placed into service.
     * 
     * @param servletConfig
     *                the object containing the servlet's configuration and
     *                initialization parameters
     * @throws javax.servlet.ServletException
     *                 if an exception has occurred that interferes with the
     *                 servlet's normal operation.
     */
    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);

        // Get applicationRunner
        final String applicationRunner = servletConfig
                .getInitParameter("applicationRunner");
        if (applicationRunner != null) {
            if ("true".equals(applicationRunner)) {
                isApplicationRunnerServlet = true;
            } else if ("false".equals(applicationRunner)) {
                isApplicationRunnerServlet = false;
            } else {
                throw new ServletException(
                        "If applicationRunner parameter is given for an application, it must be 'true' or 'false'");
            }
        }

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

        // Gets the debug window parameter
        final String debug = getApplicationOrSystemProperty(PARAMETER_DEBUG, "")
                .toLowerCase();

        // Enables application specific debug
        if (!"".equals(debug) && !"true".equals(debug)
                && !"false".equals(debug)) {
            throw new ServletException(
                    "If debug parameter is given for an application, it must be 'true' or 'false'");
        }
        debugMode = debug;

        // Gets Testing Tools parameters if feature is activated
        if (getApplicationOrSystemProperty("testingToolsActive", "false")
                .equals("true")) {
            testingToolsActive = true;
            testingToolsServerUri = getApplicationOrSystemProperty(
                    "testingToolsServerUri", null);
        }

        // Gets custom class loader
        final String classLoaderName = getApplicationOrSystemProperty(
                "ClassLoader", null);
        ClassLoader classLoader;
        if (classLoaderName == null) {
            classLoader = getClass().getClassLoader();
        } else {
            try {
                final Class classLoaderClass = getClass().getClassLoader()
                        .loadClass(classLoaderName);
                final Constructor c = classLoaderClass
                        .getConstructor(new Class[] { ClassLoader.class });
                classLoader = (ClassLoader) c
                        .newInstance(new Object[] { getClass().getClassLoader() });
            } catch (final Exception e) {
                System.err.println("Could not find specified class loader: "
                        + classLoaderName);
                throw new ServletException(e);
            }
        }
        this.classLoader = classLoader;

        // Loads the application class using the same class loader
        // as the servlet itself
        if (!isApplicationRunnerServlet) {
            // Gets the application class name
            final String applicationClassName = servletConfig
                    .getInitParameter("application");
            if (applicationClassName == null) {
                throw new ServletException(
                        "Application not specified in servlet parameters");
            }
            try {
                applicationClass = classLoader.loadClass(applicationClassName);
            } catch (final ClassNotFoundException e) {
                throw new ServletException("Failed to load application class: "
                        + applicationClassName);
            }
        } else {
            // This servlet is in application runner mode, it uses classloader
            // later to create Applications based on URL
        }

    }

    /**
     * Gets an application or system property value.
     * 
     * @param parameterName
     *                the Name or the parameter.
     * @param defaultValue
     *                the Default to be used.
     * @return String value or default if not found
     */
    private String getApplicationOrSystemProperty(String parameterName,
            String defaultValue) {

        // Try application properties
        String val = applicationProperties.getProperty(parameterName);
        if (val != null) {
            return val;
        }

        // Try lowercased application properties for backward compability with
        // 3.0.2 and earlier
        val = applicationProperties.getProperty(parameterName.toLowerCase());
        if (val != null) {
            return val;
        }

        // Try system properties
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
        if (val != null) {
            return val;
        }

        return defaultValue;
    }

    /**
     * Receives standard HTTP requests from the public service method and
     * dispatches them.
     * 
     * @param request
     *                the object that contains the request the client made of
     *                the servlet.
     * @param response
     *                the object that contains the response the servlet returns
     *                to the client.
     * @throws ServletException
     *                 if an input or output error occurs while the servlet is
     *                 handling the TRACE request.
     * @throws IOException
     *                 if the request for the TRACE cannot be handled.
     */
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        // check if we should serve static files (widgetsets, themes)
        if ((request.getPathInfo() != null)
                && (request.getPathInfo().length() > 10)) {
            if ((request.getContextPath() != null)
                    && (request.getRequestURI().startsWith("/ITMILL/"))) {
                serveStaticResourcesInITMILL(request.getRequestURI(), response);
                return;
            } else if (request.getRequestURI().startsWith(
                    request.getContextPath() + "/ITMILL/")) {
                serveStaticResourcesInITMILL(request.getRequestURI().substring(
                        request.getContextPath().length()), response);
                return;
            }
        }

        Application application = null;
        boolean UIDLrequest = false;
        try {
            // handle file upload if multipart request
            if (ServletFileUpload.isMultipartContent(request)) {
                application = getExistingApplication(request, response);
                if (application == null) {
                    throw new SessionExpired();
                }
                // Invokes context transaction listeners
                // note: endTransaction is called on finalize below
                ((WebApplicationContext) application.getContext())
                        .startTransaction(application, request);
                getApplicationManager(application).handleFileUpload(request,
                        response);
                return;
            }

            // Update browser details
            final WebBrowser browser = WebApplicationContext
                    .getApplicationContext(request.getSession()).getBrowser();
            browser.updateBrowserProperties(request);
            // TODO Add screen height and width to the GWT client

            // Handles AJAX UIDL requests
            if (request.getPathInfo() != null) {

                String compare = AJAX_UIDL_URI;
                if (isApplicationRunnerServlet) {
                    final String[] URIparts = getApplicationRunnerURIs(request);
                    applicationRunnerClassname = URIparts[4];
                    compare = "/" + applicationRunnerClassname + AJAX_UIDL_URI;
                }

                if (request.getPathInfo().startsWith(compare + "/")
                        || request.getPathInfo().endsWith(compare)) {
                    UIDLrequest = true;
                    application = getExistingApplication(request, response);
                    if (application == null) {
                        // No existing applications found
                        final String repaintAll = request
                                .getParameter("repaintAll");
                        if ((repaintAll != null) && (repaintAll.equals("1"))) {
                            // UIDL request contains valid repaintAll=1 event,
                            // probably user wants to initiate new application
                            // through custom index.html without writeAjaxPage
                            application = getNewApplication(request, response);
                        } else {
                            // UIDL request refers to non-existing application
                            throw new SessionExpired();
                        }
                    }

                    // Invokes context transaction listeners
                    // note: endTransaction is called on finalize below
                    ((WebApplicationContext) application.getContext())
                            .startTransaction(application, request);

                    // Handle UIDL request
                    getApplicationManager(application).handleUidlRequest(
                            request, response, this);
                    return;
                }
            }

            // Get existing application
            application = getExistingApplication(request, response);
            if (application == null
                    || request.getParameter("restartApplication") != null
                    || request.getParameter("closeApplication") != null) {
                if (application != null) {
                    application.close();
                    final HttpSession session = request.getSession(false);
                    if (session != null) {
                        ApplicationServlet.applicationToAjaxAppMgrMap
                                .remove(application);
                        WebApplicationContext.getApplicationContext(session)
                                .removeApplication(application);
                    }
                }
                if (request.getParameter("closeApplication") != null) {
                    return;
                }
                // Not found, creating new application
                application = getNewApplication(request, response);
            }

            // Invokes context transaction listeners
            // note: endTransaction is called on finalize below
            ((WebApplicationContext) application.getContext())
                    .startTransaction(application, request);

            // Removes application if it has stopped
            if (!application.isRunning()) {
                endApplication(request, response, application);
                return;
            }

            // Finds the window within the application
            Window window = null;
            window = getApplicationWindow(request, application);

            // Handle parameters
            final Map parameters = request.getParameterMap();
            if (window != null && parameters != null) {
                window.handleParameters(parameters);
            }

            // Is this a download request from application
            DownloadStream download = null;

            // Handles the URI if the application is still running
            download = handleURI(application, request, response);

            // If this is not a download request
            if (download == null) {

                // Sets terminal type for the window, if not already set
                if (window.getTerminal() == null) {
                    window.setTerminal(browser);
                }

                // Finds theme name
                String themeName = window.getTheme();
                if (request.getParameter("theme") != null) {
                    themeName = request.getParameter("theme");
                }

                if (themeName == null) {
                    themeName = "default";
                }

                // Handles resource requests
                if (handleResourceRequest(request, response, themeName)) {
                    return;
                }

                // Send initial AJAX page that kickstarts Toolkit application
                writeAjaxPage(request, response, window, themeName, application);

            } else {
                // Client downloads an resource
                handleDownload(download, request, response);
            }

        } catch (final SessionExpired e) {
            // Session has expired, notify user
            Application.SystemMessages ci = Application.getSystemMessages();
            try {
                Method m = applicationClass
                        .getMethod("getSystemMessages", null);
                ci = (Application.SystemMessages) m.invoke(null, null);
            } catch (Exception e2) {
                // Not critical, but something is still wrong; print stacktrace
                e2.printStackTrace();
            }

            if (!UIDLrequest) {
                // 'plain' http req - e.g. browser reload;
                // just go ahead redirect the browser
                response.sendRedirect(ci.getSessionExpiredURL());
            } else {
                // send uidl redirect
                criticalNotification(request, response, ci
                        .getSessionExpiredCaption(), ci
                        .getSessionExpiredMessage(), ci.getSessionExpiredURL());
            }

        } catch (final Throwable e) {
            e.printStackTrace();
            // if this was an UIDL request, response UIDL back to client
            if (UIDLrequest) {
                Application.SystemMessages ci = Application.getSystemMessages();
                try {
                    Method m = applicationClass.getMethod("getSystemMessages",
                            null);
                    ci = (Application.SystemMessages) m.invoke(null, null);
                } catch (Exception e2) {
                    // Not critical, but something is still wrong; print
                    // stacktrace
                    e2.printStackTrace();
                }
                criticalNotification(request, response, ci
                        .getInternalErrorCaption(), ci
                        .getInternalErrorMessage(), ci.getInternalErrorURL());
            } else {
                // Re-throw other exceptions
                throw new ServletException(e);
            }
        } finally {
            // Notifies transaction end
            if (application != null) {
                ((WebApplicationContext) application.getContext())
                        .endTransaction(application, request);
            }
        }
    }

    /**
     * Serve resources in ITMILL directory if requested.
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    private void serveStaticResourcesInITMILL(String filename,
            HttpServletResponse response) throws IOException {

        final ServletContext sc = getServletContext();
        InputStream is = sc.getResourceAsStream(filename);
        if (is == null) {
            // try if requested file is found from classloader
            try {
                // strip leading "/" otherwise stream from JAR wont work
                filename = filename.substring(1);
                is = classLoader.getResourceAsStream(filename);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (is == null) {
                // cannot serve requested file
                System.err
                        .println("Requested resource ["
                                + filename
                                + "] not found from filesystem or through class loader."
                                + " Add widgetset and/or theme JAR to your classpath or add files to WebContent/ITMILL folder.");
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

    /**
     * Send notification to client's application. Used to notify client of
     * critical errors and session expiration due to long inactivity. Server has
     * no knowledge of what application client refers to.
     * 
     * @param request
     *                the HTTP request instance.
     * @param response
     *                the HTTP response to write to.
     * @param caption
     *                for the notification
     * @param message
     *                for the notification
     * @param url
     *                url to load after message, null for current page
     * @throws IOException
     *                 if the writing failed due to input/output error.
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
     * Resolve application URL and widgetset URL. Widgetset is not application
     * specific.
     * 
     * @param request
     * @return string array consisting of application url first and then
     *         widgetset url.
     */
    private String[] getAppAndWidgetUrl(HttpServletRequest request) {
        // don't use server and port in uri. It may cause problems with some
        // virtual server configurations which lose the server name
        String appUrl = null;
        String widgetsetUrl = null;
        if (isApplicationRunnerServlet) {
            final String[] URIparts = getApplicationRunnerURIs(request);
            widgetsetUrl = URIparts[0];
            if (widgetsetUrl.equals("/")) {
                widgetsetUrl = "";
            }
            appUrl = URIparts[1];
        } else {
            String[] urlParts;
            try {
                urlParts = getApplicationUrl(request).toString().split("\\/");
                appUrl = "";
                widgetsetUrl = "";
                // if context is specified add it to widgetsetUrl
                String ctxPath = request.getContextPath();
                if (ctxPath.length() == 0
                        && request
                                .getAttribute("javax.servlet.include.context_path") != null) {
                    // include request (e.g portlet), get contex path from
                    // attribute
                    ctxPath = (String) request
                            .getAttribute("javax.servlet.include.context_path");
                }
                if (urlParts.length > 3
                        && urlParts[3].equals(ctxPath.replaceAll("\\/", ""))) {
                    widgetsetUrl += "/" + urlParts[3];
                }
                for (int i = 3; i < urlParts.length; i++) {
                    appUrl += "/" + urlParts[i];
                }
                if (appUrl.endsWith("/")) {
                    appUrl = appUrl.substring(0, appUrl.length() - 1);
                }
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }

        }
        return new String[] { appUrl, widgetsetUrl };
    }

    /**
     * 
     * @param request
     *                the HTTP request.
     * @param response
     *                the HTTP response to write to.
     * @param out
     * @param unhandledParameters
     * @param window
     * @param terminalType
     * @param theme
     * @throws IOException
     *                 if the writing failed due to input/output error.
     * @throws MalformedURLException
     *                 if the application is denied access the persistent data
     *                 store represented by the given URL.
     */
    private void writeAjaxPage(HttpServletRequest request,
            HttpServletResponse response, Window window, String themeName,
            Application application) throws IOException, MalformedURLException {

        // e.g portlets only want a html fragment
        boolean fragment = (request.getAttribute(REQUEST_FRAGMENT) != null);
        if (fragment) {
            request.setAttribute(Application.class.getName(), application);
        }

        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getOutputStream()));
        final String pathInfo = request.getPathInfo() == null ? "/" : request
                .getPathInfo();
        String title = ((window == null || window.getCaption() == null) ? "IT Mill Toolkit 5"
                : window.getCaption());

        String widgetset = null;
        // request widgetset takes precedence (e.g portlet include)
        Object reqParam = request.getAttribute(REQUEST_WIDGETSET);
        try {
            widgetset = (String) reqParam;
        } catch (Exception e) {
            System.err.println("Warning: request param " + REQUEST_WIDGETSET
                    + " could not be used (not a String?)" + e);
        }
        if (widgetset == null) {
            widgetset = applicationProperties.getProperty(PARAMETER_WIDGETSET);
        }
        if (widgetset == null) {
            widgetset = DEFAULT_WIDGETSET;
        }
        final String[] urls = getAppAndWidgetUrl(request);
        final String appUrl = urls[0];
        final String widgetsetUrl = urls[1];

        final String staticFilePath = getApplicationOrSystemProperty(
                PARAMETER_ITMILL_RESOURCES, widgetsetUrl);

        // Default theme does not use theme URI
        String themeUri = null;
        if (themeName != null) {
            // Using custom theme
            themeUri = staticFilePath + "/" + THEME_DIRECTORY_PATH + themeName;
        }

        boolean testingApplication = testingToolsActive
                && request.getParameter("TT") != null;

        if (!fragment) {
            // Window renders are not cacheable
            response.setCharacterEncoding("utf-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("text/html");

            // write html header
            page.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD "
                    + "XHTML 1.0 Transitional//EN\" "
                    + "\"http://www.w3.org/TR/xhtml1/"
                    + "DTD/xhtml1-transitional.dtd\">\n");

            page.write("<html xmlns=\"http://www.w3.org/1999/xhtml\""
                    + ">\n<head>\n");
            page.write("<style type=\"text/css\">"
                    + "html, body {height:100%;}</style>");
            page.write("<title>" + title + "</title>");

            if (testingApplication) {
                // TT script needs to be in head as it needs to be the first
                // to hook capturing event listeners
                writeTestingToolsScripts(page, request);
            }

            page.write("\n</head>\n<body class=\"i-generated-body\">\n");
        }

        String appId = appUrl;
        appId = appId.replaceAll("[^a-zA-Z0-9]", "");

        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");
        page.write("if(!itmill || !itmill.toolkitConfigurations) {\n "
                + "if(!itmill) { var itmill = {}} \n"
                + "itmill.toolkitConfigurations = {};\n"
                + "itmill.themesLoaded = {};\n");
        page.write("document.write('<iframe id=\"__gwt_historyFrame\" "
                + "style=\"width:0;height:0;border:0;overflow:"
                + "hidden\" src=\"javascript:false\"></iframe>');\n");
        page.write("document.write(\"<script language='javascript' src='"
                + staticFilePath + "/" + WIDGETSET_DIRECTORY_PATH + widgetset
                + "/" + widgetset + ".nocache.js'><\\/script>\");\n}\n");

        page.write("itmill.toolkitConfigurations[\"" + appId + "\"] = {");
        page.write("appUri:'" + appUrl + "', ");
        page.write("pathInfo: '" + pathInfo + "', ");
        page.write("themeUri:");
        page.write(themeUri != null ? "'" + themeUri + "'" : "null");
        if (testingApplication) {
            page.write(", versionInfo : {toolkitVersion:\"");
            page.write(VERSION);
            page.write("\",applicationVersion:\"");
            page.write(application.getVersion());
            page.write("\"}");
        }
        page.write("};\n//]]>\n</script>\n");

        if (themeName != null) {
            // Custom theme's stylesheet, load only once, in different script
            // tag to be dominate styles injected by widget
            // set
            page.write("<script type=\"text/javascript\">\n");
            page.write("//<![CDATA[\n");
            page.write("if(!itmill.themesLoaded['" + themeName + "']) {\n");
            page.write("var stylesheet = document.createElement('link');\n");
            page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
            page.write("stylesheet.setAttribute('type', 'text/css');\n");
            page.write("stylesheet.setAttribute('href', '" + themeUri
                    + "/styles.css');\n");
            page
                    .write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
            page.write("itmill.themesLoaded['" + themeName + "'] = true;\n}\n");
            page.write("//]]>\n</script>\n");
        }

        String style = null;
        reqParam = request.getAttribute(REQUEST_APPSTYLE);
        if (reqParam != null) {
            style = "style=\"" + reqParam + "\"";
        }
        page.write("<div id=\"" + appId + "\" class=\"i-app\" "
                + (style != null ? style : "") + "></div>\n");

        if (!fragment) {
            page.write("</body>\n</html>\n");
        }
        page.close();

    }

    private void writeTestingToolsScripts(Writer page,
            HttpServletRequest request) throws IOException {
        // Testing Tools script and CSS files are served from Testing Tools
        // Server
        String ext = getTestingToolsUri(request);
        ext = ext.substring(0, ext.lastIndexOf('/'));
        page.write("<script src=\"" + ext + "/ext/TT.js"
                + "\" type=\"text/javascript\"></script>\n");
        page.write("<link rel=\"stylesheet\" href=\"" + ext + "/ext/TT.css"
                + "\" type=\"text/css\" />\n");

    }

    private String getTestingToolsUri(HttpServletRequest request) {
        if (testingToolsServerUri == null) {
            // Default behavior is that Testing Tools Server application exists
            // on same host as current application does in port 8099.
            testingToolsServerUri = "http" + "://" + request.getServerName()
                    + ":8099" + "/TestingToolsServer";
        }
        return testingToolsServerUri;
    }

    /**
     * Handles the requested URI. An application can add handlers to do special
     * processing, when a certain URI is requested. The handlers are invoked
     * before any windows URIs are processed and if a DownloadStream is returned
     * it is sent to the client.
     * 
     * @param application
     *                the Application owning the URI.
     * @param request
     *                the HTTP request instance.
     * @param response
     *                the HTTP response to write to.
     * @return boolean <code>true</code> if the request was handled and
     *         further processing should be suppressed, <code>false</code>
     *         otherwise.
     * @see com.itmill.toolkit.terminal.URIHandler
     */
    private DownloadStream handleURI(Application application,
            HttpServletRequest request, HttpServletResponse response) {

        String uri = request.getPathInfo();

        // If no URI is available
        if (uri == null) {
            uri = "";
        }

        // Removes the leading /
        while (uri.startsWith("/") && uri.length() > 0) {
            uri = uri.substring(1);
        }

        // If using application runner, remove package and class name
        if (isApplicationRunnerServlet) {
            uri = uri.replaceFirst(applicationRunnerClassname + "/", "");
        }

        // Handles the uri
        DownloadStream stream = null;
        try {
            stream = application.handleURI(application.getURL(), uri);
        } catch (final Throwable t) {
            application.terminalError(new URIHandlerErrorImpl(application, t));
        }

        return stream;
    }

    /**
     * Handles the requested URI. An application can add handlers to do special
     * processing, when a certain URI is requested. The handlers are invoked
     * before any windows URIs are processed and if a DownloadStream is returned
     * it is sent to the client.
     * 
     * @param stream
     *                the download stream.
     * 
     * @param request
     *                the HTTP request instance.
     * @param response
     *                the HTTP response to write to.
     * 
     * @see com.itmill.toolkit.terminal.URIHandler
     */
    private void handleDownload(DownloadStream stream,
            HttpServletRequest request, HttpServletResponse response) {

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

            int bufferSize = stream.getBufferSize();
            if (bufferSize <= 0 || bufferSize > MAX_BUFFER_SIZE) {
                bufferSize = DEFAULT_BUFFER_SIZE;
            }
            final byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;

            try {
                final OutputStream out = response.getOutputStream();

                while ((bytesRead = data.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                    out.flush();
                }
                out.close();
            } catch (final IOException ignored) {
                System.err
                        .println("Warning: ApplicationServlet.handleDownload()"
                                + " threw IOException.");
            }

        }

    }

    /**
     * Handles theme resource file requests. Resources supplied with the themes
     * are provided by the WebAdapterServlet.
     * 
     * @param request
     *                the HTTP request.
     * @param response
     *                the HTTP response.
     * @return boolean <code>true</code> if the request was handled and
     *         further processing should be suppressed, <code>false</code>
     *         otherwise.
     * @throws ServletException
     *                 if an exception has occurred that interferes with the
     *                 servlet's normal operation.
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
            System.err.println("Resource transfer failed:  "
                    + request.getRequestURI() + ". (" + e.getMessage() + ")");
        }

        return true;
    }

    /**
     * Gets the current application URL from request.
     * 
     * @param request
     *                the HTTP request.
     * @throws MalformedURLException
     *                 if the application is denied access to the persistent
     *                 data store represented by the given URL.
     */
    private URL getApplicationUrl(HttpServletRequest request)
            throws MalformedURLException {

        URL applicationUrl;
        try {
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
                servletPath = request.getContextPath()
                        + request.getServletPath();
            }
            if (servletPath.length() == 0
                    || servletPath.charAt(servletPath.length() - 1) != '/') {
                servletPath = servletPath + "/";
            }
            applicationUrl = new URL(reqURL, servletPath);
        } catch (final MalformedURLException e) {
            System.err.println("Error constructing application url "
                    + request.getRequestURI() + " (" + e + ")");
            throw e;
        }

        return applicationUrl;
    }

    /**
     * Parses application runner URIs.
     * 
     * If request URL is e.g.
     * http://localhost:8080/itmill/run/com.itmill.toolkit.demo.Calc then
     * <ul>
     * <li>context=itmill</li>
     * <li>Runner servlet=run</li>
     * <li>Toolkit application=com.itmill.toolkit.demo.Calc</li>
     * </ul>
     * 
     * @param request
     * @return string array containing widgetset URI, application URI and
     *         context, runner, application classname
     */
    private String[] getApplicationRunnerURIs(HttpServletRequest request) {
        final String[] urlParts = request.getRequestURI().toString().split(
                "\\/");
        String context = null;
        String runner = null;
        String applicationClassname = null;
        if (urlParts[1].equals(request.getContextPath().replaceAll("\\/", ""))) {
            // class name comes after web context and runner application
            context = urlParts[1];
            runner = urlParts[2];
            applicationClassname = urlParts[3];
            return new String[] { "/" + context,
                    "/" + context + "/" + runner + "/" + applicationClassname,
                    context, runner, applicationClassname };
        } else {
            // no context
            context = "";
            runner = urlParts[1];
            applicationClassname = urlParts[2];
            return new String[] { "/",
                    "/" + runner + "/" + applicationClassname, context, runner,
                    applicationClassname };
        }
    }

    /**
     * Gets the existing application for given request. Looks for application
     * instance for given request based on the requested URL.
     * 
     * @param request
     *                the HTTP request.
     * @param response
     * @return Application instance, or null if the URL does not map to valid
     *         application.
     * @throws MalformedURLException
     *                 if the application is denied access to the persistent
     *                 data store represented by the given URL.
     * @throws SAXException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Application getExistingApplication(HttpServletRequest request,
            HttpServletResponse response) throws MalformedURLException,
            SAXException, IllegalAccessException, InstantiationException {

        // Ensures that the session is still valid
        final HttpSession session = request.getSession(true);

        // Gets application list for the session.
        final Collection applications = WebApplicationContext
                .getApplicationContext(session).getApplications();

        // Search for the application (using the application URI) from the list
        for (final Iterator i = applications.iterator(); i.hasNext();) {
            final Application a = (Application) i.next();
            final String aPath = a.getURL().getPath();
            String servletPath = "";
            if (isApplicationRunnerServlet) {
                final String[] URIparts = getApplicationRunnerURIs(request);
                servletPath = URIparts[1];
            } else {
                servletPath = request.getContextPath()
                        + request.getServletPath();
                if (servletPath.length() < aPath.length()) {
                    servletPath += "/";
                }
            }
            if (servletPath.equals(aPath)) {
                // Found a running application
                if (a.isRunning()) {
                    return a;
                }
                // Application has stopped, so remove it before creating a new
                // application
                WebApplicationContext.getApplicationContext(session)
                        .removeApplication(a);
                break;
            }
        }

        // Existing application not found
        return null;
    }

    /**
     * Creates new application for given request.
     * 
     * @param request
     *                the HTTP request.
     * @param response
     * @return Application instance, or null if the URL does not map to valid
     *         application.
     * @throws MalformedURLException
     *                 if the application is denied access to the persistent
     *                 data store represented by the given URL.
     * @throws SAXException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Application getNewApplication(HttpServletRequest request,
            HttpServletResponse response) throws MalformedURLException,
            SAXException, IllegalAccessException, InstantiationException {

        // Create application
        final WebApplicationContext context = WebApplicationContext
                .getApplicationContext(request.getSession());
        final URL applicationUrl;

        if (isApplicationRunnerServlet) {
            final String[] URIparts = getApplicationRunnerURIs(request);
            final String applicationClassname = URIparts[4];
            applicationUrl = new URL(getApplicationUrl(request).toString()
                    + applicationClassname);
            try {
                applicationClass = classLoader.loadClass(applicationClassname);
            } catch (final ClassNotFoundException e) {
                throw new InstantiationException(
                        "Failed to load application class: "
                                + applicationClassname);
            }
        } else {
            applicationUrl = getApplicationUrl(request);
        }

        // Creates new application and start it
        try {
            final Application application = (Application) applicationClass
                    .newInstance();
            context.addApplication(application);

            // Sets initial locale from the request
            application.setLocale(request.getLocale());

            // Starts application
            application.start(applicationUrl, applicationProperties, context);

            return application;

        } catch (final IllegalAccessException e) {
            System.err.println("Illegal access to application class "
                    + applicationClass.getName());
            throw e;
        } catch (final InstantiationException e) {
            System.err.println("Failed to instantiate application class: "
                    + applicationClass.getName());
            throw e;
        }
    }

    /**
     * Ends the application.
     * 
     * @param request
     *                the HTTP request.
     * @param response
     *                the HTTP response to write to.
     * @param application
     *                the application to end.
     * @throws IOException
     *                 if the writing failed due to input/output error.
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
     *                the HTTP Request.
     * @param application
     *                the Application to query for window.
     * @return Window matching the given URI or null if not found.
     * @throws ServletException
     *                 if an exception has occurred that interferes with the
     *                 servlet's normal operation.
     */
    private Window getApplicationWindow(HttpServletRequest request,
            Application application) throws ServletException {

        Window window = null;

        // Finds the window where the request is handled
        String path = request.getPathInfo();

        // Main window as the URI is empty
        if (path == null || path.length() == 0 || path.equals("/")) {
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

    /**
     * Gets relative location of a theme resource.
     * 
     * @param theme
     *                the Theme name.
     * @param resource
     *                the Theme resource.
     * @return External URI specifying the resource
     */
    public String getResourceLocation(String theme, ThemeResource resource) {

        if (resourcePath == null) {
            return resource.getResourceId();
        }
        return resourcePath + theme + "/" + resource.getResourceId();
    }

    /**
     * Checks if web adapter is in debug mode. Extra output is generated to log
     * when debug mode is enabled.
     * 
     * @param parameters
     * @return <code>true</code> if the web adapter is in debug mode.
     *         otherwise <code>false</code>.
     */
    public boolean isDebugMode(Map parameters) {
        if (parameters != null) {
            final Object[] debug = (Object[]) parameters.get("debug");
            if (debug != null && !"false".equals(debug[0].toString())
                    && !"false".equals(debugMode)) {
                return true;
            }
        }
        return "true".equals(debugMode);
    }

    /**
     * Implementation of ParameterHandler.ErrorEvent interface.
     */
    public class ParameterHandlerErrorImpl implements
            ParameterHandler.ErrorEvent {

        private ParameterHandler owner;

        private Throwable throwable;

        /**
         * Gets the contained throwable.
         * 
         * @see com.itmill.toolkit.terminal.Terminal.ErrorEvent#getThrowable()
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Gets the source ParameterHandler.
         * 
         * @see com.itmill.toolkit.terminal.ParameterHandler.ErrorEvent#getParameterHandler()
         */
        public ParameterHandler getParameterHandler() {
            return owner;
        }

    }

    /**
     * Implementation of URIHandler.ErrorEvent interface.
     */
    public class URIHandlerErrorImpl implements URIHandler.ErrorEvent {

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
         * @see com.itmill.toolkit.terminal.Terminal.ErrorEvent#getThrowable()
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Gets the source URIHandler.
         * 
         * @see com.itmill.toolkit.terminal.URIHandler.ErrorEvent#getURIHandler()
         */
        public URIHandler getURIHandler() {
            return owner;
        }
    }

    /**
     * Gets communication manager for an application.
     * 
     * If this application has not been running before, new manager is created.
     * 
     * @param application
     * @return CommunicationManager
     */
    private CommunicationManager getApplicationManager(Application application) {
        CommunicationManager mgr = (CommunicationManager) applicationToAjaxAppMgrMap
                .get(application);

        if (mgr == null) {
            // Creates new manager
            mgr = new CommunicationManager(application);
            applicationToAjaxAppMgrMap.put(application, mgr);
        }
        return mgr;
    }

    /**
     * Gets resource path using different implementations. Required to
     * supporting different servlet container implementations (application
     * servers).
     * 
     * @param servletContext
     * @param path
     *                the resource path.
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
                e.printStackTrace();
            }
        }
        return resultPath;
    }

}