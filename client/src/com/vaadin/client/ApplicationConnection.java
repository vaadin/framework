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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration.ErrorMessage;
import com.vaadin.client.ResourceLoader.ResourceLoadEvent;
import com.vaadin.client.ResourceLoader.ResourceLoadListener;
import com.vaadin.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.client.communication.JsonDecoder;
import com.vaadin.client.communication.JsonEncoder;
import com.vaadin.client.communication.RpcManager;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.metadata.ConnectorBundleLoader;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.metadata.Type;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.AbstractConnector;
import com.vaadin.client.ui.VContextMenu;
import com.vaadin.client.ui.dd.VDragAndDropManager;
import com.vaadin.client.ui.notification.VNotification;
import com.vaadin.client.ui.notification.VNotification.HideEvent;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.ComponentState;
import com.vaadin.shared.Version;
import com.vaadin.shared.communication.LegacyChangeVariablesInvocation;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.ui.ui.UIConstants;

/**
 * This is the client side communication "engine", managing client-server
 * communication with its server side counterpart
 * com.vaadin.server.AbstractCommunicationManager.
 * 
 * Client-side connectors receive updates from the corresponding server-side
 * connector (typically component) as state updates or RPC calls. The connector
 * has the possibility to communicate back with its server side counter part
 * through RPC calls.
 * 
 * TODO document better
 * 
 * Entry point classes (widgetsets) define <code>onModuleLoad()</code>.
 */
public class ApplicationConnection {

    public static final String MODIFIED_CLASSNAME = "v-modified";

    public static final String DISABLED_CLASSNAME = "v-disabled";

    public static final String REQUIRED_CLASSNAME_EXT = "-required";

    public static final String ERROR_CLASSNAME_EXT = "-error";

    public static final char VAR_BURST_SEPARATOR = '\u001d';

    public static final char VAR_ESCAPE_CHARACTER = '\u001b';

    /**
     * A string that, if found in a non-JSON response to a UIDL request, will
     * cause the browser to refresh the page. If followed by a colon, optional
     * whitespace, and a URI, causes the browser to synchronously load the URI.
     * 
     * <p>
     * This allows, for instance, a servlet filter to redirect the application
     * to a custom login page when the session expires. For example:
     * </p>
     * 
     * <pre>
     * if (sessionExpired) {
     *     response.setHeader(&quot;Content-Type&quot;, &quot;text/html&quot;);
     *     response.getWriter().write(
     *             myLoginPageHtml + &quot;&lt;!-- Vaadin-Refresh: &quot;
     *                     + request.getContextPath() + &quot; --&gt;&quot;);
     * }
     * </pre>
     */
    public static final String UIDL_REFRESH_TOKEN = "Vaadin-Refresh";

    // will hold the UIDL security key (for XSS protection) once received
    private String uidlSecurityKey = "init";

    private final HashMap<String, String> resourcesMap = new HashMap<String, String>();

    /**
     * The pending method invocations that will be send to the server by
     * {@link #sendPendingCommand}. The key is defined differently based on
     * whether the method invocation is enqueued with lastonly. With lastonly
     * enabled, the method signature ( {@link MethodInvocation#getLastonlyTag()}
     * ) is used as the key to make enable removing a previously enqueued
     * invocation. Without lastonly, an incremental id based on
     * {@link #lastInvocationTag} is used to get unique values.
     */
    private LinkedHashMap<String, MethodInvocation> pendingInvocations = new LinkedHashMap<String, MethodInvocation>();

    private int lastInvocationTag = 0;

    private WidgetSet widgetSet;

    private VContextMenu contextMenu = null;

    private Timer loadTimer;
    private Timer loadTimer2;
    private Timer loadTimer3;
    private Element loadElement;

    private final UIConnector uIConnector;

    protected boolean applicationRunning = false;

    private boolean hasActiveRequest = false;

    protected boolean cssLoaded = false;

    /** Parameters for this application connection loaded from the web-page */
    private ApplicationConfiguration configuration;

    /** List of pending variable change bursts that must be submitted in order */
    private final ArrayList<LinkedHashMap<String, MethodInvocation>> pendingBursts = new ArrayList<LinkedHashMap<String, MethodInvocation>>();

    /** Timer for automatic refirect to SessionExpiredURL */
    private Timer redirectTimer;

    /** redirectTimer scheduling interval in seconds */
    private int sessionExpirationInterval;

    private ArrayList<Widget> componentCaptionSizeChanges = new ArrayList<Widget>();

    private Date requestStartTime;

    private boolean validatingLayouts = false;

    private Set<ComponentConnector> zeroWidthComponents = null;

    private Set<ComponentConnector> zeroHeightComponents = null;

    private final LayoutManager layoutManager;

    private final RpcManager rpcManager;

    /**
     * If renderingLocks contains any objects, rendering is to be suspended
     * until the collection is empty or a timeout has occurred.
     */
    private Set<Object> renderingLocks = new HashSet<Object>();

    /**
     * Data structure holding information about pending UIDL messages.
     */
    private class PendingUIDLMessage {
        private Date start;
        private String jsonText;
        private ValueMap json;

        public PendingUIDLMessage(Date start, String jsonText, ValueMap json) {
            this.start = start;
            this.jsonText = jsonText;
            this.json = json;
        }

        public Date getStart() {
            return start;
        }

        public String getJsonText() {
            return jsonText;
        }

        public ValueMap getJson() {
            return json;
        }
    }

    /** Contains all UIDL messages received while the rendering is suspended */
    private List<PendingUIDLMessage> pendingUIDLMessages = new ArrayList<PendingUIDLMessage>();

    /** The max timeout the rendering phase may be suspended */
    private static final int MAX_SUSPENDED_TIMEOUT = 5000;

    /** Event bus for communication events */
    private EventBus eventBus = GWT.create(SimpleEventBus.class);

    /**
     * The communication handler methods are called at certain points during
     * communication with the server. This allows for making add-ons that keep
     * track of different aspects of the communication.
     */
    public interface CommunicationHandler extends EventHandler {
        void onRequestStarted(RequestStartedEvent e);

        void onRequestEnded(RequestEndedEvent e);

        void onResponseReceived(ResponseReceivedEvent e);
    }

    public static class RequestStartedEvent extends
            GwtEvent<CommunicationHandler> {
        public static Type<CommunicationHandler> TYPE = new Type<CommunicationHandler>();

        @Override
        public com.google.gwt.event.shared.GwtEvent.Type<CommunicationHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(CommunicationHandler handler) {
            handler.onRequestStarted(this);
        }
    }

    public static class RequestEndedEvent extends
            GwtEvent<CommunicationHandler> {
        public static Type<CommunicationHandler> TYPE = new Type<CommunicationHandler>();

        @Override
        public com.google.gwt.event.shared.GwtEvent.Type<CommunicationHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(CommunicationHandler handler) {
            handler.onRequestEnded(this);
        }
    }

    public static class ResponseReceivedEvent extends
            GwtEvent<CommunicationHandler> {
        public static Type<CommunicationHandler> TYPE = new Type<CommunicationHandler>();

        @Override
        public com.google.gwt.event.shared.GwtEvent.Type<CommunicationHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(CommunicationHandler handler) {
            handler.onResponseReceived(this);
        }
    }

    /**
     * Allows custom handling of communication errors.
     */
    public interface CommunicationErrorDelegate {
        /**
         * Called when a communication error has occurred. Returning
         * <code>true</code> from this method suppresses error handling.
         * 
         * @param details
         *            A string describing the error.
         * @param statusCode
         *            The HTTP status code (e.g. 404, etc).
         * @return true if the error reporting should be suppressed, false to
         *         perform normal error reporting.
         */
        public boolean onError(String details, int statusCode);
    }

    private CommunicationErrorDelegate communicationErrorDelegate = null;

    public static class MultiStepDuration extends Duration {
        private int previousStep = elapsedMillis();

        public void logDuration(String message) {
            logDuration(message, 0);
        }

        public void logDuration(String message, int minDuration) {
            int currentTime = elapsedMillis();
            int stepDuration = currentTime - previousStep;
            if (stepDuration >= minDuration) {
                VConsole.log(message + ": " + stepDuration + " ms");
            }
            previousStep = currentTime;
        }
    }

    public ApplicationConnection() {
        // Assuming UI data is eagerly loaded
        ConnectorBundleLoader.get().loadBundle(
                ConnectorBundleLoader.EAGER_BUNDLE_NAME, null);
        uIConnector = GWT.create(UIConnector.class);
        rpcManager = GWT.create(RpcManager.class);
        layoutManager = GWT.create(LayoutManager.class);
        layoutManager.setConnection(this);
    }

    public void init(WidgetSet widgetSet, ApplicationConfiguration cnf) {
        VConsole.log("Starting application " + cnf.getRootPanelId());

        VConsole.log("Vaadin application servlet version: "
                + cnf.getServletVersion());

        if (!cnf.getServletVersion().equals(Version.getFullVersion())) {
            VConsole.error("Warning: your widget set seems to be built with a different "
                    + "version than the one used on server. Unexpected "
                    + "behavior may occur.");
        }

        this.widgetSet = widgetSet;
        configuration = cnf;

        ComponentLocator componentLocator = new ComponentLocator(this);

        String appRootPanelName = cnf.getRootPanelId();
        // remove the end (window name) of autogenerated rootpanel id
        appRootPanelName = appRootPanelName.replaceFirst("-\\d+$", "");

        initializeTestbenchHooks(componentLocator, appRootPanelName);

        initializeClientHooks();

        uIConnector.init(cnf.getRootPanelId(), this);
        showLoadingIndicator();

        scheduleHeartbeat();
    }

    /**
     * Starts this application. Don't call this method directly - it's called by
     * {@link ApplicationConfiguration#startNextApplication()}, which should be
     * called once this application has started (first response received) or
     * failed to start. This ensures that the applications are started in order,
     * to avoid session-id problems.
     * 
     */
    public void start() {
        String jsonText = configuration.getUIDL();
        if (jsonText == null) {
            // inital UIDL not in DOM, request later
            repaintAll();
        } else {
            // Update counter so TestBench knows something is still going on
            hasActiveRequest = true;

            // initial UIDL provided in DOM, continue as if returned by request
            handleJSONText(jsonText, -1);
        }
    }

