/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.vaadin.terminal.gwt.client.ui.VUnknownComponent;

public class ApplicationConfiguration implements EntryPoint {

    private static class JsoConfiguration extends JavaScriptObject {
        protected JsoConfiguration() {
            // JSO Constructor
        }

        private native String getConfigString(String name)
        /*-{
            var value = this.getConfig(name);
            if (!value) {
                return null;
            } else {
                return value +"";
            } 
        }-*/;

        private native Boolean getConfigBoolean(String name)
        /*-{
            var value = this.getConfig(name);
            if (value === null || value === undefined) {
                return null;
            } else {
                return @java.lang.Boolean::valueOf(Z)(value);
            } 
        }-*/;

        private native Integer getConfigInteger(String name)
        /*-{
            var value = this.getConfig(name);
            if (value === null || value === undefined) {
                return null;
            } else {
                return @java.lang.Integer::valueOf(I)(value);
            } 
        }-*/;

        private native ErrorMessage getConfigError(String name)
        /*-{
            return this.getConfig(name);
        }-*/;

        private native JavaScriptObject getVersionInfoJSObject()
        /*-{
            return this.getConfig("versionInfo");
        }-*/;

        private native String getVaadinVersion()
        /*-{
            return this.getConfig("versionInfo").vaadinVersion;
        }-*/;

        private native String getApplicationVersion()
        /*-{
            return this.getConfig("versionInfo").applicationVersion;
        }-*/;
    }

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

    private HashMap<String, String> unknownComponents;

    private Class<? extends Paintable>[] classes = new Class[1024];

    private String windowId;

    static// TODO consider to make this hashmap per application
    LinkedList<Command> callbacks = new LinkedList<Command>();

    private static int widgetsLoading;

    private static ArrayList<ApplicationConnection> runningApplications = new ArrayList<ApplicationConnection>();

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

    public String getThemeUri() {
        return themeUri;
    }

    public void setAppId(String appId) {
        id = appId;
    }

    /**
     * @return true if the application is served by std. Vaadin servlet and is
     *         considered to be the only or main content of the host page.
     */
    public boolean isStandalone() {
        return standalone;
    }

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

    }

    /**
     * Starts the next unstarted application. The WidgetSet should call this
     * once to start the first application; after that, each application should
     * call this once it has started. This ensures that the applications are
     * started synchronously, which is neccessary to avoid session-id problems.
     * 
     * @return true if an unstarted application was found
     */
    public static boolean startNextApplication() {
        String applicationId = getNextUnstartedApplicationId(GWT
                .getModuleName());
        if (applicationId != null) {
            ApplicationConfiguration appConf = getConfigFromDOM(applicationId);
            ApplicationConnection a = GWT.create(ApplicationConnection.class);
            a.init(widgetSet, appConf);
            a.start();
            runningApplications.add(a);
            return true;
        } else {
            deferredWidgetLoader = new DeferredWidgetLoader();
            return false;
        }
    }

    public static List<ApplicationConnection> getRunningApplications() {
        return runningApplications;
    }

    private native static String getNextUnstartedApplicationId(
            String widgetsetName)
    /*-{
        return $wnd.vaadin.popWidgetsetApp(widgetsetName);
     }-*/;

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

    public Class<? extends Paintable> getWidgetClassByEncodedTag(String tag) {
        try {
            int parseInt = Integer.parseInt(tag);
            return classes[parseInt];
        } catch (Exception e) {
            // component was not present in mappings
            return VUnknownComponent.class;
        }
    }

    public void addComponentMappings(ValueMap valueMap, WidgetSet widgetSet) {
        JsArrayString keyArray = valueMap.getKeyArray();
        for (int i = 0; i < keyArray.length(); i++) {
            String key = keyArray.get(i).intern();
            int value = valueMap.getInt(key);
            classes[value] = widgetSet.getImplementationByClassName(key);
            if (classes[value] == VUnknownComponent.class) {
                if (unknownComponents == null) {
                    unknownComponents = new HashMap<String, String>();
                }
                unknownComponents.put("" + value, key);
            } else if (key == "com.vaadin.ui.Root") {
                windowId = "" + value;
            }
        }
    }

    /**
     * @return the integer value that is used to code top level windows
     *         "com.vaadin.ui.Window"
     */
    String getEncodedWindowTag() {
        return windowId;
    }

    String getUnknownServerClassNameByEncodedTagName(String tag) {
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
                Class<? extends Paintable> nextType = getNextType();
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

        private Class<? extends Paintable> getNextType() {
            Class<? extends Paintable>[] deferredLoadedWidgets = widgetSet
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
            VDebugConsole console = GWT.create(VDebugConsole.class);
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

        startNextApplication();
    }

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

    private native static boolean isQuietDebugMode()
    /*-{
        var uri = $wnd.location;
        var re = /debug=q[^\/]*$/;
        return re.test(uri);
    }-*/;

}
