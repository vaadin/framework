/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private JavaScriptObject versionInfo;
    private String windowName;
    private boolean standalone;
    private String communicationErrorCaption;
    private String communicationErrorMessage;
    private String communicationErrorUrl;
    private String authorizationErrorCaption;
    private String authorizationErrorMessage;
    private String authorizationErrorUrl;
    private String requiredWidgetset;
    private boolean useDebugIdInDom = true;
    private boolean usePortletURLs = false;
    private String portletUidlURLBase;

    private HashMap<String, String> unknownComponents;

    private Class<? extends Paintable>[] classes = new Class[1024];

    private String windowId;

    static// TODO consider to make this hashmap per application
    LinkedList<Command> callbacks = new LinkedList<Command>();

    private static int widgetsLoading;

    private static ArrayList<ApplicationConnection> unstartedApplications = new ArrayList<ApplicationConnection>();
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

    public void setInitialWindowName(String name) {
        windowName = name;
    }

    public String getInitialWindowName() {
        return windowName;
    }

    public JavaScriptObject getVersionInfoJSObject() {
        return versionInfo;
    }

    public String getCommunicationErrorCaption() {
        return communicationErrorCaption;
    }

    public String getCommunicationErrorMessage() {
        return communicationErrorMessage;
    }

    public String getCommunicationErrorUrl() {
        return communicationErrorUrl;
    }

    public String getAuthorizationErrorCaption() {
        return authorizationErrorCaption;
    }

    public String getAuthorizationErrorMessage() {
        return authorizationErrorMessage;
    }

    public String getAuthorizationErrorUrl() {
        return authorizationErrorUrl;
    }

    public String getRequiredWidgetset() {
        return requiredWidgetset;
    }

    private native void loadFromDOM()
    /*-{

        var id = this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::id;
        if($wnd.vaadin.vaadinConfigurations && $wnd.vaadin.vaadinConfigurations[id]) {
            var jsobj = $wnd.vaadin.vaadinConfigurations[id];
            var uri = jsobj.appUri;
            if(uri != null && uri[uri.length -1] != "/") {
                uri = uri + "/";
            }
            this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::appUri = uri;
            this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::themeUri = jsobj.themeUri;
            if(jsobj.windowName) {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::windowName = jsobj.windowName;
            }
            if('useDebugIdInDom' in jsobj && typeof(jsobj.useDebugIdInDom) == "boolean") {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::useDebugIdInDom = jsobj.useDebugIdInDom;
            }
            if(jsobj.versionInfo) {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::versionInfo = jsobj.versionInfo;
            }
            if(jsobj.comErrMsg) {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::communicationErrorCaption = jsobj.comErrMsg.caption;
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::communicationErrorMessage = jsobj.comErrMsg.message;
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::communicationErrorUrl = jsobj.comErrMsg.url;
            }
            if(jsobj.authErrMsg) {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::authorizationErrorCaption = jsobj.authErrMsg.caption;
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::authorizationErrorMessage = jsobj.authErrMsg.message;
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::authorizationErrorUrl = jsobj.authErrMsg.url;
            }
            if (jsobj.usePortletURLs) {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::usePortletURLs = jsobj.usePortletURLs;
            }
            if (jsobj.portletUidlURLBase) {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::portletUidlURLBase = jsobj.portletUidlURLBase;
            }
            if (jsobj.standalone) {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::standalone = true;
            }
            if (jsobj.widgetset) {
                this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::requiredWidgetset = jsobj.widgetset;
            }
        } else {
            $wnd.alert("Vaadin app failed to initialize: " + this.id);
        }

     }-*/;

    /**
     * Inits the ApplicationConfiguration by reading the DOM and instantiating
     * ApplicationConnections accordingly. Call {@link #startNextApplication()}
     * to actually start the applications.
     * 
     * @param widgetset
     *            the widgetset that is running the apps
     */
    public static void initConfigurations() {

        ArrayList<String> appIds = new ArrayList<String>();
        loadAppIdListFromDOM(appIds);

        for (Iterator<String> it = appIds.iterator(); it.hasNext();) {
            String appId = it.next();
            ApplicationConfiguration appConf = getConfigFromDOM(appId);
            if (canStartApplication(appConf)) {
                ApplicationConnection a = GWT
                        .create(ApplicationConnection.class);
                a.init(widgetSet, appConf);
                unstartedApplications.add(a);
                consumeApplication(appId);
            } else {
                VConsole.log("Application "
                        + appId
                        + " was not started. Provided widgetset did not match with this module.");
            }
        }

    }

    /**
     * Marks an applicatin with given id to be initialized. Suggesting other
     * modules should not try to start this application anymore.
     * 
     * @param appId
     */
    private native static void consumeApplication(String appId)
    /*-{
         $wnd.vaadin.vaadinConfigurations[appId].initialized = true;
    }-*/;

    private static boolean canStartApplication(ApplicationConfiguration appConf) {
        return appConf.getRequiredWidgetset() == null
                || appConf.getRequiredWidgetset().equals(GWT.getModuleName());
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
        if (unstartedApplications.size() > 0) {
            ApplicationConnection a = unstartedApplications.remove(0);
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

    private native static void loadAppIdListFromDOM(ArrayList<String> list)
    /*-{
         var j;
         for(j in $wnd.vaadin.vaadinConfigurations) {
             if(!$wnd.vaadin.vaadinConfigurations[j].initialized) {
                 list.@java.util.Collection::add(Ljava/lang/Object;)(j);
             }
         }
     }-*/;

    public static ApplicationConfiguration getConfigFromDOM(String appId) {
        ApplicationConfiguration conf = new ApplicationConfiguration();
        conf.setAppId(appId);
        conf.loadFromDOM();
        return conf;
    }

    public native String getServletVersion()
    /*-{
        return this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::versionInfo.vaadinVersion;
    }-*/;

    public native String getApplicationVersion()
    /*-{
        return this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::versionInfo.applicationVersion;
    }-*/;

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
            } else if (key == "com.vaadin.ui.Window") {
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
        } else if(widgetsLoading == 0 && deferredWidgetLoader != null) {
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
            if(!pending) {
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

        // Enable IE6 Background image caching
        if (BrowserInfo.get().isIE6()) {
            enableIE6BackgroundImageCache();
        }
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

        initConfigurations();
        startNextApplication();
    }

    // From ImageSrcIE6
    private static native void enableIE6BackgroundImageCache()
    /*-{
       // Fix IE background image refresh bug, present through IE6
       // see http://www.mister-pixel.com/#Content__state=is_that_simple
       // this only works with IE6 SP1+
       try {
         $doc.execCommand("BackgroundImageCache", false, true);
       } catch (e) {
         // ignore error on other browsers
       }
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

    private native static boolean isQuietDebugMode()
    /*-{
        var uri = $wnd.location;
        var re = /debug=q[^\/]*$/;
        return re.test(uri);
    }-*/;

}
