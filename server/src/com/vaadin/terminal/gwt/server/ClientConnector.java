/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.server;

import java.util.Collection;
import java.util.List;

import com.vaadin.external.json.JSONException;
import com.vaadin.external.json.JSONObject;
import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.terminal.AbstractClientConnector;
import com.vaadin.terminal.Extension;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

/**
 * Interface implemented by all connectors that are capable of communicating
 * with the client side
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 * 
 */
public interface ClientConnector extends Connector, RpcTarget {
    /**
     * Returns the list of pending server to client RPC calls and clears the
     * list.
     * 
     * @return an unmodifiable ordered list of pending server to client method
     *         calls (not null)
     */
    public List<ClientMethodInvocation> retrievePendingRpcCalls();

    /**
     * Checks if the communicator is enabled. An enabled communicator is allowed
     * to receive messages from its counter-part.
     * 
     * @return true if the connector can receive messages, false otherwise
     */
    public boolean isConnectorEnabled();

    /**
     * Returns the type of the shared state for this connector
     * 
     * @return The type of the state. Must never return null.
     */
    public Class<? extends SharedState> getStateType();

    @Override
    public ClientConnector getParent();

    /**
     * @deprecated As of 7.0.0, use {@link #markAsDirty()} instead
     */
    @Deprecated
    public void requestRepaint();

    /**
     * Marks that this connector's state might have changed. When the framework
     * is about to send new data to the client-side, it will run
     * {@link #beforeClientResponse(boolean)} followed by {@link #encodeState()}
     * for all connectors that are marked as dirty and send any updated state
     * info to the client.
     * 
     * @since 7.0.0
     */
    public void markAsDirty();

    /**
     * @deprecated As of 7.0.0, use {@link #markAsDirtyRecursive()} instead
     */
    @Deprecated
    public void requestRepaintAll();

    /**
     * Causes this connector and all connectors below it to be marked as dirty.
     * <p>
     * This should only be used in special cases, e.g when the state of a
     * descendant depends on the state of an ancestor.
     * 
     * @see #markAsDirty()
     * 
     * @since 7.0.0
     */
    public void markAsDirtyRecursive();

    /**
     * Sets the parent connector of the connector.
     * 
     * <p>
     * This method automatically calls {@link #attach()} if the connector
     * becomes attached to the application, regardless of whether it was
     * attached previously. Conversely, if the parent is {@code null} and the
     * connector is attached to the application, {@link #detach()} is called for
     * the connector.
     * </p>
     * <p>
     * This method is rarely called directly. One of the
     * {@link ComponentContainer#addComponent(Component)} or
     * {@link AbstractClientConnector#addExtension(Extension)} methods are
     * normally used for adding connectors to a parent and they will call this
     * method implicitly.
     * </p>
     * 
     * <p>
     * It is not possible to change the parent without first setting the parent
     * to {@code null}.
     * </p>
     * 
     * @param parent
     *            the parent connector
     * @throws IllegalStateException
     *             if a parent is given even though the connector already has a
     *             parent
     */
    public void setParent(ClientConnector parent);

    /**
     * Notifies the connector that it is connected to an application.
     * 
     * <p>
     * The caller of this method is {@link #setParent(ClientConnector)} if the
     * parent is itself already attached to the application. If not, the parent
     * will call the {@link #attach()} for all its children when it is attached
     * to the application. This method is always called before the connector's
     * data is sent to the client-side for the first time.
     * </p>
     * 
     * <p>
     * The attachment logic is implemented in {@link AbstractClientConnector}.
     * </p>
     */
    public void attach();

    /**
     * Notifies the component that it is detached from the application.
     * 
     * <p>
     * The caller of this method is {@link #setParent(ClientConnector)} if the
     * parent is in the application. When the parent is detached from the
     * application it is its response to call {@link #detach()} for all the
     * children and to detach itself from the terminal.
     * </p>
     */
    public void detach();

    /**
     * Get a read-only collection of all extensions attached to this connector.
     * 
     * @return a collection of extensions
     */
    public Collection<Extension> getExtensions();

    /**
     * Remove an extension from this connector.
     * 
     * @param extension
     *            the extension to remove.
     */
    public void removeExtension(Extension extension);

    /**
     * Returns the UI this connector is attached to
     * 
     * @return The UI this connector is attached to or null if it is not
     *         attached to any UI
     */
    public UI getUI();

    /**
     * Called before the shared state and RPC invocations are sent to the
     * client. Gives the connector an opportunity to set computed/dynamic state
     * values or to invoke last minute RPC methods depending on other component
     * features.
     * <p>
     * This method must not alter the component hierarchy in any way. Calling
     * requestRepaint() from this method will have no effect.
     * </p>
     * 
     * @param initial
     *            <code>true</code> if the client-side connector will be created
     *            and initialized after this method has been invoked.
     *            <code>false</code> if there is already an initialized
     *            client-side connector.
     * 
     * @since 7.0
     */
    public void beforeClientResponse(boolean initial);

    /**
     * Called by the framework to encode the state to a JSONObject. This is
     * typically done by calling the static method
     * {@link AbstractCommunicationManager#encodeState(ClientConnector, SharedState)}
     * .
     * 
     * @return a JSON object with the encoded connector state
     * @throws JSONException
     *             if the state can not be encoded
     */
    public JSONObject encodeState() throws JSONException;
}
