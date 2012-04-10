/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

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
import com.google.gwt.user.client.Timer;
import com.vaadin.terminal.gwt.client.ui.UnknownComponentConnector;

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
         * @see com.vaadin.terminal.gwt.server.AbstractApplicationServlet#VERSION
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

    /**
     * Builds number. For example 0-custom_tag in 5.0.0-custom_tag.
     */
    public static final String VERSION;

    /* Initialize version numbers from string replaced by build-script. */
    static {
        if ("@VERSION@".equals("@" + "VERSION" + "@")) {
            VERSION = "9.9.9.INTERNAL-DEBUG-BUILD";
        } else {
            VERSION = "@VERSION@";
        }
    }

    private static WidgetSet widgetSet = GWT.create(WidgetSet.class);

    private String id;
    private String themeUri;
    private String appUri;
    private int rootId;
    private boolean standalone;
    private ErrorMessage communicationError;
    private ErrorMessage authorizationError;
    private boolean useDebugIdInDom = true;
    private boolean usePortletURLs = false;
    private String portletUidlURLBase;

    private HashMap<Integer, String> unknownComponents;

    private Class<? extends ComponentConnector>[] classes = new Class[1024];

    private boolean browserDetailsSent = false;

    static// TODO consider to make this hashmap per application
    LinkedList<Command> callbacks = new LinkedList<Command>();

    private static int widgetsLoading;

    private static ArrayList<ApplicationConnection> runningApplications = new ArrayList<ApplicationConnection>();

    private Map<Integer, Integer> componentInheritanceMap = new HashMap<Integer, Integer>();
    private Map<Integer, String> tagToServerSideClassName = new HashMap<Integer, String>();

    public boolean usePortletURLs() {
        return usePortletURLs;
    }

    public String getPortletUidlURLBase() {
        return portletUidlURLBase;
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
     * associate it with the right Root instance on the server.
     * 
     * @return the root id
     */
    public int getRootId() {
        return rootId;
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
        rootId = jsoConfiguration.getConfigInteger("rootId").intValue();

        // null -> true
        useDebugIdInDom = jsoConfiguration.getConfigBoolean("useDebugIdInDom") != Boolean.FALSE;

        // null -> false
        usePortletURLs = jsoConfiguration.getConfigBoolean("usePortletURLs") == Boolean.TRUE;

        portletUidlURLBase = jsoConfiguration
                .getConfigString("portletUidlURLBase");

        // null -> false
        standalone = jsoConfiguration.getConfigBoolean("standalone") == Boolean.TRUE;

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

    public boolean useDebugIdInDOM() {
        return useDebugIdInDom;
    }

    public Class<? extends ComponentConnector> getWidgetClassByEncodedTag(
            int tag) {
        try {
            return classes[tag];
        } catch (Exception e) {
            // component was not present in mappings
            return UnknownComponentConnector.class;
        }
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
            classes[value] = widgetSet.getConnectorClassByTag(value, this);
            if (classes[value] == UnknownComponentConnector.class) {
                if (unknownComponents == null) {
                    unknownComponents = new HashMap<Integer, String>();
                }
                unknownComponents.put(value, key);
            }
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
    static void runWhenWidgetsLoaded(Command c) {
        if (widgetsLoading == 0) {
            c.execute();
        } else {
            callbacks.add(c);
        }
    }

    static void startWidgetLoading() {
        widgetsLoading++;
    }

    static void endWidgetLoading() {
        widgetsLoading--;
        if (widgetsLoading == 0 && !callbacks.isEmpty()) {
            for (Command cmd : callbacks) {
                cmd.execute();
            }
            callbacks.clear();
        } else if (widgetsLoading == 0 && deferredWidgetLoader != null) {
            deferredWidgetLoader.trigger();
        }

    }

    /*
     * This loop loads widget implementation that should be loaded deferred.
     */
    static class DeferredWidgetLoader extends Timer {
        private static final int FREE_LIMIT = 4;
        private static final int FREE_CHECK_TIMEOUT = 100;

        int communicationFree = 0;
        int nextWidgetIndex = 0;
        private boolean pending;

        public DeferredWidgetLoader() {
            schedule(5000);
        }

        public void trigger() {
            if (!pending) {
                schedule(FREE_CHECK_TIMEOUT);
            }
        }

        @Override
        public void schedule(int delayMillis) {
            super.schedule(delayMillis);
            pending = true;
        }

        @Override
        public void run() {
            pending = false;
            if (!isBusy()) {
                Class<? extends ComponentConnector> nextType = getNextType();
                if (nextType == null) {
                    // ensured that all widgets are loaded
                    deferredWidgetLoader = null;
                } else {
                    communicationFree = 0;
                    widgetSet.loadImplementation(nextType);
                }
            } else {
                schedule(FREE_CHECK_TIMEOUT);
            }
        }

        private Class<? extends ComponentConnector> getNextType() {
            Class<? extends ComponentConnector>[] deferredLoadedWidgets = widgetSet
                    .getDeferredLoadedWidgets();
            if (deferredLoadedWidgets.length <= nextWidgetIndex) {
                return null;
            } else {
                return deferredLoadedWidgets[nextWidgetIndex++];
            }
        }

        private boolean isBusy() {
            if (widgetsLoading > 0) {
                communicationFree = 0;
                return true;
            }
            for (ApplicationConnection app : runningApplications) {
                if (app.hasActiveRequest()) {
                    // if an UIDL request or widget loading is active, mark as
                    // busy
                    communicationFree = 0;
                    return true;
                }
            }
            communicationFree++;
            return communicationFree < FREE_LIMIT;
        }
    }

    private static DeferredWidgetLoader deferredWidgetLoader;

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
            public void onUncaughtException(Throwable e) {
                /*
                 * Note in case of null console (without ?debug) we eat
                 * exceptions. "a1 is not an object" style errors helps nobody,
                 * especially end user. It does not work tells just as much.
                 */
                VConsole.getImplementation().error(e);
            }
        });

        registerCallback(GWT.getModuleName());
        deferredWidgetLoader = new DeferredWidgetLoader();
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
        var callbackHandler = @com.vaadin.terminal.gwt.client.ApplicationConfiguration::startApplication(Ljava/lang/String;);
        $wnd.vaadin.registerWidgetset(widgetsetName, callbackHandler);
    }-*/;

    /**
     * Checks if client side is in debug mode. Practically this is invoked by
     * adding ?debug parameter to URI.
     * 
     * @return true if client side is currently been debugged
     */
    public native static boolean isDebugMode()
    /*-{
        if($wnd.vaadin.debug) {
            var parameters = $wnd.location.search;
            var re = /debug[^\/]*$/;
            return re.test(parameters);
        } else {
            return false;
        }
    }-*/;

    /**
     * Checks whether debug logging should be quiet
     * 
     * @return <code>true</code> if debug logging should be quiet
     */
    public native static boolean isQuietDebugMode()
    /*-{
        var uri = $wnd.location;
        var re = /debug=q[^\/]*$/;
        return re.test(uri);
    }-*/;

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

}
