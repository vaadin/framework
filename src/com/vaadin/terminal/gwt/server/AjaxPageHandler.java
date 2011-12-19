/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.RootRequiresMoreInformation;
import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.ui.Root;

public abstract class AjaxPageHandler implements RequestHandler {

    protected class AjaxPageContext implements Serializable {
        private final WrappedResponse response;
        private final WrappedRequest request;
        private final Application application;
        private final int rootId;

        private Writer writer;
        private Root root;
        private String widgetsetName;
        private String themeName;
        private String appId;

        private boolean rootFetched = false;

        public AjaxPageContext(WrappedResponse response,
                WrappedRequest request, Application application, int rootId) {
            this.response = response;
            this.request = request;
            this.application = application;
            this.rootId = rootId;
        }

        public WrappedResponse getResponse() {
            return response;
        }

        public WrappedRequest getRequest() {
            return request;
        }

        public Application getApplication() {
            return application;
        }

        public Writer getWriter() throws IOException {
            if (writer == null) {
                response.setContentType("text/html");
                writer = new BufferedWriter(new OutputStreamWriter(
                        response.getOutputStream(), "UTF-8"));
            }
            return writer;
        }

        public int getRootId() {
            return rootId;
        }

        public Root getRoot() {
            if (!rootFetched) {
                root = Root.getCurrentRoot();
                rootFetched = true;
            }
            return root;
        }

        public String getWidgetsetName() {
            if (widgetsetName == null) {
                Root root = getRoot();
                if (root != null) {
                    widgetsetName = getWidgetsetForRoot(this);
                }
            }
            return widgetsetName;
        }

        public String getThemeName() {
            if (themeName == null) {
                Root root = getRoot();
                if (root != null) {
                    themeName = findAndEscapeThemeName(this);
                }
            }
            return themeName;
        }

        public String getAppId() {
            if (appId == null) {
                appId = getApplicationId(this);
            }
            return appId;
        }

    }

    public boolean handleRequest(Application application,
            WrappedRequest request, WrappedResponse response)
            throws IOException {

        // TODO Should all urls be handled here?
        int rootId;
        try {
            Root root = application.getRootForRequest(request);
            if (root == null) {
                writeError(response, new Throwable("No Root found"));
                return true;
            }

            rootId = root.getRootId();
        } catch (RootRequiresMoreInformation e) {
            rootId = application.registerPendingRoot(request);
        }

        try {
            writeAjaxPage(request, response, application, rootId);
        } catch (JSONException e) {
            writeError(response, e);
        }

        return true;
    }

    protected final void writeAjaxPage(WrappedRequest request,
            WrappedResponse response, Application application, int rootId)
            throws IOException, JSONException {

        AjaxPageContext context = createContext(request, response, application,
                rootId);

        DeploymentConfiguration deploymentConfiguration = request
                .getDeploymentConfiguration();

        boolean standalone = deploymentConfiguration.isStandalone(request);
        if (standalone) {
            setAjaxPageHeaders(context);
            writeAjaxPageHtmlHeadStart(context);
            writeAjaxPageHtmlHeader(context);
            writeAjaxPageHtmlBodyStart(context);
        }

        // TODO include initial UIDL in the scripts?
        writeAjaxPageHtmlVaadinScripts(context);

        writeAjaxPageHtmlMainDiv(context);

        Writer page = context.getWriter();
        if (standalone) {
            page.write("</body>\n</html>\n");
        }

        page.close();
    }

    public AjaxPageContext createContext(WrappedRequest request,
            WrappedResponse response, Application application, int rootId) {
        AjaxPageContext context = new AjaxPageContext(response, request,
                application, rootId);
        return context;
    }

    protected String getMainDivStyle(AjaxPageContext context) {
        return null;
    }

    /**
     * Creates and returns a unique ID for the DIV where the application is to
     * be rendered.
     * 
     * @param context
     * 
     * @return the id to use in the DOM
     */
    protected abstract String getApplicationId(AjaxPageContext context);

