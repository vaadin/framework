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
package com.vaadin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.vaadin.client.metadata.BundleLoadCallback;
import com.vaadin.client.metadata.ConnectorBundleLoader;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.ui.UnknownComponentConnector;
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
         * @see com.vaadin.server.AbstractApplicationServlet#VERSION
         */
        private native String getVaadinVersion()
        /*-{
            return this.getConfig("versionInfo").vaadinVersion;
        }-*/;

        /**
         * Gets the version of the application running on the server.
         * 
         * @return a string with the application version
         * 
         * @see com.vaadin.Application#getVersion()
         */
        private native String getApplicationVersion()
        /*-{
            return this.getConfig("versionInfo").applicationVersion;
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
    private String themeUri;
    private String appUri;
    private int uiId;
    private boolean standalone;
    private ErrorMessage communicationError;
    private ErrorMessage authorizationError;
    private int heartbeatInterval;

    private HashMap<Integer, String> unknownComponents;

    private Map<Integer, Class<? extends ServerConnector>> classes = new HashMap<Integer, Class<? extends ServerConnector>>();

    private boolean browserDetailsSent = false;
    private boolean widgetsetVersionSent = false;

    static// TODO consider to make this hashmap per application
    LinkedList<Command> callbacks = new LinkedList<Command>();

    private static int dependenciesLoading;

    private static ArrayList<ApplicationConnection> runningApplications = new ArrayList<ApplicationConnection>();

    private Map<Integer, Integer> componentInheritanceMap = new HashMap<Integer, Integer>();
    private Map<Integer, String> tagToServerSideClassName = new HashMap<Integer, String>();

    public boolean usePortletURLs() {
        return getPortletResourceUrl() != null;
    }

    public String getPortletResourceUrl() {
        return getJsoConfiguration(id).getConfigString(
                ApplicationConstants.PORTLET_RESOUCE_URL_BASE);
    }

    public String getRootPanelId() {
        return id;
    }

    /**
     * Gets the application base URI. Using this other than as the download
     * action URI can cause problems in Portlet 2.0 deployments.
     * 
     * @return application base URI
     */
    public String getApplicationUri() {
        return appUri;
    }

    public String getThemeName() {
        String uri = getThemeUri();
        String themeName = uri.substring(uri.lastIndexOf('/'));
        themeName = themeName.replaceAll("[^a-zA-Z0-9]", "");
        return themeName;
    }

    public String getThemeUri() {
        return themeUri;
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
     * Gets the root if of this application instance. The root id should be
     * included in every request originating from this instance in order to
     * associate it with the right UI instance on the server.
     * 
     * @return the root id
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

    /**
     * Reads the configuration values defined by the bootstrap javascript.
     */
    private void loadFromDOM() {
        JsoConfiguration jsoConfiguration = getJsoConfiguration(id);
        appUri = jsoConfiguration.getConfigString("appUri");
        if (appUri != null && !appUri.endsWith("/")) {
            appUri += '/';
        }
        themeUri = jsoConfiguration.getConfigString("themeUri");
        uiId = jsoConfiguration.getConfigInteger(UIConstants.UI_ID_PARAMETER)
                .intValue();

        // null -> false
        standalone = jsoConfiguration.getConfigBoolean("standalone") == Boolean.TRUE;

        heartbeatInterval = jsoConfiguration
                .getConfigInteger("heartbeatInterval");

        communicationError = jsoConfiguration.getConfigError("comErrMsg");
        authorizationError = jsoConfiguration.getConfigError("authErrMsg");

        // boostrap sets initPending to false if it has sent the browser details
        if (jsoConfiguration.getConfigBoolean("initPending") == Boolean.FALSE) {
            setBrowserDetailsSent();
        }

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
                ApplicationConfiguration appConf = getConfigFromDOM(applicationId);
                ApplicationConnection a = GWT
                        .create(ApplicationConnection.class);
                a.init(widgetSet, appConf);
                a.start();
                runningApplications.add(a);
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

    public String getApplicationVersion() {
        return getJsoConfiguration(id).getApplicationVersion();
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

        // Prepare VConsole for debugging
        if (isDebugMode()) {
            Console console = GWT.create(Console.class);
            console.setQuietMode(isQuietDebugMode());
            console.init();
            VConsole.setImplementation(console);
        } else {
            VConsole.setImplementation((Console) GWT.create(NullConsole.class));
        }
        /*
         * Display some sort of error of exceptions in web mode to debug
         * console. After this, exceptions are reported to VConsole and possible
         * GWT hosted mode.
         */
        GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void onUncaughtException(Throwable e) {
                /*
                 * Note in case of null console (without ?debug) we eat
                 * exceptions. "a1 is not an object" style errors helps nobody,
                 * especially end user. It does not work tells just as much.
                 */
                VConsole.getImplementation().error(e);
            }
        });

        if (SuperDevMode.enableBasedOnParameter()) {
            // Do not start any application as super dev mode will refresh the
            // page once done compiling
            return;
        }
        registerCallback(GWT.getModuleName());
    }

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
     * adding ?debug parameter to URI.
     * 
     * @return true if client side is currently been debugged
     */
    public static boolean isDebugMode() {
        return isDebugAvailable()
                && Window.Location.getParameter("debug") != null;
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
     * Checks whether information from the web browser (e.g. uri fragment and
     * screen size) has been sent to the server.
     * 
     * @return <code>true</code> if browser information has already been sent
     * 
     * @see ApplicationConnection#getNativeBrowserDetailsParameters(String)
     */
    public boolean isBrowserDetailsSent() {
        return browserDetailsSent;
    }

    /**
     * Registers that the browser details have been sent.
     * {@link #isBrowserDetailsSent()} will return
     * <code> after this method has been invoked.
     */
    public void setBrowserDetailsSent() {
        browserDetailsSent = true;
    }

    /**
     * Checks whether the widget set version has been sent to the server. It is
     * sent in the first UIDL request.
     * 
     * @return <code>true</code> if browser information has already been sent
     * 
     * @see ApplicationConnection#getNativeBrowserDetailsParameters(String)
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

}
