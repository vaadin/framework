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
package com.vaadin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.vaadin.client.debug.internal.ErrorNotificationHandler;
import com.vaadin.client.debug.internal.HierarchySection;
import com.vaadin.client.debug.internal.InfoSection;
import com.vaadin.client.debug.internal.LogSection;
import com.vaadin.client.debug.internal.NetworkSection;
import com.vaadin.client.debug.internal.ProfilerSection;
import com.vaadin.client.debug.internal.Section;
import com.vaadin.client.debug.internal.TestBenchSection;
import com.vaadin.client.debug.internal.VDebugWindow;
import com.vaadin.client.debug.internal.theme.DebugWindowStyles;
import com.vaadin.client.event.PointerEventSupport;
import com.vaadin.client.metadata.BundleLoadCallback;
import com.vaadin.client.metadata.ConnectorBundleLoader;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.ui.UnknownComponentConnector;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.ui.ui.UIConstants;

public class ApplicationConfiguration implements EntryPoint {

    /**
     * Helper class for reading configuration options from the bootstap
     * javascript
     * 
     * @since 7.0
     */
    private static class JsoConfiguration extends JavaScriptObject {
        protected JsoConfiguration() {
            // JSO Constructor
        }

        /**
         * Reads a configuration parameter as a string. Please note that the
         * javascript value of the parameter should also be a string, or else an
         * undefined exception may be thrown.
         * 
         * @param name
         *            name of the configuration parameter
         * @return value of the configuration parameter, or <code>null</code> if
         *         not defined
         */
        private native String getConfigString(String name)
        /*-{
            var value = this.getConfig(name);
            if (value === null || value === undefined) {
                return null;
            } else {
                return value +"";
            }
        }-*/;

        /**
         * Reads a configuration parameter as a boolean object. Please note that
         * the javascript value of the parameter should also be a boolean, or
         * else an undefined exception may be thrown.
         * 
         * @param name
         *            name of the configuration parameter
         * @return boolean value of the configuration paramter, or
         *         <code>null</code> if no value is defined
         */
        private native Boolean getConfigBoolean(String name)
        /*-{
            var value = this.getConfig(name);
            if (value === null || value === undefined) {
                return null;
            } else {
                 // $entry not needed as function is not exported
                return @java.lang.Boolean::valueOf(Z)(value);
            }
        }-*/;

        /**
         * Reads a configuration parameter as an integer object. Please note
         * that the javascript value of the parameter should also be an integer,
         * or else an undefined exception may be thrown.
         * 
         * @param name
         *            name of the configuration parameter
         * @return integer value of the configuration paramter, or
         *         <code>null</code> if no value is defined
         */
        private native Integer getConfigInteger(String name)
        /*-{
            var value = this.getConfig(name);
            if (value === null || value === undefined) {
                return null;
            } else {
                 // $entry not needed as function is not exported
                return @java.lang.Integer::valueOf(I)(value);
            }
        }-*/;

        /**
         * Reads a configuration parameter as an {@link ErrorMessage} object.
         * Please note that the javascript value of the parameter should also be
         * an object with appropriate fields, or else an undefined exception may
         * be thrown when calling this method or when calling methods on the
         * returned object.
         * 
         * @param name
         *            name of the configuration parameter
         * @return error message with the given name, or <code>null</code> if no
         *         value is defined
         */
        private native ErrorMessage getConfigError(String name)
        /*-{
            return this.getConfig(name);
        }-*/;

        /**
         * Returns a native javascript object containing version information
         * from the server.
         * 
         * @return a javascript object with the version information
         */
        private native JavaScriptObject getVersionInfoJSObject()
        /*-{
            return this.getConfig("versionInfo");
        }-*/;

        /**
         * Gets the version of the Vaadin framework used on the server.
         * 
         * @return a string with the version
         * 
         * @see com.vaadin.server.VaadinServlet#VERSION
         */
        private native String getVaadinVersion()
        /*-{
            return this.getConfig("versionInfo").vaadinVersion;
        }-*/;

        private native String getUIDL()
        /*-{
           return this.getConfig("uidl");
         }-*/;
    }

    /**
     * Wraps a native javascript object containing fields for an error message
     * 
     * @since 7.0
     */
    public static final class ErrorMessage extends JavaScriptObject {

        protected ErrorMessage() {
            // JSO constructor
        }

        public final native String getCaption()
        /*-{
            return this.caption;
        }-*/;

        public final native String getMessage()
        /*-{
            return this.message;
        }-*/;

        public final native String getUrl()
        /*-{
            return this.url;
        }-*/;
    }

