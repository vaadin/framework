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

import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.ui.ui.UIState.ReconnectDialogConfigurationState;

import elemental.json.JsonObject;

/**
 * Default implementation of the communication problem handler.
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
public class ReconnectingCommunicationProblemHandler implements
        CommunicationProblemHandler {

    private static final String STYLE_RECONNECTING = "active";
    private ApplicationConnection connection;
    private ReconnectDialog reconnectDialog = GWT.create(ReconnectDialog.class);
    private int reconnectAttempt = 0;
    private Type reconnectionCause = null;

    private enum Type {
        HEARTBEAT, MESSAGE
    }

    @Override
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    };

    private static Logger getLogger() {
        return Logger.getLogger(ReconnectingCommunicationProblemHandler.class
                .getName());
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
    public void xhrException(CommunicationProblemEvent event) {
        getLogger().warning("xhrException");
        handleRecoverableError(Type.MESSAGE, event.getPayload());
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
        } else {
            handleRecoverableError(Type.HEARTBEAT, null);
        }
    }

    @Override
    public void heartbeatOk() {
        getLogger().warning("heartbeatOk");
        resolveTemporaryError(Type.HEARTBEAT, true);
    }

    protected void handleRecoverableError(Type type, final JsonObject payload) {
        getLogger().warning("handleTemporaryError(" + type + ")");

        reconnectAttempt++;
        reconnectionCause = type;
        if (!reconnectDialog.isAttached()) {
            reconnectDialog.setStyleName(STYLE_RECONNECTING, true);
            reconnectDialog.setOwner(getConnection().getUIConnector()
                    .getWidget());
            reconnectDialog.show();
        }
        if (payload != null) {
            getConnection().getServerCommunicationHandler().endRequest();
        }

        if (reconnectAttempt >= getConfiguration().reconnectAttempts) {
            // Max attempts reached
            reconnectDialog.setText(getDialogTextGaveUp(reconnectAttempt));
            reconnectDialog.setStyleName(STYLE_RECONNECTING, false);

            getConnection().setApplicationRunning(false);

        } else {
            reconnectDialog.setText(getDialogText(reconnectAttempt));

            // Here and not in timer to avoid TB for getting in between
            if (payload != null) {
                getConnection().getServerCommunicationHandler().startRequest();
            }

            // Reconnect
            new Timer() {
                @Override
                public void run() {
                    if (payload != null) {
                        getLogger().info(
                                "Re-sending last message to the server...");
                        getConnection().getServerCommunicationHandler().send(
                                payload);
                    } else {
                        // Use heartbeat
                        getLogger().info(
                                "Trying to re-establish server connection...");
                        getConnection().getHeartbeat().send();
                    }
                }
            }.schedule(getConfiguration().reconnectInterval);
        }
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
        return getConfiguration().dialogText.replace("{0}", reconnectAttempt
                + "");
    }

    private ReconnectDialogConfigurationState getConfiguration() {
        return connection.getUIConnector().getState().reconnectDialog;
    }

    @Override
    public void xhrInvalidContent(CommunicationProblemEvent event) {
        getLogger().warning("xhrInvalidContent");
        String responseText = event.getResponse().getText();
        /*
         * A servlet filter or equivalent may have intercepted the request and
         * served non-UIDL content (for instance, a login page if the session
         * has expired.) If the response contains a magic substring, do a
         * synchronous refresh. See #8241.
         */
        MatchResult refreshToken = RegExp.compile(
                ApplicationConnection.UIDL_REFRESH_TOKEN
                        + "(:\\s*(.*?))?(\\s|$)").exec(responseText);
        if (refreshToken != null) {
            WidgetUtil.redirect(refreshToken.getGroup(2));
        } else {
            handleUnrecoverableCommunicationError(
                    "Invalid JSON response from server: " + responseText, event);
        }

    }

    @Override
    public void pushInvalidContent(PushConnection pushConnection, String message) {
        // Do nothing for now. Should likely do the same as xhrInvalidContent
    }

    @Override
    public void xhrInvalidStatusCode(CommunicationProblemEvent event) {
        getLogger().warning("xhrInvalidStatusCode");
        Response response = event.getResponse();
        int statusCode = response.getStatusCode();
        getLogger().warning("Server returned " + statusCode + " for xhr");

        if (statusCode == 401) {
            // Authentication/authorization failed, no need to re-try
            handleUnauthorized(event);
            return;
        } else {
            // 404, 408 and other 4xx codes CAN be temporary when you have a
            // proxy between the client and the server and e.g. restart the
            // server
            // 5xx codes may or may not be temporary
            handleRecoverableError(Type.MESSAGE, event.getPayload());
        }
    }

    protected void handleUnauthorized(CommunicationProblemEvent event) {
        /*
         * Authorization has failed (401). Could be that the session has timed
         * out.
         */
        connection.showAuthenticationError("");
        endRequestAndStopApplication();
    }

    private void endRequestAndStopApplication() {
        connection.getServerCommunicationHandler().endRequest();

        stopApplication();
    }

    private void stopApplication() {
        // Consider application not running any more and prevent all
        // future requests
        connection.setApplicationRunning(false);
    }

    /**
     * @since
     * @param event
     */
    private void handleUnrecoverableCommunicationError(String details,
            CommunicationProblemEvent event) {
        Response response = event.getResponse();
        int statusCode = -1;
        if (response != null) {
            statusCode = response.getStatusCode();
        }
        connection.handleCommunicationError(details, statusCode);

        endRequestAndStopApplication();

    }

    @Override
    public void xhrOk() {
        getLogger().warning("xhrOk");
        resolveTemporaryError(Type.MESSAGE, true);
    }

    private void resolveTemporaryError(Type type, boolean success) {
        getLogger().warning("resolveTemporaryError(" + type + ")");

        if (reconnectionCause == null) {
            // Not trying to reconnect
            return;
        }
        if (reconnectionCause == Type.MESSAGE && type == Type.HEARTBEAT) {
            // If a heartbeat goes through while we are trying to re-send an
            // XHR, we wait for the XHR to go through to avoid removing the
            // reconnect dialog and then possible showing it again
            return;
        }

        reconnectionCause = null;
        if (reconnectDialog.isAttached()) {
            reconnectDialog.hide();
        }

        if (success && reconnectAttempt != 0) {
            getLogger().info("Re-established connection to server");
            reconnectAttempt = 0;
        }

    }

    @Override
    public void pushOk(PushConnection pushConnection) {
        getLogger().warning("pushOk()");
        resolveTemporaryError(Type.MESSAGE, true);
    }

    @Override
    public void pushScriptLoadError(String resourceUrl) {
        connection.handleCommunicationError(resourceUrl
                + " could not be loaded. Push will not work.", 0);
    }

    @Override
    public void pushNotConnected(JsonObject payload) {
        getLogger().warning("pushNotConnected()");
        handleRecoverableError(Type.MESSAGE, payload);
    }

    @Override
    public void pushReconnectPending(PushConnection pushConnection) {
        getLogger().warning(
                "pushReconnectPending(" + pushConnection.getTransportType()
                        + ")");
        getLogger().info("Reopening push connection");
    }

    @Override
    public void pushError(PushConnection pushConnection) {
        getLogger().warning("pushError()");
        connection.handleCommunicationError("Push connection using "
                + pushConnection.getTransportType() + " failed!", -1);
    }

    @Override
    public void pushClientTimeout(PushConnection pushConnection) {
        getLogger().warning("pushClientTimeout()");
        // TODO Reconnect, allowing client timeout to be set
        // https://dev.vaadin.com/ticket/18429
        connection
                .handleCommunicationError(
                        "Client unexpectedly disconnected. Ensure client timeout is disabled.",
                        -1);
    }

    @Override
    public void pushClosed(PushConnection pushConnection) {
        getLogger().warning("pushClosed()");
        getLogger().info("Push connection closed");
    }

}