    public String getWidgetsetForRoot(AjaxPageContext context) {
        Root root = context.getRoot();
        WrappedRequest request = context.getRequest();

        String widgetset = root.getApplication().getWidgetsetForRoot(root);
        if (widgetset == null) {
            widgetset = request.getDeploymentConfiguration()
                    .getConfiguredWidgetset(request);
        }

        widgetset = AbstractApplicationServlet.stripSpecialChars(widgetset);
        return widgetset;
    }

    /**
     * Method to write the div element into which that actual Vaadin application
     * is rendered.
     * <p>
     * Override this method if you want to add some custom html around around
     * the div element into which the actual Vaadin application will be
     * rendered.
     * 
     * @param context
     * 
     * @throws IOException
     */
    protected void writeAjaxPageHtmlMainDiv(AjaxPageContext context)
            throws IOException {
        Writer page = context.getWriter();
        String style = getMainDivStyle(context);

        /*- Add classnames;
         *      .v-app
         *      .v-app-loading
         *      .v-app-<simpleName for app class>
         *- Additionally added from javascript:
         *      .v-theme-<themeName, remove non-alphanum> 
         */

        String appClass = "v-app-"
                + getApplicationCSSClassName(context.getApplication());

        String classNames = "v-app " + appClass;

        if (style != null && style.length() != 0) {
            style = " style=\"" + style + "\"";
        }
        page.write("<div id=\"" + context.getAppId() + "\" class=\""
                + classNames + "\"" + style + ">");
        page.write("<div class=\"v-app-loading\"></div>");
        page.write("</div>\n");
        page.write("<noscript>" + getNoScriptMessage() + "</noscript>");
    }

    /**
     * Returns a message printed for browsers without scripting support or if
     * browsers scripting support is disabled.
     */
    protected String getNoScriptMessage() {
        return "You have to enable javascript in your browser to use an application built with Vaadin.";
    }

    /**
     * Returns the application class identifier for use in the application CSS
     * class name in the root DIV. The application CSS class name is of form
     * "v-app-"+getApplicationCSSClassName().
     * 
     * This method should normally not be overridden.
     * 
     * @return The CSS class name to use in combination with "v-app-".
     */
    protected String getApplicationCSSClassName(Application application) {
        return application.getClass().getSimpleName();
    }

    /**
     * 
     * Method to open the body tag of the html kickstart page.
     * <p>
     * This method is responsible for closing the head tag and opening the body
     * tag.
     * <p>
     * Override this method if you want to add some custom html to the page.
     * 
     * @throws IOException
     */
    protected void writeAjaxPageHtmlBodyStart(AjaxPageContext context)
            throws IOException {
        Writer page = context.getWriter();
        page.write("\n</head>\n<body scroll=\"auto\" class=\""
                + ApplicationConnection.GENERATED_BODY_CLASSNAME + "\">\n");
    }

    /**
     * Method to write the script part of the page which loads needed Vaadin
     * scripts and themes.
     * <p>
     * Override this method if you want to add some custom html around scripts.
     * 
     * @param context
     * 
     * @throws IOException
     * @throws JSONException
     */
    protected void writeAjaxPageHtmlVaadinScripts(AjaxPageContext context)
            throws IOException, JSONException {
        WrappedRequest request = context.getRequest();
        Writer page = context.getWriter();

        DeploymentConfiguration deploymentConfiguration = request
                .getDeploymentConfiguration();
        String staticFileLocation = deploymentConfiguration
                .getStaticFileLocation(request);

        page.write("<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                + "style=\"position:absolute;width:0;height:0;border:0;overflow:"
                + "hidden;\" src=\"javascript:false\"></iframe>");

        page.write("<script type=\"text/javascript\" src=\"");
        page.write(staticFileLocation);
        page.write("/VAADIN/vaadinBootstrap.js\"></script>\n");

        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");

        writeMainScriptTagContents(context);
        page.write("//]]>\n</script>\n");
    }

