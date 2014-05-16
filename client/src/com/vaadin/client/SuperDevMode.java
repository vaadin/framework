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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.VNotification.EventListener;
import com.vaadin.client.ui.VNotification.HideEvent;

/**
 * Class that enables SuperDevMode using a ?superdevmode parameter in the url.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 * 
 */
public class SuperDevMode {

    private static final int COMPILE_TIMEOUT_IN_SECONDS = 60;
    protected static final String SKIP_RECOMPILE = "VaadinSuperDevMode_skip_recompile";

    public static class RecompileResult extends JavaScriptObject {
        protected RecompileResult() {

        }

        public final native boolean ok()
        /*-{
         return this.status == "ok";
        }-*/;
    }

    private static void recompileWidgetsetAndStartInDevMode(
            final String serverUrl) {
        VConsole.log("Recompiling widgetset using<br/>" + serverUrl
                + "<br/>and then reloading in super dev mode");
        VNotification n = new VNotification();
        n.show("<b>Recompiling widgetset, please wait</b>",
                VNotification.CENTERED, VNotification.STYLE_SYSTEM);

        JsonpRequestBuilder b = new JsonpRequestBuilder();
        b.setCallbackParam("_callback");
        b.setTimeout(COMPILE_TIMEOUT_IN_SECONDS * 1000);
        b.requestObject(serverUrl + "recompile/" + GWT.getModuleName() + "?"
                + getRecompileParameters(GWT.getModuleName()),
                new AsyncCallback<RecompileResult>() {

                    @Override
                    public void onSuccess(RecompileResult result) {
                        VConsole.log("JSONP compile call successful");

                        if (!result.ok()) {
                            VConsole.log("* result: " + result);
                            failed();
                            return;
                        }

                        setSession(
                                getSuperDevModeHookKey(),
                                getSuperDevWidgetSetUrl(GWT.getModuleName(),
                                        serverUrl));
                        setSession(SKIP_RECOMPILE, "1");

                        VConsole.log("* result: OK. Reloading");
                        Location.reload();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        VConsole.error("JSONP compile call failed");
                        // Don't log exception as they are shown as
                        // notifications
                        VConsole.error(Util.getSimpleName(caught) + ": "
                                + caught.getMessage());
                        failed();

                    }

                    private void failed() {
                        VNotification n = new VNotification();
                        n.addEventListener(new EventListener() {

                            @Override
                            public void notificationHidden(HideEvent event) {
                                recompileWidgetsetAndStartInDevMode(serverUrl);
                            }
                        });
                        n.show("Recompilation failed.<br/>"
                                + "Make sure CodeServer is running, "
                                + "check its output and click to retry",
                                VNotification.CENTERED,
                                VNotification.STYLE_SYSTEM);
                    }
                });

    }

    protected static String getSuperDevWidgetSetUrl(String widgetsetName,
            String serverUrl) {
        return serverUrl + GWT.getModuleName() + "/" + GWT.getModuleName()
                + ".nocache.js";
    }

    private native static String getRecompileParameters(String moduleName)
    /*-{
        var prop_map = $wnd.__gwt_activeModules[moduleName].bindings();
        
        // convert map to URL parameter string
        var props = [];
        for (var key in prop_map) {
           props.push(encodeURIComponent(key) + '=' + encodeURIComponent(prop_map[key]))
        }
        
        return props.join('&') + '&';
    }-*/;

    private static void setSession(String key, String value) {
        Storage.getSessionStorageIfSupported().setItem(key, value);
    }

    private static String getSession(String key) {
        return Storage.getSessionStorageIfSupported().getItem(key);
    }

    private static void removeSession(String key) {
        Storage.getSessionStorageIfSupported().removeItem(key);
    }

    protected static void disableDevModeAndReload() {
        removeSession(getSuperDevModeHookKey());
        redirect(false);
    }

    protected static void redirect(boolean devModeOn) {
        UrlBuilder createUrlBuilder = Location.createUrlBuilder();
        if (!devModeOn) {
            createUrlBuilder.removeParameter("superdevmode");
        } else {
            createUrlBuilder.setParameter("superdevmode", "");
        }

        Location.assign(createUrlBuilder.buildString());

    }

    private static String getSuperDevModeHookKey() {
        String widgetsetName = GWT.getModuleName();
        final String superDevModeKey = "__gwtDevModeHook:" + widgetsetName;
        return superDevModeKey;
    }

    private static boolean hasSession(String key) {
        return getSession(key) != null;
    }

    /**
     * The URL of the code server. The default URL (http://localhost:9876/) will
     * be used if this is empty or null.
     * 
     * @param serverUrl
     *            The url of the code server or null to use the default
     * @return true if recompile started, false if we are running in
     *         SuperDevMode
     */
    protected static boolean recompileIfNeeded(String serverUrl) {
        if (serverUrl == null || "".equals(serverUrl)) {
            serverUrl = "http://localhost:9876/";
        } else {
            serverUrl = "http://" + serverUrl + "/";
        }

        if (hasSession(SKIP_RECOMPILE)) {
            VConsole.log("Running in SuperDevMode");
            // When we get here, we are running in super dev mode

            // Remove the flag so next reload will recompile
            removeSession(SKIP_RECOMPILE);

            // Remove the gwt flag so we will not end up in dev mode if we
            // remove the url parameter manually
            removeSession(getSuperDevModeHookKey());

            return false;
        }

        recompileWidgetsetAndStartInDevMode(serverUrl);
        return true;
    }

    protected static boolean isSuperDevModeEnabledInModule() {
        String moduleName = GWT.getModuleName();
        return isSuperDevModeEnabledInModule(moduleName);
    }

    protected native static boolean isSuperDevModeEnabledInModule(
            String moduleName)
    /*-{
        if (!$wnd.__gwt_activeModules)
           return false;
        var mod = $wnd.__gwt_activeModules[moduleName];
        if (!mod)
            return false;

        if (mod.superdevmode) {
           // Running in super dev mode already, it is supported
           return true;
        }

        return !!mod.canRedirect;
    }-*/;

    /**
     * Enables SuperDevMode if the url contains the "superdevmode" parameter.
     * <p>
     * The caller should not continue initialization of the application if this
     * method returns true. The application will be restarted once compilation
     * is done and then this method will return false.
     * </p>
     * 
     * @return true if a recompile operation has started and the page will be
     *         reloaded once it is done, false if no recompilation will be done.
     */
    public static boolean enableBasedOnParameter() {
        String superDevModeParameter = Location.getParameter("superdevmode");
        if (superDevModeParameter != null) {
            // Need to check the recompile flag also because if we are running
            // in super dev mode, as a result of the recompile, the enabled
            // check will fail...
            if (!isSuperDevModeEnabledInModule()) {
                showError("SuperDevMode is disabled for this module/widgetset.<br/>"
                        + "Ensure that your module definition (.gwt.xml) does not contain <br/>"
                        + "&lt;set-configuration-property name=&quot;devModeRedirectEnabled&quot; value=&quot;false&quot; /&gt;<br/>");
                return false;
            }
            return SuperDevMode.recompileIfNeeded(superDevModeParameter);
        }
        return false;
    }

    private static void showError(String message) {
        VNotification n = new VNotification();
        n.show(message, VNotification.CENTERED_TOP, VNotification.STYLE_SYSTEM);
    }
}
