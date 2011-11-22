package com.vaadin.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;

import com.vaadin.Application;
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
        Root root = application.getRoot(request);

        if (root == null) {
            writeError(response, null);
            return true;
        }

        writeAjaxPage(request, response, root);

        return true;
    }

    protected final void writeAjaxPage(WrappedRequest request,
            WrappedResponse response, Root root) throws IOException {
        Application application = root.getApplication();
        final BufferedWriter page = new BufferedWriter(new OutputStreamWriter(
                response.getOutputStream(), "UTF-8"));

        String title = ((root.getCaption() == null) ? "Vaadin 6" : root
                .getCaption());

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

        writeAjaxPageHtmlVaadinScripts(themeName, page, appUrl, themeUri,
                appId, request, root);

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
     * @param themeName
     * @param page
     * @param appUrl
     * @param themeUri
     * @param appId
     * @param request
     * @param rootId
     * @throws ServletException
     * @throws IOException
     */
    protected void writeAjaxPageHtmlVaadinScripts(String themeName,
            final BufferedWriter page, String appUrl, String themeUri,
            String appId, WrappedRequest request, Root root) throws IOException {

        Application application = root.getApplication();

        String widgetset = application.getWidgetsetForRoot(root);
        if (widgetset == null) {
            widgetset = getApplicationOrSystemProperty(request,
                    AbstractApplicationServlet.PARAMETER_WIDGETSET,
                    AbstractApplicationServlet.DEFAULT_WIDGETSET);
        }
        String widgetsetBasePath = request.getStaticFileLocation();

        widgetset = AbstractApplicationServlet.stripSpecialChars(widgetset);

        final String widgetsetFilePath = widgetsetBasePath + "/"
                + AbstractApplicationServlet.WIDGETSET_DIRECTORY_PATH
                + widgetset + "/" + widgetset + ".nocache.js?"
                + System.currentTimeMillis();

        // Get system messages
        Application.SystemMessages systemMessages = AbstractApplicationServlet
                .getSystemMessages(application.getClass());

        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");
        page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n "
                + "if(!vaadin) { var vaadin = {}} \n"
                + "vaadin.vaadinConfigurations = {};\n"
                + "if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }\n");
        if (!application.isProductionMode()) {
            page.write("vaadin.debug = true;\n");
        }
        page.write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
                + "style=\"position:absolute;width:0;height:0;border:0;overflow:"
                + "hidden;\" src=\"javascript:false\"></iframe>');\n");
        page.write("document.write(\"<script language='javascript' src='"
                + widgetsetFilePath + "'><\\/script>\");\n}\n");

        page.write("vaadin.vaadinConfigurations[\"" + appId + "\"] = {");
        page.write("appUri:'" + appUrl + "', ");

        page.write(ApplicationConnection.ROOT_ID_PARAMETER + ": "
                + root.getRootId() + ", ");

        if (isStandalone()) {
            page.write("standalone: true, ");
        }
        page.write("themeUri:");
        page.write(themeUri != null ? "\"" + themeUri + "\"" : "null");
        page.write(", versionInfo : {vaadinVersion:\"");
        page.write(AbstractApplicationServlet.VERSION);
        page.write("\",applicationVersion:\"");
        page.write(JsonPaintTarget.escapeJSON(application.getVersion()));
        page.write("\"}");
        if (systemMessages != null) {
            // Write the CommunicationError -message to client
            String caption = systemMessages.getCommunicationErrorCaption();
            if (caption != null) {
                caption = "\"" + JsonPaintTarget.escapeJSON(caption) + "\"";
            }
            String message = systemMessages.getCommunicationErrorMessage();
            if (message != null) {
                message = "\"" + JsonPaintTarget.escapeJSON(message) + "\"";
            }
            String url = systemMessages.getCommunicationErrorURL();
            if (url != null) {
                url = "\"" + JsonPaintTarget.escapeJSON(url) + "\"";
            }

            page.write(",\"comErrMsg\": {" + "\"caption\":" + caption + ","
                    + "\"message\" : " + message + "," + "\"url\" : " + url
                    + "}");

            // Write the AuthenticationError -message to client
            caption = systemMessages.getAuthenticationErrorCaption();
            if (caption != null) {
                caption = "\"" + JsonPaintTarget.escapeJSON(caption) + "\"";
            }
            message = systemMessages.getAuthenticationErrorMessage();
            if (message != null) {
                message = "\"" + JsonPaintTarget.escapeJSON(message) + "\"";
            }
            url = systemMessages.getAuthenticationErrorURL();
            if (url != null) {
                url = "\"" + JsonPaintTarget.escapeJSON(url) + "\"";
            }

            page.write(",\"authErrMsg\": {" + "\"caption\":" + caption + ","
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
            page.write("var stylesheet = document.createElement('link');\n");
            page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
            page.write("stylesheet.setAttribute('type', 'text/css');\n");
            page.write("stylesheet.setAttribute('href', '" + themeUri
                    + "/styles.css');\n");
            page.write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
            page.write("vaadin.themesLoaded['" + themeName + "'] = true;\n}\n");
            page.write("//]]>\n</script>\n");
        }

        // Warn if the widgetset has not been loaded after 15 seconds on
        // inactivity
        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");
        page.write("setTimeout('if (typeof " + widgetset.replace('.', '_')
                + " == \"undefined\") {alert(\"Failed to load the widgetset: "
                + widgetsetFilePath + "\")};',15000);\n" + "//]]>\n</script>\n");
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
        page.write("<link rel=\"shortcut icon\" type=\"image/vnd.microsoft.icon\" href=\""
                + themeUri + "/favicon.ico\" />");
        page.write("<link rel=\"icon\" type=\"image/vnd.microsoft.icon\" href=\""
                + themeUri + "/favicon.ico\" />");

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
    private String getThemeUri(String themeName, WrappedRequest request) {
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
    private String getThemeForRoot(WrappedRequest request, Root root) {
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
        response.setStatus(500);
        response.getWriter().println("Error");
    }

}