    private static WidgetSet widgetSet = GWT.create(WidgetSet.class);

    private String id;
    /**
     * The URL to the VAADIN directory containing themes and widgetsets. Should
     * always end with a slash (/).
     */
    private String vaadinDirUrl;
    private String serviceUrl;
    private int uiId;
    private boolean standalone;
    private ErrorMessage communicationError;
    private ErrorMessage authorizationError;
    private ErrorMessage sessionExpiredError;
    private int heartbeatInterval;

    private HashMap<Integer, String> unknownComponents;

    private Map<Integer, Class<? extends ServerConnector>> classes = new HashMap<Integer, Class<? extends ServerConnector>>();

    private boolean widgetsetVersionSent = false;
    private static boolean moduleLoaded = false;

    static// TODO consider to make this hashmap per application
    LinkedList<Command> callbacks = new LinkedList<Command>();

    private static int dependenciesLoading;

    private static ArrayList<ApplicationConnection> runningApplications = new ArrayList<ApplicationConnection>();

    private Map<Integer, Integer> componentInheritanceMap = new HashMap<Integer, Integer>();
    private Map<Integer, String> tagToServerSideClassName = new HashMap<Integer, String>();

    /**
     * Checks whether path info in requests to the server-side service should be
     * in a request parameter (named <code>v-resourcePath</code>) or appended to
     * the end of the service URL.
     * 
     * @see #getServiceUrl()
     * 
     * @return <code>true</code> if path info should be a request parameter;
     *         <code>false</code> if the path info goes after the service URL
     */
    public boolean useServiceUrlPathParam() {
        return getJsoConfiguration(id).getConfigBoolean(
                ApplicationConstants.SERVICE_URL_PATH_AS_PARAMETER) == Boolean.TRUE;
    }

    /**
     * Return the name of the parameter used to to send data to the service url.
     * This method should only be called if {@link #useServiceUrlPathParam()} is
     * true.
     * 
     * @since 7.1.6
     * @return The parameter name, by default <code>v-resourcePath</code>
     */
    public String getServiceUrlParameterName() {
        String prefix = getJsoConfiguration(id).getConfigString(
                ApplicationConstants.SERVICE_URL_PARAMETER_NAMESPACE);
        if (prefix == null) {
            prefix = "";
        }
        return prefix + ApplicationConstants.V_RESOURCE_PATH;
    }

    public String getRootPanelId() {
        return id;
    }

    /**
     * Gets the URL to the server-side VaadinService. If
     * {@link #useServiceUrlPathParam()} return <code>true</code>, the requested
     * path info should be in the <code>v-resourcePath</code> query parameter;
     * else the path info should be appended to the end of the URL.
     * 
     * @see #useServiceUrlPathParam()
     * 
     * @return the URL to the server-side service as a string
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * @return the theme name used when initializing the application
     * @deprecated as of 7.3. Use {@link UIConnector#getActiveTheme()} to get
     *             the theme currently in use
     */
    @Deprecated
    public String getThemeName() {
        return getJsoConfiguration(id).getConfigString("theme");
    }

    /**
     * Gets the URL of the VAADIN directory on the server.
     * 
     * @return the URL of the VAADIN directory
     */
    public String getVaadinDirUrl() {
        return vaadinDirUrl;
    }

    public void setAppId(String appId) {
        id = appId;
    }

    /**
     * Gets the initial UIDL from the DOM, if it was provided during the init
     * process.
     * 
     * @return
     */
    public String getUIDL() {
        return getJsoConfiguration(id).getUIDL();
    }

    /**
     * @return true if the application is served by std. Vaadin servlet and is
     *         considered to be the only or main content of the host page.
     */
    public boolean isStandalone() {
        return standalone;
    }

    /**
     * Gets the UI id of the server-side UI associated with this client-side
     * instance. The UI id should be included in every request originating from
     * this instance in order to associate the request with the right UI
     * instance on the server.
     * 
     * @return the UI id
     */
    public int getUIId() {
        return uiId;
    }

    /**
     * @return The interval in seconds between heartbeat requests, or a
     *         non-positive number if heartbeat is disabled.
     */
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public JavaScriptObject getVersionInfoJSObject() {
        return getJsoConfiguration(id).getVersionInfoJSObject();
    }

    public ErrorMessage getCommunicationError() {
        return communicationError;
    }

    public ErrorMessage getAuthorizationError() {
        return authorizationError;
    }

    public ErrorMessage getSessionExpiredError() {
        return sessionExpiredError;
    }