    private native void initializeTestbenchHooks(
            ComponentLocator componentLocator, String TTAppId)
    /*-{
    	var ap = this;
    	var client = {};
    	client.isActive = $entry(function() {
    		return ap.@com.vaadin.client.ApplicationConnection::hasActiveRequest()()
    				|| ap.@com.vaadin.client.ApplicationConnection::isExecutingDeferredCommands()();
    	});
    	var vi = ap.@com.vaadin.client.ApplicationConnection::getVersionInfo()();
    	if (vi) {
    		client.getVersionInfo = function() {
    			return vi;
    		}
    	}

    	client.getProfilingData = $entry(function() {
    	    var pd = [
        	    ap.@com.vaadin.client.ApplicationConnection::lastProcessingTime,
                    ap.@com.vaadin.client.ApplicationConnection::totalProcessingTime
    	        ];
    	    pd = pd.concat(ap.@com.vaadin.client.ApplicationConnection::serverTimingInfo);
    	    return pd;
    	});

    	client.getElementByPath = $entry(function(id) {
    		return componentLocator.@com.vaadin.client.ComponentLocator::getElementByPath(Ljava/lang/String;)(id);
    	});
    	client.getPathForElement = $entry(function(element) {
    		return componentLocator.@com.vaadin.client.ComponentLocator::getPathForElement(Lcom/google/gwt/user/client/Element;)(element);
    	});

    	$wnd.vaadin.clients[TTAppId] = client;
    }-*/;

    /**
     * Helper for tt initialization
     */
    private JavaScriptObject getVersionInfo() {
        return configuration.getVersionInfoJSObject();
    }

    /**
     * Publishes a JavaScript API for mash-up applications.
     * <ul>
     * <li><code>vaadin.forceSync()</code> sends pending variable changes, in
     * effect synchronizing the server and client state. This is done for all
     * applications on host page.</li>
     * <li><code>vaadin.postRequestHooks</code> is a map of functions which gets
     * called after each XHR made by vaadin application. Note, that it is
     * attaching js functions responsibility to create the variable like this:
     * 
     * <code><pre>
     * if(!vaadin.postRequestHooks) {vaadin.postRequestHooks = new Object();}
     * postRequestHooks.myHook = function(appId) {
     *          if(appId == "MyAppOfInterest") {
     *                  // do the staff you need on xhr activity
     *          }
     * }
     * </pre></code> First parameter passed to these functions is the identifier
     * of Vaadin application that made the request.
     * </ul>
     * 
     * TODO make this multi-app aware
     */
    private native void initializeClientHooks()
    /*-{
    	var app = this;
    	var oldSync;
    	if ($wnd.vaadin.forceSync) {
    		oldSync = $wnd.vaadin.forceSync;
    	}
    	$wnd.vaadin.forceSync = $entry(function() {
    		if (oldSync) {
    			oldSync();
    		}
    		app.@com.vaadin.client.ApplicationConnection::sendPendingVariableChanges()();
    	});
    	var oldForceLayout;
    	if ($wnd.vaadin.forceLayout) {
    		oldForceLayout = $wnd.vaadin.forceLayout;
    	}
    	$wnd.vaadin.forceLayout = $entry(function() {
    		if (oldForceLayout) {
    			oldForceLayout();
    		}
    		app.@com.vaadin.client.ApplicationConnection::forceLayout()();
    	});
    }-*/;

    /**
     * Runs possibly registered client side post request hooks. This is expected
     * to be run after each uidl request made by Vaadin application.
     * 
     * @param appId
     */
    private static native void runPostRequestHooks(String appId)
    /*-{
    	if ($wnd.vaadin.postRequestHooks) {
    		for ( var hook in $wnd.vaadin.postRequestHooks) {
    			if (typeof ($wnd.vaadin.postRequestHooks[hook]) == "function") {
    				try {
    					$wnd.vaadin.postRequestHooks[hook](appId);
    				} catch (e) {
    				}
    			}
    		}
    	}
    }-*/;

    /**
     * If on Liferay and logged in, ask the client side session management
     * JavaScript to extend the session duration.
     * 
     * Otherwise, Liferay client side JavaScript will explicitly expire the
     * session even though the server side considers the session to be active.
     * See ticket #8305 for more information.
     */
    protected native void extendLiferaySession()
    /*-{
    if ($wnd.Liferay && $wnd.Liferay.Session) {
        $wnd.Liferay.Session.extend();
        // if the extend banner is visible, hide it
        if ($wnd.Liferay.Session.banner) {
            $wnd.Liferay.Session.banner.remove();
        }
    }
    }-*/;

    /**
     * Indicates whether or not there are currently active UIDL requests. Used
     * internally to sequence requests properly, seldom needed in Widgets.
     * 
     * @return true if there are active requests
     */
    public boolean hasActiveRequest() {
        return hasActiveRequest;
    }

    private String getRepaintAllParameters() {
        // collect some client side data that will be sent to server on
        // initial uidl request
        String nativeBootstrapParameters = getNativeBrowserDetailsParameters(getConfiguration()
                .getRootPanelId());
        // TODO figure out how client and view size could be used better on
        // server. screen size can be accessed via Browser object, but other
        // values currently only via transaction listener.
        String parameters = ApplicationConstants.URL_PARAMETER_REPAINT_ALL
                + "=1&" + nativeBootstrapParameters;
        return parameters;
    }

    /**
     * Gets the browser detail parameters that are sent by the bootstrap
     * javascript for two-request initialization.
     * 
     * @param parentElementId
     * @return
     */
    private static native String getNativeBrowserDetailsParameters(
            String parentElementId)
    /*-{
       return $wnd.vaadin.getBrowserDetailsParameters(parentElementId);
    }-*/;

    protected void repaintAll() {
        String repainAllParameters = getRepaintAllParameters();
        makeUidlRequest("", repainAllParameters, false);
    }

    /**
     * Requests an analyze of layouts, to find inconsistencies. Exclusively used
     * for debugging during development.
     */
    public void analyzeLayouts() {
        String params = getRepaintAllParameters() + "&analyzeLayouts=1";
        makeUidlRequest("", params, false);
    }

    /**
     * Sends a request to the server to print details to console that will help
     * the developer to locate the corresponding server-side connector in the
     * source code.
     * 
     * @param serverConnector
     */
    void highlightConnector(ServerConnector serverConnector) {
        String params = getRepaintAllParameters() + "&highlightConnector="
                + serverConnector.getConnectorId();
        makeUidlRequest("", params, false);
    }

    /**
     * Makes an UIDL request to the server.
     * 
     * @param requestData
     *            Data that is passed to the server.
     * @param extraParams
     *            Parameters that are added as GET parameters to the url.
     *            Contains key=value pairs joined by & characters or is empty if
     *            no parameters should be added. Should not start with any
     *            special character.
     * @param forceSync
     *            true if the request should be synchronous, false otherwise
     */
    protected void makeUidlRequest(final String requestData,
            final String extraParams, final boolean forceSync) {
        startRequest();
        // Security: double cookie submission pattern
        final String payload = uidlSecurityKey + VAR_BURST_SEPARATOR
                + requestData;
        VConsole.log("Making UIDL Request with params: " + payload);
        String uri = translateVaadinUri(ApplicationConstants.APP_PROTOCOL_PREFIX
                + ApplicationConstants.UIDL_REQUEST_PATH);

        if (extraParams != null && extraParams.length() > 0) {
            uri = addGetParameters(uri, extraParams);
        }
        uri = addGetParameters(uri, UIConstants.UI_ID_PARAMETER + "="
                + configuration.getUIId());

        doUidlRequest(uri, payload, forceSync);

    }

