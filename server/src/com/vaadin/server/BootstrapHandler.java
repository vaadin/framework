/*
 * Copyright 2000-2014 Vaadin Ltd.
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
import java.util.Locale;
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

import com.google.gwt.thirdparty.guava.common.net.UrlEscapers;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.ViewportGeneratorClass;
import com.vaadin.server.communication.AtmospherePushConnection;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.VaadinUriResolver;
import com.vaadin.shared.Version;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

import elemental.json.Json;
import elemental.json.JsonException;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;

/**
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 * 
 * @deprecated As of 7.0. Will likely change or be removed in a future version
 */
@Deprecated
public abstract class BootstrapHandler extends SynchronizedRequestHandler {

    /**
     * Parameter that is added to the UI init request if the session has already
     * been restarted when generating the bootstrap HTML and ?restartApplication
     * should thus be ignored when handling the UI init request.
     */
    public static final String IGNORE_RESTART_PARAM = "ignoreRestart";

    protected class BootstrapContext implements Serializable {

        private final VaadinResponse response;
        private final BootstrapFragmentResponse bootstrapResponse;

        private String widgetsetName;
        private String themeName;
        private String appId;
        private PushMode pushMode;
        private JsonObject applicationParameters;
        private VaadinUriResolver uriResolver;

        public BootstrapContext(VaadinResponse response,
                BootstrapFragmentResponse bootstrapResponse) {
            this.response = response;
            this.bootstrapResponse = bootstrapResponse;
        }

        public VaadinResponse getResponse() {
            return response;
        }

        public VaadinRequest getRequest() {
            return bootstrapResponse.getRequest();
        }

        public VaadinSession getSession() {
            return bootstrapResponse.getSession();
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

        public PushMode getPushMode() {
            if (pushMode == null) {
                UICreateEvent event = new UICreateEvent(getRequest(),
                        getUIClass());

                pushMode = getBootstrapResponse().getUIProvider().getPushMode(
                        event);
                if (pushMode == null) {
                    pushMode = getRequest().getService()
                            .getDeploymentConfiguration().getPushMode();
                }

                if (pushMode.isEnabled()
                        && !getRequest().getService().ensurePushAvailable()) {
                    /*
                     * Fall back if not supported (ensurePushAvailable will log
                     * information to the developer the first time this happens)
                     */
                    pushMode = PushMode.DISABLED;
                }
            }
            return pushMode;
        }

        public String getAppId() {
            if (appId == null) {
                appId = getRequest().getService().getMainDivId(getSession(),
                        getRequest(), getUIClass());
            }
            return appId;
        }

        public BootstrapFragmentResponse getBootstrapResponse() {
            return bootstrapResponse;
        }

        public JsonObject getApplicationParameters() {
            if (applicationParameters == null) {
                applicationParameters = BootstrapHandler.this
                        .getApplicationParameters(this);
            }

            return applicationParameters;
        }

        public VaadinUriResolver getUriResolver() {
            if (uriResolver == null) {
                uriResolver = new BootstrapUriResolver(this);
            }

            return uriResolver;
        }
    }

    private class BootstrapUriResolver extends VaadinUriResolver {
        private final BootstrapContext context;

        public BootstrapUriResolver(BootstrapContext bootstrapContext) {
            context = bootstrapContext;
        }

        @Override
        protected String getVaadinDirUrl() {
            return context.getApplicationParameters().getString(
                    ApplicationConstants.VAADIN_DIR_URL);
        }

        @Override
        protected String getThemeUri() {
            return getVaadinDirUrl() + "themes/" + context.getThemeName();
        }

        @Override
        protected String getServiceUrlParameterName() {
            return getConfigOrNull(ApplicationConstants.SERVICE_URL_PARAMETER_NAME);
        }

        @Override
        protected String getServiceUrl() {
            String serviceUrl = getConfigOrNull(ApplicationConstants.SERVICE_URL);
            if (serviceUrl == null) {
                return "./";
            } else if (!serviceUrl.endsWith("/")) {
                serviceUrl += "/";
            }
            return serviceUrl;
        }