    protected void writeMainScriptTagContents(AjaxPageContext context)
            throws JSONException, IOException {
        JSONObject defaults = getDefaultParameters(context);
        JSONObject appConfig = getApplicationParameters(context);

        boolean isDebug = !context.getApplication().isProductionMode();
        Writer page = context.getWriter();

        page.write("vaadin.setDefaults(");
        printJsonObject(page, defaults, isDebug);
        page.write(");\n");

        page.write("vaadin.initApplication(\"");
        page.write(context.getAppId());
        page.write("\",");
        printJsonObject(page, appConfig, isDebug);
        page.write(");\n");
    }

    private static void printJsonObject(Writer page, JSONObject jsonObject,
            boolean isDebug) throws IOException, JSONException {
        if (isDebug) {
            page.write(jsonObject.toString(4));
        } else {
            page.write(jsonObject.toString());
        }
    }

    protected JSONObject getApplicationParameters(AjaxPageContext context)
            throws JSONException, PaintException {
        Application application = context.getApplication();
        int rootId = context.getRootId();

        JSONObject appConfig = new JSONObject();

        appConfig.put(ApplicationConnection.ROOT_ID_PARAMETER, rootId);

        if (context.getThemeName() != null) {
            appConfig.put("themeUri",
                    getThemeUri(context, context.getThemeName()));
        }

        JSONObject versionInfo = new JSONObject();
        versionInfo.put("vaadinVersion", AbstractApplicationServlet.VERSION);
        versionInfo.put("applicationVersion", application.getVersion());
        appConfig.put("versionInfo", versionInfo);

        appConfig.put("widgetset", context.getWidgetsetName());

        if (application.isRootInitPending(rootId)) {
            appConfig.put("initPending", true);
        } else {
            // write the initial UIDL into the config
            appConfig.put("uidl",
                    getInitialUIDL(context.getRequest(), context.getRoot()));
        }

        return appConfig;
    }

    protected JSONObject getDefaultParameters(AjaxPageContext context)
            throws JSONException {
        JSONObject defaults = new JSONObject();

        WrappedRequest request = context.getRequest();
        Application application = context.getApplication();

        // Get system messages
        Application.SystemMessages systemMessages = AbstractApplicationServlet
                .getSystemMessages(application.getClass());
        if (systemMessages != null) {
            // Write the CommunicationError -message to client
            JSONObject comErrMsg = new JSONObject();
            comErrMsg.put("caption",
                    systemMessages.getCommunicationErrorCaption());
            comErrMsg.put("message",
                    systemMessages.getCommunicationErrorMessage());
            comErrMsg.put("url", systemMessages.getCommunicationErrorURL());

            defaults.put("comErrMsg", comErrMsg);

            JSONObject authErrMsg = new JSONObject();
            authErrMsg.put("caption",
                    systemMessages.getAuthenticationErrorCaption());
            authErrMsg.put("message",
                    systemMessages.getAuthenticationErrorMessage());
            authErrMsg.put("url", systemMessages.getAuthenticationErrorURL());

            defaults.put("authErrMsg", authErrMsg);
        }

        DeploymentConfiguration deploymentConfiguration = request
                .getDeploymentConfiguration();
        String staticFileLocation = deploymentConfiguration
                .getStaticFileLocation(request);
        String widgetsetBase = staticFileLocation + "/"
                + AbstractApplicationServlet.WIDGETSET_DIRECTORY_PATH;
        defaults.put("widgetsetBase", widgetsetBase);

        if (!application.isProductionMode()) {
            defaults.put("debug", true);
        }

        if (deploymentConfiguration.isStandalone(request)) {
            defaults.put("standalone", true);
        }

        defaults.put("appUri", getAppUri(context));

        return defaults;
    }

    protected abstract String getAppUri(AjaxPageContext context);