    /**
     * Sends an asynchronous or synchronous UIDL request to the server using the
     * given URI.
     * 
     * @param uri
     *            The URI to use for the request. May includes GET parameters
     * @param payload
     *            The contents of the request to send
     * @param synchronous
     *            true if the request should be synchronous, false otherwise
     */
    protected void doUidlRequest(final String uri, final String payload,
            final boolean synchronous) {
        if (!synchronous) {
            RequestCallback requestCallback = new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                    handleCommunicationError(exception.getMessage(), -1);
                }

                private void handleCommunicationError(String details,
                        int statusCode) {
                    if (!handleErrorInDelegate(details, statusCode)) {
                        showCommunicationError(details, statusCode);
                    }
                    endRequest();
                }

                @Override
                public void onResponseReceived(Request request,
                        Response response) {
                    VConsole.log("Server visit took "
                            + String.valueOf((new Date()).getTime()
                                    - requestStartTime.getTime()) + "ms");

                    int statusCode = response.getStatusCode();

                    switch (statusCode) {
                    case 0:
                        handleCommunicationError(
                                "Invalid status code 0 (server down?)",
                                statusCode);
                        return;

                    case 401:
                        /*
                         * Authorization has failed. Could be that the session
                         * has timed out and the container is redirecting to a
                         * login page.
                         */
                        showAuthenticationError("");
                        endRequest();
                        return;

                    case 503:
                        /*
                         * We'll assume msec instead of the usual seconds. If
                         * there's no Retry-After header, handle the error like
                         * a 500, as per RFC 2616 section 10.5.4.
                         */
                        String delay = response.getHeader("Retry-After");
                        if (delay != null) {
                            VConsole.log("503, retrying in " + delay + "msec");
                            (new Timer() {
                                @Override
                                public void run() {
                                    doUidlRequest(uri, payload, synchronous);
                                }
                            }).schedule(Integer.parseInt(delay));
                            return;
                        }
                    }

                    if ((statusCode / 100) == 4) {
                        // Handle all 4xx errors the same way as (they are
                        // all permanent errors)
                        showCommunicationError(
                                "UIDL could not be read from server. Check servlets mappings. Error code: "
                                        + statusCode, statusCode);
                        endRequest();
                        return;
                    } else if ((statusCode / 100) == 5) {
                        // Something's wrong on the server, there's nothing the
                        // client can do except maybe try again.
                        handleCommunicationError("Server error. Error code: "
                                + statusCode, statusCode);
                        return;
                    }

                    String contentType = response.getHeader("Content-Type");
                    if (contentType == null
                            || !contentType.startsWith("application/json")) {
                        /*
                         * A servlet filter or equivalent may have intercepted
                         * the request and served non-UIDL content (for
                         * instance, a login page if the session has expired.)
                         * If the response contains a magic substring, do a
                         * synchronous refresh. See #8241.
                         */
                        MatchResult refreshToken = RegExp.compile(
                                UIDL_REFRESH_TOKEN + "(:\\s*(.*?))?(\\s|$)")
                                .exec(response.getText());
                        if (refreshToken != null) {
                            redirect(refreshToken.getGroup(2));
                            return;
                        }
                    }

                    // for(;;);[realjson]
                    final String jsonText = response.getText().substring(9,
                            response.getText().length() - 1);
                    handleJSONText(jsonText, statusCode);
                }

            };
            try {
                doAsyncUIDLRequest(uri, payload, requestCallback);
            } catch (RequestException e) {
                VConsole.error(e);
                endRequest();
            }
        } else {
            // Synchronized call, discarded response (leaving the page)
            SynchronousXHR syncXHR = (SynchronousXHR) SynchronousXHR.create();
            syncXHR.synchronousPost(uri + "&"
                    + ApplicationConstants.PARAM_UNLOADBURST + "=1", payload);
            /*
             * Although we are in theory leaving the page, the page may still
             * stay open. End request properly here too. See #3289
             */
            endRequest();
        }

    }

    /**
     * Handles received UIDL JSON text, parsing it, and passing it on to the
     * appropriate handlers, while logging timing information.
     * 
     * @param jsonText
     * @param statusCode
     */
    private void handleJSONText(String jsonText, int statusCode) {
        final Date start = new Date();
        final ValueMap json;
        try {
            json = parseJSONResponse(jsonText);
        } catch (final Exception e) {
            endRequest();
            showCommunicationError(e.getMessage() + " - Original JSON-text:"
                    + jsonText, statusCode);
            return;
        }

        VConsole.log("JSON parsing took "
                + (new Date().getTime() - start.getTime()) + "ms");
        if (applicationRunning) {
            handleReceivedJSONMessage(start, jsonText, json);
        } else {
            applicationRunning = true;
            handleWhenCSSLoaded(jsonText, json);
        }
    }

    /**
     * Sends an asynchronous UIDL request to the server using the given URI.
     * 
     * @param uri
     *            The URI to use for the request. May includes GET parameters
     * @param payload
     *            The contents of the request to send
     * @param requestCallback
     *            The handler for the response
     * @throws RequestException
     *             if the request could not be sent
     */
    protected void doAsyncUIDLRequest(String uri, String payload,
            RequestCallback requestCallback) throws RequestException {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, uri);
        // TODO enable timeout
        // rb.setTimeoutMillis(timeoutMillis);
        rb.setHeader("Content-Type", "text/plain;charset=utf-8");
        rb.setRequestData(payload);
        rb.setCallback(requestCallback);

        rb.send();
    }

    int cssWaits = 0;

    /**
     * Holds the time spent rendering the last request
     */
    protected int lastProcessingTime;

    /**
     * Holds the total time spent rendering requests during the lifetime of the
     * session.
     */
    protected int totalProcessingTime;

    /**
     * Holds the timing information from the server-side. How much time was
     * spent servicing the last request and how much time has been spent
     * servicing the session so far. These values are always one request behind,
     * since they cannot be measured before the request is finished.
     */
    private ValueMap serverTimingInfo;

    static final int MAX_CSS_WAITS = 100;

    protected void handleWhenCSSLoaded(final String jsonText,
            final ValueMap json) {
        if (!isCSSLoaded() && cssWaits < MAX_CSS_WAITS) {
            (new Timer() {
                @Override
                public void run() {
                    handleWhenCSSLoaded(jsonText, json);
                }
            }).schedule(50);
            VConsole.log("Assuming CSS loading is not complete, "
                    + "postponing render phase. "
                    + "(.v-loading-indicator height == 0)");
            cssWaits++;
        } else {
            cssLoaded = true;
            handleReceivedJSONMessage(new Date(), jsonText, json);
            if (cssWaits >= MAX_CSS_WAITS) {
                VConsole.error("CSS files may have not loaded properly.");
            }
        }
    }

    /**
     * Checks whether or not the CSS is loaded. By default checks the size of
     * the loading indicator element.
     * 
     * @return
     */
    protected boolean isCSSLoaded() {
        return cssLoaded
                || DOM.getElementPropertyInt(loadElement, "offsetHeight") != 0;
    }

    /**
     * Shows the communication error notification.
     * 
     * @param details
     *            Optional details for debugging.
     * @param statusCode
     *            The status code returned for the request
     * 
     */
    protected void showCommunicationError(String details, int statusCode) {
        VConsole.error("Communication error: " + details);
        ErrorMessage communicationError = configuration.getCommunicationError();
        showError(details, communicationError.getCaption(),
                communicationError.getMessage(), communicationError.getUrl());
    }

    /**
     * Shows the authentication error notification.
     * 
     * @param details
     *            Optional details for debugging.
     */
    protected void showAuthenticationError(String details) {
        VConsole.error("Authentication error: " + details);
        ErrorMessage authorizationError = configuration.getAuthorizationError();
        showError(details, authorizationError.getCaption(),
                authorizationError.getMessage(), authorizationError.getUrl());
    }

    /**
     * Shows the error notification.
     * 
     * @param details
     *            Optional details for debugging.
     */
    private void showError(String details, String caption, String message,
            String url) {

        StringBuilder html = new StringBuilder();
        if (caption != null) {
            html.append("<h1>");
            html.append(caption);
            html.append("</h1>");
        }
        if (message != null) {
            html.append("<p>");
            html.append(message);
            html.append("</p>");
        }

        if (html.length() > 0) {

            // Add error description
            html.append("<br/><p><I style=\"font-size:0.7em\">");
            html.append(details);
            html.append("</I></p>");

            VNotification n = VNotification.createNotification(1000 * 60 * 45);
            n.addEventListener(new NotificationRedirect(url));
            n.show(html.toString(), VNotification.CENTERED_TOP,
                    VNotification.STYLE_SYSTEM);
        } else {
            redirect(url);
        }
    }

    protected void startRequest() {
        if (hasActiveRequest) {
            VConsole.error("Trying to start a new request while another is active");
        }
        hasActiveRequest = true;
        requestStartTime = new Date();
        // show initial throbber
        if (loadTimer == null) {
            loadTimer = new Timer() {
                @Override
                public void run() {
                    /*
                     * IE7 does not properly cancel the event with
                     * loadTimer.cancel() so we have to check that we really
                     * should make it visible
                     */
                    if (loadTimer != null) {
                        showLoadingIndicator();
                    }

                }
            };
            // First one kicks in at 300ms
        }
        loadTimer.schedule(300);
        eventBus.fireEvent(new RequestStartedEvent());
    }

    protected void endRequest() {
        if (!hasActiveRequest) {
            VConsole.error("No active request");
        }
        // After checkForPendingVariableBursts() there may be a new active
        // request, so we must set hasActiveRequest to false before, not after,
        // the call. Active requests used to be tracked with an integer counter,
        // so setting it after used to work but not with the #8505 changes.
        hasActiveRequest = false;
        if (applicationRunning) {
            checkForPendingVariableBursts();
            runPostRequestHooks(configuration.getRootPanelId());
        }
        // deferring to avoid flickering
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                if (!hasActiveRequest()) {
                    hideLoadingIndicator();

                    // If on Liferay and session expiration management is in
                    // use, extend session duration on each request.
                    // Doing it here rather than before the request to improve
                    // responsiveness.
                    // Postponed until the end of the next request if other
                    // requests still pending.
                    extendLiferaySession();
                }
            }
        });
        eventBus.fireEvent(new RequestEndedEvent());
    }

    /**
     * This method is called after applying uidl change set to application.
     * 
     * It will clean current and queued variable change sets. And send next
     * change set if it exists.
     */
    private void checkForPendingVariableBursts() {
        cleanVariableBurst(pendingInvocations);
        if (pendingBursts.size() > 0) {
            for (LinkedHashMap<String, MethodInvocation> pendingBurst : pendingBursts) {
                cleanVariableBurst(pendingBurst);
            }
            LinkedHashMap<String, MethodInvocation> nextBurst = pendingBursts
                    .remove(0);
            buildAndSendVariableBurst(nextBurst, false);
        }
    }

    /**
     * Cleans given queue of variable changes of such changes that came from
     * components that do not exist anymore.
     * 
     * @param variableBurst
     */
    private void cleanVariableBurst(
            LinkedHashMap<String, MethodInvocation> variableBurst) {
        Iterator<MethodInvocation> iterator = variableBurst.values().iterator();
        while (iterator.hasNext()) {
            String id = iterator.next().getConnectorId();
            if (!getConnectorMap().hasConnector(id)
                    && !getConnectorMap().isDragAndDropPaintable(id)) {
                // variable owner does not exist anymore
                iterator.remove();
                VConsole.log("Removed variable from removed component: " + id);
            }
        }
    }

    private void showLoadingIndicator() {
        // show initial throbber
        if (loadElement == null) {
            loadElement = DOM.createDiv();
            DOM.setStyleAttribute(loadElement, "position", "absolute");
            DOM.appendChild(uIConnector.getWidget().getElement(), loadElement);
            VConsole.log("inserting load indicator");
        }
        DOM.setElementProperty(loadElement, "className", "v-loading-indicator");
        DOM.setStyleAttribute(loadElement, "display", "block");
        // Initialize other timers
        loadTimer2 = new Timer() {
            @Override
            public void run() {
                DOM.setElementProperty(loadElement, "className",
                        "v-loading-indicator-delay");
            }
        };
        // Second one kicks in at 1500ms from request start
        loadTimer2.schedule(1200);

        loadTimer3 = new Timer() {
            @Override
            public void run() {
                DOM.setElementProperty(loadElement, "className",
                        "v-loading-indicator-wait");
            }
        };
        // Third one kicks in at 5000ms from request start
        loadTimer3.schedule(4700);
    }

    private void hideLoadingIndicator() {
        if (loadTimer != null) {
            loadTimer.cancel();
            loadTimer = null;
        }
        if (loadTimer2 != null) {
            loadTimer2.cancel();
            loadTimer3.cancel();
            loadTimer2 = null;
            loadTimer3 = null;
        }
        if (loadElement != null) {
            DOM.setStyleAttribute(loadElement, "display", "none");
        }
    }

    /**
     * Checks if deferred commands are (potentially) still being executed as a
     * result of an update from the server. Returns true if a deferred command
     * might still be executing, false otherwise. This will not work correctly
     * if a deferred command is added in another deferred command.
     * <p>
     * Used by the native "client.isActive" function.
     * </p>
     * 
     * @return true if deferred commands are (potentially) being executed, false
     *         otherwise
     */
    private boolean isExecutingDeferredCommands() {
        Scheduler s = Scheduler.get();
        if (s instanceof VSchedulerImpl) {
            return ((VSchedulerImpl) s).hasWorkQueued();
        } else {
            return false;
        }
    }

    /**
     * Determines whether or not the loading indicator is showing.
     * 
     * @return true if the loading indicator is visible
     */
    public boolean isLoadingIndicatorVisible() {
        if (loadElement == null) {
            return false;
        }
        if (loadElement.getStyle().getProperty("display").equals("none")) {
            return false;
        }

        return true;
    }

    private static native ValueMap parseJSONResponse(String jsonText)
    /*-{
    	try {
    		return JSON.parse(jsonText);
    	} catch (ignored) {
    		return eval('(' + jsonText + ')');
    	}
    }-*/;

    private void handleReceivedJSONMessage(Date start, String jsonText,
            ValueMap json) {
        handleUIDLMessage(start, jsonText, json);
    }

    protected void handleUIDLMessage(final Date start, final String jsonText,
            final ValueMap json) {
        if (!renderingLocks.isEmpty()) {
            // Some component is doing something that can't be interrupted
            // (e.g. animation that should be smooth). Enqueue the UIDL
            // message for later processing.
            VConsole.log("Postponing UIDL handling due to lock...");
            pendingUIDLMessages.add(new PendingUIDLMessage(start, jsonText,
                    json));
            forceHandleMessage.schedule(MAX_SUSPENDED_TIMEOUT);
            return;
        }

        VConsole.log("Handling message from server");
        eventBus.fireEvent(new ResponseReceivedEvent());

        // Handle redirect
        if (json.containsKey("redirect")) {
            String url = json.getValueMap("redirect").getString("url");
            VConsole.log("redirecting to " + url);
            redirect(url);
            return;
        }

        final MultiStepDuration handleUIDLDuration = new MultiStepDuration();

        // Get security key
        if (json.containsKey(ApplicationConstants.UIDL_SECURITY_TOKEN_ID)) {
            uidlSecurityKey = json
                    .getString(ApplicationConstants.UIDL_SECURITY_TOKEN_ID);
        }
        VConsole.log(" * Handling resources from server");

        if (json.containsKey("resources")) {
            ValueMap resources = json.getValueMap("resources");
            JsArrayString keyArray = resources.getKeyArray();
            int l = keyArray.length();
            for (int i = 0; i < l; i++) {
                String key = keyArray.get(i);
                resourcesMap.put(key, resources.getAsString(key));
            }
        }
        handleUIDLDuration.logDuration(
                " * Handling resources from server completed", 10);

        VConsole.log(" * Handling type inheritance map from server");

        if (json.containsKey("typeInheritanceMap")) {
            configuration.addComponentInheritanceInfo(json
                    .getValueMap("typeInheritanceMap"));
        }
        handleUIDLDuration.logDuration(
                " * Handling type inheritance map from server completed", 10);

        VConsole.log("Handling type mappings from server");

        if (json.containsKey("typeMappings")) {
            configuration.addComponentMappings(
                    json.getValueMap("typeMappings"), widgetSet);
        }

        VConsole.log("Handling resource dependencies");
        if (json.containsKey("scriptDependencies")) {
            loadScriptDependencies(json.getJSStringArray("scriptDependencies"));
        }
        if (json.containsKey("styleDependencies")) {
            loadStyleDependencies(json.getJSStringArray("styleDependencies"));
        }

        handleUIDLDuration.logDuration(
                " * Handling type mappings from server completed", 10);
        /*
         * Hook for e.g. TestBench to get details about server peformance
         */
        if (json.containsKey("timings")) {
            serverTimingInfo = json.getValueMap("timings");
        }

        Command c = new Command() {
            @Override
            public void execute() {
                handleUIDLDuration.logDuration(" * Loading widgets completed",
                        10);

                MultiStepDuration updateDuration = new MultiStepDuration();

                if (json.containsKey("locales")) {
                    VConsole.log(" * Handling locales");
                    // Store locale data
                    JsArray<ValueMap> valueMapArray = json
                            .getJSValueMapArray("locales");
                    LocaleService.addLocales(valueMapArray);
                }

                updateDuration.logDuration(" * Handling locales completed", 10);

                boolean repaintAll = false;
                ValueMap meta = null;
                if (json.containsKey("meta")) {
                    VConsole.log(" * Handling meta information");
                    meta = json.getValueMap("meta");
                    if (meta.containsKey("repaintAll")) {
                        repaintAll = true;
                        uIConnector.getWidget().clear();
                        getConnectorMap().clear();
                        if (meta.containsKey("invalidLayouts")) {
                            validatingLayouts = true;
                            zeroWidthComponents = new HashSet<ComponentConnector>();
                            zeroHeightComponents = new HashSet<ComponentConnector>();
                        }
                    }
                    if (meta.containsKey("timedRedirect")) {
                        final ValueMap timedRedirect = meta
                                .getValueMap("timedRedirect");
                        redirectTimer = new Timer() {
                            @Override
                            public void run() {
                                redirect(timedRedirect.getString("url"));
                            }
                        };
                        sessionExpirationInterval = timedRedirect
                                .getInt("interval");
                    }
                }

                updateDuration.logDuration(
                        " * Handling meta information completed", 10);

                if (redirectTimer != null) {
                    redirectTimer.schedule(1000 * sessionExpirationInterval);
                }

                componentCaptionSizeChanges.clear();

                int startProcessing = updateDuration.elapsedMillis();

                // Ensure that all connectors that we are about to update exist
                Set<ServerConnector> createdConnectors = createConnectorsIfNeeded(json);

                updateDuration.logDuration(" * Creating connectors completed",
                        10);

                // Update states, do not fire events
                Collection<StateChangeEvent> pendingStateChangeEvents = updateConnectorState(
                        json, createdConnectors);

                updateDuration.logDuration(
                        " * Update of connector states completed", 10);

                // Update hierarchy, do not fire events
                Collection<ConnectorHierarchyChangeEvent> pendingHierarchyChangeEvents = updateConnectorHierarchy(json);

                updateDuration.logDuration(
                        " * Update of connector hierarchy completed", 10);

                // Fire hierarchy change events
                sendHierarchyChangeEvents(pendingHierarchyChangeEvents);

                updateDuration.logDuration(
                        " * Hierarchy state change event processing completed",
                        10);

                delegateToWidget(pendingStateChangeEvents);

                // Fire state change events.
                sendStateChangeEvents(pendingStateChangeEvents);

                updateDuration.logDuration(
                        " * State change event processing completed", 10);

                // Update of legacy (UIDL) style connectors
                updateVaadin6StyleConnectors(json);

                updateDuration
                        .logDuration(
                                " * Vaadin 6 style connector updates (updateFromUidl) completed",
                                10);

                // Handle any RPC invocations done on the server side
                handleRpcInvocations(json);

                updateDuration.logDuration(
                        " * Processing of RPC invocations completed", 10);

                if (json.containsKey("dd")) {
                    // response contains data for drag and drop service
                    VDragAndDropManager.get().handleServerResponse(
                            json.getValueMap("dd"));
                }

                updateDuration
                        .logDuration(
                                " * Processing of drag and drop server response completed",
                                10);

                unregisterRemovedConnectors();

                updateDuration.logDuration(
                        " * Unregistering of removed components completed", 10);

                VConsole.log("handleUIDLMessage: "
                        + (updateDuration.elapsedMillis() - startProcessing)
                        + " ms");

                LayoutManager layoutManager = getLayoutManager();
                layoutManager.setEverythingNeedsMeasure();
                layoutManager.layoutNow();

                updateDuration
                        .logDuration(" * Layout processing completed", 10);

                if (ApplicationConfiguration.isDebugMode()) {
                    VConsole.log(" * Dumping state changes to the console");
                    VConsole.dirUIDL(json, ApplicationConnection.this);

                    updateDuration
                            .logDuration(
                                    " * Dumping state changes to the console completed",
                                    10);
                }

                if (meta != null) {
                    if (meta.containsKey("appError")) {
                        ValueMap error = meta.getValueMap("appError");
                        String html = "";
                        if (error.containsKey("caption")
                                && error.getString("caption") != null) {
                            html += "<h1>" + error.getAsString("caption")
                                    + "</h1>";
                        }
                        if (error.containsKey("message")
                                && error.getString("message") != null) {
                            html += "<p>" + error.getAsString("message")
                                    + "</p>";
                        }
                        String url = null;
                        if (error.containsKey("url")) {
                            url = error.getString("url");
                        }

                        if (html.length() != 0) {
                            /* 45 min */
                            VNotification n = VNotification
                                    .createNotification(1000 * 60 * 45);
                            n.addEventListener(new NotificationRedirect(url));
                            n.show(html, VNotification.CENTERED_TOP,
                                    VNotification.STYLE_SYSTEM);
                        } else {
                            redirect(url);
                        }
                        applicationRunning = false;
                    }
                    if (validatingLayouts) {
                        VConsole.printLayoutProblems(meta,
                                ApplicationConnection.this,
                                zeroHeightComponents, zeroWidthComponents);
                        zeroHeightComponents = null;
                        zeroWidthComponents = null;
                        validatingLayouts = false;

                    }
                }

                updateDuration.logDuration(" * Error handling completed", 10);

                // TODO build profiling for widget impl loading time

                lastProcessingTime = (int) ((new Date().getTime()) - start
                        .getTime());
                totalProcessingTime += lastProcessingTime;

                VConsole.log(" Processing time was "
                        + String.valueOf(lastProcessingTime) + "ms for "
                        + jsonText.length() + " characters of JSON");
                VConsole.log("Referenced paintables: " + connectorMap.size());

                endRequest();

            }

            private void delegateToWidget(
                    Collection<StateChangeEvent> pendingStateChangeEvents) {
                VConsole.log(" * Running @DelegateToWidget");

                for (StateChangeEvent sce : pendingStateChangeEvents) {
                    ServerConnector connector = sce.getConnector();
                    if (connector instanceof ComponentConnector) {
                        ComponentConnector component = (ComponentConnector) connector;

                        Type stateType = AbstractConnector
                                .getStateType(component);

                        Set<String> changedProperties = sce
                                .getChangedProperties();
                        for (String propertyName : changedProperties) {
                            Property property = stateType
                                    .getProperty(propertyName);
                            String method = property
                                    .getDelegateToWidgetMethodName();
                            if (method != null) {
                                doDelegateToWidget(component, property, method);
                            }
                        }

                    }
                }
            }

            private void doDelegateToWidget(ComponentConnector component,
                    Property property, String methodName) {
                Type type = TypeData.getType(component.getClass());
                try {
                    Type widgetType = type.getMethod("getWidget")
                            .getReturnType();
                    Widget widget = component.getWidget();

                    Object propertyValue = property.getValue(component
                            .getState());

                    widgetType.getMethod(methodName).invoke(widget,
                            propertyValue);
                } catch (NoDataException e) {
                    throw new RuntimeException(
                            "Missing data needed to invoke @DelegateToWidget for "
                                    + Util.getSimpleName(component), e);
                }
            }

            /**
             * Sends the state change events created while updating the state
             * information.
             * 
             * This must be called after hierarchy change listeners have been
             * called. At least caption updates for the parent are strange if
             * fired from state change listeners and thus calls the parent
             * BEFORE the parent is aware of the child (through a
             * ConnectorHierarchyChangedEvent)
             * 
             * @param pendingStateChangeEvents
             *            The events to send
             */
            private void sendStateChangeEvents(
                    Collection<StateChangeEvent> pendingStateChangeEvents) {
                VConsole.log(" * Sending state change events");

                for (StateChangeEvent sce : pendingStateChangeEvents) {
                    try {
                        sce.getConnector().fireEvent(sce);
                    } catch (final Throwable e) {
                        VConsole.error(e);
                    }
                }

            }

            private void unregisterRemovedConnectors() {
                int unregistered = 0;
                List<ServerConnector> currentConnectors = new ArrayList<ServerConnector>(
                        connectorMap.getConnectors());
                for (ServerConnector c : currentConnectors) {
                    if (c.getParent() != null) {
                        if (!c.getParent().getChildren().contains(c)) {
                            VConsole.error("ERROR: Connector is connected to a parent but the parent does not contain the connector");
                        }
                    } else if ((c instanceof UIConnector && c == getRootConnector())) {
                        // UIConnector for this connection, leave as-is
                    } else if (c instanceof WindowConnector
                            && getRootConnector().hasSubWindow(
                                    (WindowConnector) c)) {
                        // Sub window attached to this UIConnector, leave
                        // as-is
                    } else {
                        // The connector has been detached from the
                        // hierarchy, unregister it and any possible
                        // children. The UIConnector should never be
                        // unregistered even though it has no parent.
                        connectorMap.unregisterConnector(c);
                        unregistered++;
                    }

                }

                VConsole.log("* Unregistered " + unregistered + " connectors");
            }

            private Set<ServerConnector> createConnectorsIfNeeded(ValueMap json) {
                VConsole.log(" * Creating connectors (if needed)");

                if (!json.containsKey("types")) {
                    return Collections.emptySet();
                }

                Set<ServerConnector> createdConnectors = new HashSet<ServerConnector>();

                ValueMap types = json.getValueMap("types");
                JsArrayString keyArray = types.getKeyArray();
                for (int i = 0; i < keyArray.length(); i++) {
                    try {
                        String connectorId = keyArray.get(i);
                        int connectorType = Integer.parseInt(types
                                .getString((connectorId)));
                        ServerConnector connector = connectorMap
                                .getConnector(connectorId);
                        if (connector != null) {
                            continue;
                        }

                        Class<? extends ServerConnector> connectorClass = configuration
                                .getConnectorClassByEncodedTag(connectorType);

                        // Connector does not exist so we must create it
                        if (connectorClass != UIConnector.class) {
                            // create, initialize and register the paintable
                            connector = getConnector(connectorId, connectorType);
                            createdConnectors.add(connector);
                        } else {
                            // First UIConnector update. Before this the
                            // UIConnector has been created but not
                            // initialized as the connector id has not been
                            // known
                            connectorMap.registerConnector(connectorId,
                                    uIConnector);
                            uIConnector.doInit(connectorId,
                                    ApplicationConnection.this);
                            createdConnectors.add(uIConnector);
                        }
                    } catch (final Throwable e) {
                        VConsole.error(e);
                    }
                }
                return createdConnectors;
            }

            private void updateVaadin6StyleConnectors(ValueMap json) {
                JsArray<ValueMap> changes = json.getJSValueMapArray("changes");
                int length = changes.length();

                VConsole.log(" * Passing UIDL to Vaadin 6 style connectors");
                // update paintables
                for (int i = 0; i < length; i++) {
                    try {
                        final UIDL change = changes.get(i).cast();
                        final UIDL uidl = change.getChildUIDL(0);
                        String connectorId = uidl.getId();

                        final ComponentConnector legacyConnector = (ComponentConnector) connectorMap
                                .getConnector(connectorId);
                        if (legacyConnector instanceof Paintable) {
                            ((Paintable) legacyConnector).updateFromUIDL(uidl,
                                    ApplicationConnection.this);
                        } else if (legacyConnector == null) {
                            VConsole.error("Received update for "
                                    + uidl.getTag()
                                    + ", but there is no such paintable ("
                                    + connectorId + ") rendered.");
                        } else {
                            VConsole.error("Server sent Vaadin 6 style updates for "
                                    + Util.getConnectorString(legacyConnector)
                                    + " but this is not a Vaadin 6 Paintable");
                        }

                    } catch (final Throwable e) {
                        VConsole.error(e);
                    }
                }
            }

            private void sendHierarchyChangeEvents(
                    Collection<ConnectorHierarchyChangeEvent> pendingHierarchyChangeEvents) {
                if (pendingHierarchyChangeEvents.isEmpty()) {
                    return;
                }

                VConsole.log(" * Sending hierarchy change events");
                for (ConnectorHierarchyChangeEvent event : pendingHierarchyChangeEvents) {
                    try {
                        logHierarchyChange(event);
                        event.getConnector().fireEvent(event);
                    } catch (final Throwable e) {
                        VConsole.error(e);
                    }
                }

            }

            private void logHierarchyChange(ConnectorHierarchyChangeEvent event) {
                if (true) {
                    // Always disabled for now. Can be enabled manually
                    return;
                }

                VConsole.log("Hierarchy changed for "
                        + Util.getConnectorString(event.getConnector()));
                String oldChildren = "* Old children: ";
                for (ComponentConnector child : event.getOldChildren()) {
                    oldChildren += Util.getConnectorString(child) + " ";
                }
                VConsole.log(oldChildren);

                String newChildren = "* New children: ";
                ComponentContainerConnector parent = (ComponentContainerConnector) event
                        .getConnector();
                for (ComponentConnector child : parent.getChildComponents()) {
                    newChildren += Util.getConnectorString(child) + " ";
                }
                VConsole.log(newChildren);
            }

            private Collection<StateChangeEvent> updateConnectorState(
                    ValueMap json, Set<ServerConnector> newConnectors) {
                ArrayList<StateChangeEvent> events = new ArrayList<StateChangeEvent>();
                VConsole.log(" * Updating connector states");
                if (!json.containsKey("state")) {
                    return events;
                }
                HashSet<ServerConnector> remainingNewConnectors = new HashSet<ServerConnector>(
                        newConnectors);

                // set states for all paintables mentioned in "state"
                ValueMap states = json.getValueMap("state");
                JsArrayString keyArray = states.getKeyArray();
                for (int i = 0; i < keyArray.length(); i++) {
                    try {
                        String connectorId = keyArray.get(i);
                        ServerConnector connector = connectorMap
                                .getConnector(connectorId);
                        if (null != connector) {

                            JSONObject stateJson = new JSONObject(
                                    states.getJavaScriptObject(connectorId));

                            if (connector instanceof HasJavaScriptConnectorHelper) {
                                ((HasJavaScriptConnectorHelper) connector)
                                        .getJavascriptConnectorHelper()
                                        .setNativeState(
                                                stateJson.getJavaScriptObject());
                            }

                            SharedState state = connector.getState();
                            JsonDecoder.decodeValue(new Type(state.getClass()
                                    .getName(), null), stateJson, state,
                                    ApplicationConnection.this);

                            Set<String> changedProperties = new HashSet<String>();
                            addJsonFields(stateJson, changedProperties, "");

                            if (newConnectors.contains(connector)) {
                                remainingNewConnectors.remove(connector);
                                // Fire events for properties using the default
                                // value for newly created connectors
                                addAllStateFields(
                                        AbstractConnector
                                                .getStateType(connector),
                                        changedProperties, "");
                            }

                            StateChangeEvent event = new StateChangeEvent(
                                    connector, changedProperties);

                            events.add(event);
                        }
                    } catch (final Throwable e) {
                        VConsole.error(e);
                    }
                }

                // Fire events for properties using the default value for newly
                // created connectors even if there were no state changes
                for (ServerConnector connector : remainingNewConnectors) {
                    Set<String> changedProperties = new HashSet<String>();
                    addAllStateFields(
                            AbstractConnector.getStateType(connector),
                            changedProperties, "");

                    StateChangeEvent event = new StateChangeEvent(connector,
                            changedProperties);

                    events.add(event);

                }

                return events;
            }

            /**
             * Recursively adds the names of all properties in the provided
             * state type.
             * 
             * @param type
             *            the type to process
             * @param foundProperties
             *            a set of all currently added properties
             * @param context
             *            the base name of the current object
             */
            private void addAllStateFields(Type type,
                    Set<String> foundProperties, String context) {
                try {
                    Collection<Property> properties = type.getProperties();
                    for (Property property : properties) {
                        String propertyName = context + property.getName();
                        foundProperties.add(propertyName);

                        Type propertyType = property.getType();
                        if (propertyType.hasProperties()) {
                            addAllStateFields(propertyType, foundProperties,
                                    propertyName + ".");
                        }
                    }
                } catch (NoDataException e) {
                    throw new IllegalStateException(
                            "No property info for "
                                    + type
                                    + ". Did you remember to compile the right widgetset?",
                            e);
                }
            }

            /**
             * Recursively adds the names of all fields in all objects in the
             * provided json object.
             * 
             * @param json
             *            the json object to process
             * @param fields
             *            a set of all currently added fields
             * @param context
             *            the base name of the current object
             */
            private void addJsonFields(JSONObject json, Set<String> fields,
                    String context) {
                for (String key : json.keySet()) {
                    String fieldName = context + key;
                    fields.add(fieldName);

                    JSONObject object = json.get(key).isObject();
                    if (object != null) {
                        addJsonFields(object, fields, fieldName + ".");
                    }
                }
            }

            /**
             * Updates the connector hierarchy and returns a list of events that
             * should be fired after update of the hierarchy and the state is
             * done.
             * 
             * @param json
             *            The JSON containing the hierarchy information
             * @return A collection of events that should be fired when update
             *         of hierarchy and state is complete
             */
            private Collection<ConnectorHierarchyChangeEvent> updateConnectorHierarchy(
                    ValueMap json) {
                List<ConnectorHierarchyChangeEvent> events = new LinkedList<ConnectorHierarchyChangeEvent>();

                VConsole.log(" * Updating connector hierarchy");
                if (!json.containsKey("hierarchy")) {
                    return events;
                }

                ValueMap hierarchies = json.getValueMap("hierarchy");
                JsArrayString hierarchyKeys = hierarchies.getKeyArray();
                for (int i = 0; i < hierarchyKeys.length(); i++) {
                    try {
                        String connectorId = hierarchyKeys.get(i);
                        ServerConnector parentConnector = connectorMap
                                .getConnector(connectorId);
                        JsArrayString childConnectorIds = hierarchies
                                .getJSStringArray(connectorId);
                        int childConnectorSize = childConnectorIds.length();

                        List<ServerConnector> newChildren = new ArrayList<ServerConnector>();
                        List<ComponentConnector> newComponents = new ArrayList<ComponentConnector>();
                        for (int connectorIndex = 0; connectorIndex < childConnectorSize; connectorIndex++) {
                            String childConnectorId = childConnectorIds
                                    .get(connectorIndex);
                            ServerConnector childConnector = connectorMap
                                    .getConnector(childConnectorId);
                            if (childConnector == null) {
                                VConsole.error("Hierarchy claims that "
                                        + childConnectorId + " is a child for "
                                        + connectorId + " ("
                                        + parentConnector.getClass().getName()
                                        + ") but no connector with id "
                                        + childConnectorId
                                        + " has been registered");
                                continue;
                            }
                            newChildren.add(childConnector);
                            if (childConnector instanceof ComponentConnector) {
                                newComponents
                                        .add((ComponentConnector) childConnector);
                            } else if (!(childConnector instanceof AbstractExtensionConnector)) {
                                throw new IllegalStateException(
                                        Util.getConnectorString(childConnector)
                                                + " is not a ComponentConnector nor an AbstractExtensionConnector");
                            }
                            if (childConnector.getParent() != parentConnector) {
                                // Avoid extra calls to setParent
                                childConnector.setParent(parentConnector);
                            }
                        }

                        // TODO This check should be done on the server side in
                        // the future so the hierarchy update is only sent when
                        // something actually has changed
                        List<ServerConnector> oldChildren = parentConnector
                                .getChildren();
                        boolean actuallyChanged = !Util.collectionsEquals(
                                oldChildren, newChildren);

                        if (!actuallyChanged) {
                            continue;
                        }

                        if (parentConnector instanceof ComponentContainerConnector) {
                            ComponentContainerConnector ccc = (ComponentContainerConnector) parentConnector;
                            List<ComponentConnector> oldComponents = ccc
                                    .getChildComponents();
                            if (!Util.collectionsEquals(oldComponents,
                                    newComponents)) {
                                // Fire change event if the hierarchy has
                                // changed
                                ConnectorHierarchyChangeEvent event = GWT
                                        .create(ConnectorHierarchyChangeEvent.class);
                                event.setOldChildren(oldComponents);
                                event.setConnector(parentConnector);
                                ccc.setChildComponents(newComponents);
                                events.add(event);
                            }
                        } else if (!newComponents.isEmpty()) {
                            VConsole.error("Hierachy claims "
                                    + Util.getConnectorString(parentConnector)
                                    + " has component children even though it isn't a ComponentContainerConnector");
                        }

                        parentConnector.setChildren(newChildren);

                        // Remove parent for children that are no longer
                        // attached to this (avoid updating children if they
                        // have already been assigned to a new parent)
                        for (ServerConnector oldChild : oldChildren) {
                            if (oldChild.getParent() != parentConnector) {
                                continue;
                            }

                            // TODO This could probably be optimized
                            if (!newChildren.contains(oldChild)) {
                                oldChild.setParent(null);
                            }
                        }
                    } catch (final Throwable e) {
                        VConsole.error(e);
                    }
                }
                return events;

            }

            private void handleRpcInvocations(ValueMap json) {
                if (json.containsKey("rpc")) {
                    VConsole.log(" * Performing server to client RPC calls");

                    JSONArray rpcCalls = new JSONArray(
                            json.getJavaScriptObject("rpc"));

                    int rpcLength = rpcCalls.size();
                    for (int i = 0; i < rpcLength; i++) {
                        try {
                            JSONArray rpcCall = (JSONArray) rpcCalls.get(i);
                            rpcManager.parseAndApplyInvocation(rpcCall,
                                    ApplicationConnection.this);
                        } catch (final Throwable e) {
                            VConsole.error(e);
                        }
                    }
                }

            }

        };
        ApplicationConfiguration.runWhenDependenciesLoaded(c);
    }

    private void loadStyleDependencies(JsArrayString dependencies) {
        // Assuming no reason to interpret in a defined order
        ResourceLoadListener resourceLoadListener = new ResourceLoadListener() {
            @Override
            public void onLoad(ResourceLoadEvent event) {
                ApplicationConfiguration.endDependencyLoading();
            }

            @Override
            public void onError(ResourceLoadEvent event) {
                VConsole.error(event.getResourceUrl()
                        + " could not be loaded, or the load detection failed because the stylesheet is empty.");
                // The show must go on
                onLoad(event);
            }
        };
        ResourceLoader loader = ResourceLoader.get();
        for (int i = 0; i < dependencies.length(); i++) {
            String url = translateVaadinUri(dependencies.get(i));
            ApplicationConfiguration.startDependencyLoading();
            loader.loadStylesheet(url, resourceLoadListener);
        }
    }

    private void loadScriptDependencies(final JsArrayString dependencies) {
        if (dependencies.length() == 0) {
            return;
        }

        // Listener that loads the next when one is completed
        ResourceLoadListener resourceLoadListener = new ResourceLoadListener() {
            @Override
            public void onLoad(ResourceLoadEvent event) {
                if (dependencies.length() != 0) {
                    String url = translateVaadinUri(dependencies.shift());
                    ApplicationConfiguration.startDependencyLoading();
                    // Load next in chain (hopefully already preloaded)
                    event.getResourceLoader().loadScript(url, this);
                }
                // Call start for next before calling end for current
                ApplicationConfiguration.endDependencyLoading();
            }

            @Override
            public void onError(ResourceLoadEvent event) {
                VConsole.error(event.getResourceUrl() + " could not be loaded.");
                // The show must go on
                onLoad(event);
            }
        };

        ResourceLoader loader = ResourceLoader.get();

        // Start chain by loading first
        String url = translateVaadinUri(dependencies.shift());
        ApplicationConfiguration.startDependencyLoading();
        loader.loadScript(url, resourceLoadListener);

        // Preload all remaining
        for (int i = 0; i < dependencies.length(); i++) {
            String preloadUrl = translateVaadinUri(dependencies.get(i));
            loader.preloadResource(preloadUrl, null);
        }
    }

    // Redirect browser, null reloads current page
    private static native void redirect(String url)
    /*-{
    	if (url) {
    		$wnd.location = url;
    	} else {
    		$wnd.location.reload(false);
    	}
    }-*/;

    private void addVariableToQueue(String connectorId, String variableName,
            Object value, boolean immediate) {
        boolean lastOnly = !immediate;
        // note that type is now deduced from value
        addMethodInvocationToQueue(new LegacyChangeVariablesInvocation(
                connectorId, variableName, value), lastOnly, lastOnly);
    }

    /**
     * Adds an explicit RPC method invocation to the send queue.
     * 
     * @since 7.0
     * 
     * @param invocation
     *            RPC method invocation
     * @param delayed
     *            <code>false</code> to trigger sending within a short time
     *            window (possibly combining subsequent calls to a single
     *            request), <code>true</code> to let the framework delay sending
     *            of RPC calls and variable changes until the next non-delayed
     *            change
     * @param lastonly
     *            <code>true</code> to remove all previously delayed invocations
     *            of the same method that were also enqueued with lastonly set
     *            to <code>true</code>. <code>false</code> to add invocation to
     *            the end of the queue without touching previously enqueued
     *            invocations.
     */
    public void addMethodInvocationToQueue(MethodInvocation invocation,
            boolean delayed, boolean lastonly) {
        String tag;
        if (lastonly) {
            tag = invocation.getLastonlyTag();
            assert !tag.matches("\\d+") : "getLastonlyTag value must have at least one non-digit character";
            pendingInvocations.remove(tag);
        } else {
            tag = Integer.toString(lastInvocationTag++);
        }
        pendingInvocations.put(tag, invocation);
        if (!delayed) {
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
        if (!deferedSendPending) {
            deferedSendPending = true;
            Scheduler.get().scheduleDeferred(sendPendingCommand);
        }
    }

    private final ScheduledCommand sendPendingCommand = new ScheduledCommand() {
        @Override
        public void execute() {
            deferedSendPending = false;
            doSendPendingVariableChanges();
        }
    };
    private boolean deferedSendPending = false;

    private void doSendPendingVariableChanges() {
        if (applicationRunning) {
            if (hasActiveRequest()) {
                // skip empty queues if there are pending bursts to be sent
                if (pendingInvocations.size() > 0 || pendingBursts.size() == 0) {
                    pendingBursts.add(pendingInvocations);
                    pendingInvocations = new LinkedHashMap<String, MethodInvocation>();
                    // Keep tag string short
                    lastInvocationTag = 0;
                }
            } else {
                buildAndSendVariableBurst(pendingInvocations, false);
            }
        }
    }

    /**
     * Build the variable burst and send it to server.
     * 
     * When sync is forced, we also force sending of all pending variable-bursts
     * at the same time. This is ok as we can assume that DOM will never be
     * updated after this.
     * 
     * @param pendingInvocations
     *            List of RPC method invocations to send
     * @param forceSync
     *            Should we use synchronous request?
     */
    private void buildAndSendVariableBurst(
            LinkedHashMap<String, MethodInvocation> pendingInvocations,
            boolean forceSync) {
        final StringBuffer req = new StringBuffer();

        while (!pendingInvocations.isEmpty()) {
            if (ApplicationConfiguration.isDebugMode()) {
                Util.logVariableBurst(this, pendingInvocations.values());
            }

            JSONArray reqJson = new JSONArray();

            for (MethodInvocation invocation : pendingInvocations.values()) {
                JSONArray invocationJson = new JSONArray();
                invocationJson.set(0,
                        new JSONString(invocation.getConnectorId()));
                invocationJson.set(1,
                        new JSONString(invocation.getInterfaceName()));
                invocationJson.set(2,
                        new JSONString(invocation.getMethodName()));
                JSONArray paramJson = new JSONArray();
                boolean restrictToInternalTypes = isLegacyVariableChange(invocation);
                for (int i = 0; i < invocation.getParameters().length; ++i) {
                    // TODO non-static encoder? type registration?
                    paramJson.set(i, JsonEncoder.encode(
                            invocation.getParameters()[i],
                            restrictToInternalTypes, this));
                }
                invocationJson.set(3, paramJson);
                reqJson.set(reqJson.size(), invocationJson);
            }

            // escape burst separators (if any)
            req.append(escapeBurstContents(reqJson.toString()));

            pendingInvocations.clear();
            // Keep tag string short
            lastInvocationTag = 0;
            // Append all the bursts to this synchronous request
            if (forceSync && !pendingBursts.isEmpty()) {
                pendingInvocations = pendingBursts.get(0);
                pendingBursts.remove(0);
                req.append(VAR_BURST_SEPARATOR);
            }
        }

        // Include the browser detail parameters if they aren't already sent
        String extraParams;
        if (!getConfiguration().isBrowserDetailsSent()) {
            extraParams = getNativeBrowserDetailsParameters(getConfiguration()
                    .getRootPanelId());
            getConfiguration().setBrowserDetailsSent();
        } else {
            extraParams = "";
        }
        if (!getConfiguration().isWidgetsetVersionSent()) {
            if (!extraParams.isEmpty()) {
                extraParams += "&";
            }
            String widgetsetVersion = Version.getFullVersion();
            extraParams += "wsver=" + widgetsetVersion;

            getConfiguration().setWidgetsetVersionSent();
        }
        makeUidlRequest(req.toString(), extraParams, forceSync);
    }

    private boolean isLegacyVariableChange(MethodInvocation invocation) {
        return ApplicationConstants.UPDATE_VARIABLE_METHOD.equals(invocation
                .getInterfaceName())
                && ApplicationConstants.UPDATE_VARIABLE_METHOD
                        .equals(invocation.getMethodName());
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * <p>
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * </p>
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param newValue
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */
    public void updateVariable(String paintableId, String variableName,
            ServerConnector newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * <p>
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * </p>
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param newValue
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */

    public void updateVariable(String paintableId, String variableName,
            String newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * <p>
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * </p>
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param newValue
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */

    public void updateVariable(String paintableId, String variableName,
            int newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * <p>
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * </p>
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param newValue
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */

    public void updateVariable(String paintableId, String variableName,
            long newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * <p>
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * </p>
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param newValue
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */

    public void updateVariable(String paintableId, String variableName,
            float newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * <p>
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * </p>
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param newValue
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */

    public void updateVariable(String paintableId, String variableName,
            double newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * <p>
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * </p>
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param newValue
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */

    public void updateVariable(String paintableId, String variableName,
            boolean newValue, boolean immediate) {
        addVariableToQueue(paintableId, variableName, newValue, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * <p>
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * </p>
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param map
     *            the new values to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */
    public void updateVariable(String paintableId, String variableName,
            Map<String, Object> map, boolean immediate) {
        addVariableToQueue(paintableId, variableName, map, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * 
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update.
     * 
     * A null array is sent as an empty array.
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param values
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */
    public void updateVariable(String paintableId, String variableName,
            String[] values, boolean immediate) {
        addVariableToQueue(paintableId, variableName, values, immediate);
    }

    /**
     * Sends a new value for the given paintables given variable to the server.
     * 
     * The update is actually queued to be sent at a suitable time. If immediate
     * is true, the update is sent as soon as possible. If immediate is false,
     * the update will be sent along with the next immediate update. </p>
     * 
     * A null array is sent as an empty array.
     * 
     * 
     * @param paintableId
     *            the id of the paintable that owns the variable
     * @param variableName
     *            the name of the variable
     * @param values
     *            the new value to be sent
     * @param immediate
     *            true if the update is to be sent as soon as possible
     */
    public void updateVariable(String paintableId, String variableName,
            Object[] values, boolean immediate) {
        addVariableToQueue(paintableId, variableName, values, immediate);
    }

    /**
     * Encode burst separator characters in a String for transport over the
     * network. This protects from separator injection attacks.
     * 
     * @param value
     *            to encode
     * @return encoded value
     */
    protected String escapeBurstContents(String value) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); ++i) {
            char character = value.charAt(i);
            switch (character) {
            case VAR_ESCAPE_CHARACTER:
                // fall-through - escape character is duplicated
            case VAR_BURST_SEPARATOR:
                result.append(VAR_ESCAPE_CHARACTER);
                // encode as letters for easier reading
                result.append(((char) (character + 0x30)));
                break;
            default:
                // the char is not a special one - add it to the result as is
                result.append(character);
                break;
            }
        }
        return result.toString();
    }

    private boolean runningLayout = false;

    /**
     * Causes a re-calculation/re-layout of all paintables in a container.
     * 
     * @param container
     */
    public void runDescendentsLayout(HasWidgets container) {
        if (runningLayout) {
            return;
        }
        runningLayout = true;
        internalRunDescendentsLayout(container);
        runningLayout = false;
    }

    /**
     * This will cause re-layouting of all components. Mainly used for
     * development. Published to JavaScript.
     */
    public void forceLayout() {
        Duration duration = new Duration();

        layoutManager.forceLayout();

        VConsole.log("forceLayout in " + duration.elapsedMillis() + " ms");
    }

    private void internalRunDescendentsLayout(HasWidgets container) {
        // getConsole().log(
        // "runDescendentsLayout(" + Util.getSimpleName(container) + ")");
        final Iterator<Widget> childWidgets = container.iterator();
        while (childWidgets.hasNext()) {
            final Widget child = childWidgets.next();

            if (getConnectorMap().isConnector(child)) {

                if (handleComponentRelativeSize(child)) {
                    /*
                     * Only need to propagate event if "child" has a relative
                     * size
                     */

                    if (child instanceof ContainerResizedListener) {
                        ((ContainerResizedListener) child).iLayout();
                    }

                    if (child instanceof HasWidgets) {
                        final HasWidgets childContainer = (HasWidgets) child;
                        internalRunDescendentsLayout(childContainer);
                    }
                }
            } else if (child instanceof HasWidgets) {
                // propagate over non Paintable HasWidgets
                internalRunDescendentsLayout((HasWidgets) child);
            }

        }
    }

    /**
     * Converts relative sizes into pixel sizes.
     * 
     * @param child
     * @return true if the child has a relative size
     */
    private boolean handleComponentRelativeSize(ComponentConnector paintable) {
        return false;
    }

    /**
     * Converts relative sizes into pixel sizes.
     * 
     * @param child
     * @return true if the child has a relative size
     */
    public boolean handleComponentRelativeSize(Widget widget) {
        return handleComponentRelativeSize(connectorMap.getConnector(widget));

    }

    @Deprecated
    public ComponentConnector getPaintable(UIDL uidl) {
        // Non-component connectors shouldn't be painted from legacy connectors
        return (ComponentConnector) getConnector(uidl.getId(),
                Integer.parseInt(uidl.getTag()));
    }

    /**
     * Get either an existing ComponentConnector or create a new
     * ComponentConnector with the given type and id.
     * 
     * If a ComponentConnector with the given id already exists, returns it.
     * Otherwise creates and registers a new ComponentConnector of the given
     * type.
     * 
     * @param connectorId
     *            Id of the paintable
     * @param connectorType
     *            Type of the connector, as passed from the server side
     * 
     * @return Either an existing ComponentConnector or a new ComponentConnector
     *         of the given type
     */
    public ServerConnector getConnector(String connectorId, int connectorType) {
        if (!connectorMap.hasConnector(connectorId)) {
            return createAndRegisterConnector(connectorId, connectorType);
        }
        return connectorMap.getConnector(connectorId);
    }

    /**
     * Creates a new ServerConnector with the given type and id.
     * 
     * Creates and registers a new ServerConnector of the given type. Should
     * never be called with the connector id of an existing connector.
     * 
     * @param connectorId
     *            Id of the new connector
     * @param connectorType
     *            Type of the connector, as passed from the server side
     * 
     * @return A new ServerConnector of the given type
     */
    private ServerConnector createAndRegisterConnector(String connectorId,
            int connectorType) {
        // Create and register a new connector with the given type
        ServerConnector p = widgetSet.createConnector(connectorType,
                configuration);
        connectorMap.registerConnector(connectorId, p);
        p.doInit(connectorId, this);

        return p;
    }

    /**
     * Gets a recource that has been pre-loaded via UIDL, such as custom
     * layouts.
     * 
     * @param name
     *            identifier of the resource to get
     * @return the resource
     */
    public String getResource(String name) {
        return resourcesMap.get(name);
    }

    /**
     * Singleton method to get instance of app's context menu.
     * 
     * @return VContextMenu object
     */
    public VContextMenu getContextMenu() {
        if (contextMenu == null) {
            contextMenu = new VContextMenu();
            DOM.setElementProperty(contextMenu.getElement(), "id",
                    "PID_VAADIN_CM");
        }
        return contextMenu;
    }

    /**
     * Translates custom protocols in UIDL URI's to be recognizable by browser.
     * All uri's from UIDL should be routed via this method before giving them
     * to browser due URI's in UIDL may contain custom protocols like theme://.
     * 
     * @param uidlUri
     *            Vaadin URI from uidl
     * @return translated URI ready for browser
     */
    public String translateVaadinUri(String uidlUri) {
        if (uidlUri == null) {
            return null;
        }
        if (uidlUri.startsWith("theme://")) {
            final String themeUri = configuration.getThemeUri();
            if (themeUri == null) {
                VConsole.error("Theme not set: ThemeResource will not be found. ("
                        + uidlUri + ")");
            }
            uidlUri = themeUri + uidlUri.substring(7);
        }

        if (uidlUri.startsWith(ApplicationConstants.DEPENDENCY_PROTOCOL_PREFIX)) {
            // getAppUri *should* always end with /
            // substring *should* always start with / (dependency:///foo.bar
            // without dependency://)
            uidlUri = ApplicationConstants.APP_PROTOCOL_PREFIX
                    + ApplicationConstants.DEPENDENCY_RESOURCE_PREFIX
                    + uidlUri
                            .substring(ApplicationConstants.DEPENDENCY_PROTOCOL_PREFIX
                                    .length());
            // Let translation of app:// urls take care of the rest
        }
        if (uidlUri.startsWith(ApplicationConstants.APP_PROTOCOL_PREFIX)) {
            String relativeUrl = uidlUri
                    .substring(ApplicationConstants.APP_PROTOCOL_PREFIX
                            .length());
            ApplicationConfiguration conf = getConfiguration();
            String serviceUrl = conf.getServiceUrl();
            if (conf.useServiceUrlPathParam()) {
                // Should put path in v-resourcePath parameter and append query
                // params to base portlet url
                String[] parts = relativeUrl.split("\\?", 2);
                String path = parts[0];

                // If there's a "?" followed by something, append it as a query
                // string to the base URL
                if (parts.length > 1) {
                    String appUrlParams = parts[1];
                    serviceUrl = addGetParameters(serviceUrl, appUrlParams);
                }
                if (!path.startsWith("/")) {
                    path = '/' + path;
                }
                String pathParam = ApplicationConstants.V_RESOURCE_PATH + "="
                        + URL.encodeQueryString(path);
                serviceUrl = addGetParameters(serviceUrl, pathParam);
                uidlUri = serviceUrl;
            } else {
                uidlUri = serviceUrl + relativeUrl;
            }
        }
        return uidlUri;
    }

    /**
     * Gets the URI for the current theme. Can be used to reference theme
     * resources.
     * 
     * @return URI to the current theme
     */
    public String getThemeUri() {
        return configuration.getThemeUri();
    }

    /**
     * Listens for Notification hide event, and redirects. Used for system
     * messages, such as session expired.
     * 
     */
    private class NotificationRedirect implements VNotification.EventListener {
        String url;

        NotificationRedirect(String url) {
            this.url = url;
        }

        @Override
        public void notificationHidden(HideEvent event) {
            redirect(url);
        }

    }

    /* Extended title handling */

    private final VTooltip tooltip = new VTooltip(this);

    private ConnectorMap connectorMap = GWT.create(ConnectorMap.class);

    protected String getUidlSecurityKey() {
        return uidlSecurityKey;
    }

    /**
     * Use to notify that the given component's caption has changed; layouts may
     * have to be recalculated.
     * 
     * @param component
     *            the Paintable whose caption has changed
     */
    public void captionSizeUpdated(Widget widget) {
        componentCaptionSizeChanges.add(widget);
    }

    /**
     * Gets the main view
     * 
     * @return the main view
     */
    public UIConnector getRootConnector() {
        return uIConnector;
    }

    /**
     * Gets the {@link ApplicationConfiguration} for the current application.
     * 
     * @see ApplicationConfiguration
     * @return the configuration for this application
     */
    public ApplicationConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Checks if there is a registered server side listener for the event. The
     * list of events which has server side listeners is updated automatically
     * before the component is updated so the value is correct if called from
     * updatedFromUIDL.
     * 
     * @param paintable
     *            The connector to register event listeners for
     * @param eventIdentifier
     *            The identifier for the event
     * @return true if at least one listener has been registered on server side
     *         for the event identified by eventIdentifier.
     * @deprecated as of Vaadin 7. Use
     *             {@link ComponentState#hasEventListener(String)} instead
     */
    @Deprecated
    public boolean hasEventListeners(ComponentConnector paintable,
            String eventIdentifier) {
        return paintable.hasEventListener(eventIdentifier);
    }

    /**
     * Adds the get parameters to the uri and returns the new uri that contains
     * the parameters.
     * 
     * @param uri
     *            The uri to which the parameters should be added.
     * @param extraParams
     *            One or more parameters in the format "a=b" or "c=d&e=f". An
     *            empty string is allowed but will not modify the url.
     * @return The modified URI with the get parameters in extraParams added.
     */
    public static String addGetParameters(String uri, String extraParams) {
        if (extraParams == null || extraParams.length() == 0) {
            return uri;
        }
        // RFC 3986: The query component is indicated by the first question
        // mark ("?") character and terminated by a number sign ("#") character
        // or by the end of the URI.
        String fragment = null;
        int hashPosition = uri.indexOf('#');
        if (hashPosition != -1) {
            // Fragment including "#"
            fragment = uri.substring(hashPosition);
            // The full uri before the fragment
            uri = uri.substring(0, hashPosition);
        }

        if (uri.contains("?")) {
            uri += "&";
        } else {
            uri += "?";
        }
        uri += extraParams;

        if (fragment != null) {
            uri += fragment;
        }

        return uri;
    }

    ConnectorMap getConnectorMap() {
        return connectorMap;
    }

    /**
     * @deprecated No longer needed in Vaadin 7
     */
    @Deprecated
    public void unregisterPaintable(ServerConnector p) {
        VConsole.log("unregisterPaintable (unnecessarily) called for "
                + Util.getConnectorString(p));
    }

    /**
     * Get VTooltip instance related to application connection
     * 
     * @return VTooltip instance
     */
    public VTooltip getVTooltip() {
        return tooltip;
    }

    /**
     * Method provided for backwards compatibility. Duties previously done by
     * this method is now handled by the state change event handler in
     * AbstractComponentConnector. The only function this method has is to
     * return true if the UIDL is a "cached" update.
     * 
     * @param component
     * @param uidl
     * @param manageCaption
     * @return
     */
    @Deprecated
    public boolean updateComponent(Widget component, UIDL uidl,
            boolean manageCaption) {
        ComponentConnector connector = getConnectorMap()
                .getConnector(component);
        if (!AbstractComponentConnector.isRealUpdate(uidl)) {
            return true;
        }

        if (!manageCaption) {
            VConsole.error(Util.getConnectorString(connector)
                    + " called updateComponent with manageCaption=false. The parameter was ignored - override delegateCaption() to return false instead. It is however not recommended to use caption this way at all.");
        }
        return false;
    }

    /**
     * @deprecated as of Vaadin 7. Use
     *             {@link ComponentState#hasEventListener(String)} instead
     */
    @Deprecated
    public boolean hasEventListeners(Widget widget, String eventIdentifier) {
        return hasEventListeners(getConnectorMap().getConnector(widget),
                eventIdentifier);
    }

    LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * Schedules a heartbeat request to occur after the configured heartbeat
     * interval elapses if the interval is a positive number. Otherwise, does
     * nothing.
     * 
     * @see #sendHeartbeat()
     * @see ApplicationConfiguration#getHeartbeatInterval()
     */
    protected void scheduleHeartbeat() {
        final int interval = getConfiguration().getHeartbeatInterval();
        if (interval > 0) {
            VConsole.log("Scheduling heartbeat in " + interval + " seconds");
            new Timer() {
                @Override
                public void run() {
                    sendHeartbeat();
                }
            }.schedule(interval * 1000);
        }
    }

    /**
     * Sends a heartbeat request to the server.
     * <p>
     * Heartbeat requests are used to inform the server that the client-side is
     * still alive. If the client page is closed or the connection lost, the
     * server will eventually close the inactive Root.
     * <p>
     * <b>TODO</b>: Improved error handling, like in doUidlRequest().
     * 
     * @see #scheduleHeartbeat()
     */
    protected void sendHeartbeat() {
        final String uri = addGetParameters(
                translateVaadinUri(ApplicationConstants.APP_PROTOCOL_PREFIX
                        + ApplicationConstants.HEARTBEAT_REQUEST_PATH),
                UIConstants.UI_ID_PARAMETER + "="
                        + getConfiguration().getUIId());

        final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, uri);

        final RequestCallback callback = new RequestCallback() {

            @Override
            public void onResponseReceived(Request request, Response response) {
                int status = response.getStatusCode();
                if (status == Response.SC_OK) {
                    // TODO Permit retry in some error situations
                    VConsole.log("Heartbeat response OK");
                    scheduleHeartbeat();
                } else {
                    VConsole.error("Heartbeat request failed with status code "
                            + status);
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                VConsole.error("Heartbeat request resulted in exception");
                VConsole.error(exception);
            }
        };

        rb.setCallback(callback);

        try {
            VConsole.log("Sending heartbeat request...");
            rb.send();
        } catch (RequestException re) {
            callback.onError(null, re);
        }
    }

    /**
     * Timer used to make sure that no misbehaving components can lock the
     * rendering phase forever.
     */
    Timer forceHandleMessage = new Timer() {
        @Override
        public void run() {
            VConsole.log("WARNING: rendering was never resumed, forcing reload...");
            renderingLocks.clear();
            handlePendingMessages();
        }
    };

    /**
     * This method can be used to postpone rendering of a response for a short
     * period of time (e.g. to avoid the rendering process during animation).
     * 
     * @param lock
     */
    public void suspendRendering(Object lock) {
        renderingLocks.add(lock);
    }

    /**
     * Resumes the rendering process once all locks have been removed.
     * 
     * @param lock
     */
    public void resumeRendering(Object lock) {
        VConsole.log("...resuming UIDL handling.");
        renderingLocks.remove(lock);
        if (renderingLocks.isEmpty()) {
            VConsole.log("No more rendering locks, rendering pending requests.");
            forceHandleMessage.cancel();
            handlePendingMessages();
        }
    }

    /**
     * Handles all pending UIDL messages queued while the rendering was
     * suspended.
     */
    private void handlePendingMessages() {
        for (PendingUIDLMessage pending : pendingUIDLMessages) {
            handleUIDLMessage(pending.getStart(), pending.getJsonText(),
                    pending.getJson());
        }
        pendingUIDLMessages.clear();
    }

    private boolean handleErrorInDelegate(String details, int statusCode) {
        if (communicationErrorDelegate == null) {
            return false;
        }
        return communicationErrorDelegate.onError(details, statusCode);
    }

    /**
     * Sets the delegate that is called whenever a communication error occurrs.
     * 
     * @param delegate
     *            the delegate.
     */
    public void setCommunicationErrorDelegate(
            CommunicationErrorDelegate delegate) {
        communicationErrorDelegate = delegate;
    }

    public void setApplicationRunning(boolean running) {
        applicationRunning = running;
    }

    public boolean isApplicationRunning() {
        return applicationRunning;
    }

    public <H extends EventHandler> HandlerRegistration addHandler(
            GwtEvent.Type<H> type, H handler) {
        return eventBus.addHandler(type, handler);
    }
}
