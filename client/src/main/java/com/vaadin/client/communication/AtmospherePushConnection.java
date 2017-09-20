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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window.Location;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.ApplicationStoppedEvent;
import com.vaadin.client.ApplicationConnection.ApplicationStoppedHandler;
import com.vaadin.client.ResourceLoader;
import com.vaadin.client.ResourceLoader.ResourceLoadEvent;
import com.vaadin.client.ResourceLoader.ResourceLoadListener;
import com.vaadin.client.ValueMap;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.Version;
import com.vaadin.shared.communication.PushConstants;
import com.vaadin.shared.ui.ui.UIConstants;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;
import com.vaadin.shared.util.SharedUtil;

import elemental.json.JsonObject;

/**
 * The default {@link PushConnection} implementation that uses Atmosphere for
 * handling the communication channel.
 *
 * @author Vaadin Ltd
 * @since 7.1
 */
public class AtmospherePushConnection implements PushConnection {

    protected enum State {
        /**
         * Opening request has been sent, but still waiting for confirmation
         */
        CONNECT_PENDING,

        /**
         * Connection is open and ready to use.
         */
        CONNECTED,

        /**
         * Connection was disconnected while the connection was pending. Wait
         * for the connection to get established before closing it. No new
         * messages are accepted, but pending messages will still be delivered.
         */
        DISCONNECT_PENDING,

        /**
         * Connection has been disconnected and should not be used any more.
         */
        DISCONNECTED;
    }

    /**
     * Represents a message that should be sent as multiple fragments.
     */
    protected static class FragmentedMessage {

        private static final int FRAGMENT_LENGTH = PushConstants.WEBSOCKET_FRAGMENT_SIZE;

        private String message;
        private int index = 0;

        public FragmentedMessage(String message) {
            this.message = message;
        }

        public boolean hasNextFragment() {
            return index < message.length();
        }

        public String getNextFragment() {
            assert hasNextFragment();

            String result;
            if (index == 0) {
                String header = "" + message.length()
                        + PushConstants.MESSAGE_DELIMITER;
                int fragmentLen = FRAGMENT_LENGTH - header.length();
                result = header + getFragment(0, fragmentLen);
                index += fragmentLen;
            } else {
                result = getFragment(index, index + FRAGMENT_LENGTH);
                index += FRAGMENT_LENGTH;
            }
            return result;
        }

        private String getFragment(int begin, int end) {
            return message.substring(begin, Math.min(message.length(), end));
        }
    }

    private ApplicationConnection connection;

    private JavaScriptObject socket;

    private State state = State.CONNECT_PENDING;

    private AtmosphereConfiguration config;

    private String uri;

    private String transport;

    /**
     * Keeps track of the disconnect confirmation command for cases where
     * pending messages should be pushed before actually disconnecting.
     */
    private Command pendingDisconnectCommand;

    /**
     * The url to use for push requests
     */
    private String url;

