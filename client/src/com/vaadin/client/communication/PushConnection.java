package com.vaadin.client.communication;

import com.google.gwt.user.client.Command;
import com.vaadin.client.ApplicationConnection;

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
    public void init(ApplicationConnection connection);

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
     * @param message
     *            the message to push
     * @throws IllegalStateException
     *             if this connection is not active
     * 
     * @see #isActive()
     */
    public void push(String message);

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

}