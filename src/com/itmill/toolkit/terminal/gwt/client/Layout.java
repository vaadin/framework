package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;

public interface Layout extends Paintable {

	/**
	 * Replace child of this layout with another component.
	 * 
	 * Each layout must be able to switch children. To to this, one must just
	 * give references to a current and new child. Note that the Layout is not
	 * responsible for registering paintable into client.
	 * 
	 * @param oldComponent
	 *            Child to be replaced
	 * @param newComponent
	 *            Child that replaces the oldComponent
	 */
	void replaceChildComponent(Widget oldComponent, Widget newComponent);

	/**
	 * Is a given component child of this layout.
	 * 
	 * @param component
	 *            Component to test.
	 * @return true iff component is a child of this layout.
	 */
	boolean hasChildComponent(Widget component);

	/**
	 * Update child components caption, description and error message.
	 * 
	 * <p>
	 * Each component is responsible for maintaining its caption, description
	 * and error message. In most cases components doesn't want to do that and
	 * those elements reside outside of the component. Because of this layouts
	 * must provide service for it's childen to show those elements for them.
	 * </p>
	 * 
	 * @param component
	 *            Child component that requests the service.
	 * @param uidl
	 *            UIDL of the child component.
	 */
	void updateCaption(Widget component, UIDL uidl);
}