    /**
     * Reads the configuration values defined by the bootstrap javascript.
     */
    private void loadFromDOM() {
        JsoConfiguration jsoConfiguration = getJsoConfiguration(id);
        serviceUrl = jsoConfiguration
                .getConfigString(ApplicationConstants.SERVICE_URL);
        if (serviceUrl == null || "".equals(serviceUrl)) {
            /*
             * Use the current url without query parameters and fragment as the
             * default value.
             */
            serviceUrl = Window.Location.getHref().replaceFirst("[?#].*", "");
        } else {
            /*
             * Resolve potentially relative URLs to ensure they point to the
             * desired locations even if the base URL of the page changes later
             * (e.g. with pushState)
             */
            serviceUrl = Util.getAbsoluteUrl(serviceUrl);
        }
        // Ensure there's an ending slash (to make appending e.g. UIDL work)
        if (!useServiceUrlPathParam() && !serviceUrl.endsWith("/")) {
            serviceUrl += '/';
        }

        vaadinDirUrl = Util.getAbsoluteUrl(jsoConfiguration
                .getConfigString(ApplicationConstants.VAADIN_DIR_URL));
        uiId = jsoConfiguration.getConfigInteger(UIConstants.UI_ID_PARAMETER)
                .intValue();

        // null -> false
        standalone = jsoConfiguration.getConfigBoolean("standalone") == Boolean.TRUE;

        heartbeatInterval = jsoConfiguration
                .getConfigInteger("heartbeatInterval");

        communicationError = jsoConfiguration.getConfigError("comErrMsg");
        authorizationError = jsoConfiguration.getConfigError("authErrMsg");
        sessionExpiredError = jsoConfiguration.getConfigError("sessExpMsg");
    }

    /**
     * Starts the application with a given id by reading the configuration
     * options stored by the bootstrap javascript.
     * 
     * @param applicationId
     *            id of the application to load, this is also the id of the html
     *            element into which the application should be rendered.
     */
    public static void startApplication(final String applicationId) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                Profiler.enter("ApplicationConfiguration.startApplication");
                ApplicationConfiguration appConf = getConfigFromDOM(applicationId);
                ApplicationConnection a = GWT
                        .create(ApplicationConnection.class);
                a.init(widgetSet, appConf);
                runningApplications.add(a);
                Profiler.leave("ApplicationConfiguration.startApplication");

