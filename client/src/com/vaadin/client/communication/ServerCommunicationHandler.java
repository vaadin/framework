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
package com.vaadin.client.communication;

import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.RequestStartingEvent;
import com.vaadin.client.ApplicationConnection.ResponseHandlingEndedEvent;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;
import com.vaadin.client.VLoadingIndicator;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;
import com.vaadin.shared.util.SharedUtil;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * ServerCommunicationHandler is responsible for communicating (sending and
 * receiving messages) with the servlet.
 * 
 * It will internally use either XHR or websockets for communicating, depending
 * on how the application is configured.
 * 
 * Uses {@link ServerMessageHandler} for processing received messages
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ServerCommunicationHandler {

    private final String JSON_COMMUNICATION_PREFIX = "for(;;);[";
    private final String JSON_COMMUNICATION_SUFFIX = "]";

    private ApplicationConnection connection;
    private PushConnection push;
    private boolean hasActiveRequest = false;
    private Date requestStartTime;

    /**
     * Webkit will ignore outgoing requests while waiting for a response to a
     * navigation event (indicated by a beforeunload event). When this happens,
     * we should keep trying to send the request every now and then until there
     * is a response or until it throws an exception saying that it is already
     * being sent.
     */
    private boolean webkitMaybeIgnoringRequests = false;

    /**
     * Counter for the messages send to the server. First sent message has id 0.
     */
    private int clientToServerMessageId = 0;

    public ServerCommunicationHandler() {
        Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
                webkitMaybeIgnoringRequests = true;
            }
        });

    }

    /**
     * Sets the application connection this handler is connected to
     *
     * @param connection
     *            the application connection this handler is connected to
     */
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    public static Logger getLogger() {
        return Logger.getLogger(ServerCommunicationHandler.class.getName());
    }

    public void sendInvocationsToServer() {
        if (!connection.isApplicationRunning()) {
            getLogger()
                    .warning(
                            "Trying to send RPC from not yet started or stopped application");
            return;
        }

        if (hasActiveRequest() || (push != null && !push.isActive())) {
            // There is an active request or push is enabled but not active
            // -> send when current request completes or push becomes active
        } else {
            doSendInvocationsToServer();
        }
    }

    /**
     * Sends all pending method invocations (server RPC and legacy variable
     * changes) to the server.
     * 
     */
    private void doSendInvocationsToServer() {
        ServerRpcQueue serverRpcQueue = getServerRpcQueue();
        if (serverRpcQueue.isEmpty()) {
            return;
        }

        if (ApplicationConfiguration.isDebugMode()) {
            Util.logMethodInvocations(connection, serverRpcQueue.getAll());
        }

        boolean showLoadingIndicator = serverRpcQueue.showLoadingIndicator();
        JsonArray reqJson = serverRpcQueue.toJson();
        serverRpcQueue.clear();

        if (reqJson.length() == 0) {
            // Nothing to send, all invocations were filtered out (for
            // non-existing connectors)
            getLogger()
                    .warning(
                            "All RPCs filtered out, not sending anything to the server");
            return;
        }

        JsonObject extraJson = Json.createObject();
        if (!connection.getConfiguration().isWidgetsetVersionSent()) {
            extraJson.put(ApplicationConstants.WIDGETSET_VERSION_ID,
                    Version.getFullVersion());
            connection.getConfiguration().setWidgetsetVersionSent();
        }
        if (showLoadingIndicator) {
            connection.getLoadingIndicator().trigger();
        }
        send(reqJson, extraJson);
    }

    private ServerRpcQueue getServerRpcQueue() {
        return connection.getServerRpcQueue();
    }

    /**
     * Makes an UIDL request to the server.
     * 
     * @param reqInvocations
     *            Data containing RPC invocations and all related information.
     * @param extraParams
     *            Parameters that are added to the payload
     */
    protected void send(final JsonArray reqInvocations,
            final JsonObject extraJson) {
        startRequest();

        JsonObject payload = Json.createObject();
        String csrfToken = getServerMessageHandler().getCsrfToken();
        if (!csrfToken.equals(ApplicationConstants.CSRF_TOKEN_DEFAULT_VALUE)) {
            payload.put(ApplicationConstants.CSRF_TOKEN, csrfToken);
        }
        payload.put(ApplicationConstants.RPC_INVOCATIONS, reqInvocations);
        payload.put(ApplicationConstants.SERVER_SYNC_ID,
                getServerMessageHandler().getLastSeenServerSyncId());
        payload.put(ApplicationConstants.CLIENT_TO_SERVER_ID,
                clientToServerMessageId++);

        getLogger()
                .info("Making UIDL Request with params: " + payload.toJson());
        if (extraJson != null) {
            for (String key : extraJson.keys()) {
                payload.put(key, extraJson.get(key));
            }
        }

        // FIXME XHR specific
        String uri = connection
                .translateVaadinUri(ApplicationConstants.APP_PROTOCOL_PREFIX
                        + ApplicationConstants.UIDL_PATH + '/');

        uri = SharedUtil.addGetParameters(uri, UIConstants.UI_ID_PARAMETER
                + "=" + connection.getConfiguration().getUIId());

        doUidlRequest(uri, payload, true);

    }

    /**
     * Sends an asynchronous or synchronous UIDL request to the server using the
     * given URI.
     * 
     * @param uri
     *            The URI to use for the request. May includes GET parameters
     * @param payload
     *            The contents of the request to send
     * @param retry
     *            true when a status code 0 should be retried
     */
    public void doUidlRequest(final String uri, final JsonObject payload,
            final boolean retry) {
        RequestCallback requestCallback = new RequestCallback() {

            @Override
            public void onError(Request request, Throwable exception) {
                getCommunicationProblemHandler().xhrException(
                        payload,
                        new CommunicationProblemEvent(request, uri, payload,
                                exception));
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                getLogger().info(
                        "Server visit took "
                                + String.valueOf((new Date()).getTime()
                                        - requestStartTime.getTime()) + "ms");

                int statusCode = response.getStatusCode();

                if (statusCode == 200) {
                    getCommunicationProblemHandler().xhrOk();
                } else {
                    // There was a problem
                    CommunicationProblemEvent problemEvent = new CommunicationProblemEvent(
                            request, uri, payload, response);

                    getCommunicationProblemHandler().xhrInvalidStatusCode(
                            problemEvent, retry);
                    return;
                }

                String contentType = response.getHeader("Content-Type");
                if (contentType == null
                        || !contentType.startsWith("application/json")) {
                    getCommunicationProblemHandler().xhrInvalidContent(
                            new CommunicationProblemEvent(request, uri,
                                    payload, response));
                    return;
                }

                // for(;;);["+ realJson +"]"
                String responseText = response.getText();

                if (!responseText.startsWith(JSON_COMMUNICATION_PREFIX)) {
                    getCommunicationProblemHandler().xhrInvalidContent(
                            new CommunicationProblemEvent(request, uri,
                                    payload, response));
                    return;
                }

                final String jsonText = responseText.substring(
                        JSON_COMMUNICATION_PREFIX.length(),
                        responseText.length()
                                - JSON_COMMUNICATION_SUFFIX.length());

                getServerMessageHandler().handleMessage(jsonText);
            }
        };
        if (push != null) {
            push.push(payload);
        } else {
            try {
                doAjaxRequest(uri, payload, requestCallback);
            } catch (RequestException e) {
                getCommunicationProblemHandler().xhrException(payload,
                        new CommunicationProblemEvent(null, uri, payload, e));
            }
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
    protected void doAjaxRequest(String uri, JsonObject payload,
            RequestCallback requestCallback) throws RequestException {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, uri);
        // TODO enable timeout
        // rb.setTimeoutMillis(timeoutMillis);
        // TODO this should be configurable
        rb.setHeader("Content-Type", JsonConstants.JSON_CONTENT_TYPE);
        rb.setRequestData(payload.toJson());
        rb.setCallback(requestCallback);

        final Request request = rb.send();
        if (webkitMaybeIgnoringRequests && BrowserInfo.get().isWebkit()) {
            final int retryTimeout = 250;
            new Timer() {
                @Override
                public void run() {
                    // Use native js to access private field in Request
                    if (resendRequest(request) && webkitMaybeIgnoringRequests) {
                        // Schedule retry if still needed
                        schedule(retryTimeout);
                    }
                }
            }.schedule(retryTimeout);
        }
    }

    private static native boolean resendRequest(Request request)
    /*-{
        var xhr = request.@com.google.gwt.http.client.Request::xmlHttpRequest
        if (xhr.readyState != 1) {
            // Progressed to some other readyState -> no longer blocked
            return false;
        }
        try {
            xhr.send();
            return true;
        } catch (e) {
            // send throws exception if it is running for real
            return false;
        }
    }-*/;

    /**
     * Sets the status for the push connection.
     * 
     * @param enabled
     *            <code>true</code> to enable the push connection;
     *            <code>false</code> to disable the push connection.
     */
    public void setPushEnabled(boolean enabled) {
        final PushConfigurationState pushState = connection.getUIConnector()
                .getState().pushConfiguration;

        if (enabled && push == null) {
            push = GWT.create(PushConnection.class);
            push.init(connection, pushState);
        } else if (!enabled && push != null && push.isActive()) {
            push.disconnect(new Command() {
                @Override
                public void execute() {
                    push = null;
                    /*
                     * If push has been enabled again while we were waiting for
                     * the old connection to disconnect, now is the right time
                     * to open a new connection
                     */
                    if (pushState.mode.isEnabled()) {
                        setPushEnabled(true);
                    }

                    /*
                     * Send anything that was enqueued while we waited for the
                     * connection to close
                     */
                    if (getServerRpcQueue().isFlushPending()) {
                        getServerRpcQueue().flush();
                    }
                }
            });
        }
    }

    public void startRequest() {
        if (hasActiveRequest) {
            getLogger().severe(
                    "Trying to start a new request while another is active");
        }
        hasActiveRequest = true;
        requestStartTime = new Date();
        connection.fireEvent(new RequestStartingEvent(connection));
    }

    public void endRequest() {
        if (!hasActiveRequest) {
            getLogger().severe("No active request");
        }
        // After sendInvocationsToServer() there may be a new active
        // request, so we must set hasActiveRequest to false before, not after,
        // the call. Active requests used to be tracked with an integer counter,
        // so setting it after used to work but not with the #8505 changes.
        hasActiveRequest = false;

        webkitMaybeIgnoringRequests = false;

        if (connection.isApplicationRunning()) {
            if (getServerRpcQueue().isFlushPending()) {
                sendInvocationsToServer();
            }
            ApplicationConnection.runPostRequestHooks(connection
                    .getConfiguration().getRootPanelId());
        }

        // deferring to avoid flickering
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                if (!connection.isApplicationRunning()
                        || !(hasActiveRequest() || getServerRpcQueue()
                                .isFlushPending())) {
                    getLoadingIndicator().hide();

                    // If on Liferay and session expiration management is in
                    // use, extend session duration on each request.
                    // Doing it here rather than before the request to improve
                    // responsiveness.
                    // Postponed until the end of the next request if other
                    // requests still pending.
                    ApplicationConnection.extendLiferaySession();
                }
            }
        });
        connection.fireEvent(new ResponseHandlingEndedEvent(connection));
    }

    /**
     * Indicates whether or not there are currently active UIDL requests. Used
     * internally to sequence requests properly, seldom needed in Widgets.
     * 
     * @return true if there are active requests
     */
    public boolean hasActiveRequest() {
        return hasActiveRequest;
    }

    /**
     * Returns a human readable string representation of the method used to
     * communicate with the server.
     * 
     * @return A string representation of the current transport type
     */
    public String getCommunicationMethodName() {
        if (push != null) {
            return "Push (" + push.getTransportType() + ")";
        } else {
            return "XHR";
        }
    }

    private CommunicationProblemHandler getCommunicationProblemHandler() {
        return connection.getCommunicationProblemHandler();
    }

    private ServerMessageHandler getServerMessageHandler() {
        return connection.getServerMessageHandler();
    }

    private VLoadingIndicator getLoadingIndicator() {
        return connection.getLoadingIndicator();
    }

    /**
     * Resynchronize the client side, i.e. reload all component hierarchy and
     * state from the server
     */
    public void resynchronize() {
        getLogger().info("Resynchronizing from server");
        JsonObject resyncParam = Json.createObject();
        resyncParam.put(ApplicationConstants.RESYNCHRONIZE_ID, true);
        send(Json.createArray(), resyncParam);
    }

    /**
     * Used internally to update what the server expects
     * 
     * @param clientToServerMessageId
     *            the new client id to set
     */
    public void setClientToServerMessageId(int nextExpectedId) {
        if (nextExpectedId == clientToServerMessageId) {
            // No op as everything matches they way it should
            return;
        }

        if (nextExpectedId > clientToServerMessageId) {
            if (clientToServerMessageId == 0) {
                // We have never sent a message to the server, so likely the
                // server knows better (typical case is that we refreshed a
                // @PreserveOnRefresh UI)
                getLogger().info(
                        "Updating client-to-server id to " + nextExpectedId
                                + " based on server");
            } else {
                getLogger().warning(
                        "Server expects next client-to-server id to be "
                                + nextExpectedId + " but we were going to use "
                                + clientToServerMessageId + ". Will use "
                                + nextExpectedId + ".");
            }
            clientToServerMessageId = nextExpectedId;
        } else {
            // Server has not yet seen all our messages
            // Do nothing as they will arrive eventually
        }
    }
}
