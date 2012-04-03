/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

/**
 * An interface used by client-side widgets or paintable parts to receive
 * updates from the corresponding server-side components in the form of
 * {@link UIDL}.
 * 
 * Updates can be sent back to the server using the
 * {@link ApplicationConnection#updateVariable()} methods.
 */
@Deprecated
public interface Paintable {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client);
}
