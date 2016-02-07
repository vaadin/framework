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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.CommunicationHandler;
import com.vaadin.client.ApplicationConnection.RequestStartingEvent;
import com.vaadin.client.ApplicationConnection.ResponseHandlingEndedEvent;
import com.vaadin.client.ApplicationConnection.ResponseHandlingStartedEvent;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Profiler;
import com.vaadin.client.Util;
import com.vaadin.client.ValueMap;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.util.SharedUtil;

import elemental.json.JsonObject;

/**
 * Provides a connection to the /UIDL url on the server and knows how to send
 * messages to that end point
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
public class XhrConnection {

    private ApplicationConnection connection;

    /**
     * Webkit will ignore outgoing requests while waiting for a response to a
     * navigation event (indicated by a beforeunload event). When this happens,
     * we should keep trying to send the request every now and then until there
     * is a response or until it throws an exception saying that it is already
     * being sent.
     */
    private boolean webkitMaybeIgnoringRequests = false;

    public XhrConnection() {
        Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
                webkitMaybeIgnoringRequests = true;
            }
        });
    }

    /**
     * Sets the application connection this instance is connected to. Called
     * internally by the framework.
     *
     * @param connection
     *            the application connection this instance is connected to
     */
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;

        connection.addHandler(ResponseHandlingEndedEvent.TYPE,
                new CommunicationHandler() {
                    @Override
                    public void onRequestStarting(RequestStartingEvent e) {
                    }

                    @Override
                    public void onResponseHandlingStarted(
                            ResponseHandlingStartedEvent e) {
                    }

                    @Override
                    public void onResponseHandlingEnded(
                            ResponseHandlingEndedEvent e) {
                        webkitMaybeIgnoringRequests = false;
                    }
                });

    }

    private static Logger getLogger() {
        return Logger.getLogger(XhrConnection.class.getName());
    }

    protected XhrResponseHandler createResponseHandler() {
        return new XhrResponseHandler();
    }

    public class XhrResponseHandler implements RequestCallback {

        private JsonObject payload;
        private double requestStartTime;

        public XhrResponseHandler() {
        }

        /**
         * Sets the payload which was sent to the server
         * 
         * @param payload
         *            the payload which was sent to the server
         */
        public void setPayload(JsonObject payload) {
            this.payload = payload;
        }

        @Override
        public void onError(Request request, Throwable exception) {
            getConnectionStateHandler().xhrException(
                    new XhrConnectionError(request, payload, exception));
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            int statusCode = response.getStatusCode();

            if (statusCode != 200) {
                // There was a problem
                XhrConnectionError problemEvent = new XhrConnectionError(
                        request, payload, response);

                getConnectionStateHandler().xhrInvalidStatusCode(problemEvent);
                return;
            }

            getLogger().info(
                    "Server visit took "
                            + Util.round(Profiler.getRelativeTimeMillis()
                                    - requestStartTime, 3) + "ms");

            // for(;;);["+ realJson +"]"
            String responseText = response.getText();

            ValueMap json = MessageHandler.parseWrappedJson(responseText);
            if (json == null) {
                // Invalid string (not wrapped as expected or can't parse)
                getConnectionStateHandler().xhrInvalidContent(
                        new XhrConnectionError(request, payload, response));
                return;
            }

            getConnectionStateHandler().xhrOk();
            getLogger().info("Received xhr message: " + responseText);
            getMessageHandler().handleMessage(json);
        }

        /**
         * Sets the relative time (see {@link Profiler#getRelativeTimeMillis()})
         * when the request was sent.
         * 
         * @param requestStartTime
         *            the relative time when the request was sent
         */
        private void setRequestStartTime(double requestStartTime) {
            this.requestStartTime = requestStartTime;

        }
    };

    /**
     * Sends an asynchronous UIDL request to the server using the given URI.
     * 
     * @param payload
     *            The URI to use for the request. May includes GET parameters
     * @throws RequestException
     *             if the request could not be sent
     */
    public void send(JsonObject payload) {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, getUri());
        // TODO enable timeout
        // rb.setTimeoutMillis(timeoutMillis);
        // TODO this should be configurable
        rb.setHeader("Content-Type", JsonConstants.JSON_CONTENT_TYPE);
        rb.setRequestData(payload.toJson());

        XhrResponseHandler responseHandler = createResponseHandler();
        responseHandler.setPayload(payload);
        responseHandler.setRequestStartTime(Profiler.getRelativeTimeMillis());

        rb.setCallback(responseHandler);

        getLogger().info("Sending xhr message to server: " + payload.toJson());
        try {
            final Request request = rb.send();

            if (webkitMaybeIgnoringRequests && BrowserInfo.get().isWebkit()) {
                final int retryTimeout = 250;
                new Timer() {
                    @Override
                    public void run() {
                        // Use native js to access private field in Request
                        if (resendRequest(request)
                                && webkitMaybeIgnoringRequests) {
                            // Schedule retry if still needed
                            schedule(retryTimeout);
                        }
                    }
                }.schedule(retryTimeout);
            }
        } catch (RequestException e) {
            getConnectionStateHandler().xhrException(
                    new XhrConnectionError(null, payload, e));
        }
    }

    /**
     * Retrieves the URI to use when sending RPCs to the server
     * 
     * @return The URI to use for server messages.
     */
    protected String getUri() {
        String uri = connection
                .translateVaadinUri(ApplicationConstants.APP_PROTOCOL_PREFIX
                        + ApplicationConstants.UIDL_PATH + '/');

        uri = SharedUtil.addGetParameters(uri, UIConstants.UI_ID_PARAMETER
                + "=" + connection.getConfiguration().getUIId());

        return uri;

    }

    private ConnectionStateHandler getConnectionStateHandler() {
        return connection.getConnectionStateHandler();
    }

    private MessageHandler getMessageHandler() {
        return connection.getMessageHandler();
    }

    private static native boolean resendRequest(Request request)
    /*-{
        var xhr = request.@com.google.gwt.http.client.Request::xmlHttpRequest
        if (xhr == null) {
            // This might be called even though the request has completed,
            // if the webkitMaybeIgnoringRequests has been set to true on beforeunload
            // but unload was cancelled after that. It will then stay on until the following
            // request and if that request completes before we get here (<250mS), we will
            // hit this case.
            return false;
        }
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

}
