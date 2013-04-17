/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.VConsole;

/**
 * Represents the client-side endpoint of a bidirectional ("push") communication
 * channel. Can be used to send UIDL request messages to the server and to
 * receive UIDL messages from the server (either asynchronously or as a response
 * to a UIDL request.) Delegates the UIDL handling to the
 * {@link ApplicationConnection}.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class PushConnection {

    private ApplicationConnection connection;

    private JavaScriptObject socket;

    private ArrayList<String> messageQueue = new ArrayList<String>();

    private boolean connected = false;

    private AtmosphereConfiguration config;

    public PushConnection() {
    }

    /**
     * Two-phase construction to allow using GWT.create()
     * 
     * @param connection
     *            The ApplicationConnection
     */
    public void init(ApplicationConnection connection) {
        this.connection = connection;
    }

    public void connect(String uri) {
        VConsole.log("Establishing push connection");
        socket = doConnect(uri, getConfig());
    }

    public void push(String message) {
        if (!connected) {
            VConsole.log("Queuing push message: " + message);
            messageQueue.add(message);
        } else {
            VConsole.log("Sending push message: " + message);
            doPush(socket, message);
        }
    }

    protected AtmosphereConfiguration getConfig() {
        if (config == null) {
            config = createConfig();
        }
        return config;
    }

    protected void onOpen(AtmosphereResponse response) {
        VConsole.log("Push connection established using "
                + response.getTransport());
        connected = true;
        for (String message : messageQueue) {
            push(message);
        }
        messageQueue.clear();
    }

    protected void onMessage(AtmosphereResponse response) {
        String message = response.getResponseBody();
        if (message.startsWith("for(;;);")) {
            VConsole.log("Received push message: " + message);
            // "for(;;);[{json}]" -> "{json}"
            message = message.substring(9, message.length() - 1);
            connection.handlePushMessage(message);
        }
    }

    /**
     * Called if the transport mechanism cannot be used and the fallback will be
     * tried
     */
    protected void onTransportFailure() {
        VConsole.log("Push connection using primary method ("
                + getConfig().getTransport() + ") failed. Trying with "
                + getConfig().getFallbackTransport());
    }

    /**
     * Called if the push connection fails. Atmosphere will automatically retry
     * the connection until successful.
     * 
     */
    protected void onError() {
        VConsole.error("Push connection using "
                + getConfig().getTransport()
                + " failed!");
    }

    public static abstract class AbstractJSO extends JavaScriptObject {
        protected AbstractJSO() {

        }

        protected final native String getStringValue(String key)
        /*-{
           return this[key];
         }-*/;

        protected final native void setStringValue(String key, String value)
        /*-{
            this[key] = value;
        }-*/;

        protected final native int getIntValue(String key)
        /*-{
           return this[key];
         }-*/;

        protected final native void setIntValue(String key, int value)
        /*-{
            this[key] = value;
        }-*/;

    }

    public static class AtmosphereConfiguration extends AbstractJSO {

        protected AtmosphereConfiguration() {
            super();
        }

        public final String getTransport() {
            return getStringValue("transport");
        }

        public final String getFallbackTransport() {
            return getStringValue("fallbackTransport");
        }

        public final void setTransport(String transport) {
            setStringValue("transport", transport);
        }

        public final void setFallbackTransport(String fallbackTransport) {
            setStringValue("fallbackTransport", fallbackTransport);
        }
    }

    public static class AtmosphereResponse extends AbstractJSO {

        protected AtmosphereResponse() {

        }

        public final String getResponseBody() {
            return getStringValue("responseBody");
        }

        public final String getState() {
            return getStringValue("state");
        }

        public final String getError() {
            return getStringValue("error");
        }

        public final String getTransport() {
            return getStringValue("transport");
        }

    }

    protected native AtmosphereConfiguration createConfig()
    /*-{
        return {
            transport: 'websocket',
            fallbackTransport: 'streaming',
            contentType: 'application/json; charset=UTF-8',
            reconnectInterval: '5000',
            trackMessageLength: true 
        };
    }-*/;

    private native JavaScriptObject doConnect(String uri,
            JavaScriptObject config)
    /*-{
        var self = this;

        config.url = uri;
        config.onOpen = $entry(function(response) {
            self.@com.vaadin.client.communication.PushConnection::onOpen(*)(response);
        });
        config.onMessage = $entry(function(response) {
            self.@com.vaadin.client.communication.PushConnection::onMessage(*)(response);
        });
        config.onError = $entry(function(response) {
            self.@com.vaadin.client.communication.PushConnection::onError()(response);
        });
        config.onTransportFailure = $entry(function(reason,request) {
            self.@com.vaadin.client.communication.PushConnection::onTransportFailure(*)(reason);
        });
        return $wnd.atmosphere.subscribe(config);
    }-*/;

    private native void doPush(JavaScriptObject socket, String message)
    /*-{
       socket.push(message);
    }-*/;
}
