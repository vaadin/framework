/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.impl.HTTPRequestImpl;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.FloatSize;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation.Size;
import com.itmill.toolkit.terminal.gwt.client.ui.Field;
import com.itmill.toolkit.terminal.gwt.client.ui.IContextMenu;
import com.itmill.toolkit.terminal.gwt.client.ui.INotification;
import com.itmill.toolkit.terminal.gwt.client.ui.IView;
import com.itmill.toolkit.terminal.gwt.client.ui.INotification.HideEvent;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ApplicationConnection {
    private static final String MODIFIED_CLASSNAME = "i-modified";

    private static final String REQUIRED_CLASSNAME_EXT = "-required";

    private static final String ERROR_CLASSNAME_EXT = "-error";

    public static final String VAR_RECORD_SEPARATOR = "\u001e";

    public static final String VAR_FIELD_SEPARATOR = "\u001f";

    public static final String VAR_BURST_SEPARATOR = "\u001d";

    public static final String UIDL_SECURITY_HEADER = "com.itmill.seckey";

    private static String uidl_security_key = "init";

    private final HashMap resourcesMap = new HashMap();

    private static Console console;

    private static boolean testingMode;

    private final Vector pendingVariables = new Vector();

    private final HashMap<String, Paintable> idToPaintable = new HashMap<String, Paintable>();

    private final HashMap<Paintable, String> paintableToId = new HashMap<Paintable, String>();

    /** Contains ExtendedTitleInfo by paintable id */
    private final HashMap<Paintable, TooltipInfo> paintableToTitle = new HashMap<Paintable, TooltipInfo>();

    private final HashMap<Widget, FloatSize> componentRelativeSizes = new HashMap<Widget, FloatSize>();
    private final HashMap<Widget, Size> componentOffsetSizes = new HashMap<Widget, Size>();

    private final WidgetSet widgetSet;

    private IContextMenu contextMenu = null;

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

    /**
     * Contains reference for client wrapper given to Testing Tools.
     * 
     * Used in JSNI functions
     * 
     */
    @SuppressWarnings("unused")
    private final JavaScriptObject ttClientWrapper = null;

    private int activeRequests = 0;

    /** Parameters for this application connection loaded from the web-page */
    private final ApplicationConfiguration configuration;

    /** List of pending variable change bursts that must be submitted in order */
    private final Vector pendingVariableBursts = new Vector();

    /** Timer for automatic refirect to SessionExpiredURL */
    private Timer redirectTimer;

    /** redirectTimer scheduling interval in seconds */
    private int sessionExpirationInterval;

    private ArrayList<Widget> relativeSizeChanges = new ArrayList<Widget>();;
    private ArrayList<Widget> captionSizeChanges = new ArrayList<Widget>();;

    public ApplicationConnection(WidgetSet widgetSet,
            ApplicationConfiguration cnf) {
        this.widgetSet = widgetSet;
        configuration = cnf;

        if (isDebugMode()) {
            console = new IDebugConsole(this, cnf, !isQuietDebugMode());
        } else {
            console = new NullConsole();
        }

        if (checkTestingMode()) {
            usePaintableIdsInDOM = true;
            initializeTestingTools();
            Window.addWindowCloseListener(new WindowCloseListener() {
                public void onWindowClosed() {
                    uninitializeTestingTools();
                }

                public String onWindowClosing() {
                    return null;
                }
            });
        }

        initializeClientHooks();

        view = new IView(cnf.getRootPanelId());
        showLoadingIndicator();

    }

    /**
     * Starts this application. Don't call this method directly - it's called by
     * {@link ApplicationConfiguration#startNextApplication()}, which should be
     * called once this application has started (first response received) or
     * failed to start. This ensures that the applications are started in order,
     * to avoid session-id problems.
     */
    void start() {
        makeUidlRequest("", true, false);
    }

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
        try {
            @com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::testingMode = $wnd.top.itmill && $wnd.top.itmill.registerToTT ? true : false;
            return @com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::testingMode;
        } catch(e) {
            // if run in iframe SOP may cause exception, return false then
            return false;
        }
    }-*/;

    private native void initializeTestingTools()
    /*-{
         var ap = this;
         var client = {};
         client.isActive = function() {
             return ap.@com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::hasActiveRequest()();
         }
         var vi = ap.@com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::getVersionInfo()();
         if (vi) {
             client.getVersionInfo = function() {
                 return vi;
             }
         }
         $wnd.top.itmill.registerToTT(client);
         this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::ttClientWrapper = client;
    }-*/;

    /**
     * Helper for tt initialization
     */
    @SuppressWarnings("unused")
    private JavaScriptObject getVersionInfo() {
        return configuration.getVersionInfoJSObject();
    }

    private native void uninitializeTestingTools()
    /*-{
         $wnd.top.itmill.unregisterFromTT(this.@com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::ttClientWrapper);
    }-*/;

    /**
     * Publishes a JavaScript API for mash-up applications.
     * <ul>
     * <li><code>itmill.forceSync()</code> sends pending variable changes, in
     * effect synchronizing the server and client state. This is done for all
     * applications on host page.</li>
     * </ul>
     * 
     * TODO make this multi-app aware
     */
    private native void initializeClientHooks()
    /*-{
        var app = this;
        var oldSync;
        if($wnd.itmill.forceSync) {
            oldSync = $wnd.itmill.forceSync;
        }
        $wnd.itmill.forceSync = function() {
            if(oldSync) {
                oldSync();
            }
            app.@com.itmill.toolkit.terminal.gwt.client.ApplicationConnection::sendPendingVariableChanges()();
        }
    }-*/;

    public static Console getConsole() {
        return console;
    }

    private native static boolean isDebugMode()
    /*-{
        if($wnd.itmill.debug) {
            var uri = $wnd.location;
            var re = /debug[^\/]*$/;
            return re.test(uri);
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

    public String getAppUri() {
        return configuration.getApplicationUri();
    };

    public boolean hasActiveRequest() {
        return (activeRequests > 0);
    }

    private void makeUidlRequest(String requestData, boolean repaintAll,
            boolean forceSync) {
        startRequest();

        // Security: double cookie submission pattern
        requestData = uidl_security_key + VAR_BURST_SEPARATOR + requestData;

        console.log("Making UIDL Request with params: " + requestData);
        String uri = getAppUri() + "UIDL" + configuration.getPathInfo();
        if (repaintAll) {
            uri += "?repaintAll=1";
        }
        if (windowName != null && windowName.length() > 0) {
            uri += (repaintAll ? "&" : "?") + "windowName=" + windowName;
        }

        if (!forceSync) {
            final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,
                    uri);
            rb.setHeader("Content-Type", "text/plain;charset=utf-8");
            try {
                rb.sendRequest(requestData, new RequestCallback() {
                    public void onError(Request request, Throwable exception) {
                        // TODO Better reporting to user
                        console.error("Got error");
                        endRequest();
                        if (!applicationRunning) {
                            // start failed, let's try to start the next app
                            ApplicationConfiguration.startNextApplication();
                        }
                    }

                    public void onResponseReceived(Request request,
                            Response response) {
                        if ("init".equals(uidl_security_key)) {
                            // Read security key
                            String key = response
                                    .getHeader(UIDL_SECURITY_HEADER);
                            if (null != key) {
                                uidl_security_key = key;
                            }
                        }
                        if (applicationRunning) {
                            handleReceivedJSONMessage(response);
                        } else {
                            applicationRunning = true;
                            handleWhenCSSLoaded(response);
                            ApplicationConfiguration.startNextApplication();
                        }
                    }

                    int cssWaits = 0;
                    static final int MAX_CSS_WAITS = 20;

                    private void handleWhenCSSLoaded(final Response response) {
                        int heightOfLoadElement = DOM.getElementPropertyInt(
                                loadElement, "offsetHeight");
                        if (heightOfLoadElement == 0
                                && cssWaits < MAX_CSS_WAITS) {
                            (new Timer() {
                                @Override
                                public void run() {
                                    handleWhenCSSLoaded(response);
                                }
                            }).schedule(50);
                            console
                                    .log("Assuming CSS loading is not complete, "
                                            + "postponing render phase. "
                                            + "(.i-loading-indicator height == 0)");
                            cssWaits++;
                        } else {
                            handleReceivedJSONMessage(response);
                            if (cssWaits >= MAX_CSS_WAITS) {
                                console
                                        .error("CSS files may have not loaded properly.");
                            }
                        }
                    }

                });

            } catch (final RequestException e) {
                ClientExceptionHandler.displayError(e);
                endRequest();
            }
        } else {
            // Synchronized call, discarded response

            syncSendForce(((HTTPRequestImpl) GWT.create(HTTPRequestImpl.class))
                    .createXmlHTTPRequest(), uri, requestData);
        }
    }

    private native void syncSendForce(JavaScriptObject xmlHttpRequest,
            String uri, String requestData)
    /*-{
         try {
             xmlHttpRequest.open("POST", uri, false);
             xmlHttpRequest.setRequestHeader("Content-Type", "text/plain;charset=utf-8");
             xmlHttpRequest.send(requestData);
    } catch (e) {
        // No errors are managed as this is synchronous forceful send that can just fail
    }
                    
    }-*/;

    private void startRequest() {
        activeRequests++;
        // show initial throbber
        if (loadTimer == null) {
            loadTimer = new Timer() {
                @Override
                public void run() {
                    showLoadingIndicator();
                }
            };
            // First one kicks in at 300ms
        }
        loadTimer.schedule(300);
    }

    private void endRequest() {
        checkForPendingVariableBursts();
        activeRequests--;
        // deferring to avoid flickering
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                if (activeRequests == 0) {
                    hideLoadingIndicator();
                }
            }
        });
    }

    /**
     * This method is called after applying uidl change set to application.
     * 
     * It will clean current and queued variable change sets. And send next
     * change set if it exists.
     */
    private void checkForPendingVariableBursts() {
        cleanVariableBurst(pendingVariables);
        if (pendingVariableBursts.size() > 0) {
            for (Iterator iterator = pendingVariableBursts.iterator(); iterator
                    .hasNext();) {
                cleanVariableBurst((Vector) iterator.next());
            }
            Vector nextBurst = (Vector) pendingVariableBursts.firstElement();
            pendingVariableBursts.remove(0);
            buildAndSendVariableBurst(nextBurst, false);
        }
    }

    /**
     * Cleans given queue of variable changes of such changes that came from
     * components that do not exist anymore.
     * 
     * @param variableBurst
     */
    private void cleanVariableBurst(Vector variableBurst) {
        for (int i = 1; i < variableBurst.size(); i += 2) {
            String id = (String) variableBurst.get(i);
            id = id.substring(0, id.indexOf(VAR_FIELD_SEPARATOR));
            if (!idToPaintable.containsKey(id)) {
                // variable owner does not exist anymore
                variableBurst.remove(i - 1);
                variableBurst.remove(i - 1);
                i -= 2;
                ApplicationConnection.getConsole().log(
                        "Removed variable from removed component: " + id);
            }
        }
    }

    private void showLoadingIndicator() {
        // show initial throbber
        if (loadElement == null) {
            loadElement = DOM.createDiv();
            DOM.setStyleAttribute(loadElement, "position", "absolute");
            DOM.appendChild(view.getElement(), loadElement);
            ApplicationConnection.getConsole().log("inserting load indicator");
        }
        DOM.setElementProperty(loadElement, "className", "i-loading-indicator");
        DOM.setStyleAttribute(loadElement, "display", "block");
        final int updatedX = Window.getScrollLeft() + view.getAbsoluteLeft()
                + view.getOffsetWidth()
                - DOM.getElementPropertyInt(loadElement, "offsetWidth") - 5;
        DOM.setStyleAttribute(loadElement, "left", updatedX + "px");
        final int updatedY = Window.getScrollTop() + 6 + view.getAbsoluteTop();
        DOM.setStyleAttribute(loadElement, "top", updatedY + "px");
        // Initialize other timers
        loadTimer2 = new Timer() {
            @Override
            public void run() {
                DOM.setElementProperty(loadElement, "className",
                        "i-loading-indicator-delay");
            }
        };
        // Second one kicks in at 1500ms from request start
        loadTimer2.schedule(1200);

        loadTimer3 = new Timer() {
            @Override
            public void run() {
                DOM.setElementProperty(loadElement, "className",
                        "i-loading-indicator-wait");
            }
        };
        // Third one kicks in at 5000ms from request start
        loadTimer3.schedule(4700);
    }

    private void hideLoadingIndicator() {
        if (loadTimer != null) {
            loadTimer.cancel();
            if (loadTimer2 != null) {
                loadTimer2.cancel();
                loadTimer3.cancel();
            }
            loadTimer = null;
        }
        if (loadElement != null) {
            DOM.setStyleAttribute(loadElement, "display", "none");
        }
    }

    private void handleReceivedJSONMessage(Response response) {
        final Date start = new Date();
        String jsonText = response.getText();
        // for(;;);[realjson]
        jsonText = jsonText.substring(9, jsonText.length() - 1);
        JSONValue json;
        try {
            json = JSONParser.parse(jsonText);
        } catch (final com.google.gwt.json.client.JSONException e) {
            endRequest();
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

        JSONObject meta = null;
        if (((JSONObject) json).containsKey("meta")) {
            meta = ((JSONObject) json).get("meta").isObject();
            if (meta.containsKey("repaintAll")) {
                view.clear();
                idToPaintable.clear();
                paintableToId.clear();
            }
            if (meta.containsKey("timedRedirect")) {
                final JSONObject timedRedirect = meta.get("timedRedirect")
                        .isObject();
                redirectTimer = new Timer() {
                    @Override
                    public void run() {
                        redirect(timedRedirect.get("url").isString()
                                .stringValue());
                    }
                };
                sessionExpirationInterval = Integer.parseInt(timedRedirect.get(
                        "interval").toString());
            }
        }
        if (redirectTimer != null) {
            redirectTimer.schedule(1000 * sessionExpirationInterval);
        }
        // Process changes
        final JSONArray changes = (JSONArray) ((JSONObject) json)
                .get("changes");

        Vector<Widget> updatedWidgets = new Vector<Widget>();
        relativeSizeChanges.clear();
        captionSizeChanges.clear();

        for (int i = 0; i < changes.size(); i++) {
            try {
                final UIDL change = new UIDL((JSONArray) changes.get(i));
                try {
                    console.dirUIDL(change);
                } catch (final Exception e) {
                    ClientExceptionHandler.displayError(e);
                    // TODO: dir doesn't work in any browser although it should
                    // work (works in hosted mode)
                    // it partially did at some part but now broken.
                }
                final UIDL uidl = change.getChildUIDL(0);
                final Paintable paintable = getPaintable(uidl.getId());
                if (paintable != null) {
                    paintable.updateFromUIDL(uidl, this);
                    // paintable may have changed during render to another
                    // implementation, use the new one for updated widgets map
                    updatedWidgets
                            .add((Widget) idToPaintable.get(uidl.getId()));
                } else {
                    if (!uidl.getTag().equals("window")) {
                        ClientExceptionHandler
                                .displayError("Received update for "
                                        + uidl.getTag()
                                        + ", but there is no such paintable ("
                                        + uidl.getId() + ") rendered.");
                    } else {
                        view.updateFromUIDL(uidl, this);
                    }
                }
            } catch (final Throwable e) {
                ClientExceptionHandler.displayError(e);
            }
        }

        // Check which widgets' size has been updated
        Set<Widget> sizeUpdatedWidgets = new HashSet<Widget>();

        updatedWidgets.addAll(relativeSizeChanges);
        sizeUpdatedWidgets.addAll(captionSizeChanges);

        for (Widget widget : updatedWidgets) {
            Size oldSize = componentOffsetSizes.get(widget);
            Size newSize = new Size(widget.getOffsetWidth(), widget
                    .getOffsetHeight());

            if (oldSize == null || !oldSize.equals(newSize)) {
                sizeUpdatedWidgets.add(widget);
                componentOffsetSizes.put(widget, newSize);
            }

        }

        Util.componentSizeUpdated(sizeUpdatedWidgets);

        if (meta != null) {
            if (meta.containsKey("appError")) {
                JSONObject error = meta.get("appError").isObject();
                JSONValue val = error.get("caption");
                String html = "";
                if (val.isString() != null) {
                    html += "<h1>" + val.isString().stringValue() + "</h1>";
                }
                val = error.get("message");
                if (val.isString() != null) {
                    html += "<p>" + val.isString().stringValue() + "</p>";
                }
                val = error.get("url");
                String url = null;
                if (val.isString() != null) {
                    url = val.isString().stringValue();
                }

                if (html.length() != 0) {
                    INotification n = new INotification(1000 * 60 * 45); //45min
                    n.addEventListener(new NotificationRedirect(url));
                    n.show(html, INotification.CENTERED_TOP,
                            INotification.STYLE_SYSTEM);
                } else {
                    redirect(url);
                }
                applicationRunning = false;
            }
        }

        final long prosessingTime = (new Date().getTime()) - start.getTime();
        console.log(" Processing time was " + String.valueOf(prosessingTime)
                + "ms for " + jsonText.length() + " characters of JSON");
        console.log("Referenced paintables: " + idToPaintable.size());

        endRequest();
    }

    /**
     * This method assures that all pending variable changes are sent to server.
     * Method uses synchronized xmlhttprequest and does not return before the
     * changes are sent. No UIDL updates are processed and thut UI is left in
     * inconsistent state. This method should be called only when closing
     * windows - normally sendPendingVariableChanges() should be used.
     */
    public void sendPendingVariableChangesSync() {
        pendingVariableBursts.add(pendingVariables);
        Vector nextBurst = (Vector) pendingVariableBursts.firstElement();
        pendingVariableBursts.remove(0);
        buildAndSendVariableBurst(nextBurst, true);
    }

    // Redirect browser, null reloads current page
    private static native void redirect(String url)
    /*-{
      if (url) {
         $wnd.location = url;
      } else {
          $wnd.location = $wnd.location;
      }
     }-*/;

    public void registerPaintable(String id, Paintable paintable) {
        idToPaintable.put(id, paintable);
        paintableToId.put(paintable, id);
    }

    public void unregisterPaintable(Paintable p) {
        String id = paintableToId.get(p);
        idToPaintable.remove(id);
        paintableToTitle.remove(id);
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
            } else if (w instanceof HasWidgets) {
                unregisterChildPaintables((HasWidgets) w);
            }
        }
    }

    /**
     * Returns Paintable element by its id
     * 
     * @param id
     *            Paintable ID
     */
    public Paintable getPaintable(String id) {
        return idToPaintable.get(id);
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

    /**
     * This method sends currently queued variable changes to server. It is
     * called when immediate variable update must happen.
     * 
     * To ensure correct order for variable changes (due servers multithreading
     * or network), we always wait for active request to be handler before
     * sending a new one. If there is an active request, we will put varible
     * "burst" to queue that will be purged after current request is handled.
     * 
     */
    public void sendPendingVariableChanges() {
        if (applicationRunning) {
            if (hasActiveRequest()) {
                // skip empty queues if there are pending bursts to be sent
                if (pendingVariables.size() > 0
                        || pendingVariableBursts.size() == 0) {
                    Vector burst = (Vector) pendingVariables.clone();
                    pendingVariableBursts.add(burst);
                    pendingVariables.clear();
                }
            } else {
                buildAndSendVariableBurst(pendingVariables, false);
            }
        }
    }

    /**
     * Build the variable burst and send it to server.
     * 
     * When sync is forced, we also force sending of all pending variable-bursts
     * at the same time. This is ok as we can assume that DOM will newer be
     * updated after this.
     * 
     * @param pendingVariables
     *            Vector of variablechanges to send
     * @param forceSync
     *            Should we use synchronous request?
     */
    private void buildAndSendVariableBurst(Vector pendingVariables,
            boolean forceSync) {
        final StringBuffer req = new StringBuffer();

        while (!pendingVariables.isEmpty()) {
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
            // Append all the busts to this synchronous request
            if (forceSync && !pendingVariableBursts.isEmpty()) {
                pendingVariables = (Vector) pendingVariableBursts
                        .firstElement();
                pendingVariableBursts.remove(0);
                req.append(VAR_BURST_SEPARATOR);
            }
        }
        makeUidlRequest(req.toString(), false, forceSync);
    }

    public void updateVariable(String paintableId, String variableName,
            String newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue, immediate, 's');
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
            buf.append(values[i].toString());
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
     *            Widget to be updated, expected to implement an instance of
     *            Paintable
     * @param uidl
     *            UIDL to be painted
     * @param manageCaption
     *            True if you want to delegate caption, icon, description and
     *            error message management to parent.
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
        boolean wasVisible = component.isVisible();
        component.setVisible(visible);
        if (wasVisible != visible) {
            // Changed invisibile <-> visible
            if (wasVisible && manageCaption) {
                // Must hide caption when component is hidden
                final Container parent = Util.getLayout(component);
                if (parent != null) {
                    parent.updateCaption((Paintable) component, uidl);
                }

            }
        }

        if (!visible) {
            return true;
        }

        // Switch to correct implementation if needed
        if (!widgetSet.isCorrectImplementation(component, uidl)) {
            final Container parent = Util.getLayout(component);
            if (parent != null) {
                final Widget w = (Widget) widgetSet.createWidget(uidl);
                parent.replaceChildComponent(component, w);
                unregisterPaintable((Paintable) component);
                registerPaintable(uidl.getId(), (Paintable) w);
                ((Paintable) w).updateFromUIDL(uidl, this);
                return true;
            }
        }

        boolean enabled = !uidl.getBooleanAttribute("disabled");
        if (component instanceof FocusWidget) {
            FocusWidget fw = (FocusWidget) component;
            fw.setEnabled(enabled);
            if (uidl.hasAttribute("tabindex")) {
                fw.setTabIndex(uidl.getIntAttribute("tabindex"));
            }
        }

        StringBuffer styleBuf = new StringBuffer();
        final String primaryName = component.getStylePrimaryName();
        styleBuf.append(primaryName);

        // first disabling and read-only status
        if (!enabled) {
            styleBuf.append(" ");
            styleBuf.append("i-disabled");
        }
        if (uidl.getBooleanAttribute("readonly")) {
            styleBuf.append(" ");
            styleBuf.append("i-readonly");
        }

        // add additional styles as css classes, prefixed with component default
        // stylename
        if (uidl.hasAttribute("style")) {
            final String[] styles = uidl.getStringAttribute("style").split(" ");
            for (int i = 0; i < styles.length; i++) {
                styleBuf.append(" ");
                styleBuf.append(primaryName);
                styleBuf.append("-");
                styleBuf.append(styles[i]);
            }
        }

        // add modified classname to Fields
        if (uidl.hasAttribute("modified") && component instanceof Field) {
            styleBuf.append(" ");
            styleBuf.append(MODIFIED_CLASSNAME);
        }

        TooltipInfo tooltipInfo = getTitleInfo((Paintable) component);
        if (uidl.hasAttribute("description")) {
            tooltipInfo.setTitle(uidl.getStringAttribute("description"));
        } else {
            tooltipInfo.setTitle(null);
        }

        // add error classname to components w/ error
        if (uidl.hasAttribute("error")) {
            styleBuf.append(" ");
            styleBuf.append(primaryName);
            styleBuf.append(ERROR_CLASSNAME_EXT);

            tooltipInfo.setErrorUidl(uidl.getErrors());
        } else {
            tooltipInfo.setErrorUidl(null);
        }

        // add required style to required components
        if (uidl.hasAttribute("required")) {
            styleBuf.append(" ");
            styleBuf.append(primaryName);
            styleBuf.append(REQUIRED_CLASSNAME_EXT);
        }

        // Styles + disabled & readonly
        component.setStyleName(styleBuf.toString());

        // Set captions
        if (manageCaption) {
            final Container parent = Util.getLayout(component);
            if (parent != null) {
                parent.updateCaption((Paintable) component, uidl);
            }
        }

        if (usePaintableIdsInDOM) {
            DOM.setElementProperty(component.getElement(), "id", uidl.getId());
        }

        /*
         * updateComponentSize need to be after caption update so caption can be
         * taken into account
         */

        updateComponentSize(component, uidl);

        return false;
    }

    private void updateComponentSize(Widget component, UIDL uidl) {
        String w = uidl.hasAttribute("width") ? uidl
                .getStringAttribute("width") : "";

        String h = uidl.hasAttribute("height") ? uidl
                .getStringAttribute("height") : "";

        float relativeWidth = Util.parseRelativeSize(w);
        float relativeHeight = Util.parseRelativeSize(h);

        // first set absolute sizes
        if (relativeHeight < 0.0) {
            component.setHeight(h);
        }
        if (relativeWidth < 0.0) {
            component.setWidth(w);
        }

        if (relativeHeight >= 0.0 || relativeWidth >= 0.0) {
            // One or both is relative
            FloatSize relativeSize = new FloatSize(relativeWidth,
                    relativeHeight);
            if (componentRelativeSizes.put(component, relativeSize) == null
                    && componentOffsetSizes.containsKey(component)) {
                // The component has changed from absolute size to relative size
                relativeSizeChanges.add(component);
            }
            handleComponentRelativeSize(component);
        } else if (relativeHeight < 0.0 && relativeWidth < 0.0) {

            if (componentRelativeSizes.remove(component) != null) {
                // The component has changed from relative size to absolute size
                relativeSizeChanges.add(component);
            }
        }

    }

    /**
     * Traverses recursively child widgets until ContainerResizedListener child
     * widget is found. They will delegate it further if needed.
     * 
     * @param container
     */
    public void runDescendentsLayout(HasWidgets container) {
        // getConsole().log(
        // "runDescendentsLayout(" + Util.getSimpleName(container) + ")");
        final Iterator childWidgets = container.iterator();
        while (childWidgets.hasNext()) {
            final Widget child = (Widget) childWidgets.next();

            if (child instanceof Paintable) {

                if (handleComponentRelativeSize(child)) {
                    /*
                     * Only need to propagate event if "child" has a relative
                     * size
                     */

                    if (child instanceof ContainerResizedListener) {
                        ((ContainerResizedListener) child).iLayout();
                    } else if (child instanceof HasWidgets) {
                        final HasWidgets childContainer = (HasWidgets) child;
                        runDescendentsLayout(childContainer);
                    }
                }
            } else if (child instanceof HasWidgets) {
                // propagate over non Paintable HasWidgets
                runDescendentsLayout((HasWidgets) child);
            }

        }
    }

    /**
     * Converts relative sizes into pixel sizes.
     * 
     * @param child
     * @return true if the child has a relative size
     */
    public boolean handleComponentRelativeSize(Widget child) {
        Widget widget = child;
        FloatSize relativeSize = getRelativeSize(child);
        if (relativeSize == null) {
            return false;
        }

        boolean horizontalScrollBar = false;
        boolean verticalScrollBar = false;

        Container parent = Util.getLayout(widget);
        RenderSpace renderSpace;

        // Parent-less components (like sub-windows) are relative to browser
        // window.
        if (parent == null) {
            renderSpace = new RenderSpace(Window.getClientWidth(), Window
                    .getClientHeight());
        } else {
            renderSpace = parent.getAllocatedSpace(widget);
        }
        if (relativeSize.getHeight() >= 0) {
            if (renderSpace != null) {

                if (renderSpace.getScrollbarSize() > 0) {
                    if (relativeSize.getWidth() > 100) {
                        horizontalScrollBar = true;
                    } else if (relativeSize.getWidth() < 0
                            && renderSpace.getWidth() > 0) {
                        int offsetWidth = widget.getOffsetWidth();
                        int width = renderSpace.getWidth();
                        if (offsetWidth > width) {
                            horizontalScrollBar = true;
                        }
                    }
                }

                int height = renderSpace.getHeight();
                if (horizontalScrollBar) {
                    height -= renderSpace.getScrollbarSize();
                }
                height = (int) (height * relativeSize.getHeight() / 100.0);

                if (height < 0) {
                    height = 0;
                }

                // getConsole().log(
                // "Widget " + Util.getSimpleName(widget) + "/"
                // + widget.hashCode() + " relative height "
                // + relativeSize.getHeight() + "% of "
                // + renderSpace.getHeight() + "px (reported by "
                //
                // + Util.getSimpleName(parent) + "/"
                // + parent.hashCode() + ") : " + height + "px");
                widget.setHeight(height + "px");
            } else {
                widget.setHeight(relativeSize.getHeight() + "%");
                ApplicationConnection.getConsole().error(
                        Util.getLayout(widget).getClass().getName()
                                + " did not produce allocatedSpace for "
                                + widget.getClass().getName());
            }
        }

        if (relativeSize.getWidth() >= 0) {

            if (renderSpace != null) {

                int width = renderSpace.getWidth();

                if (renderSpace.getScrollbarSize() > 0) {
                    if (relativeSize.getHeight() > 100) {
                        verticalScrollBar = true;
                    } else if (relativeSize.getHeight() < 0
                            && renderSpace.getHeight() > 0
                            && widget.getOffsetHeight() > renderSpace
                                    .getHeight()) {
                        verticalScrollBar = true;
                    }
                }

                if (verticalScrollBar) {
                    width -= renderSpace.getScrollbarSize();
                }
                width = (int) (width * relativeSize.getWidth() / 100.0);

                if (width < 0) {
                    width = 0;
                }

                // getConsole().log(
                // "Widget " + Util.getSimpleName(widget) + "/"
                // + widget.hashCode() + " relative width "
                // + relativeSize.getWidth() + "% of "
                // + renderSpace.getWidth() + "px (reported by "
                // + Util.getSimpleName(parent) + "/"
                // + parent.hashCode() + ") : " + width + "px");
                widget.setWidth(width + "px");
            } else {
                widget.setWidth(relativeSize.getWidth() + "%");
                ApplicationConnection.getConsole().error(
                        Util.getLayout(widget).getClass().getName()
                                + " did not produce allocatedSpace for "
                                + widget.getClass().getName());
            }
        }

        return true;

    }

    private FloatSize getRelativeSize(Widget widget) {
        return componentRelativeSizes.get(widget);
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
     *            UIDL to create Paintable from.
     * @return Either existing or new Paintable corresponding to UIDL.
     */
    public Paintable getPaintable(UIDL uidl) {
        final String id = uidl.getId();
        Paintable w = getPaintable(id);
        if (w != null) {
            return w;
        }
        w = widgetSet.createWidget(uidl);
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
    public IContextMenu getContextMenu() {
        if (contextMenu == null) {
            contextMenu = new IContextMenu();
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
     *            toolkit URI from uidl
     * @return translated URI ready for browser
     */
    public String translateToolkitUri(String toolkitUri) {
        if (toolkitUri == null) {
            return null;
        }
        if (toolkitUri.startsWith("theme://")) {
            final String themeUri = configuration.getThemeUri();
            if (themeUri == null) {
                console
                        .error("Theme not set: ThemeResource will not be found. ("
                                + toolkitUri + ")");
            }
            toolkitUri = themeUri + toolkitUri.substring(7);
        }
        return toolkitUri;
    }

    public String getThemeUri() {
        return configuration.getThemeUri();
    }

    /**
     * Listens for Notification hide event, and redirects. Used for system
     * messages, such as session expired.
     * 
     */
    private class NotificationRedirect implements INotification.EventListener {
        String url;

        NotificationRedirect(String url) {
            this.url = url;
        }

        public void notificationHidden(HideEvent event) {
            redirect(url);
        }

    }

    /* Extended title handling */

    /**
     * Data showed in tooltips are stored centrilized as it may be needed in
     * varios place: caption, layouts, and in owner components themselves.
     * 
     * Updating TooltipInfo is done in updateComponent method.
     * 
     */
    public TooltipInfo getTitleInfo(Paintable titleOwner) {
        TooltipInfo info = paintableToTitle.get(titleOwner);
        if (info == null) {
            info = new TooltipInfo();
            paintableToTitle.put(titleOwner, info);
        }
        return info;
    }

    private final ITooltip tooltip = new ITooltip(this);

    /**
     * Component may want to delegate Tooltip handling to client. Layouts add
     * Tooltip (description, errors) to caption, but some components may want
     * them to appear one other elements too.
     * 
     * Events wanted by this handler are same as in Tooltip.TOOLTIP_EVENTS
     * 
     * @param event
     * @param owner
     */
    public void handleTooltipEvent(Event event, Paintable owner) {
        tooltip.handleTooltipEvent(event, owner);

    }

    /**
     * Adds PNG-fix conditionally (only for IE6) to the specified IMG -element.
     * 
     * @param el
     *            the IMG element to fix
     */
    public void addPngFix(Element el) {
        BrowserInfo b = BrowserInfo.get();
        if (b.isIE6()) {
            Util.addPngFix(el, getThemeUri()
                    + "/../default/common/img/blank.gif");
        }
    }

    /*
     * Helper to run layout functions triggered by child components with a
     * decent interval.
     */
    private final Timer layoutTimer = new Timer() {

        private boolean isPending = false;

        @Override
        public void schedule(int delayMillis) {
            if (!isPending) {
                super.schedule(delayMillis);
                isPending = true;
            }
        }

        @Override
        public void run() {
            getConsole().log(
                    "Running re-layout of " + view.getClass().getName());
            runDescendentsLayout(view);
            isPending = false;
        }
    };

    /**
     * Components can call this function to run all layout functions. This is
     * usually done, when component knows that its size has changed.
     */
    public void requestLayoutPhase() {
        layoutTimer.schedule(500);
    }

    private String windowName = null;

    /**
     * Reset the name of the current browser-window. This should reflect the
     * window-name used in the server, but might be different from the
     * window-object target-name on client.
     * 
     * @param stringAttribute
     *            New name for the window.
     */
    public void setWindowName(String newName) {
        windowName = newName;
    }

    public void captionSizeUpdated(Widget component) {
        captionSizeChanges.add(component);
    }
}
