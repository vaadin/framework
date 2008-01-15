/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.ContextMenu;
import com.itmill.toolkit.terminal.gwt.client.ui.IView;
import com.itmill.toolkit.terminal.gwt.client.ui.Notification;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ApplicationConnection {

    private static final String VAR_RECORD_SEPARATOR = "\u001e";

    private static final String VAR_FIELD_SEPARATOR = "\u001f";

    private final String appUri;

    private final HashMap resourcesMap = new HashMap();

    private static Console console;

    private static boolean testingMode;

    private final Vector pendingVariables = new Vector();

    private final HashMap idToPaintable = new HashMap();

    private final HashMap paintableToId = new HashMap();

    private final WidgetSet widgetSet;

    private ContextMenu contextMenu = null;

    private Timer loadTimer;
    private Timer loadTimer2;
    private Timer loadTimer3;
    private Element loadElement;

    private final IView view;

    private boolean applicationRunning = false;

    /**
     * True if each Paintable objects id is injected to DOM. Used for Testing
     * Tools.
     */
    private boolean usePaintableIdsInDOM = false;

    private Request uidlRequest;

    public ApplicationConnection(WidgetSet widgetSet) {
        this.widgetSet = widgetSet;
        appUri = getAppUri();

        if (isDebugMode()) {
            console = new DebugConsole(this);
        } else {
            console = new NullConsole();
        }

        if (checkTestingMode()) {
            usePaintableIdsInDOM = true;
            initializeTestingTools(getTestServerUri(), this);
        }

        makeUidlRequest("repaintAll=1");
        applicationRunning = true;

        // TODO remove hardcoded id name
        view = new IView("itmill-ajax-window");

    }

    private native static String getTestServerUri()
    /*-{
        return $wnd.itmill.testingToolsUri;
    }-*/;

    /**
     * Method to check if application is in testing mode. Can be used after
     * application init.
     * 
     * @return true if in testing mode
     */
    public static boolean isTestingMode() {
        return testingMode;
    }

    /**
     * Check is application is run in testing mode.
     * 
     * @return true if in testing mode
     */
    private native static boolean checkTestingMode()
    /*-{
        @com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::testingMode = $wnd.itmill.testingToolsUri ? true : false;
        return @com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::testingMode;
    }-*/;

    private native static void initializeTestingTools(String testServerUri,
            ApplicationConnection ap)
    /*-{
        $wnd.itmill.gwtClient = {};
        $wnd.itmill.gwtClient.hasActiveRequest = function() {
                return ap.@com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::hasActiveRequest()();
        }
        $wnd.itmill.startTT(testServerUri);
    }-*/;

    public static Console getConsole() {
        return console;
    }

    private native static boolean isDebugMode()
    /*-{
     var uri = $wnd.location;
     var re = /debug[^\/]*$/;
     return re.test(uri);
     }-*/;

    public native String getAppUri()
    /*-{
     var u = $wnd.itmill.appUri;
     if (u.indexOf("/") != 0 && u.indexOf("http") != 0) {
     var b = $wnd.location.href;
     var i = b.length-1;
     while (b.charAt(i) != "/" && i>0) i--;
     b = b.substring(0,i+1);
     u = b + u;
     }
     return u;
     }-*/;

    public boolean hasActiveRequest() {
        return uidlRequest.isPending();
    }

    private native String getPathInfo()
    /*-{
     return $wnd.itmill.pathInfo;
     }-*/;

    private native String getThemeUri()
    /*-{
     return $wnd.itmill.themeUri;
     }-*/;

    private void makeUidlRequest(String requestData) {

        // place loading indicator
        showLoadingIndicator();

        console.log("Making UIDL Request with params: " + requestData);
        final String uri = appUri + "/UIDL" + getPathInfo();
        final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, uri);
        rb.setHeader("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
        try {
            uidlRequest = rb.sendRequest(requestData, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    // TODO Better reporting to user
                    console.error("Got error");
                }

                public void onResponseReceived(Request request,
                        Response response) {
                    handleReceivedJSONMessage(response);
                }

            });

        } catch (final RequestException e) {
            // TODO Better reporting to user
            console.error(e.getMessage());
        }
    }

    private void showLoadingIndicator() {
        loadTimer = new Timer() {
            public void run() {
                // show initial throbber
                if (loadElement == null) {
                    loadElement = DOM.createDiv();
                    DOM.setStyleAttribute(loadElement, "position", "absolute");
                    DOM.appendChild(view.getElement(), loadElement);
                }
                DOM.setElementProperty(loadElement, "className",
                        "i-loading-indicator");
                DOM.setStyleAttribute(loadElement, "display", "block");
                // Position
                DOM.setStyleAttribute(loadElement, "top", (view
                        .getAbsoluteTop() + 6)
                        + "px");
                DOM.setStyleAttribute(loadElement, "left",
                        (view.getAbsoluteLeft()
                                + view.getOffsetWidth()
                                - DOM.getElementPropertyInt(loadElement,
                                        "offsetWidth") - 5)
                                + "px");

                // Initialize other timers
                loadTimer2 = new Timer() {
                    public void run() {
                        DOM.setElementProperty(loadElement, "className",
                                "i-loading-indicator-delay");
                    }
                };
                // Second one kicks in at 1500ms
                loadTimer2.schedule(1200);

                loadTimer3 = new Timer() {
                    public void run() {
                        DOM.setElementProperty(loadElement, "className",
                                "i-loading-indicator-wait");
                    }
                };
                // Third one kicks in at 5000ms
                loadTimer3.schedule(4700);
            }
        };
        // First one kicks in at 300ms
        loadTimer.schedule(300);
    }

    private void hideLoadingIndicator() {
        if (loadTimer != null) {
            loadTimer.cancel();
            if (loadTimer2 != null) {
                loadTimer2.cancel();
                loadTimer3.cancel();
            }
        }
        if (loadElement != null) {
            DOM.setStyleAttribute(loadElement, "display", "none");
        }
    }

    private void handleReceivedJSONMessage(Response response) {
        hideLoadingIndicator();

        final Date start = new Date();
        final String jsonText = response.getText().substring(3) + "}";
        JSONValue json;
        try {
            json = JSONParser.parse(jsonText);
        } catch (final com.google.gwt.json.client.JSONException e) {
            console.log(e.getMessage() + " - Original JSON-text:");
            console.log(jsonText);
            return;
        }
        // Handle redirect
        final JSONObject redirect = (JSONObject) ((JSONObject) json)
                .get("redirect");
        if (redirect != null) {
            final JSONString url = (JSONString) redirect.get("url");
            if (url != null) {
                console.log("redirecting to " + url.stringValue());
                redirect(url.stringValue());
                return;
            }
        }

        // Store resources
        final JSONObject resources = (JSONObject) ((JSONObject) json)
                .get("resources");
        for (final Iterator i = resources.keySet().iterator(); i.hasNext();) {
            final String key = (String) i.next();
            resourcesMap.put(key, ((JSONString) resources.get(key))
                    .stringValue());
        }

        // Store locale data
        if (((JSONObject) json).containsKey("locales")) {
            final JSONArray l = (JSONArray) ((JSONObject) json).get("locales");
            for (int i = 0; i < l.size(); i++) {
                LocaleService.addLocale((JSONObject) l.get(i));
            }
        }

        // Process changes
        final JSONArray changes = (JSONArray) ((JSONObject) json)
                .get("changes");
        for (int i = 0; i < changes.size(); i++) {
            try {
                final UIDL change = new UIDL((JSONArray) changes.get(i));
                try {
                    console.dirUIDL(change);
                } catch (final Exception e) {
                    console.log(e.getMessage());
                    // TODO: dir doesn't work in any browser although it should
                    // work (works in hosted mode)
                    // it partially did at some part but now broken.
                }
                final UIDL uidl = change.getChildUIDL(0);
                final Paintable paintable = getPaintable(uidl.getId());
                if (paintable != null) {
                    paintable.updateFromUIDL(uidl, this);
                } else {
                    if (!uidl.getTag().equals("window")) {
                        System.out.println("Received update for "
                                + uidl.getTag()
                                + ", but there is no such paintable ("
                                + uidl.getId() + ") rendered.");
                    } else {
                        view.updateFromUIDL(uidl, this);
                    }
                }
            } catch (final Throwable e) {
                e.printStackTrace();
            }
        }

        if (((JSONObject) json).containsKey("meta")) {
            final JSONObject meta = ((JSONObject) json).get("meta").isObject();
            if (meta.containsKey("focus")) {
                final String focusPid = meta.get("focus").isString()
                        .stringValue();
                final Paintable toBeFocused = getPaintable(focusPid);
                if (toBeFocused instanceof HasFocus) {
                    final HasFocus toBeFocusedWidget = (HasFocus) toBeFocused;
                    toBeFocusedWidget.setFocus(true);
                }
            }
            if (meta.containsKey("appError")) {
                JSONObject error = meta.get("appError").isObject();
                String caption = error.get("caption").isString().stringValue();
                String message = error.get("message").isString().stringValue();
                String html = "<h1>" + caption + "</h1><p>" + message + "</p>";
                new Notification(Notification.DELAY_FOREVER).show(html,
                        Notification.CENTERED, "error");
                applicationRunning = false;
            }
        }

        final long prosessingTime = (new Date().getTime()) - start.getTime();
        console.log(" Processing time was " + String.valueOf(prosessingTime)
                + "ms for " + jsonText.length() + " characters of JSON");
        console.log("Referenced paintables: " + idToPaintable.size());

    }

    // Redirect browser
    private static native void redirect(String url)
    /*-{
     $wnd.location = url;
     }-*/;

    public void registerPaintable(String id, Paintable paintable) {
        idToPaintable.put(id, paintable);
        paintableToId.put(paintable, id);
    }

    public void unregisterPaintable(Paintable p) {
        idToPaintable.remove(paintableToId.get(p));
        paintableToId.remove(p);

        if (p instanceof HasWidgets) {
            unregisterChildPaintables((HasWidgets) p);
        }
    }

    public void unregisterChildPaintables(HasWidgets container) {
        final Iterator it = container.iterator();
        while (it.hasNext()) {
            final Widget w = (Widget) it.next();
            if (w instanceof Paintable) {
                unregisterPaintable((Paintable) w);
            }
            if (w instanceof HasWidgets) {
                unregisterChildPaintables((HasWidgets) w);
            }
        }
    }

    /**
     * Returns Paintable element by its id
     * 
     * @param id
     *                Paintable ID
     */
    public Paintable getPaintable(String id) {
        return (Paintable) idToPaintable.get(id);
    }

    private void addVariableToQueue(String paintableId, String variableName,
            String encodedValue, boolean immediate, char type) {
        final String id = paintableId + VAR_FIELD_SEPARATOR + variableName
                + VAR_FIELD_SEPARATOR + type;
        for (int i = 1; i < pendingVariables.size(); i += 2) {
            if ((pendingVariables.get(i)).equals(id)) {
                pendingVariables.remove(i - 1);
                pendingVariables.remove(i - 1);
                break;
            }
        }
        pendingVariables.add(encodedValue);
        pendingVariables.add(id);
        if (immediate) {
            sendPendingVariableChanges();
        }
    }

    public void sendPendingVariableChanges() {
        if (applicationRunning) {
            final StringBuffer req = new StringBuffer();

            req.append("changes=");
            for (int i = 0; i < pendingVariables.size(); i++) {
                if (i > 0) {
                    if (i % 2 == 0) {
                        req.append(VAR_RECORD_SEPARATOR);
                    } else {
                        req.append(VAR_FIELD_SEPARATOR);
                    }
                }
                req.append(pendingVariables.get(i));
            }

            pendingVariables.clear();
            makeUidlRequest(req.toString());
        }
    }

    private static native String escapeString(String value)
    /*-{
     return encodeURIComponent(value);
     }-*/;

    public void updateVariable(String paintableId, String variableName,
            String newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, escapeString(newValue),
                immediate, 's');
    }

    public void updateVariable(String paintableId, String variableName,
            int newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, "" + newValue, immediate,
                'i');
    }

    public void updateVariable(String paintableId, String variableName,
            long newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, "" + newValue, immediate,
                'l');
    }

    public void updateVariable(String paintableId, String variableName,
            float newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, "" + newValue, immediate,
                'f');
    }

    public void updateVariable(String paintableId, String variableName,
            double newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, "" + newValue, immediate,
                'd');
    }

    public void updateVariable(String paintableId, String variableName,
            boolean newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue ? "true"
                : "false", immediate, 'b');
    }

    public void updateVariable(String paintableId, String variableName,
            Object[] values, boolean immediate) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                buf.append(",");
            }
            buf.append(escapeString(values[i].toString()));
        }
        addVariableToQueue(paintableId, variableName, buf.toString(),
                immediate, 'a');
    }

    /**
     * Update generic component features.
     * 
     * <h2>Selecting correct implementation</h2>
     * 
     * <p>
     * The implementation of a component depends on many properties, including
     * styles, component features, etc. Sometimes the user changes those
     * properties after the component has been created. Calling this method in
     * the beginning of your updateFromUIDL -method automatically replaces your
     * component with more appropriate if the requested implementation changes.
     * </p>
     * 
     * <h2>Caption, icon, error messages and description</h2>
     * 
     * <p>
     * Component can delegate management of caption, icon, error messages and
     * description to parent layout. This is optional an should be decided by
     * component author
     * </p>
     * 
     * <h2>Component visibility and disabling</h2>
     * 
     * This method will manage component visibility automatically and if
     * component is an instanceof FocusWidget, also handle component disabling
     * when needed.
     * 
     * @param component
     *                Widget to be updated, expected to implement an instance of
     *                Paintable
     * @param uidl
     *                UIDL to be painted
     * @param manageCaption
     *                True if you want to delegate caption, icon, description
     *                and error message management to parent.
     * 
     * @return Returns true iff no further painting is needed by caller
     */
    public boolean updateComponent(Widget component, UIDL uidl,
            boolean manageCaption) {

        // If the server request that a cached instance should be used, do
        // nothing
        if (uidl.getBooleanAttribute("cached")) {
            return true;
        }

        // Visibility
        boolean visible = !uidl.getBooleanAttribute("invisible");
        component.setVisible(visible);
        // Set captions
        if (manageCaption) {
            final Container parent = Util.getParentLayout(component);
            if (parent != null) {
                parent.updateCaption((Paintable) component, uidl);
            }
        }

        if (!visible) {
            return true;
        }

        // Switch to correct implementation if needed
        if (!widgetSet.isCorrectImplementation(component, uidl)) {
            final Container parent = Util.getParentLayout(component);
            if (parent != null) {
                final Widget w = widgetSet.createWidget(uidl);
                parent.replaceChildComponent(component, w);
                registerPaintable(uidl.getId(), (Paintable) w);
                ((Paintable) w).updateFromUIDL(uidl, this);
                return true;
            }
        }

        // Styles + disabled & readonly
        component.setStyleName(component.getStylePrimaryName());

        // first disabling and read-only status
        boolean enabled = true;
        if (uidl.hasAttribute("disabled")) {
            enabled = !uidl.getBooleanAttribute("disabled");
        }
        if (component instanceof FocusWidget) {
            ((FocusWidget) component).setEnabled(enabled);
        }
        if (!enabled) {
            component.addStyleName("i-disabled");
        } else {
            component.removeStyleName("i-disabled");
        }

        // add additional styles as css classes, prefixed with component default
        // stylename
        if (uidl.hasAttribute("style")) {
            final String[] styles = uidl.getStringAttribute("style").split(" ");
            for (int i = 0; i < styles.length; i++) {
                component.addStyleDependentName(styles[i]);
            }
        }

        if (usePaintableIdsInDOM) {
            DOM.setElementProperty(component.getElement(), "id", uidl.getId());
        }

        return false;
    }

    /**
     * Get either existing or new Paintable for given UIDL.
     * 
     * If corresponding Paintable has been previously painted, return it.
     * Otherwise create and register a new Paintable from UIDL. Caller must
     * update the returned Paintable from UIDL after it has been connected to
     * parent.
     * 
     * @param uidl
     *                UIDL to create Paintable from.
     * @return Either existing or new Paintable corresponding to UIDL.
     */
    public Paintable getPaintable(UIDL uidl) {
        final String id = uidl.getId();
        Paintable w = getPaintable(id);
        if (w != null) {
            return w;
        }
        w = (Paintable) widgetSet.createWidget(uidl);
        registerPaintable(id, w);
        return w;
    }

    public String getResource(String name) {
        return (String) resourcesMap.get(name);
    }

    /**
     * Singleton method to get instance of app's context menu.
     * 
     * @return IContextMenu object
     */
    public ContextMenu getContextMenu() {
        if (contextMenu == null) {
            contextMenu = new ContextMenu();
            if (usePaintableIdsInDOM) {
                DOM.setElementProperty(contextMenu.getElement(), "id",
                        "PID_TOOLKIT_CM");
            }
        }
        return contextMenu;
    }

    /**
     * Translates custom protocols in UIRL URI's to be recognizable by browser.
     * All uri's from UIDL should be routed via this method before giving them
     * to browser due URI's in UIDL may contain custom protocols like theme://.
     * 
     * @param toolkitUri
     *                toolkit URI from uidl
     * @return translated URI ready for browser
     */
    public String translateToolkitUri(String toolkitUri) {
        if (toolkitUri.startsWith("theme://")) {
            final String themeUri = getThemeUri();
            if (themeUri == null) {
                console
                        .error("Theme not set: ThemeResource will not be found. ("
                                + toolkitUri + ")");
            }
            toolkitUri = themeUri + toolkitUri.substring(7);
        }
        return toolkitUri;
    }

    public String getTheme() {
        return view.getTheme();
    }
}