        private String getConfigOrNull(String name) {
            JsonObject parameters = context.getApplicationParameters();
            if (parameters.hasKey(name)) {
                return parameters.getString(name);
            } else {
                return null;
            }
        }

        @Override
        protected String encodeQueryStringParameterValue(String queryString) {
            return UrlEscapers.urlFormParameterEscaper().escape(queryString);
        }
    }

    @Override
    protected boolean canHandleRequest(VaadinRequest request) {
        // We do not want to handle /APP requests here, instead let it fall
        // through and produce a 404
        return !ServletPortletHelper.isAppRequest(request);
    }

    @Override
    public boolean synchronizedHandleRequest(VaadinSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {
        try {
            List<UIProvider> uiProviders = session.getUIProviders();

            UIClassSelectionEvent classSelectionEvent = new UIClassSelectionEvent(
                    request);

            // Find UI provider and UI class
            Class<? extends UI> uiClass = null;
            UIProvider provider = null;
            for (UIProvider p : uiProviders) {
                uiClass = p.getUIClass(classSelectionEvent);
                // If we found something
                if (uiClass != null) {
                    provider = p;
                    break;
                }
            }

            if (provider == null) {
                // Can't generate bootstrap if no UI provider matches
                return false;
            }

            BootstrapContext context = new BootstrapContext(response,
                    new BootstrapFragmentResponse(this, request, session,
                            uiClass, new ArrayList<Node>(), provider));

            setupMainDiv(context);

            BootstrapFragmentResponse fragmentResponse = context
                    .getBootstrapResponse();
            session.modifyBootstrapResponse(fragmentResponse);

            String html = getBootstrapHtml(context);

            writeBootstrapPage(response, html);
        } catch (JsonException e) {
            writeError(response, e);
        }

        return true;
    }

    private String getBootstrapHtml(BootstrapContext context) {
        VaadinRequest request = context.getRequest();
        VaadinResponse response = context.getResponse();
        VaadinService vaadinService = request.getService();

        BootstrapFragmentResponse fragmentResponse = context
                .getBootstrapResponse();

        if (vaadinService.isStandalone(request)) {
            Map<String, Object> headers = new LinkedHashMap<String, Object>();
            Document document = Document.createShell("");
            BootstrapPageResponse pageResponse = new BootstrapPageResponse(
                    this, request, context.getSession(), context.getUIClass(),
                    document, headers, fragmentResponse.getUIProvider());
            List<Node> fragmentNodes = fragmentResponse.getFragmentNodes();
            Element body = document.body();
            for (Node node : fragmentNodes) {
                body.appendChild(node);
            }

            setupStandaloneDocument(context, pageResponse);
            context.getSession().modifyBootstrapResponse(pageResponse);

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

    private void sendBootstrapHeaders(VaadinResponse response,
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

    private void writeBootstrapPage(VaadinResponse response, String html)
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

        DocumentType doctype = new DocumentType("html", "", "",
                document.baseUri());
        document.child(0).before(doctype);

        Element head = document.head();
        head.appendElement("meta").attr("http-equiv", "Content-Type")
                .attr("content", "text/html; charset=utf-8");

        /*
         * Enable Chrome Frame in all versions of IE if installed.
         */
        head.appendElement("meta").attr("http-equiv", "X-UA-Compatible")
                .attr("content", "IE=11;chrome=1");

        Class<? extends UI> uiClass = context.getUIClass();

        String viewportContent = null;
        Viewport viewportAnnotation = uiClass.getAnnotation(Viewport.class);
        ViewportGeneratorClass viewportGeneratorClassAnnotation = uiClass
                .getAnnotation(ViewportGeneratorClass.class);
        if (viewportAnnotation != null
                && viewportGeneratorClassAnnotation != null) {
            throw new IllegalStateException(uiClass.getCanonicalName()
                    + " cannot be annotated with both @"
                    + Viewport.class.getSimpleName() + " and @"
                    + ViewportGeneratorClass.class.getSimpleName());
        }

        if (viewportAnnotation != null) {
            viewportContent = viewportAnnotation.value();
        } else if (viewportGeneratorClassAnnotation != null) {
            Class<? extends ViewportGenerator> viewportGeneratorClass = viewportGeneratorClassAnnotation
                    .value();
            try {
                viewportContent = viewportGeneratorClass.newInstance()
                        .getViewport(context.getRequest());
            } catch (Exception e) {
                throw new RuntimeException(
                        "Error processing viewport generator "
                                + viewportGeneratorClass.getCanonicalName(), e);
            }
        }

        if (viewportContent != null) {
            head.appendElement("meta").attr("name", "viewport")
                    .attr("content", viewportContent);
        }

        String title = response.getUIProvider().getPageTitle(
                new UICreateEvent(context.getRequest(), context.getUIClass()));
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

        JavaScript javaScript = uiClass.getAnnotation(JavaScript.class);
        if (javaScript != null) {
            String[] resources = javaScript.value();
            for (String resource : resources) {
                String url = registerDependency(context, uiClass, resource);
                head.appendElement("script").attr("type", "text/javascript")
                        .attr("src", url);
            }
        }

        StyleSheet styleSheet = uiClass.getAnnotation(StyleSheet.class);
        if (styleSheet != null) {
            String[] resources = styleSheet.value();
            for (String resource : resources) {
                String url = registerDependency(context, uiClass, resource);
                head.appendElement("link").attr("rel", "stylesheet")
                        .attr("type", "text/css").attr("href", url);
            }
        }

        Element body = document.body();
        body.attr("scroll", "auto");
        body.addClass(ApplicationConstants.GENERATED_BODY_CLASSNAME);
    }

    private String registerDependency(BootstrapContext context,
            Class<? extends UI> uiClass, String resource) {
        String url = context.getSession().getCommunicationManager()
                .registerDependency(resource, uiClass);

        url = context.getUriResolver().resolveVaadinUri(url);

        return url;
    }

    protected String getMainDivStyle(BootstrapContext context) {
        return null;
    }

    public String getWidgetsetForUI(BootstrapContext context) {
        VaadinRequest request = context.getRequest();

        UICreateEvent event = new UICreateEvent(context.getRequest(),
                context.getUIClass());
        String widgetset = context.getBootstrapResponse().getUIProvider()
                .getWidgetset(event);
        if (widgetset == null) {
            widgetset = request.getService().getConfiguredWidgetset(request);
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
     */
    private void setupMainDiv(BootstrapContext context) throws IOException {
        String style = getMainDivStyle(context);

        /*- Add classnames;
         *      .v-app
         *      .v-app-loading
         *- Additionally added from javascript:
         *      <themeName, remove non-alphanum>
         */

        List<Node> fragmentNodes = context.getBootstrapResponse()
                .getFragmentNodes();

        Element mainDiv = new Element(Tag.valueOf("div"), "");
        mainDiv.attr("id", context.getAppId());
        mainDiv.addClass("v-app");
        mainDiv.addClass(context.getThemeName());
        mainDiv.addClass(context.getUIClass().getSimpleName()
                .toLowerCase(Locale.ENGLISH));
        if (style != null && style.length() != 0) {
            mainDiv.attr("style", style);
        }
        mainDiv.appendElement("div").addClass("v-app-loading");
        mainDiv.appendElement("noscript")
                .append("You have to enable javascript in your browser to use an application built with Vaadin.");
        fragmentNodes.add(mainDiv);

        VaadinRequest request = context.getRequest();

        VaadinService vaadinService = request.getService();
        String vaadinLocation = vaadinService.getStaticFileLocation(request)
                + "/VAADIN/";

        // Parameter appended to JS to bypass caches after version upgrade.
        String versionQueryParam = "?v=" + Version.getFullVersion();

        if (context.getPushMode().isEnabled()) {
            // Load client-side dependencies for push support
            String pushJS = vaadinLocation;
            if (context.getRequest().getService().getDeploymentConfiguration()
                    .isProductionMode()) {
                pushJS += ApplicationConstants.VAADIN_PUSH_JS;
            } else {
                pushJS += ApplicationConstants.VAADIN_PUSH_DEBUG_JS;
            }

            pushJS += versionQueryParam;

            fragmentNodes.add(new Element(Tag.valueOf("script"), "").attr(
                    "type", "text/javascript").attr("src", pushJS));
        }

        String bootstrapLocation = vaadinLocation
                + ApplicationConstants.VAADIN_BOOTSTRAP_JS + versionQueryParam;
        fragmentNodes.add(new Element(Tag.valueOf("script"), "").attr("type",
                "text/javascript").attr("src", bootstrapLocation));
        Element mainScriptTag = new Element(Tag.valueOf("script"), "").attr(
                "type", "text/javascript");

        StringBuilder builder = new StringBuilder();
        builder.append("//<![CDATA[\n");
        builder.append("if (!window.vaadin) alert("
                + JsonUtil.quote("Failed to load the bootstrap javascript: "
                        + bootstrapLocation) + ");\n");

        appendMainScriptTagContents(context, builder);

        builder.append("//]]>");
        mainScriptTag.appendChild(new DataNode(builder.toString(),
                mainScriptTag.baseUri()));
        fragmentNodes.add(mainScriptTag);

    }

    protected void appendMainScriptTagContents(BootstrapContext context,
            StringBuilder builder) throws IOException {
        JsonObject appConfig = context.getApplicationParameters();

        boolean isDebug = !context.getSession().getConfiguration()
                .isProductionMode();

        if (isDebug) {
            /*
             * Add tracking needed for getting bootstrap metrics to the client
             * side Profiler if another implementation hasn't already been
             * added.
             */
            builder.append("if (typeof window.__gwtStatsEvent != 'function') {\n");
            builder.append("vaadin.gwtStatsEvents = [];\n");
            builder.append("window.__gwtStatsEvent = function(event) {vaadin.gwtStatsEvents.push(event); return true;};\n");
            builder.append("}\n");
        }

        builder.append("vaadin.initApplication(\"");
        builder.append(context.getAppId());
        builder.append("\",");
        appendJsonObject(builder, appConfig, isDebug);
        builder.append(");\n");
    }

    private static void appendJsonObject(StringBuilder builder,
            JsonObject jsonObject, boolean isDebug) {
        if (isDebug) {
            builder.append(JsonUtil.stringify(jsonObject, 4));
        } else {
            builder.append(JsonUtil.stringify(jsonObject));
        }
    }

    protected JsonObject getApplicationParameters(BootstrapContext context) {
        VaadinRequest request = context.getRequest();
        VaadinSession session = context.getSession();
        VaadinService vaadinService = request.getService();

        JsonObject appConfig = Json.createObject();

        String themeName = context.getThemeName();
        if (themeName != null) {
            appConfig.put("theme", themeName);
        }

        // Ignore restartApplication that might be passed to UI init
        if (request
                .getParameter(VaadinService.URL_PARAMETER_RESTART_APPLICATION) != null) {
            appConfig.put("extraParams", "&" + IGNORE_RESTART_PARAM + "=1");
        }

        JsonObject versionInfo = Json.createObject();
        versionInfo.put("vaadinVersion", Version.getFullVersion());
        String atmosphereVersion = AtmospherePushConnection
                .getAtmosphereVersion();
        if (atmosphereVersion != null) {
            versionInfo.put("atmosphereVersion", atmosphereVersion);
        }

        appConfig.put("versionInfo", versionInfo);
        appConfig.put("widgetset", context.getWidgetsetName());

        // Use locale from session if set, else from the request
        Locale locale = ServletPortletHelper.findLocale(null,
                context.getSession(), context.getRequest());
        // Get system messages
        SystemMessages systemMessages = vaadinService.getSystemMessages(locale,
                request);
        if (systemMessages != null) {
            // Write the CommunicationError -message to client
            JsonObject comErrMsg = Json.createObject();
            putValueOrNull(comErrMsg, "caption",
                    systemMessages.getCommunicationErrorCaption());
            putValueOrNull(comErrMsg, "message",
                    systemMessages.getCommunicationErrorMessage());
            putValueOrNull(comErrMsg, "url",
                    systemMessages.getCommunicationErrorURL());

            appConfig.put("comErrMsg", comErrMsg);

            JsonObject authErrMsg = Json.createObject();
            putValueOrNull(authErrMsg, "caption",
                    systemMessages.getAuthenticationErrorCaption());
            putValueOrNull(authErrMsg, "message",
                    systemMessages.getAuthenticationErrorMessage());
            putValueOrNull(authErrMsg, "url",
                    systemMessages.getAuthenticationErrorURL());

            appConfig.put("authErrMsg", authErrMsg);

            JsonObject sessExpMsg = Json.createObject();
            putValueOrNull(sessExpMsg, "caption",
                    systemMessages.getSessionExpiredCaption());
            putValueOrNull(sessExpMsg, "message",
                    systemMessages.getSessionExpiredMessage());
            putValueOrNull(sessExpMsg, "url",
                    systemMessages.getSessionExpiredURL());

            appConfig.put("sessExpMsg", sessExpMsg);
        }

        // getStaticFileLocation documented to never end with a slash
        // vaadinDir should always end with a slash
        String vaadinDir = vaadinService.getStaticFileLocation(request)
                + "/VAADIN/";
        appConfig.put(ApplicationConstants.VAADIN_DIR_URL, vaadinDir);

        if (!session.getConfiguration().isProductionMode()) {
            appConfig.put("debug", true);
        }

        if (vaadinService.isStandalone(request)) {
            appConfig.put("standalone", true);
        }

        appConfig.put("heartbeatInterval", vaadinService
                .getDeploymentConfiguration().getHeartbeatInterval());

        String serviceUrl = getServiceUrl(context);
        if (serviceUrl != null) {
            appConfig.put(ApplicationConstants.SERVICE_URL, serviceUrl);
        }

        boolean sendUrlsAsParameters = vaadinService
                .getDeploymentConfiguration().isSendUrlsAsParameters();
        if (!sendUrlsAsParameters) {
            appConfig.put("sendUrlsAsParameters", false);
        }

        return appConfig;
    }

    protected abstract String getServiceUrl(BootstrapContext context);

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
        VaadinRequest request = context.getRequest();
        final String staticFilePath = request.getService()
                .getStaticFileLocation(request);
        return staticFilePath + "/" + VaadinServlet.THEME_DIR_PATH + '/'
                + themeName;
    }

    /**
     * Override if required
     * 
     * @param context
     * @return
     */
    public String getThemeName(BootstrapContext context) {
        UICreateEvent event = new UICreateEvent(context.getRequest(),
                context.getUIClass());
        return context.getBootstrapResponse().getUIProvider().getTheme(event);
    }

    /**
     * Do not override.
     * 
     * @param context
     * @return
     */
    public String findAndEscapeThemeName(BootstrapContext context) {
        String themeName = getThemeName(context);
        if (themeName == null) {
            VaadinRequest request = context.getRequest();
            themeName = request.getService().getConfiguredTheme(request);
        }

        // XSS preventation, theme names shouldn't contain special chars anyway.
        // The servlet denies them via url parameter.
        themeName = VaadinServlet.stripSpecialChars(themeName);

        return themeName;
    }

    protected void writeError(VaadinResponse response, Throwable e)
            throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getLocalizedMessage());
    }

    private void putValueOrNull(JsonObject object, String key, String value) {
        assert object != null;
        assert key != null;
        if (value == null) {
            object.put(key, Json.createNull());
        } else {
            object.put(key, value);
        }
    }
}
