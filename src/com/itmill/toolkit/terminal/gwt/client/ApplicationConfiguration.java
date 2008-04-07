package com.itmill.toolkit.terminal.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;

public class ApplicationConfiguration {

    private String id;
    private String themeUri;
    private String pathInfo;
    private String appUri;
    private JavaScriptObject versionInfo;

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

    public native static void loadAppIdListFromDOM(ArrayList list)
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

}
