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
 * Extensions can use shared state and RPC in the same way as components.
 * <p>
 * AbstractExtension adds a mechanism for adding the extension to any Connector
 * (extend). To let the Extension determine what kind target it can be added to,
 * the extend method is declared as protected.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public abstract class AbstractExtension extends AbstractClientConnector
        implements Extension {
    private boolean previouslyAttached = false;

    private ClientConnector parent;

    /**
     * Gets a type that the parent must be an instance of. Override this if the
     * extension only support certain targets, e.g. if only TextFields can be
     * extended.
     * 
     * @return a type that the parent must be an instance of
     */
    protected Class<? extends ClientConnector> getSupportedParentType() {
        return ClientConnector.class;
    }

    /**
     * Add this extension to the target connector. This method is protected to
     * allow subclasses to require a more specific type of target.
     * 
     * @param target
     *            the connector to attach this extension to
     */
    protected void extend(AbstractClientConnector target) {
        target.addExtension(this);
    }

    @Override
    public void remove() {
        getParent().removeExtension(this);
    }

    @Override
    public void setParent(ClientConnector parent) {
        if (previouslyAttached && parent != null) {
            throw new IllegalStateException(
                    "An extension can not be set to extend a new target after getting detached from the previous.");
        }

        Class<? extends ClientConnector> supportedParentType = getSupportedParentType();
        if (parent == null || supportedParentType.isInstance(parent)) {
            internalSetParent(parent);
            previouslyAttached = true;
        } else {
            throw new IllegalArgumentException(getClass().getName()
                    + " can only be attached to targets of type "
                    + supportedParentType.getName() + " but attach to "
                    + parent.getClass().getName() + " was attempted.");
        }
    }

    /**
     * Actually sets the parent and calls required listeners.
     * 
     * @since 7.1
     * @param parent
     *            The parent to set
     */
    private void internalSetParent(ClientConnector parent) {

        // Send a detach event if the component is currently attached
        if (isAttached()) {
            detach();
        }

        // Connect to new parent
        this.parent = parent;

        // Send attach event if the component is now attached
        if (isAttached()) {
            attach();
        }

    }

    @Override
    public ClientConnector getParent() {
        return parent;
    }

}
