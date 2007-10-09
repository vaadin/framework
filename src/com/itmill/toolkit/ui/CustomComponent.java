/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.VariableOwner;

/**
 * Custom component provides simple implementation of Component interface for
 * creation of new UI components by composition of existing components.
 * <p>
 * The component is used by inheriting the CustomComponent class and setting
 * composite root inside the Custom component. The composite root itself can
 * contain more components, but their interfaces are hidden from the users.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class CustomComponent implements Component {

	/**
	 * The root component implementing the custom component.
	 */
	private Component root = null;

	/**
	 * The visibility of the component.
	 */
	private boolean visible = true;

	/**
	 * The parent of the component.
	 */
	private Component parent = null;

	/**
	 * Dependencies of the component, or null.
	 */
	private HashSet dependencies = null;

	/**
	 * Type of the component.
	 */
	private String componentType = null;

	/**
	 * List of repaint request listeners or null if not listened at all.
	 */
	private LinkedList repaintRequestListeners = null;

	/**
	 * Are all the repaint listeners notified about recent changes ?
	 */
	private boolean repaintRequestListenersNotified = false;

	/**
	 * Constructs a new custom component.
	 * 
	 * <p>
	 * The component is implemented by wrapping the methods of the composition
	 * root component given as parameter. The composition root must be set
	 * before the component can be used.
	 * </p>
	 */
	public CustomComponent() {
	}

	/**
	 * Constructs a new custom component.
	 * 
	 * <p>
	 * The component is implemented by wrapping the methods of the composition
	 * root component given as parameter. The composition root must not be null
	 * and can not be changed after the composition.
	 * </p>
	 * 
	 * @param compositionRoot
	 *            the root of the composition component tree.
	 */
	public CustomComponent(Component compositionRoot) {
		setCompositionRoot(compositionRoot);
	}

	/**
	 * Returns the composition root.
	 * 
	 * @return the Component Composition root.
	 */
	protected final Component getCompositionRoot() {
		return root;
	}

	/**
	 * Sets the compositions root.
	 * <p>
	 * The composition root must be set to non-null value before the component
	 * can be used. The composition root can only be set once.
	 * </p>
	 * 
	 * @param compositionRoot
	 *            the root of the composition component tree.
	 */
	protected final void setCompositionRoot(Component compositionRoot) {
		if (compositionRoot != root && root != null)
			root.setParent(null);
		this.root = compositionRoot;
		if (root != null)
			root.setParent(this);
	}

	/* Basic component features ------------------------------------------ */

	/**
	 * Notifies the component that it is connected to an application.
	 * 
	 * @see com.itmill.toolkit.ui.Component#attach()
	 */
	public void attach() {
		if (root != null) {
			root.attach();
			requestRepaint();
		}
	}

	/**
	 * Notifies the component that it is detached from the application.
	 * 
	 * @see com.itmill.toolkit.ui.Component#detach()
	 */
	public void detach() {
		if (root != null)
			root.detach();
	}

	/**
	 * Gets the component's parent application
	 * 
	 * @see com.itmill.toolkit.ui.Component#getApplication()
	 */
	public Application getApplication() {
		if (parent == null)
			return null;
		return parent.getApplication();
	}

	/**
	 * The caption of the custom component is by default the caption of the root
	 * component, or null if the root is not set.
	 * 
	 * @see com.itmill.toolkit.ui.Component#getCaption()
	 */
	public String getCaption() {
		if (root == null)
			return null;
		return root.getCaption();
	}

	/**
	 * The icon of the custom component is by default the icon of the root
	 * component, or null if the root is not set.
	 * 
	 * @see com.itmill.toolkit.ui.Component#getIcon()
	 */
	public Resource getIcon() {
		if (root == null)
			return null;
		return root.getIcon();
	}

	/**
	 * The icon of the custom component is by default the locale of the parent
	 * or null if the parent is not set.
	 * 
	 * @see com.itmill.toolkit.ui.Component#getLocale()
	 */
	public Locale getLocale() {
		if (parent == null)
			return null;
		return parent.getLocale();
	}

	/**
	 * Gets the visual parent of the component.
	 * 
	 * @see com.itmill.toolkit.ui.Component#getParent()
	 */
	public Component getParent() {
		return parent;
	}

	/**
	 * Custom component does not implement custom styles by default and this
	 * function returns null.
	 * 
	 * @see com.itmill.toolkit.ui.Component#getStyle()
	 */
	public String getStyle() {
		return null;
	}

	/**
	 * Gets the component's parent window.
	 * 
	 * @see com.itmill.toolkit.ui.Component#getWindow()
	 */
	public Window getWindow() {
		if (parent == null)
			return null;
		return parent.getWindow();
	}

	/**
	 * Custom component is allways enabled by default.
	 * 
	 * @see com.itmill.toolkit.ui.Component#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Custom component is by default in the non-immediate mode. The
	 * immediateness of the custom component is defined by the components it is
	 * composed of.
	 * 
	 * @see com.itmill.toolkit.terminal.VariableOwner#isImmediate()
	 */
	public boolean isImmediate() {
		return false;
	}

	/**
	 * The custom components are not readonly by default.
	 * 
	 * @see com.itmill.toolkit.ui.Component#isReadOnly()
	 */
	public boolean isReadOnly() {
		return false;
	}

	/**
	 * Tests if the component is visible or not.
	 * 
	 * @see com.itmill.toolkit.ui.Component#isVisible()
	 */
	public boolean isVisible() {
		return visible;
	}

	/* Documentation copied from interface */
	public void requestRepaint() {

		// The effect of the repaint request is identical to case where a
		// child requests repaint
		childRequestedRepaint(null);
	}

	/* Documentation copied from interface */
	public void childRequestedRepaint(Collection alreadyNotified) {

		// Notify listeners only once
		if (!repaintRequestListenersNotified) {

			// Notify the listeners
			if (repaintRequestListeners != null
					&& !repaintRequestListeners.isEmpty()) {
				Object[] listeners = repaintRequestListeners.toArray();
				RepaintRequestEvent event = new RepaintRequestEvent(this);
				for (int i = 0; i < listeners.length; i++) {
					if (alreadyNotified == null)
						alreadyNotified = new LinkedList();
					if (!alreadyNotified.contains(listeners[i])) {
						((RepaintRequestListener) listeners[i])
								.repaintRequested(event);
						alreadyNotified.add(listeners[i]);
					}
				}
			}

			repaintRequestListenersNotified = true;

			// Notify the parent
			Component parent = getParent();
			if (parent != null)
				parent.childRequestedRepaint(alreadyNotified);

		}
	}

	/* Documentation copied from interface */
	public void addListener(RepaintRequestListener listener) {
		if (repaintRequestListeners == null)
			repaintRequestListeners = new LinkedList();
		if (!repaintRequestListeners.contains(listener)) {
			repaintRequestListeners.add(listener);
		}
	}

	/* Documentation copied from interface */
	public void removeListener(RepaintRequestListener listener) {
		if (repaintRequestListeners != null) {
			repaintRequestListeners.remove(listener);
			if (repaintRequestListeners.isEmpty())
				repaintRequestListeners = null;
		}
	}

	/**
	 * The custom component is allways enabled by default.
	 */
	public void setEnabled(boolean enabled) {
	}

	/**
	 * Sets the component's parent component.
	 * 
	 * @see com.itmill.toolkit.ui.Component#setParent(com.itmill.toolkit.ui.Component)
	 */
	public void setParent(Component parent) {

		// If the parent is not changed, dont do nothing
		if (parent == this.parent)
			return;

		// Sends the detach event if the component have been connected to a
		// window
		if (getApplication() != null) {
			detach();
			this.parent = null;
		}

		// Connects to new parent
		this.parent = parent;

		// Sends the attach event if connected to a window
		if (getApplication() != null)
			attach();
	}

	/**
	 * Sets the component's to read-only mode to the specified state.
	 * 
	 * @see com.itmill.toolkit.ui.Component#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) {
	}

	/**
	 * Sets the look-and-feel style of the component.
	 * 
	 * @see com.itmill.toolkit.ui.Component#setStyle(java.lang.String)
	 */
	public void setStyle(String style) {
	}

	/**
	 * Sets the components visibility status.
	 * 
	 * @see com.itmill.toolkit.ui.Component#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/* Documented in super interface */
	public void requestRepaintRequests() {
		repaintRequestListenersNotified = false;
	}

	/* Documented in super interface */
	public void paint(PaintTarget target) throws PaintException {
		if (root == null)
			throw new IllegalStateException("Composition root must be set to"
					+ " non-null value before the " + getClass().getName()
					+ " can be painted");

		if (isVisible()) {
			String type = getComponentType();
			if (type != null) {
				if (!target.startTag(this, "component")) {
					target.addAttribute("type", type);
					root.paint(target);
				}
				target.endTag("component");
			} else
				root.paint(target);
		}
		repaintRequestListenersNotified = false;
	}

	/**
	 * Called when one or more variables handled by the implementing class are
	 * changed.
	 * 
	 * @see com.itmill.toolkit.terminal.VariableOwner#changeVariables(java.lang.Object,
	 *      java.util.Map)
	 */
	public void changeVariables(Object source, Map variables) {
	}

	/**
	 * Makes this <code>VariableOwner</code> depend on the given
	 * <code>VariableOwner</code>.
	 * 
	 * @see com.itmill.toolkit.terminal.VariableOwner#dependsOn(com.itmill.toolkit.terminal.VariableOwner)
	 */
	public void dependsOn(VariableOwner depended) {
		if (depended == null)
			return;
		if (dependencies == null)
			dependencies = new HashSet();
		dependencies.add(depended);
	}

	/**
	 * Gets the variable change listeners this <code>VariableOwner</code>
	 * directly depends on.
	 * 
	 * @see com.itmill.toolkit.terminal.VariableOwner#getDirectDependencies()
	 */
	public Set getDirectDependencies() {
		return dependencies;
	}

	/**
	 * Removes the given component from this component's dependency list.
	 * 
	 * @see com.itmill.toolkit.terminal.VariableOwner#removeDirectDependency(com.itmill.toolkit.terminal.VariableOwner)
	 */
	public void removeDirectDependency(VariableOwner depended) {
		if (dependencies == null)
			return;
		dependencies.remove(depended);
		if (dependencies.isEmpty())
			dependencies = null;
	}

	/* Event functions are not implemented by default -------------------- */

	/**
	 * Custom component does not implement any component events by default.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addListener(Component.Listener listener) {
	}

	/**
	 * Custom component does not implement any component events by default.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeListener(Component.Listener listener) {
	}

	/**
	 * Gets the component type.
	 * 
	 * The component type is textual type of the component. This is included in
	 * the UIDL as component tag attribute. If the component type is null
	 * (default), the component tag is not included in the UIDL at all.
	 * 
	 * @return the component type.
	 */
	public String getComponentType() {
		return componentType;
	}

	/**
	 * Sets the component type.
	 * 
	 * The component type is textual type of the component. This is included in
	 * the UIDL as component tag attribute. If the component type is null
	 * (default), the component tag is not included in the UIDL at all.
	 * 
	 * @param componentType
	 *            the componentType to set.
	 */
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	/**
	 * Custom component does not implement custom styles by default.
	 * 
	 * @see com.itmill.toolkit.ui.Component#getStyle()
	 */
	public void addStyleName(String style) {
	}

	/**
	 * Custom component does not implement custom styles by default.
	 * 
	 * @see com.itmill.toolkit.ui.Component#getStyle()
	 */
	public void removeStyleName(String style) {

	}

}
