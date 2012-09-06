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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;

import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.Version;
import com.vaadin.ui.UI;

public abstract class BootstrapHandler implements RequestHandler {

    protected class BootstrapContext implements Serializable {

        private final WrappedResponse response;
        private final BootstrapFragmentResponse bootstrapResponse;

        private String widgetsetName;
        private String themeName;
        private String appId;

        public BootstrapContext(WrappedResponse response,
                BootstrapFragmentResponse bootstrapResponse) {
            this.response = response;
            this.bootstrapResponse = bootstrapResponse;
        }

        public WrappedResponse getResponse() {
            return response;
        }

        public WrappedRequest getRequest() {
            return bootstrapResponse.getRequest();
        }

        public VaadinSession getVaadinSession() {
            return bootstrapResponse.getVaadinSession();
        }

        public Class<? extends UI> getUIClass() {
            return bootstrapResponse.getUiClass();
        }

        public String getWidgetsetName() {
            if (widgetsetName == null) {
                widgetsetName = getWidgetsetForUI(this);
            }
            return widgetsetName;
        }

        public String getThemeName() {
            if (themeName == null) {
                themeName = findAndEscapeThemeName(this);
            }
            return themeName;
        }

        public String getAppId() {
            if (appId == null) {
                appId = getApplicationId(this);
            }
            return appId;
        }

        public BootstrapFragmentResponse getBootstrapResponse() {
            return bootstrapResponse;
        }

    }

    @Override
    public boolean handleRequest(VaadinSession session,
            WrappedRequest request, WrappedResponse response)
            throws IOException {

        try {
            Class<? extends UI> uiClass = session.getUIClass(request);

            BootstrapContext context = createContext(request, response,
                    session, uiClass);
            setupMainDiv(context);

            BootstrapFragmentResponse fragmentResponse = context
                    .getBootstrapResponse();
            session.modifyBootstrapResponse(fragmentResponse);

            String html = getBootstrapHtml(context);

            writeBootstrapPage(response, html);
        } catch (JSONException e) {
            writeError(response, e);
        }

        return true;
    }

    private String getBootstrapHtml(BootstrapContext context) {
        WrappedRequest request = context.getRequest();
        WrappedResponse response = context.getResponse();
        VaadinService vaadinService = request.getVaadinService();

        BootstrapFragmentResponse fragmentResponse = context
                .getBootstrapResponse();

        if (vaadinService.isStandalone(request)) {
            Map<String, Object> headers = new LinkedHashMap<String, Object>();
            Document document = Document.createShell("");
            BootstrapPageResponse pageResponse = new BootstrapPageResponse(
                    this, request, context.getVaadinSession(),
                    context.getUIClass(), document, headers);
            List<Node> fragmentNodes = fragmentResponse.getFragmentNodes();
            Element body = document.body();
            for (Node node : fragmentNodes) {
                body.appendChild(node);
            }

            setupStandaloneDocument(context, pageResponse);
            context.getVaadinSession().modifyBootstrapResponse(pageResponse);

            sendBootstrapHeaders(response, headers);

            return document.outerHtml();
        } else {
            StringBuilder sb = new StringBuilder();
            for (Node node : fragmentResponse.getFragmentNodes()) {
                if (sb.length() != 0) {
                    sb.append('\n');
                }
                sb.append(node.outerHtml());
            }

            return sb.toString();
        }
    }

    private void sendBootstrapHeaders(WrappedResponse response,
            Map<String, Object> headers) {
        Set<Entry<String, Object>> entrySet = headers.entrySet();
        for (Entry<String, Object> header : entrySet) {
            Object value = header.getValue();
            if (value instanceof String) {
                response.setHeader(header.getKey(), (String) value);
            } else if (value instanceof Long) {
                response.setDateHeader(header.getKey(),
                        ((Long) value).longValue());
            } else {
                throw new RuntimeException("Unsupported header value: " + value);
            }
        }
    }

