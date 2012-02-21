/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ui.VAbstractPaintableWidget;

/**
 * Interface to be implemented by all shared state classes used to communicate
 * basic information about a paintable from server to client. These typically
 * replace most of the semi-static information sent via the paintContent() and
 * updateFromUIDL() mechanism in Vaadin 6 (component sizes, captions, tooltips,
 * etc.).
 * 
 * Shared state classes have to be declared in client side packages to be
 * accessible both for server and client code. They can be static nested classes
 * of the client side widget.
 * 
 * Shared state objects are only sent from the server to the client, and any
 * modifications from the client should be performed via an RPC call that
 * modifies the authoritative state on the server.
 * 
 * In current Vaadin versions, the whole shared state is sent every time the
 * component is painted. Future versions may optimize this so that only the
 * necessary (changed or missing on the client side) parts are re-sent to the
 * client, but the client will have access to the whole state.
 * 
 * TODO the rest of the javadoc corresponds to the design that is not yet
 * implemented
 * 
 * A shared state class should be a bean with getters and setters for each
 * field, and should only contain simple data types, or arrays or maps of
 * supported data types.
 * 
 * On the client side, SharedState instances must be created using
 * {@link GWT#create(Class)} to let a generator create custom deserialization
 * support for them. For most widgets,
 * {@link VAbstractPaintableWidget#createSharedState()} method should be
 * overridden to create a shared state instance of the correct type using
 * {@link GWT#create(Class)}.
 * 
 * Subclasses of a paintable using shared state should also provide a subclass
 * of the shared state class of the parent class to extend the state - a single
 * paintable can only have one shared state object.
 * 
 * Future versions of the shared state mechanism may also support custom data
 * types as fields of a shared state class.
 * 
 * @since 7.0
 */
public class SharedState implements Serializable {
}
