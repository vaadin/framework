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

import com.google.gwt.user.client.Command;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.CommunicationErrorHandler;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;
import elemental.json.JsonObject;

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
public interface PushConnection {

    /**
     * Two-phase construction to allow using GWT.create().
     * 
     * @param connection
     *            The ApplicationConnection
     */
    public void init(ApplicationConnection connection,
            PushConfigurationState pushConfigurationState,
            CommunicationErrorHandler errorHandler);

    /**
     * Pushes a message to the server. Will throw an exception if the connection
     * is not active (see {@link #isActive()}).
     * <p>
     * Implementation detail: The implementation is responsible for queuing
     * messages that are pushed after {@link #init(ApplicationConnection)} has
     * been called but before the connection has internally been set up and then
     * replay those messages in the original order when the connection has been
     * established.
     * 
     * @param payload
     *            the payload to push
     * @throws IllegalStateException
     *             if this connection is not active
     * 
     * @see #isActive()
     */
    public void push(JsonObject payload);

    /**
     * Checks whether this push connection is in a state where it can push
     * messages to the server. A connection is active until
     * {@link #disconnect(Command)} has been called.
     * 
     * @return <code>true</code> if this connection can accept new messages;
     *         <code>false</code> if this connection is disconnected or
     *         disconnecting.
     */
    public boolean isActive();

    /**
     * Closes the push connection. To ensure correct message delivery order, new
     * messages should not be sent using any other channel until it has been
     * confirmed that all messages pending for this connection have been
     * delivered. The provided command callback is invoked when messages can be
     * passed using some other communication channel.
     * <p>
     * After this method has been called, {@link #isActive()} returns
     * <code>false</code>. Calling this method for a connection that is no
     * longer active will throw an exception.
     * 
     * @param command
     *            callback command invoked when the connection has been properly
     *            disconnected
     * @throws IllegalStateException
     *             if this connection is not active
     */
    public void disconnect(Command command);

    /**
     * Returns a human readable string representation of the transport type used
     * to communicate with the server.
     * 
     * @since 7.1
     * @return A human readable string representation of the transport type
     */
    public String getTransportType();

}
