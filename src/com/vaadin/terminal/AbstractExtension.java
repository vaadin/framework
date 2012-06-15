/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.terminal.gwt.server.ClientConnector;

public abstract class AbstractExtension extends AbstractClientConnector
        implements Extension {
    private boolean previouslyAttached = false;

    protected Class<? extends ClientConnector> getAcceptedParentType() {
        return ClientConnector.class;
    }

    protected void extend(AbstractClientConnector target) {
        target.addExtension(this);
    }

    protected void removeFromTarget() {
        getParent().removeExtension(this);
    }

    @Override
    public void setParent(ClientConnector parent) {
        if (previouslyAttached && parent != null) {
            throw new IllegalStateException(
                    "An extension can not be set to extend a new target after getting detached from the previous.");
        }

        Class<? extends ClientConnector> acceptedParentType = getAcceptedParentType();
        if (parent == null || acceptedParentType.isInstance(parent)) {
            super.setParent(parent);
            previouslyAttached = true;
        } else {
            throw new IllegalArgumentException(getClass().getName()
                    + " can only be attached to targets of type "
                    + acceptedParentType.getName() + " but attach to "
                    + parent.getClass().getName() + " was attempted.");
        }
    }

}
