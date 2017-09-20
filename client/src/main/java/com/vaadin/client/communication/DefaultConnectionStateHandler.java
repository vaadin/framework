/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.ApplicationStoppedEvent;
import com.vaadin.client.ApplicationConnection.ApplicationStoppedHandler;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.communication.AtmospherePushConnection.AtmosphereResponse;
import com.vaadin.shared.ui.ui.UIState.ReconnectDialogConfigurationState;

import elemental.json.JsonObject;

/**
 * Default implementation of the connection state handler.
 * <p>
 * Handles temporary errors by showing a reconnect dialog to the user while
 * trying to re-establish the connection to the server and re-send the pending
 * message.
 * <p>
 * Handles permanent errors by showing a critical system notification to the
 * user
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public class DefaultConnectionStateHandler implements ConnectionStateHandler {

    private ApplicationConnection connection;
    private ReconnectDialog reconnectDialog = GWT.create(ReconnectDialog.class);
    private int reconnectAttempt = 0;
    private Type reconnectionCause = null;

    private Timer scheduledReconnect;
    private Timer dialogShowTimer = new Timer() {

        @Override
        public void run() {
            showDialog();
        }

    };

    protected enum Type {
        HEARTBEAT(0), PUSH(1), XHR(2);

        private int priority;

        private Type(int priority) {
            this.priority = priority;
        }

        public boolean isMessage() {
            return this == PUSH || this == XHR;
        }

        /**
         * Checks if this type is of higher priority than the given type
         *
         * @param type
         *            the type to compare to
         * @return true if this type has higher priority than the given type,
         *         false otherwise
         */
        public boolean isHigherPriorityThan(Type type) {
            return priority > type.priority;
        }
    }

    @Override
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;

        connection.addHandler(ApplicationStoppedEvent.TYPE,
                new ApplicationStoppedHandler() {
                    @Override
                    public void onApplicationStopped(
                            ApplicationStoppedEvent event) {
                        if (isReconnecting()) {
                            giveUp();
                        }
                        if (scheduledReconnect != null
                                && scheduledReconnect.isRunning()) {
                            scheduledReconnect.cancel();
                        }
                    }

                });

        // Allow dialog to cache needed resources to make them available when we
        // are offline
        reconnectDialog.preload(connection);
    };

    /**
     * Checks if we are currently trying to reconnect
     *
     * @return true if we have noted a problem and are trying to re-establish
     *         server connection, false otherwise
     */
    private boolean isReconnecting() {
        return reconnectionCause != null;
    }

    private static Logger getLogger() {
        return Logger.getLogger(DefaultConnectionStateHandler.class.getName());
    }

    /**
     * Returns the connection this handler is connected to
     *
     * @return the connection for this handler
     */
    protected ApplicationConnection getConnection() {
        return connection;
    }

    @Override
    public void xhrException(XhrConnectionError xhrConnectionError) {
        debug("xhrException");
        handleRecoverableError(Type.XHR, xhrConnectionError.getPayload());
    }

    @Override
    public void heartbeatException(Request request, Throwable exception) {
        getLogger().severe("Heartbeat exception: " + exception.getMessage());
        handleRecoverableError(Type.HEARTBEAT, null);
    }

    @Override
    public void heartbeatInvalidStatusCode(Request request, Response response) {
        int statusCode = response.getStatusCode();
        getLogger().warning("Heartbeat request returned " + statusCode);

        if (response.getStatusCode() == Response.SC_GONE) {
            // Session expired
            getConnection().showSessionExpiredError(null);
            stopApplication();
        } else if (response.getStatusCode() == Response.SC_NOT_FOUND) {
            // UI closed, do nothing as the UI will react to this
            // Should not trigger reconnect dialog as this will prevent user
            // input
        } else {
            handleRecoverableError(Type.HEARTBEAT, null);
        }
    }

    @Override
    public void heartbeatOk() {
        debug("heartbeatOk");
        if (isReconnecting()) {
            resolveTemporaryError(Type.HEARTBEAT);
        }
    }

    private void debug(String msg) {
        if (false) {
            getLogger().warning(msg);
        }
    }

    /**
     * Called whenever an error occurs in communication which should be handled
     * by showing the reconnect dialog and retrying communication until
     * successful again
     *
     * @param type
     *            The type of failure detected
     * @param payload
     *            The message which did not reach the server, or null if no
     *            message was involved (heartbeat or push connection failed)
     */
    protected void handleRecoverableError(Type type, final JsonObject payload) {
        debug("handleTemporaryError(" + type + ")");
        if (!connection.isApplicationRunning()) {
            return;
        }

        if (!isReconnecting()) {
            // First problem encounter
            reconnectionCause = type;
            getLogger().warning("Reconnecting because of " + type + " failure");
            // Precaution only as there should never be a dialog at this point
            // and no timer running
            stopDialogTimer();
            if (isDialogVisible()) {
                hideDialog();
            }

            // Show dialog after grace period, still continue to try to
            // reconnect even before it is shown
            dialogShowTimer.schedule(getConfiguration().dialogGracePeriod);
        } else {
            // We are currently trying to reconnect
            // Priority is HEARTBEAT -> PUSH -> XHR
            // If a higher priority issues is resolved, we can assume the lower
            // one will be also
            if (type.isHigherPriorityThan(reconnectionCause)) {
                getLogger().warning(
                        "Now reconnecting because of " + type + " failure");
                reconnectionCause = type;
            }
        }

        if (reconnectionCause != type) {
            return;
        }

        reconnectAttempt++;
        getLogger()
                .info("Reconnect attempt " + reconnectAttempt + " for " + type);

        if (reconnectAttempt >= getConfiguration().reconnectAttempts) {
            // Max attempts reached, stop trying
            giveUp();
        } else {
            updateDialog();
            scheduleReconnect(payload);
        }
    }

    /**
     * Called after a problem occurred.
     *
     * This method is responsible for re-sending the payload to the server (if
     * not null) or re-send a heartbeat request at some point
     *
     * @param payload
     *            the payload that did not reach the server, null if the problem
     *            was detected by a heartbeat
     */
    protected void scheduleReconnect(final JsonObject payload) {
        // Here and not in timer to avoid TB for getting in between

        // The request is still open at this point to avoid interference, so we
        // do not need to start a new one
        if (reconnectAttempt == 1) {
            // Try once immediately
            doReconnect(payload);
        } else {
            scheduledReconnect = new Timer() {
                @Override
                public void run() {
                    scheduledReconnect = null;
                    doReconnect(payload);
                }
            };
            scheduledReconnect.schedule(getConfiguration().reconnectInterval);
        }
    }

    /**
     * Re-sends the payload to the server (if not null) or re-sends a heartbeat
     * request immediately
     *
     * @param payload
     *            the payload that did not reach the server, null if the problem
     *            was detected by a heartbeat
     */
    protected void doReconnect(JsonObject payload) {
        if (!connection.isApplicationRunning()) {
            // This should not happen as nobody should call this if the
            // application has been stopped
            getLogger().warning(
                    "Trying to reconnect after application has been stopped. Giving up");
            return;
        }
        if (payload != null) {
            getLogger().info("Re-sending last message to the server...");
            getConnection().getMessageSender().send(payload);
        } else {
            // Use heartbeat
            getLogger().info("Trying to re-establish server connection...");
            getConnection().getHeartbeat().send();
        }
    }

    /**
     * Called whenever a reconnect attempt fails to allow updating of dialog
     * contents
     */
    protected void updateDialog() {
        reconnectDialog.setText(getDialogText(reconnectAttempt));
    }

    /**
     * Called when we should give up trying to reconnect and let the user decide
     * how to continue
     *
     */
    protected void giveUp() {
        reconnectionCause = null;
        endRequest();

        stopDialogTimer();
        if (!isDialogVisible()) {
            // It SHOULD always be visible at this point, unless you have a
            // really strange configuration (grace time longer than total
            // reconnect time)
            showDialog();
        }
        reconnectDialog.setText(getDialogTextGaveUp(reconnectAttempt));
        reconnectDialog.setReconnecting(false);

        // Stopping the application stops heartbeats and push
        connection.setApplicationRunning(false);
    }

    /**
     * Ensures the reconnect dialog does not popup some time from now
     */
    private void stopDialogTimer() {
        if (dialogShowTimer.isRunning()) {
            dialogShowTimer.cancel();
        }
    }

    /**
     * Checks if the reconnect dialog is visible to the user
     *
     * @return true if the user can see the dialog, false otherwise
     */
    protected boolean isDialogVisible() {
        return reconnectDialog.isVisible();
    }

    /**
     * Called when the reconnect dialog should be shown. This is typically when
     * N seconds has passed since a problem with the connection has been
     * detected
     */
    protected void showDialog() {
        reconnectDialog.setReconnecting(true);
        reconnectDialog.show(connection);

        // We never want to show loading indicator and reconnect dialog at the
        // same time
        connection.getLoadingIndicator().hide();
    }

    /**
     * Called when the reconnect dialog should be hidden.
     */
    protected void hideDialog() {
        reconnectDialog.setReconnecting(false);
        reconnectDialog.hide();
    }

    /**
     * Gets the text to show in the reconnect dialog after giving up (reconnect
     * limit reached)
     *
     * @param reconnectAttempt
     *            The number of the current reconnection attempt
     * @return The text to show in the reconnect dialog after giving up
     */
    protected String getDialogTextGaveUp(int reconnectAttempt) {
        return getConfiguration().dialogTextGaveUp.replace("{0}",
                reconnectAttempt + "");
    }

    /**
     * Gets the text to show in the reconnect dialog
     *
     * @param reconnectAttempt
     *            The number of the current reconnection attempt
     * @return The text to show in the reconnect dialog
     */
    protected String getDialogText(int reconnectAttempt) {
        return getConfiguration().dialogText.replace("{0}",
                reconnectAttempt + "");
    }

    @Override
    public void configurationUpdated() {
        // All other properties are fetched directly from the state when needed
        reconnectDialog.setModal(getConfiguration().dialogModal);
    }

    private ReconnectDialogConfigurationState getConfiguration() {
        return connection.getUIConnector()
                .getState().reconnectDialogConfiguration;
    }

    @Override
    public void xhrInvalidContent(XhrConnectionError xhrConnectionError) {
        debug("xhrInvalidContent");
        endRequest();

        String responseText = xhrConnectionError.getResponse().getText();
        /*
         * A servlet filter or equivalent may have intercepted the request and
         * served non-UIDL content (for instance, a login page if the session
         * has expired.) If the response contains a magic substring, do a
         * synchronous refresh. See #8241.
         */
        MatchResult refreshToken = RegExp
                .compile(ApplicationConnection.UIDL_REFRESH_TOKEN
                        + "(:\\s*(.*?))?(\\s|$)")
                .exec(responseText);
        if (refreshToken != null) {
            WidgetUtil.redirect(getConnection()
                    .translateVaadinUri(refreshToken.getGroup(2)));
        } else {
            handleUnrecoverableCommunicationError(
                    "Invalid JSON response from server: " + responseText,
                    xhrConnectionError);
        }

    }

    @Override
    public void pushInvalidContent(PushConnection pushConnection,
            String message) {
        debug("pushInvalidContent");
        if (pushConnection.isBidirectional()) {
            // We can't be sure that what was pushed was actually a response but
            // at this point it should not really matter, as something is
            // seriously broken.
            endRequest();
        }

        // Do nothing special for now. Should likely do the same as
        // xhrInvalidContent
        handleUnrecoverableCommunicationError(
                "Invalid JSON from server: " + message, null);

    }

    @Override
    public void xhrInvalidStatusCode(XhrConnectionError xhrConnectionError) {
        debug("xhrInvalidStatusCode");

        Response response = xhrConnectionError.getResponse();
        int statusCode = response.getStatusCode();
        getLogger().warning("Server returned " + statusCode + " for xhr");

        if (statusCode == 401) {
            // Authentication/authorization failed, no need to re-try
            endRequest();
            handleUnauthorized(xhrConnectionError);
            return;
        } else {
            // 404, 408 and other 4xx codes CAN be temporary when you have a
            // proxy between the client and the server and e.g. restart the
            // server
            // 5xx codes may or may not be temporary
            handleRecoverableError(Type.XHR, xhrConnectionError.getPayload());
        }
    }

    private void endRequest() {
        getConnection().getMessageSender().endRequest();
    }

    protected void handleUnauthorized(XhrConnectionError xhrConnectionError) {
        /*
         * Authorization has failed (401). Could be that the session has timed
         * out.
         */
        connection.showAuthenticationError("");
        stopApplication();
    }

    private void stopApplication() {
        // Consider application not running any more and prevent all
        // future requests
        connection.setApplicationRunning(false);
    }

    private void handleUnrecoverableCommunicationError(String details,
            XhrConnectionError xhrConnectionError) {
        int statusCode = -1;
        if (xhrConnectionError != null) {
            Response response = xhrConnectionError.getResponse();
            if (response != null) {
                statusCode = response.getStatusCode();
            }
        }
        connection.handleCommunicationError(details, statusCode);

        stopApplication();

    }

    @Override
    public void xhrOk() {
        debug("xhrOk");
        if (isReconnecting()) {
            resolveTemporaryError(Type.XHR);
        }
    }

    private void resolveTemporaryError(Type type) {
        debug("resolveTemporaryError(" + type + ")");

        if (reconnectionCause != type) {
            // Waiting for some other problem to be resolved
            return;
        }

        reconnectionCause = null;
        reconnectAttempt = 0;
        // IF reconnect happens during grace period, make sure the dialog is not
        // shown and does not popup later
        stopDialogTimer();
        hideDialog();

        getLogger().info("Re-established connection to server");
    }

    @Override
    public void pushOk(PushConnection pushConnection) {
        debug("pushOk()");
        if (isReconnecting()) {
            resolveTemporaryError(Type.PUSH);
        }
    }

    @Override
    public void pushScriptLoadError(String resourceUrl) {
        connection.handleCommunicationError(
                resourceUrl + " could not be loaded. Push will not work.", 0);
    }

    @Override
    public void pushNotConnected(JsonObject payload) {
        debug("pushNotConnected()");
        handleRecoverableError(Type.PUSH, payload);
    }

    @Override
    public void pushReconnectPending(PushConnection pushConnection) {
        debug("pushReconnectPending(" + pushConnection.getTransportType()
                + ")");
        getLogger().info("Reopening push connection");
        if (pushConnection.isBidirectional()) {
            // Lost connection for a connection which will tell us when the
            // connection is available again
            handleRecoverableError(Type.PUSH, null);
        } else {
            // Lost connection for a connection we do not necessarily know when
            // it is available again (long polling behind proxy). Do nothing and
            // show reconnect dialog if the user does something and the XHR
            // fails
        }
    }

    @Override
    public void pushError(PushConnection pushConnection,
            JavaScriptObject response) {
        debug("pushError()");
        connection.handleCommunicationError("Push connection using "
                + ((AtmosphereResponse) response).getTransport() + " failed!",
                -1);
    }

    @Override
    public void pushClientTimeout(PushConnection pushConnection,
            JavaScriptObject response) {
        debug("pushClientTimeout()");
        // TODO Reconnect, allowing client timeout to be set
        // https://dev.vaadin.com/ticket/18429
        connection.handleCommunicationError(
                "Client unexpectedly disconnected. Ensure client timeout is disabled.",
                -1);
    }

    @Override
    public void pushClosed(PushConnection pushConnection,
            JavaScriptObject response) {
        debug("pushClosed()");
        getLogger().info("Push connection closed");
    }

}
