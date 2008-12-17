package com.itmill.toolkit.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public class ApplicationConfiguration {

    private String id;
    private String themeUri;
    private String pathInfo;
    private String appUri;
    private JavaScriptObject versionInfo;

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

    public JavaScriptObject getVersionInfoJSObject() {
        return versionInfo;
    }

    private native void loadFromDOM()
    /*-{

        var id = this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConfiguration::id;
        if($wnd.itmill.toolkitConfigurations && $wnd.itmill.toolkitConfigurations[id]) {
            var jsobj = $wnd.itmill.toolkitConfigurations[id];
            var uri = jsobj.appUri;
            if(uri[uri.length -1] != "/") {
                uri = uri + "/";
            }
            this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConfiguration::appUri = uri;
            this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConfiguration::pathInfo = jsobj.pathInfo;
            this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConfiguration::themeUri = jsobj.themeUri;
            if(jsobj.versionInfo) {
                this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConfiguration::versionInfo = jsobj.versionInfo;
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
        ArrayList appIds = new ArrayList();
        loadAppIdListFromDOM(appIds);

        for (Iterator it = appIds.iterator(); it.hasNext();) {
            String appId = (String) it.next();
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

    private native static void loadAppIdListFromDOM(ArrayList list)
    /*-{
     
         var j;
         for(j in $wnd.itmill.toolkitConfigurations) {
             list.@java.util.Collection::add(Ljava/lang/Object;)(j);
         }
     }-*/;

    public static ApplicationConfiguration getConfigFromDOM(String appId) {
        ApplicationConfiguration conf = new ApplicationConfiguration();
        conf.setAppId(appId);
        conf.loadFromDOM();
        return conf;
    }

    public native String getSerletVersion()
    /*-{
        return this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConfiguration::versionInfo.toolkitVersion;
    }-*/;

    public native String getApplicationVersion()
    /*-{
        return this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConfiguration::versionInfo.applicationVersion;
    }-*/;
}
