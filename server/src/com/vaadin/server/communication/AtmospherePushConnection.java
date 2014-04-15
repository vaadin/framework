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

package com.vaadin.server.communication;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT;
import org.json.JSONException;

import com.vaadin.shared.communication.PushConstants;
import com.vaadin.ui.UI;

/**
 * {@link PushConnection} implementation using the Atmosphere push support that
 * is by default included in Vaadin.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class AtmospherePushConnection implements PushConnection {

    /**
     * Represents a message that can arrive as multiple fragments.
     */
    protected static class FragmentedMessage implements Serializable {
        private final StringBuilder message = new StringBuilder();
        private final int messageLength;

        public FragmentedMessage(Reader reader) throws IOException {
            // Messages are prefixed by the total message length plus a
            // delimiter
            String length = "";
            int c;
            while ((c = reader.read()) != -1
                    && c != PushConstants.MESSAGE_DELIMITER) {
                length += (char) c;
            }
            try {
                messageLength = Integer.parseInt(length);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid message length " + length, e);
            }
        }

        /**
         * Appends all the data from the given Reader to this message and
         * returns whether the message was completed.
         * 
         * @param reader
         *            The Reader from which to read.
         * @return true if this message is complete, false otherwise.
         * @throws IOException
         */
        public boolean append(Reader reader) throws IOException {
            char[] buffer = new char[PushConstants.WEBSOCKET_BUFFER_SIZE];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                message.append(buffer, 0, read);
                assert message.length() <= messageLength : "Received message "
                        + message.length() + "chars, expected " + messageLength;
            }
            return message.length() == messageLength;
        }

        public Reader getReader() {
            return new StringReader(message.toString());
        }
    }

    private UI ui;
    private AtmosphereResource resource;
    private Future<String> outgoingMessage;
    private FragmentedMessage incomingMessage;

    public AtmospherePushConnection(UI ui, AtmosphereResource resource) {
        this.ui = ui;
        this.resource = resource;
    }

    @Override
    public void push() {
        assert isConnected();
        try {
            push(true);
        } catch (IOException e) {
            // TODO Error handling
            throw new RuntimeException("Push failed", e);
        }
    }

    /**
     * Pushes pending state changes and client RPC calls to the client.
     * 
     * @param async
     *            True if this push asynchronously originates from the server,
     *            false if it is a response to a client request.
     * @throws IOException
     */
    protected void push(boolean async) throws IOException {
        Writer writer = new StringWriter();
        try {
            new UidlWriter().write(getUI(), writer, false, async);
        } catch (JSONException e) {
            throw new IOException("Error writing UIDL", e);
        }
        sendMessage("for(;;);[{" + writer.toString() + "}]");
    }

    /**
     * Sends the given message to the current client.
     * 
     * @param message
     *            The message to send
     */
    void sendMessage(String message) {
        // "Broadcast" the changes to the single client only
        outgoingMessage = getResource().getBroadcaster().broadcast(message,
                getResource());
    }

    /**
     * Reads and buffers a (possibly partial) message. If a complete message was
     * received, or if the call resulted in the completion of a partially
     * received message, returns a {@link Reader} yielding the complete message.
     * Otherwise, returns null.
     * 
     * @param reader
     *            A Reader from which to read the (partial) message
     * @return A Reader yielding a complete message or null if the message is
     *         not yet complete.
     * @throws IOException
     */
    protected Reader receiveMessage(Reader reader) throws IOException {

        if (resource.transport() != TRANSPORT.WEBSOCKET) {
            return reader;
        }

        if (incomingMessage == null) {
            // No existing partially received message
            incomingMessage = new FragmentedMessage(reader);
        }

        if (incomingMessage.append(reader)) {
            // Message is complete
            Reader completeReader = incomingMessage.getReader();
            incomingMessage = null;
            return completeReader;
        } else {
            // Only received a partial message
            return null;
        }
    }

    @Override
    public boolean isConnected() {
        return resource != null
                && resource.getBroadcaster().getAtmosphereResources()
                        .contains(resource);
    }

    /**
     * @return the UI associated with this connection.
     */
    protected UI getUI() {
        return ui;
    }

    /**
     * @return The AtmosphereResource associated with this connection or null if
     *         connection not open.
     */
    protected AtmosphereResource getResource() {
        return resource;
    }

    @Override
    public void disconnect() {
        assert isConnected();

        if (resource.isResumed()) {
            // Calling disconnect may end up invoking it again via
            // resource.resume and PushHandler.onResume. Bail out here if
            // the resource is already resumed; this is a bit hacky and should
            // be implemented in a better way in 7.2.
            resource = null;
            return;
        }

        if (outgoingMessage != null) {
            // Wait for the last message to be sent before closing the
            // connection (assumes that futures are completed in order)
            try {
                outgoingMessage.get(1000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                getLogger()
                        .log(Level.INFO,
                                "Timeout waiting for messages to be sent to client before disconnect");
            } catch (Exception e) {
                getLogger()
                        .log(Level.INFO,
                                "Error waiting for messages to be sent to client before disconnect");
            }
            outgoingMessage = null;
        }

        resource.resume();
        resource = null;
    }

    /**
     * @since
     * @return
     */
    private static Logger getLogger() {
        return Logger.getLogger(AtmospherePushConnection.class.getName());
    }
}
