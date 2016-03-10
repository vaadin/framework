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

package com.vaadin.server;

/**
 * An extension is an entity that is attached to a Component or another
 * Extension and independently communicates between client and server.
 * <p>
 * An extension can only be attached once. It is not supported to move an
 * extension from one target to another.
 * <p>
 * Extensions can use shared state and RPC in the same way as components.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface Extension extends ClientConnector {

    /**
     * Remove this extension from its target. After an extension has been
     * removed, it cannot be attached again.
     */
    void remove();

    /**
     * Sets the parent connector of the connector.
     * 
     * This method automatically calls {@link #attach()} if the connector
     * becomes attached to the session.
     * <p>
     * This method is rarely called directly.
     * {@link AbstractClientConnector#addExtension(Extension)} is normally used
     * for adding extensions to a parent and it will call this method
     * implicitly.
     * </p>
     * 
     * @param parent
     *            the parent connector
     * @throws IllegalStateException
     *             if a parent is given even though the connector already has a
     *             parent
     */
    public void setParent(ClientConnector parent);

}
