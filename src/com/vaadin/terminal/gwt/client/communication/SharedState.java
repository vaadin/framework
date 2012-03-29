/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.io.Serializable;

import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;

/**
 * Interface to be implemented by all shared state classes used to communicate
 * basic information about a {@link Connector} from server to client.
 * 
 * Shared state classes have to be declared in client side packages to be
 * accessible both for server and client code. They can be static nested classes
 * of a {@link ServerConnector}.
 * 
 * Shared state objects are only sent from the server to the client, and any
 * modifications from the client should be performed via an RPC call that
 * modifies the authoritative state on the server.
 * 
 * A shared state class should be a bean with getters and setters for each
 * field. Supported data types are simple Java types, other beans and maps and
 * arrays of these.
 * 
 * On the client side the connector should override
 * {@link AbstractComponentConnector#createState()} to create the correct state
 * class and {@link AbstractComponentConnector#getState()} override the return
 * type.
 * 
 * Subclasses of a {@link Connector} using shared state should also provide a
 * subclass of the shared state class of the parent class to extend the state. A
 * single {@link Connector} can only have one shared state object.
 * 
 * @since 7.0
 */
public class SharedState implements Serializable {
}
