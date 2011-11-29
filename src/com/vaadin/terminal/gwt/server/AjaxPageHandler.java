/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.RootRequiresMoreInformation;
import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.ui.Root;

public abstract class AjaxPageHandler implements RequestHandler {

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
        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getOutputStream(), "UTF-8"));

        Root root = Root.getCurrentRoot();

        String title = ((root == null || root.getCaption() == null) ? "Vaadin "
                + AbstractApplicationServlet.VERSION_MAJOR : root.getCaption());

        /* Fetch relative url to application */
        // don't use server and port in uri. It may cause problems with some
        // virtual server configurations which lose the server name
        String appUrl = application.getURL().getPath();
        if (appUrl.endsWith("/")) {
            appUrl = appUrl.substring(0, appUrl.length() - 1);
        }

        String themeName = getThemeForRoot(request, root);

        String themeUri = getThemeUri(themeName, request);

        setAjaxPageHeaders(response);
        writeAjaxPageHtmlHeadStart(page, request);
        writeAjaxPageHtmlHeader(page, title, themeUri, request);
        writeAjaxPageHtmlBodyStart(page, request);

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

        String widgetset = getWidgetsetForRoot(request, root);

        // TODO include initial UIDL in the scripts?
        writeAjaxPageHtmlVaadinScripts(page, appUrl, themeUri, appId, request,
                application, rootId, widgetset);

        /*- Add classnames;
         *      .v-app
         *      .v-app-loading
         *      .v-app-<simpleName for app class>
         *      .v-theme-<themeName, remove non-alphanum>
         */

        String appClass = "v-app-" + getApplicationCSSClassName(application);

        String themeClass = "";
        if (themeName != null) {
            themeClass = "v-theme-" + themeName.replaceAll("[^a-zA-Z0-9]", "");
        } else {
            themeClass = "v-theme-"
                    + AbstractApplicationServlet.getDefaultTheme().replaceAll(
                            "[^a-zA-Z0-9]", "");
        }

        String classNames = "v-app " + themeClass + " " + appClass;

        writeAjaxPageHtmlMainDiv(page, appId, classNames, request);

        page.write("</body>\n</html>\n");

        page.close();
    }

    public String getWidgetsetForRoot(WrappedRequest request, Root root) {
        if (root == null) {
            // Defer widgetset selection
            return null;
        }

        String widgetset = root.getApplication().getWidgetsetForRoot(root);
        if (widgetset == null) {
            widgetset = getApplicationOrSystemProperty(request,
                    AbstractApplicationServlet.PARAMETER_WIDGETSET,
                    AbstractApplicationServlet.DEFAULT_WIDGETSET);
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
     * @param page
     * @param appId
     * @param classNames
     * @param request
     * @throws IOException
     */
    protected void writeAjaxPageHtmlMainDiv(final BufferedWriter page,
            String appId, String classNames, WrappedRequest request)
            throws IOException {
        page.write("<div id=\"" + appId + "\" class=\"" + classNames + "\">");
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
     * @param page
     * @param request
     * @throws IOException
     */
    protected void writeAjaxPageHtmlBodyStart(final BufferedWriter page,
            final WrappedRequest request) throws IOException {
        page.write("\n</head>\n<body scroll=\"auto\" class=\""
                + ApplicationConnection.GENERATED_BODY_CLASSNAME + "\">\n");
    }

    /**
     * Method to write the script part of the page which loads needed Vaadin
     * scripts and themes.
     * <p>
     * Override this method if you want to add some custom html around scripts.
     * 
     * @param page
     * @param appUrl
     * @param themeUri
     * @param appId
     * @param request
     * @param application
     * @param rootId
     * @throws IOException
     * @throws JSONException
     */
    protected void writeAjaxPageHtmlVaadinScripts(final BufferedWriter page,
            String appUrl, String themeUri, String appId,
            WrappedRequest request, Application application, int rootId,
            String widgetset) throws IOException, JSONException {

        String staticFileLocation = request.getStaticFileLocation();

        String widgetsetBase = staticFileLocation + "/"
                + AbstractApplicationServlet.WIDGETSET_DIRECTORY_PATH;

        // Get system messages
        Application.SystemMessages systemMessages = AbstractApplicationServlet
                .getSystemMessages(application.getClass());

        page.write("<script type=\"text/javascript\" src=\"");
        page.write(staticFileLocation);
        page.write("/VAADIN/vaadinBootstrap.js\"></script>\n");

        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");

        JSONObject defaults = new JSONObject();
        JSONObject appConfig = new JSONObject();

        boolean isDebug = !application.isProductionMode();
        if (isDebug) {
            defaults.put("debug", true);
        }

        page.write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                + "style=\"position:absolute;width:0;height:0;border:0;overflow:"
                + "hidden;\" src=\"javascript:false\"></iframe>');\n");

        defaults.put("appUri", appUrl);

        appConfig.put(ApplicationConnection.ROOT_ID_PARAMETER, rootId);

        if (isStandalone()) {
            defaults.put("standalone", true);
        }

        appConfig.put("themeUri", themeUri);

        JSONObject versionInfo = new JSONObject();
        versionInfo.put("vaadinVersion", AbstractApplicationServlet.VERSION);
        versionInfo.put("applicationVersion", application.getVersion());
        appConfig.put("versionInfo", versionInfo);

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

        defaults.put("widgetsetBase", widgetsetBase);

        appConfig.put("widgetset", widgetset);

        if (application.isRootInitPending(rootId)) {
            appConfig.put("initPending", true);
        }

        page.write("vaadin.setDefaults(");
        if (isDebug) {
            page.write(defaults.toString(4));
        } else {
            page.write(defaults.toString());
        }
        page.write(");\n");

        page.write("vaadin.initApplication(\"");
        page.write(appId);
        page.write("\",");
        if (isDebug) {
            page.write(appConfig.toString(4));
        } else {
            page.write(appConfig.toString());
        }
        page.write(");\n");
        page.write("//]]>\n</script>\n");
    }

    protected abstract String getApplicationOrSystemProperty(
            WrappedRequest request, String parameter, String defaultValue);

    /**
     * @return true if the served application is considered to be the only or
     *         main content of the host page. E.g. various embedding solutions
     *         should override this to false.
     */
    protected boolean isStandalone() {
        return true;
    }

    /**
     * Method to write the contents of head element in html kickstart page.
     * <p>
     * Override this method if you want to add some custom html to the header of
     * the page.
     * 
     * @param page
     * @param title
     * @param themeUri
     * @param request
     * @throws IOException
     */
    protected void writeAjaxPageHtmlHeader(final BufferedWriter page,
            String title, String themeUri, final WrappedRequest request)
            throws IOException {
        page.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n");

        // Chrome frame in all versions of IE (only if Chrome frame is
        // installed)
        page.write("<meta http-equiv=\"X-UA-Compatible\" content=\"chrome=1\"/>\n");

        page.write("<style type=\"text/css\">"
                + "html, body {height:100%;margin:0;}</style>");

        // Add favicon links
        if (themeUri != null) {
            page.write("<link rel=\"shortcut icon\" type=\"image/vnd.microsoft.icon\" href=\""
                    + themeUri + "/favicon.ico\" />");
            page.write("<link rel=\"icon\" type=\"image/vnd.microsoft.icon\" href=\""
                    + themeUri + "/favicon.ico\" />");
        }

        page.write("<title>"
                + AbstractApplicationServlet.safeEscapeForHtml(title)
                + "</title>");
    }

    /**
     * Method to set http request headers for the Vaadin kickstart page.
     * <p>
     * Override this method if you need to customize http headers of the page.
     * 
     * @param response
     */
    protected void setAjaxPageHeaders(WrappedResponse response) {
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
     * @param page
     * @param request
     * @throws IOException
     */
    protected void writeAjaxPageHtmlHeadStart(final BufferedWriter page,
            final WrappedRequest request) throws IOException {
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
     * @param themeName
     * @param request
     * @return
     */
    public String getThemeUri(String themeName, WrappedRequest request) {
        if (themeName == null) {
            return null;
        }
        final String staticFilePath = request.getStaticFileLocation();
        return staticFilePath + "/"
                + AbstractApplicationServlet.THEME_DIRECTORY_PATH + themeName;
    }

    /**
     * Returns the theme for given request/root
     * 
     * @param request
     * @param root
     * @return
     */
    public String getThemeForRoot(WrappedRequest request, Root root) {
        if (root == null) {
            return null;
        }
        // Finds theme name
        String themeName;

        if (request
                .getParameter(AbstractApplicationServlet.URL_PARAMETER_THEME) != null) {
            themeName = request
                    .getParameter(AbstractApplicationServlet.URL_PARAMETER_THEME);
        } else {
            themeName = root.getApplication().getThemeForRoot(root);
        }

        if (themeName == null) {
            // no explicit theme for root defined
            // using the default theme defined by Vaadin
            themeName = AbstractApplicationServlet.getDefaultTheme();
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

}