    /**
     * Method to write the contents of head element in html kickstart page.
     * <p>
     * Override this method if you want to add some custom html to the header of
     * the page.
     * 
     * @throws IOException
     */
    protected void writeAjaxPageHtmlHeader(AjaxPageContext context)
            throws IOException {
        Writer page = context.getWriter();
        String themeName = context.getThemeName();

        page.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");

        // Chrome frame in all versions of IE (only if Chrome frame is
        // installed)
        page.write("<meta http-equiv=\"X-UA-Compatible\" content=\"chrome=1\"/>\n");

        page.write("<style type=\"text/css\">"
                + "html, body {height:100%;margin:0;}</style>");

        // Add favicon links
        if (themeName != null) {
            String themeUri = getThemeUri(context, themeName);
            page.write("<link rel=\"shortcut icon\" type=\"image/vnd.microsoft.icon\" href=\""
                    + themeUri + "/favicon.ico\" />");
            page.write("<link rel=\"icon\" type=\"image/vnd.microsoft.icon\" href=\""
                    + themeUri + "/favicon.ico\" />");
        }

        Root root = context.getRoot();
        String title = ((root == null || root.getCaption() == null) ? "Vaadin "
                + AbstractApplicationServlet.VERSION_MAJOR : root.getCaption());

        page.write("<title>"
                + AbstractApplicationServlet.safeEscapeForHtml(title)
                + "</title>");
    }

    /**
     * Method to set http request headers for the Vaadin kickstart page.
     * <p>
     * Override this method if you need to customize http headers of the page.
     * 
     * @param context
     */
    protected void setAjaxPageHeaders(AjaxPageContext context) {
        WrappedResponse response = context.getResponse();

        // Window renders are not cacheable
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html; charset=UTF-8");
    }

    /**
     * Method to write the beginning of the html page.
     * <p>
     * This method is responsible for writing appropriate doc type declarations
     * and to open html and head tags.
     * <p>
     * Override this method if you want to add some custom html to the very
     * beginning of the page.
     * 
     * @param context
     * @throws IOException
     */
    protected void writeAjaxPageHtmlHeadStart(AjaxPageContext context)
            throws IOException {
        Writer page = context.getWriter();

        // write html header
        page.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD "
                + "XHTML 1.0 Transitional//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/"
                + "DTD/xhtml1-transitional.dtd\">\n");

        page.write("<html xmlns=\"http://www.w3.org/1999/xhtml\""
                + ">\n<head>\n");
    }

    /**
     * Get the URI for the application theme.
     * 
     * A portal-wide default theme is fetched from the portal shared resource
     * directory (if any), other themes from the portlet.
     * 
     * @param context
     * @param themeName
     * 
     * @return
     */
    public String getThemeUri(AjaxPageContext context, String themeName) {
        WrappedRequest request = context.getRequest();
        final String staticFilePath = request.getDeploymentConfiguration()
                .getStaticFileLocation(request);
        return staticFilePath + "/"
                + AbstractApplicationServlet.THEME_DIRECTORY_PATH + themeName;
    }

    /**
     * Override if required
     * 
     * @param context
     * @return
     */
    public String getThemeName(AjaxPageContext context) {
        return context.getApplication().getThemeForRoot(context.getRoot());
    }

    /**
     * Don not override.
     * 
     * @param context
     * @return
     */
    public String findAndEscapeThemeName(AjaxPageContext context) {
        String themeName = getThemeName(context);
        if (themeName == null) {
            WrappedRequest request = context.getRequest();
            themeName = request.getDeploymentConfiguration()
                    .getConfiguredTheme(request);
        }

        // XSS preventation, theme names shouldn't contain special chars anyway.
        // The servlet denies them via url parameter.
        themeName = AbstractApplicationServlet.stripSpecialChars(themeName);

        return themeName;
    }

    protected void writeError(WrappedResponse response, Throwable e)
            throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getLocalizedMessage());
    }

    /**
     * Gets the initial UIDL message to send to the client.
     * 
     * @param request
     *            the originating request
     * @param root
     *            the root for which the UIDL should be generated
     * @return a string with the initial UIDL message
     * @throws PaintException
     *             if an exception occurs while painting the components
     */
    protected abstract String getInitialUIDL(WrappedRequest request, Root root)
            throws PaintException;

}
