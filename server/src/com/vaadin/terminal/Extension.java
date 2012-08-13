/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import com.vaadin.terminal.gwt.server.ClientConnector;

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
 * @version @VERSION@
 * @since 7.0.0
 */
public interface Extension extends ClientConnector {
    /*
     * Currently just an empty marker interface to distinguish between
     * extensions and other connectors, e.g. components
     */
}