                a.start();
            }
        });
    }

    public static List<ApplicationConnection> getRunningApplications() {
        return runningApplications;
    }

    /**
     * Gets the configuration object for a specific application from the
     * bootstrap javascript.
     * 
     * @param appId
     *            the id of the application to get configuration data for
     * @return a native javascript object containing the configuration data
     */
    private native static JsoConfiguration getJsoConfiguration(String appId)
    /*-{
        return $wnd.vaadin.getApp(appId);
     }-*/;

    public static ApplicationConfiguration getConfigFromDOM(String appId) {
        ApplicationConfiguration conf = new ApplicationConfiguration();
        conf.setAppId(appId);
        conf.loadFromDOM();
        return conf;
    }

    public String getServletVersion() {
        return getJsoConfiguration(id).getVaadinVersion();
    }

    public Class<? extends ServerConnector> getConnectorClassByEncodedTag(
            int tag) {
        Class<? extends ServerConnector> type = classes.get(tag);
        if (type == null && !classes.containsKey(tag)) {
            // Initialize if not already loaded
            Integer currentTag = Integer.valueOf(tag);
            while (type == null && currentTag != null) {
                String serverSideClassNameForTag = getServerSideClassNameForTag(currentTag);
                if (TypeData.hasIdentifier(serverSideClassNameForTag)) {
                    try {
                        type = (Class<? extends ServerConnector>) TypeData
                                .getClass(serverSideClassNameForTag);
                    } catch (NoDataException e) {
                        throw new RuntimeException(e);
                    }
                }
                currentTag = getParentTag(currentTag.intValue());
            }
            if (type == null) {
                type = UnknownComponentConnector.class;
                if (unknownComponents == null) {
                    unknownComponents = new HashMap<Integer, String>();
                }
                unknownComponents.put(tag, getServerSideClassNameForTag(tag));
            }
            classes.put(tag, type);
        }
        return type;
    }

    public void addComponentInheritanceInfo(ValueMap valueMap) {
        JsArrayString keyArray = valueMap.getKeyArray();
        for (int i = 0; i < keyArray.length(); i++) {
            String key = keyArray.get(i);
            int value = valueMap.getInt(key);
            componentInheritanceMap.put(Integer.parseInt(key), value);
        }
    }

    public void addComponentMappings(ValueMap valueMap, WidgetSet widgetSet) {
        JsArrayString keyArray = valueMap.getKeyArray();
        for (int i = 0; i < keyArray.length(); i++) {
            String key = keyArray.get(i).intern();
            int value = valueMap.getInt(key);
            tagToServerSideClassName.put(value, key);
        }

        for (int i = 0; i < keyArray.length(); i++) {
            String key = keyArray.get(i).intern();
            int value = valueMap.getInt(key);
            widgetSet.ensureConnectorLoaded(value, this);
        }
    }

    /**
     * Returns all tags for given class. Tags are used in
     * {@link ApplicationConfiguration} to keep track of different classes and
     * their hierarchy
     * 
     * @since 7.2
     * @param classname
     *            name of class which tags we want
     * @return Integer array of tags pointing to this classname
     */
    public Integer[] getTagsForServerSideClassName(String classname) {
        List<Integer> tags = new ArrayList<Integer>();

        for (Map.Entry<Integer, String> entry : tagToServerSideClassName
                .entrySet()) {
            if (classname.equals(entry.getValue())) {
                tags.add(entry.getKey());
            }
        }

        Integer[] out = new Integer[tags.size()];
        return tags.toArray(out);
    }

    public Integer getParentTag(int tag) {
        return componentInheritanceMap.get(tag);
    }

    public String getServerSideClassNameForTag(Integer tag) {
        return tagToServerSideClassName.get(tag);
    }

    String getUnknownServerClassNameByTag(int tag) {
        if (unknownComponents != null) {
            return unknownComponents.get(tag);
        }
        return null;
    }

    /**
     * 
     * @param c
     */
    static void runWhenDependenciesLoaded(Command c) {
        if (dependenciesLoading == 0) {
            c.execute();
        } else {
            callbacks.add(c);
        }
    }

    static void startDependencyLoading() {
        dependenciesLoading++;
    }

    static void endDependencyLoading() {
        dependenciesLoading--;
        if (dependenciesLoading == 0 && !callbacks.isEmpty()) {
            for (Command cmd : callbacks) {
                cmd.execute();
            }
            callbacks.clear();
        } else if (dependenciesLoading == 0
                && !ConnectorBundleLoader.get().isBundleLoaded(
                        ConnectorBundleLoader.DEFERRED_BUNDLE_NAME)) {
            ConnectorBundleLoader.get().loadBundle(
                    ConnectorBundleLoader.DEFERRED_BUNDLE_NAME,
                    new BundleLoadCallback() {
                        @Override
                        public void loaded() {
                            // Nothing to do
                        }

                        @Override
                        public void failed(Throwable reason) {
                            VConsole.error(reason);
                        }
                    });
        }
    }

    @Override
    public void onModuleLoad() {

        // Don't run twice if the module has been inherited several times.
        if (moduleLoaded) {
            return;
        }
        moduleLoaded = true;

        Profiler.initialize();
        Profiler.enter("ApplicationConfiguration.onModuleLoad");

        BrowserInfo browserInfo = BrowserInfo.get();

        // Enable iOS6 cast fix (see #10460)
        if (browserInfo.isIOS6() && browserInfo.isWebkit()) {
            enableIOS6castFix();
        }

        // Enable IE prompt fix (#13367)
        if (browserInfo.isIE() && browserInfo.getBrowserMajorVersion() >= 10) {
            enableIEPromptFix();
        }

        // Register pointer events (must be done before any events are used)
        PointerEventSupport.init();

        // Prepare the debugging window
        if (isDebugMode()) {
            /*
             * XXX Lots of implementation details here right now. This should be
             * cleared up when an API for extending the debug window is
             * implemented.
             */
            VDebugWindow window = GWT.create(VDebugWindow.class);

            if (LogConfiguration.loggingIsEnabled()) {
                window.addSection((Section) GWT.create(LogSection.class));
            }
            window.addSection((Section) GWT.create(InfoSection.class));
            window.addSection((Section) GWT.create(HierarchySection.class));
            window.addSection((Section) GWT.create(NetworkSection.class));
            window.addSection((Section) GWT.create(TestBenchSection.class));
            if (Profiler.isEnabled()) {
                window.addSection((Section) GWT.create(ProfilerSection.class));
            }

            if (isQuietDebugMode()) {
                window.close();
            } else {
                // Load debug window styles asynchronously
                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        DebugWindowStyles dws = GWT
                                .create(DebugWindowStyles.class);
                        dws.css().ensureInjected();
                    }

                    @Override
                    public void onFailure(Throwable reason) {
                        Window.alert("Failed to load Vaadin debug window styles");
                    }
                });

                window.init();
            }

            // Connect to the legacy API
            VConsole.setImplementation(window);

            Handler errorNotificationHandler = GWT
                    .create(ErrorNotificationHandler.class);
            Logger.getLogger("").addHandler(errorNotificationHandler);
        }

        if (LogConfiguration.loggingIsEnabled()) {
            GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

                @Override
                public void onUncaughtException(Throwable e) {
                    /*
                     * If the debug window is not enabled (?debug), this will
                     * not show anything to normal users. "a1 is not an object"
                     * style errors helps nobody, especially end user. It does
                     * not work tells just as much.
                     */
                    getLogger().log(Level.SEVERE, e.getMessage(), e);
                }
            });

            if (isProductionMode()) {
                // Disable all logging if in production mode
                Logger.getLogger("").setLevel(Level.OFF);
            }
        }
        Profiler.leave("ApplicationConfiguration.onModuleLoad");

        if (SuperDevMode.enableBasedOnParameter()) {
            // Do not start any application as super dev mode will refresh the
            // page once done compiling
            return;
        }
        registerCallback(GWT.getModuleName());
    }

    /**
     * Fix to iOS6 failing when comparing with 0 directly after the kind of
     * comparison done by GWT when a double or float is cast to an int. Forcing
     * another trivial operation (other than a compare to 0) after the dangerous
     * comparison makes the issue go away. See #10460.
     */
    private static native void enableIOS6castFix()
    /*-{
          Math.max = function(a,b) {return (a > b === 1 < 2)? a : b}
          Math.min = function(a,b) {return (a < b === 1 < 2)? a : b}
    }-*/;

    /**
     * Make Metro versions of IE suggest switching to the desktop when
     * window.prompt is called.
     */
    private static native void enableIEPromptFix()
    /*-{
        var prompt = $wnd.prompt;
        $wnd.prompt = function () {
            var result = prompt.apply($wnd, Array.prototype.slice.call(arguments));
            if (result === undefined) {
                // force the browser to suggest desktop mode
                showModalDialog();
                return null;
            } else {
                return result;
            }
        };
    }-*/;

    /**
     * Registers that callback that the bootstrap javascript uses to start
     * applications once the widgetset is loaded and all required information is
     * available
     * 
     * @param widgetsetName
     *            the name of this widgetset
     */
    public native static void registerCallback(String widgetsetName)
    /*-{
        var callbackHandler = $entry(@com.vaadin.client.ApplicationConfiguration::startApplication(Ljava/lang/String;));
        $wnd.vaadin.registerWidgetset(widgetsetName, callbackHandler);
    }-*/;

    /**
     * Checks if client side is in debug mode. Practically this is invoked by
     * adding ?debug parameter to URI. Please note that debug mode is always
     * disabled if production mode is enabled, but disabling production mode
     * does not automatically enable debug mode.
     * 
     * @see #isProductionMode()
     * 
     * @return true if client side is currently been debugged
     */
    public static boolean isDebugMode() {
        return isDebugAvailable()
                && Window.Location.getParameter("debug") != null;
    }

    /**
     * Checks if production mode is enabled. When production mode is enabled,
     * client-side logging is disabled. There may also be other performance
     * optimizations.
     * 
     * @since 7.1.2
     * @return <code>true</code> if production mode is enabled; otherwise
     *         <code>false</code>.
     */
    public static boolean isProductionMode() {
        return !isDebugAvailable();
    }

    private native static boolean isDebugAvailable()
    /*-{
        if($wnd.vaadin.debug) {
            return true;
        } else {
            return false;
        }
    }-*/;

    /**
     * Checks whether debug logging should be quiet
     * 
     * @return <code>true</code> if debug logging should be quiet
     */
    public static boolean isQuietDebugMode() {
        String debugParameter = Window.Location.getParameter("debug");
        return isDebugAvailable() && debugParameter != null
                && debugParameter.startsWith("q");
    }

    /**
     * Checks whether the widget set version has been sent to the server. It is
     * sent in the first UIDL request.
     * 
     * @return <code>true</code> if browser information has already been sent
     */
    public boolean isWidgetsetVersionSent() {
        return widgetsetVersionSent;
    }

    /**
     * Registers that the widget set version has been sent to the server.
     */
    public void setWidgetsetVersionSent() {
        widgetsetVersionSent = true;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(ApplicationConfiguration.class.getName());
    }
}
