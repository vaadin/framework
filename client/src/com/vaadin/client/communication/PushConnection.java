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

    private JavaScriptObject config = createConfig();

    public PushConnection() {
    }

    /**
     * Two-phase construction to allow using GWT.create()
     * 
     * @param ac
     *            The ApplicationConnection
     */
    public void init(ApplicationConnection ac) {
        this.connection = ac;
    }

    public void connect(String uri) {
        VConsole.log("Establishing Atmosphere connection");
        socket = doConnect(uri, getConfig());
    }

    public void push(String message) {
        if (!connected) {
            VConsole.log("Queuing Atmosphere message: " + message);
            messageQueue.add(message);
        } else {
            VConsole.log("Pushing Atmosphere message: " + message);
            doPush(socket, message);
        }
    }

    protected JavaScriptObject getConfig() {
        return config;
    }

    protected void onOpen() {
        VConsole.log("Atmosphere connection established");
        connected = true;
        for (String message : messageQueue) {
            push(message);
        }
        messageQueue.clear();
    }

    protected void onMessage(String message) {
        if (message.startsWith("for(;;);")) {
            VConsole.log("Received Atmosphere message: " + message);
            // "for(;;);[{json}]" -> "{json}"
            message = message.substring(9, message.length() - 1);
            connection.handlePushMessage(message);
        }
    }

    protected void onError() {
        VConsole.error("Atmosphere connection failed!");
    }

    private static native JavaScriptObject createConfig()
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
            self.@com.vaadin.client.communication.PushConnection::onOpen()();
        });
        config.onMessage = $entry(function(response) {
            self.@com.vaadin.client.communication.PushConnection::onMessage(*)(response.responseBody);
        });
        config.onError = $entry(function(response) {
            self.@com.vaadin.client.communication.PushConnection::onError()();
        });
                                 
        return $wnd.atmosphere.subscribe(config);
    }-*/;

    private native void doPush(JavaScriptObject socket, String message)
    /*-{
       socket.push(message);
    }-*/;
}
