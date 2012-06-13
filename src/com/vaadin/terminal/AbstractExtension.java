/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.terminal.gwt.server.ClientConnector;

public abstract class AbstractExtension extends AbstractClientConnector
        implements Extension {

    protected Class<? extends ClientConnector> getAcceptedParentType() {
        return ClientConnector.class;
    }

    protected void attachTo(AbstractClientConnector parent) {
        parent.addExtension(this);
    }

    @Override
    public void setParent(ClientConnector parent) {
        Class<? extends ClientConnector> acceptedParentType = getAcceptedParentType();
        if (parent == null || acceptedParentType.isInstance(parent)) {
            super.setParent(parent);
        } else {
            throw new IllegalArgumentException(getClass().getName()
                    + " can only be attached to parents of type "
                    + acceptedParentType.getName() + " but attach to "
                    + parent.getClass().getName() + " was attempted.");
        }
    }

}
