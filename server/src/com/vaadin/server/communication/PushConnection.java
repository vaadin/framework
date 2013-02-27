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

package com.vaadin.server.communication;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;

import org.atmosphere.cpr.AtmosphereResource;
import org.json.JSONException;

import com.vaadin.ui.UI;

/**
 * Represents a bidirectional ("push") connection between a single UI and its
 * client-side.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class PushConnection implements Serializable {

    private UI ui;
    private boolean pending = true;
    private AtmosphereResource resource;

    public PushConnection(UI ui) {
        this.ui = ui;
    }

    /**
     * Pushes pending state changes and client RPC calls to the client. It is
     * NOT safe to invoke this method if not holding the session lock.
     * <p>
     * This is internal API; please use {@link UI#push()} instead.
     */
    public void push() {
        if (!isConnected()) {
            // Not currently connected; defer until connection established
            setPending(true);
        } else {
            try {
                push(true);
            } catch (IOException e) {
                // TODO Error handling
                throw new RuntimeException("Push failed", e);
            }
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
            new UidlWriter().write(getUI(), writer, false, false, async);
        } catch (JSONException e) {
            throw new IOException("Error writing UIDL", e);
        }
        // "Broadcast" the changes to the single client only
        getResource().getBroadcaster().broadcast(writer.toString(),
                getResource());
    }

    /**
     * Associates this connection with the given AtmosphereResource. If there is
     * a push pending, commits it.
     * 
     * @param resource
     *            The AtmosphereResource representing the push channel.
     * @throws IOException
     */
    protected void connect(AtmosphereResource resource) throws IOException {
        this.resource = resource;
        if (isPending()) {
            push(true);
            setPending(false);
        }
    }

    /**
     * Returns whether this connection is currently open.
     */
    protected boolean isConnected() {
        return resource != null
                && resource.getBroadcaster().getAtmosphereResources()
                        .contains(resource);
    }

    /**
     * Marks that changes in the UI should be pushed as soon as a connection is
     * established.
     */
    protected void setPending(boolean pending) {
        this.pending = pending;
    }

    /**
     * @return Whether the UI should be pushed as soon as a connection opens.
     */
    protected boolean isPending() {
        return pending;
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
}
