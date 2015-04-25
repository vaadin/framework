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

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import elemental.json.JsonObject;

//FIXME This is just a test and should be merged with DCPH
public class ReconnectingCommunicationProblemHandler extends
        DefaultCommunicationProblemHandler {

    private enum Type {
        HEARTBEAT, MESSAGE
    }

    ReconnectDialog reconnectDialog = GWT.create(ReconnectDialog.class);
    int reconnectAttempt = 0;
    private Type reconnectionCause = null;

    @Override
    public void xhrException(CommunicationProblemEvent event) {
        getLogger().warning("xhrException");
        handleTemporaryError(Type.MESSAGE, event.getPayload());
    }

    @Override
    public boolean heartbeatException(Request request, Throwable exception) {
        handleTemporaryError(Type.HEARTBEAT, null);
        return true;
    }

    @Override
    public boolean heartbeatInvalidStatusCode(Request request, Response response) {
        if (response.getStatusCode() == Response.SC_GONE) {
            // Session expired
            resolveTemporaryError(Type.HEARTBEAT, false);
            return super.heartbeatInvalidStatusCode(request, response);
        }

        handleTemporaryError(Type.HEARTBEAT, null);
        return true;
    }

    @Override
    public void heartbeatOk() {
        resolveTemporaryError(Type.HEARTBEAT, true);
    }

    private void handleTemporaryError(Type type, final JsonObject payload) {
        getLogger().warning("handleTemporaryError(" + type + ")");

        reconnectAttempt++;
        reconnectionCause = type;
        if (!reconnectDialog.isAttached()) {
            // FIXME
            reconnectDialog.setStyleName("active", true);
            reconnectDialog.setOwner(getConnection().getUIConnector()
                    .getWidget());
            reconnectDialog.setPopupPositionAndShow(new PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    // FIXME
                    reconnectDialog.setPopupPosition(0, 0);
                }
            });
        }
        if (payload != null) {
            getConnection().getServerCommunicationHandler().endRequest();
        }

        if (reconnectAttempt >= getMaxReconnectAttempts()) {
            // FIXME Remove
            reconnectDialog.setText("Server connection lost. Gave up after "
                    + reconnectAttempt + " attempts.");
            // FIXME
            reconnectDialog.setStyleName("active", false);

            getConnection().setApplicationRunning(false);

        } else {
            reconnectDialog
                    .setText("Server connection lost, trying to reconnect... Attempt "
                            + reconnectAttempt);

            // Here and not in timer to avoid TB for getting in between
            if (payload != null) {
                // FIXME: Not like this
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
            }.schedule(getReconnectInterval());
        }
    }

    /**
     * @since
     * @return
     */
    private int getMaxReconnectAttempts() {
        // FIXME Parameter
        return 15;
    }

    /**
     * @since
     * @return
     */
    private int getReconnectInterval() {
        // FIXME Parameter
        return 5000;
    }

    @Override
    public void xhrInvalidContent(CommunicationProblemEvent event) {
        getLogger().warning("xhrInvalidContent");
        super.xhrInvalidContent(event);
    };

    @Override
    public void xhrInvalidStatusCode(CommunicationProblemEvent event) {
        getLogger().info(
                "Server returned " + event.getResponse().getStatusCode()
                        + " for xhr request");
        getLogger().warning("xhrInvalidStatusCode");
        handleTemporaryError(Type.MESSAGE, event.getPayload());
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
        super.pushOk(pushConnection);
        resolveTemporaryError(Type.MESSAGE, true);
    }

    @Override
    public void pushNotConnected(JsonObject payload) {
        super.pushNotConnected(payload);
        handleTemporaryError(Type.MESSAGE, payload);
    }
}