    public AtmospherePushConnection() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.client.communication.PushConnection#init(ApplicationConnection
     * , Map<String, String>, CommunicationErrorHandler)
     */
    @Override
    public void init(final ApplicationConnection connection,
            final PushConfigurationState pushConfiguration) {
        this.connection = connection;

        connection.addHandler(ApplicationStoppedEvent.TYPE,
                new ApplicationStoppedHandler() {

                    @Override
                    public void onApplicationStopped(
                            ApplicationStoppedEvent event) {
                        if (state == State.DISCONNECT_PENDING
                                || state == State.DISCONNECTED) {
                            return;
                        }

                        disconnect(new Command() {
                            @Override
                            public void execute() {
                            }
                        });

                    }
                });
        config = createConfig();
        String debugParameter = Location.getParameter("debug");
        if ("push".equals(debugParameter)) {
            config.setStringValue("logLevel", "debug");
        }
        for (String param : pushConfiguration.parameters.keySet()) {
            String value = pushConfiguration.parameters.get(param);
            if (value.equalsIgnoreCase("true")
                    || value.equalsIgnoreCase("false")) {
                config.setBooleanValue(param, value.equalsIgnoreCase("true"));
            } else {
                config.setStringValue(param, value);
            }
        }
        if (pushConfiguration.pushUrl != null) {
            url = pushConfiguration.pushUrl;
        } else {
            url = ApplicationConstants.APP_PROTOCOL_PREFIX
                    + ApplicationConstants.PUSH_PATH;
        }
        runWhenAtmosphereLoaded(new Command() {
            @Override
            public void execute() {
                Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                        connect();
                    }
                });
            }
        });
    }

    private void connect() {
        String baseUrl = connection.translateVaadinUri(url);
        String extraParams = UIConstants.UI_ID_PARAMETER + "="
                + connection.getConfiguration().getUIId();

        String pushId = connection.getMessageHandler().getPushId();
        if (pushId != null) {
            extraParams += "&" + ApplicationConstants.PUSH_ID_PARAMETER + "="
                    + pushId;
        }

        // uri is needed to identify the right connection when closing
        uri = SharedUtil.addGetParameters(baseUrl, extraParams);

        getLogger().info("Establishing push connection");
        socket = doConnect(uri, getConfig());
    }

    @Override
    public boolean isActive() {
        switch (state) {
        case CONNECT_PENDING:
        case CONNECTED:
            return true;
        default:
            return false;
        }
    }

    @Override
    public boolean isBidirectional() {
        if (transport == null) {
            return false;
        }

        if (!transport.equals("websocket")) {
            // If we are not using websockets, we want to send XHRs
            return false;
        }
        if (getPushConfigurationState().alwaysUseXhrForServerRequests) {
            // If user has forced us to use XHR, let's abide
            return false;
        }
        if (state == State.CONNECT_PENDING) {
            // Not sure yet, let's go for using websockets still as still will
            // delay the message until a connection is established. When the
            // connection is established, bi-directionality will be checked
            // again to be sure
        }
        return true;

    };

    private PushConfigurationState getPushConfigurationState() {
        return connection.getUIConnector().getState().pushConfiguration;
    }

    @Override
    public void push(JsonObject message) {
        if (!isBidirectional()) {
            throw new IllegalStateException(
                    "This server to client push connection should not be used to send client to server messages");
        }
        if (state == State.CONNECTED) {
            getLogger().info("Sending push (" + transport
                    + ") message to server: " + message.toJson());

            if (transport.equals("websocket")) {
                FragmentedMessage fragmented = new FragmentedMessage(
                        message.toJson());
                while (fragmented.hasNextFragment()) {
                    doPush(socket, fragmented.getNextFragment());
                }
            } else {
                doPush(socket, message.toJson());
            }
            return;
        }

        if (state == State.CONNECT_PENDING) {
            getConnectionStateHandler().pushNotConnected(message);
            return;
        }

        throw new IllegalStateException("Can not push after disconnecting");
    }

    protected AtmosphereConfiguration getConfig() {
        return config;
    }

    protected void onReopen(AtmosphereResponse response) {
        getLogger().info("Push connection re-established using "
                + response.getTransport());
        onConnect(response);
    }

    protected void onOpen(AtmosphereResponse response) {
        getLogger().info(
                "Push connection established using " + response.getTransport());
        onConnect(response);
    }

    /**
     * Called whenever a server push connection is established (or
     * re-established).
     *
     * @param response
     *
     * @since 7.2
     */
    protected void onConnect(AtmosphereResponse response) {
        transport = response.getTransport();
        switch (state) {
        case CONNECT_PENDING:
            state = State.CONNECTED;
            getConnectionStateHandler().pushOk(this);
            break;
        case DISCONNECT_PENDING:
            // Set state to connected to make disconnect close the connection
            state = State.CONNECTED;
            assert pendingDisconnectCommand != null;
            disconnect(pendingDisconnectCommand);
            break;
        case CONNECTED:
            // IE likes to open the same connection multiple times, just ignore
            break;
        default:
            throw new IllegalStateException(
                    "Got onOpen event when conncetion state is " + state
                            + ". This should never happen.");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.communication.PushConenction#disconnect()
     */
    @Override
    public void disconnect(Command command) {
        assert command != null;

        switch (state) {
        case CONNECT_PENDING:
            // Make the connection callback initiate the disconnection again
            state = State.DISCONNECT_PENDING;
            pendingDisconnectCommand = command;
            break;
        case CONNECTED:
            // Normal disconnect
            getLogger().info("Closing push connection");
            doDisconnect(uri);
            state = State.DISCONNECTED;
            command.execute();
            break;
        case DISCONNECT_PENDING:
        case DISCONNECTED:
            throw new IllegalStateException(
                    "Can not disconnect more than once");
        }
    }

    protected void onMessage(AtmosphereResponse response) {
        String message = response.getResponseBody();
        ValueMap json = MessageHandler.parseWrappedJson(message);
        if (json == null) {
            // Invalid string (not wrapped as expected)
            getConnectionStateHandler().pushInvalidContent(this, message);
            return;
        } else {
            getLogger().info("Received push (" + getTransportType()
                    + ") message: " + message);
            connection.getMessageHandler().handleMessage(json);
        }
    }

    /**
     * Called if the transport mechanism cannot be used and the fallback will be
     * tried
     */
    protected void onTransportFailure() {
        getLogger().warning("Push connection using primary method ("
                + getConfig().getTransport() + ") failed. Trying with "
                + getConfig().getFallbackTransport());
    }

    /**
     * Called if the push connection fails. Atmosphere will automatically retry
     * the connection until successful.
     *
     */
    protected void onError(AtmosphereResponse response) {
        state = State.DISCONNECTED;
        getConnectionStateHandler().pushError(this, response);
    }

    protected void onClose(AtmosphereResponse response) {
        state = State.CONNECT_PENDING;
        getConnectionStateHandler().pushClosed(this, response);
    }

    protected void onClientTimeout(AtmosphereResponse response) {
        state = State.DISCONNECTED;
        getConnectionStateHandler().pushClientTimeout(this, response);
    }

    protected void onReconnect(JavaScriptObject request,
            final AtmosphereResponse response) {
        if (state == State.CONNECTED) {
            state = State.CONNECT_PENDING;
        }
        getConnectionStateHandler().pushReconnectPending(this);
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

        protected final native boolean getBooleanValue(String key)
        /*-{
           return this[key];
         }-*/;

        protected final native void setBooleanValue(String key, boolean value)
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

        public final int getStatusCode() {
            return getIntValue("status");
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
            maxStreamingLength: 1000000,
            fallbackTransport: 'long-polling',
            contentType: 'application/json; charset=UTF-8',
            reconnectInterval: 5000,
            timeout: -1,
            maxReconnectOnClose: 10000000,
            trackMessageLength: true,
            enableProtocol: true,
            handleOnlineOffline: false,
            messageDelimiter: String.fromCharCode(@com.vaadin.shared.communication.PushConstants::MESSAGE_DELIMITER)
        };
    }-*/;

    private native JavaScriptObject doConnect(String uri,
            JavaScriptObject config)
    /*-{
        var self = this;

        config.url = uri;
        config.onOpen = $entry(function(response) {
            self.@com.vaadin.client.communication.AtmospherePushConnection::onOpen(*)(response);
        });
        config.onReopen = $entry(function(response) {
            self.@com.vaadin.client.communication.AtmospherePushConnection::onReopen(*)(response);
        });
        config.onMessage = $entry(function(response) {
            self.@com.vaadin.client.communication.AtmospherePushConnection::onMessage(*)(response);
        });
        config.onError = $entry(function(response) {
            self.@com.vaadin.client.communication.AtmospherePushConnection::onError(*)(response);
        });
        config.onTransportFailure = $entry(function(reason,request) {
            self.@com.vaadin.client.communication.AtmospherePushConnection::onTransportFailure(*)(reason);
        });
        config.onClose = $entry(function(response) {
            self.@com.vaadin.client.communication.AtmospherePushConnection::onClose(*)(response);
        });
        config.onReconnect = $entry(function(request, response) {
            self.@com.vaadin.client.communication.AtmospherePushConnection::onReconnect(*)(request, response);
        });
        config.onClientTimeout = $entry(function(request) {
            self.@com.vaadin.client.communication.AtmospherePushConnection::onClientTimeout(*)(request);
        });

        return $wnd.vaadinPush.atmosphere.subscribe(config);
    }-*/;

    private native void doPush(JavaScriptObject socket, String message)
    /*-{
       socket.push(message);
    }-*/;

    private static native void doDisconnect(String url)
    /*-{
       $wnd.vaadinPush.atmosphere.unsubscribeUrl(url);
    }-*/;

    private static native boolean isAtmosphereLoaded()
    /*-{
        return $wnd.vaadinPush && $wnd.vaadinPush.atmosphere;
    }-*/;

    private void runWhenAtmosphereLoaded(final Command command) {
        if (isAtmosphereLoaded()) {
            command.execute();
        } else {
            final String pushJs = getVersionedPushJs();

            getLogger().info("Loading " + pushJs);
            ResourceLoader.get().loadScript(
                    connection.getConfiguration().getVaadinDirUrl() + pushJs,
                    new ResourceLoadListener() {
                        @Override
                        public void onLoad(ResourceLoadEvent event) {
                            if (isAtmosphereLoaded()) {
                                getLogger().info(pushJs + " loaded");
                                command.execute();
                            } else {
                                // If bootstrap tried to load vaadinPush.js,
                                // ResourceLoader assumes it succeeded even if
                                // it failed (#11673)
                                onError(event);
                            }
                        }

                        @Override
                        public void onError(ResourceLoadEvent event) {
                            getConnectionStateHandler().pushScriptLoadError(
                                    event.getResourceUrl());
                        }
                    });
        }
    }

    private String getVersionedPushJs() {
        String pushJs;
        if (ApplicationConfiguration.isProductionMode()) {
            pushJs = ApplicationConstants.VAADIN_PUSH_JS;
        } else {
            pushJs = ApplicationConstants.VAADIN_PUSH_DEBUG_JS;
        }
        // Parameter appended to bypass caches after version upgrade.
        pushJs += "?v=" + Version.getFullVersion();
        return pushJs;
    }

    @Override
    public String getTransportType() {
        return transport;
    }

    private static Logger getLogger() {
        return Logger.getLogger(AtmospherePushConnection.class.getName());
    }

    private ConnectionStateHandler getConnectionStateHandler() {
        return connection.getConnectionStateHandler();
    }

}
