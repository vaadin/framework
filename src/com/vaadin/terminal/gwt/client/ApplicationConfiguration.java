package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public class ApplicationConfiguration {

    // can only be inited once, to avoid multiple-entrypoint-problem
    private static WidgetSet initedWidgetSet;

    private String id;
    private String themeUri;
    private String pathInfo;
    private String appUri;
    private JavaScriptObject versionInfo;
    private String windowName;
    private String communicationErrorCaption;
    private String communicationErrorMessage;
    private String communicationErrorUrl;
    private boolean useDebugIdInDom = true;

    private static ArrayList<ApplicationConnection> unstartedApplications = new ArrayList<ApplicationConnection>();
    private static ArrayList<ApplicationConnection> runningApplications = new ArrayList<ApplicationConnection>();

    public String getRootPanelId() {
        return id;
    }

    public String getApplicationUri() {
        return appUri;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getThemeUri() {
        return themeUri;
    }

    public void setAppId(String appId) {
        id = appId;
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

    private native void loadFromDOM()
    /*-{

        var id = this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::id;
        if($wnd.vaadin.toolkitConfigurations && $wnd.vaadin.toolkitConfigurations[id]) {
            var jsobj = $wnd.vaadin.toolkitConfigurations[id];
            var uri = jsobj.appUri;
            if(uri[uri.length -1] != "/") {
                uri = uri + "/";
            }
            this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::appUri = uri;
            this.@com.vaadin.terminal.gwt.client.ApplicationConfiguration::pathInfo = jsobj.pathInfo;
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
        
        } else {
            $wnd.alert("Toolkit app failed to initialize: " + this.id);
        }
     
     }-*/;

    /**
     * Inits the ApplicationConfiguration by reading the DOM and instantiating
     * ApplicationConenctions accordingly. Call {@link #startNextApplication()}
     * to actually start the applications.
     * 
     * @param widgetset
     *            the widgetset that is running the apps
     */
    public static void initConfigurations(WidgetSet widgetset) {
        String wsname = widgetset.getClass().getName();
        String module = GWT.getModuleName();
        int lastdot = module.lastIndexOf(".");
        String base = module.substring(0, lastdot);
        String simpleName = module.substring(lastdot + 1);
        if (!wsname.startsWith(base) || !wsname.endsWith(simpleName)) {
            // WidgetSet module name does not match implementation name;
            // probably inherited WidgetSet with entry-point. Skip.
            GWT.log("Ignored init for " + wsname + " when starting " + module,
                    null);
            return;
        }

        if (initedWidgetSet != null) {
            // Something went wrong: multiple widgetsets inited
            String msg = "Tried to init " + widgetset.getClass().getName()
                    + ", but " + initedWidgetSet.getClass().getName()
                    + " is already inited.";
            System.err.println(msg);
            throw new IllegalStateException(msg);
        }
        initedWidgetSet = widgetset;
        ArrayList<String> appIds = new ArrayList<String>();
        loadAppIdListFromDOM(appIds);

        for (Iterator<String> it = appIds.iterator(); it.hasNext();) {
            String appId = it.next();
            ApplicationConfiguration appConf = getConfigFromDOM(appId);
            ApplicationConnection a = new ApplicationConnection(widgetset,
                    appConf);
            unstartedApplications.add(a);
        }
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
            return false;
        }
    }

    public static List<ApplicationConnection> getRunningApplications() {
        return runningApplications;
    }

    private native static void loadAppIdListFromDOM(ArrayList<String> list)
    /*-{
         var j;
         for(j in $wnd.vaadin.toolkitConfigurations) {
             list.@java.util.Collection::add(Ljava/lang/Object;)(j);
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
}