    private void writeBootstrapPage(WrappedResponse response, String html)
            throws IOException {
        response.setContentType("text/html");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                response.getOutputStream(), "UTF-8"));
        writer.append(html);
        writer.close();
    }

    private void setupStandaloneDocument(BootstrapContext context,
            BootstrapPageResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Document document = response.getDocument();

        DocumentType doctype = new DocumentType("html",
                "-//W3C//DTD XHTML 1.0 Transitional//EN",
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd",
                document.baseUri());
        document.child(0).before(doctype);
        document.body().parent().attr("xmlns", "http://www.w3.org/1999/xhtml");

        Element head = document.head();
        head.appendElement("meta").attr("http-equiv", "Content-Type")
                .attr("content", "text/html; charset=utf-8");

        // Chrome frame in all versions of IE (only if Chrome frame is
        // installed)
        head.appendElement("meta").attr("http-equiv", "X-UA-Compatible")
                .attr("content", "chrome=1");

        String title = context.getVaadinSession()
                .getUiProvider(context.getRequest(), context.getUIClass())
                .getPageTitleForUI(context.getRequest(), context.getUIClass());
        if (title != null) {
            head.appendElement("title").appendText(title);
        }

        head.appendElement("style").attr("type", "text/css")
                .appendText("html, body {height:100%;margin:0;}");

        // Add favicon links
        String themeName = context.getThemeName();
        if (themeName != null) {
            String themeUri = getThemeUri(context, themeName);
            head.appendElement("link").attr("rel", "shortcut icon")
                    .attr("type", "image/vnd.microsoft.icon")
                    .attr("href", themeUri + "/favicon.ico");
            head.appendElement("link").attr("rel", "icon")
                    .attr("type", "image/vnd.microsoft.icon")
                    .attr("href", themeUri + "/favicon.ico");
        }

        Element body = document.body();
        body.attr("scroll", "auto");
        body.addClass(ApplicationConstants.GENERATED_BODY_CLASSNAME);
    }

    private BootstrapContext createContext(WrappedRequest request,
            WrappedResponse response, VaadinSession application,
            Class<? extends UI> uiClass) {
        BootstrapContext context = new BootstrapContext(response,
                new BootstrapFragmentResponse(this, request, application,
                        uiClass, new ArrayList<Node>()));
        return context;
    }

    protected String getMainDivStyle(BootstrapContext context) {
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
    protected abstract String getApplicationId(BootstrapContext context);

    public String getWidgetsetForUI(BootstrapContext context) {
        WrappedRequest request = context.getRequest();

        String widgetset = context.getVaadinSession()
                .getUiProvider(context.getRequest(), context.getUIClass())
                .getWidgetsetForUI(context.getRequest(), context.getUIClass());
        if (widgetset == null) {
            widgetset = request.getVaadinService().getConfiguredWidgetset(
                    request);
        }

        widgetset = VaadinServlet.stripSpecialChars(widgetset);
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
     * @throws JSONException
     */
    private void setupMainDiv(BootstrapContext context) throws IOException,
            JSONException {
        String style = getMainDivStyle(context);

        /*- Add classnames;
         *      .v-app
         *      .v-app-loading
         *      .v-app-<simpleName for app class>
         *- Additionally added from javascript:
         *      .v-theme-<themeName, remove non-alphanum> 
         */

        String appClass = "v-app-"
                + context.getVaadinSession().getClass().getSimpleName();

        String classNames = "v-app " + appClass;
        List<Node> fragmentNodes = context.getBootstrapResponse()
                .getFragmentNodes();

        Element mainDiv = new Element(Tag.valueOf("div"), "");
        mainDiv.attr("id", context.getAppId());
        mainDiv.addClass(classNames);
        if (style != null && style.length() != 0) {
            mainDiv.attr("style", style);
        }
        mainDiv.appendElement("div").addClass("v-app-loading");
        mainDiv.appendElement("noscript")
                .append("You have to enable javascript in your browser to use an application built with Vaadin.");
        fragmentNodes.add(mainDiv);

        WrappedRequest request = context.getRequest();

        VaadinService vaadinService = request.getVaadinService();
        String staticFileLocation = vaadinService
                .getStaticFileLocation(request);

        fragmentNodes
                .add(new Element(Tag.valueOf("iframe"), "")
                        .attr("tabIndex", "-1")
                        .attr("id", "__gwt_historyFrame")
                        .attr("style",
                                "position:absolute;width:0;height:0;border:0;overflow:hidden")
                        .attr("src", "javascript:false"));

        String bootstrapLocation = staticFileLocation
                + "/VAADIN/vaadinBootstrap.js";
        fragmentNodes.add(new Element(Tag.valueOf("script"), "").attr("type",
                "text/javascript").attr("src", bootstrapLocation));
        Element mainScriptTag = new Element(Tag.valueOf("script"), "").attr(
                "type", "text/javascript");

        StringBuilder builder = new StringBuilder();
        builder.append("//<![CDATA[\n");
        builder.append("if (!window.vaadin) alert("
                + JSONObject.quote("Failed to load the bootstrap javascript: "
                        + bootstrapLocation) + ");\n");

        appendMainScriptTagContents(context, builder);

        builder.append("//]]>");
        mainScriptTag.appendChild(new DataNode(builder.toString(),
                mainScriptTag.baseUri()));
        fragmentNodes.add(mainScriptTag);

    }

    protected void appendMainScriptTagContents(BootstrapContext context,
            StringBuilder builder) throws JSONException, IOException {
        JSONObject defaults = getDefaultParameters(context);
        JSONObject appConfig = getApplicationParameters(context);

        boolean isDebug = !context.getVaadinSession().getConfiguration()
                .isProductionMode();

        builder.append("vaadin.setDefaults(");
        appendJsonObject(builder, defaults, isDebug);
        builder.append(");\n");

        builder.append("vaadin.initApplication(\"");
        builder.append(context.getAppId());
        builder.append("\",");
        appendJsonObject(builder, appConfig, isDebug);
        builder.append(");\n");
    }

    private static void appendJsonObject(StringBuilder builder,
            JSONObject jsonObject, boolean isDebug) throws JSONException {
        if (isDebug) {
            builder.append(jsonObject.toString(4));
        } else {
            builder.append(jsonObject.toString());
        }
    }

    protected JSONObject getApplicationParameters(BootstrapContext context)
            throws JSONException, PaintException {
        JSONObject appConfig = new JSONObject();

        if (context.getThemeName() != null) {
            appConfig.put("themeUri",
                    getThemeUri(context, context.getThemeName()));
        }

        JSONObject versionInfo = new JSONObject();
        versionInfo.put("vaadinVersion", Version.getFullVersion());
        appConfig.put("versionInfo", versionInfo);

        appConfig.put("widgetset", context.getWidgetsetName());

        appConfig.put("initialPath", context.getRequest().getRequestPathInfo());

        Map<String, String[]> parameterMap = context.getRequest()
                .getParameterMap();
        appConfig.put("initialParams", parameterMap);

        return appConfig;
    }

    protected JSONObject getDefaultParameters(BootstrapContext context)
            throws JSONException {
        JSONObject defaults = new JSONObject();

        WrappedRequest request = context.getRequest();
        VaadinSession session = context.getVaadinSession();
        VaadinService vaadinService = request.getVaadinService();

        // Get system messages
        SystemMessages systemMessages = vaadinService.getSystemMessages();
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

        String staticFileLocation = vaadinService
                .getStaticFileLocation(request);
        String widgetsetBase = staticFileLocation + "/"
                + VaadinServlet.WIDGETSET_DIRECTORY_PATH;
        defaults.put("widgetsetBase", widgetsetBase);

        if (!session.getConfiguration().isProductionMode()) {
            defaults.put("debug", true);
        }

        if (vaadinService.isStandalone(request)) {
            defaults.put("standalone", true);
        }

        defaults.put("heartbeatInterval", vaadinService
                .getDeploymentConfiguration().getHeartbeatInterval());

        defaults.put("appUri", getAppUri(context));

        return defaults;
    }

    protected abstract String getAppUri(BootstrapContext context);

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
    public String getThemeUri(BootstrapContext context, String themeName) {
        WrappedRequest request = context.getRequest();
        final String staticFilePath = request.getVaadinService()
                .getStaticFileLocation(request);
        return staticFilePath + "/" + VaadinServlet.THEME_DIRECTORY_PATH
                + themeName;
    }

    /**
     * Override if required
     * 
     * @param context
     * @return
     */
    public String getThemeName(BootstrapContext context) {
        return context.getVaadinSession()
                .getUiProvider(context.getRequest(), context.getUIClass())
                .getThemeForUI(context.getRequest(), context.getUIClass());
    }

    /**
     * Don not override.
     * 
     * @param context
     * @return
     */
    public String findAndEscapeThemeName(BootstrapContext context) {
        String themeName = getThemeName(context);
        if (themeName == null) {
            WrappedRequest request = context.getRequest();
            themeName = request.getVaadinService().getConfiguredTheme(request);
        }

        // XSS preventation, theme names shouldn't contain special chars anyway.
        // The servlet denies them via url parameter.
        themeName = VaadinServlet.stripSpecialChars(themeName);

        return themeName;
    }

    protected void writeError(WrappedResponse response, Throwable e)
            throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getLocalizedMessage());
    }
}
