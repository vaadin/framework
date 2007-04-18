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

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ErrorMessage;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.VariableOwner;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Locale;

/**
 * The top-level component interface which must be implemented by all UI
 * components that use IT Mill Toolkit.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Component extends Paintable, VariableOwner {

	/**
	 * Gets the look-and-feel style of the component.
	 * 
	 * @return the component's styleValue of property style.
	 */
	public String getStyle();

	/**
	 * Sets the look-and-feel style of the component. This method will trigger a
	 * {@link com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param style
	 *            the new style of the component.
	 */
	public void setStyle(String style);

	/**
	 * <p>
	 * Tests if the component is enabled or not. All the variable change events
	 * are blocked from disabled components. Also the component should visually
	 * indicate that it is disabled (by shading the component for example). All
	 * hidden (isVisible() == false) components must return false.
	 * </p>
	 * 
	 * <p>
	 * Components should be enabled by default.
	 * </p>
	 * 
	 * @return <code>true</code> if the component is enabled,
	 *         <code>false</code> if not.
	 * @see VariableOwner#isEnabled()
	 */
	public boolean isEnabled();

	/**
	 * Enables or disables the component. Being enabled means that the component
	 * can be edited. This method will trigger a
	 * {@link com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param enabled
	 *            the boolean value specifying if the component should be
	 *            enabled after the call or not
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Tests if the component is visible or not. Visibility defines if the
	 * component is shown in the UI or not. Default is <code>true</code>.
	 * 
	 * @return <code>true</code> if the component is visible in the UI,
	 *         <code>false</code> if not
	 */
	public boolean isVisible();

	/**
	 * Sets the components visibility status. Visibility defines if the
	 * component is shown in the UI or not.
	 * 
	 * @param visible
	 *            the Boolean value specifying if the component should be
	 *            visible after the call or not.
	 */
	public void setVisible(boolean visible);

	/**
	 * Gets the visual parent of the component. The components can be nested but
	 * one component can have only one parent.
	 * 
	 * @return the parent component.
	 */
	public Component getParent();

	/**
	 * Sets the component's parent component.
	 * 
	 * <p>
	 * This method calls automatically {@link #attach()} if the parent is
	 * attached to a window (or is itself a window}, and {@link #detach()} if
	 * <code>parent</code> is set <code>null</code>, but the component was
	 * in the application.
	 * </p>
	 * 
	 * <p>
	 * This method is rarely called directly. Instead the
	 * {@link ComponentContainer#addComponent(Component)} method is used to add
	 * components to container, which call this method implicitly.
	 * 
	 * @param parent
	 *            the new parent component.
	 */
	public void setParent(Component parent);

	/**
	 * Tests if the component is in read-only mode.
	 * 
	 * @return <code>true</code> if the component is in read-only mode,
	 *         <code>false</code> if not.
	 */
	public boolean isReadOnly();

	/**
	 * Sets the component's to read-only mode to the specified state. This
	 * method will trigger a
	 * {@link com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param readOnly
	 *            the boolean value specifying if the component should be in
	 *            read-only mode after the call or not.
	 */
	public void setReadOnly(boolean readOnly);

	/**
	 * Gets the caption of the component. Caption is the visible name of the
	 * component.
	 * 
	 * @return the component's caption <code>String</code>.
	 */
	public String getCaption();

	/**
	 * Gets the component's icon. A component may have a graphical icon
	 * associated with it, this method retrieves it if it is defined.
	 * 
	 * @return the component's icon or <code>null</code> if it not defined.
	 */
	public Resource getIcon();

	/**
	 * Gets the component's parent window. If the component does not yet belong
	 * to a window <code>null</code> is returned.
	 * 
	 * @return the parent window of the component or <code>null</code>.
	 */
	public Window getWindow();

	/**
	 * Gets the component's parent application. If the component does not yet
	 * belong to a application <code>null</code> is returned.
	 * 
	 * @return the parent application of the component or <code>null</code>.
	 */
	public Application getApplication();

	/**
	 * <p>
	 * Notifies the component that it is connected to an application. This
	 * method is always called before the component is first time painted and is
	 * suitable to be extended. The <code>getApplication</code> and
	 * <code>getWindow</code> methods might return <code>null</code> before
	 * this method is called.
	 * </p>
	 * 
	 * <p>
	 * The caller of this method is {@link #setParent(Component)} if the parent
	 * is already in the application. If the parent is not in the application,
	 * it must call the {@link #attach()} for all its children when it will be
	 * added to the application.
	 * </p>
	 */
	public void attach();

	/**
	 * Notifies the component that it is detached from the application.
	 * <p>
	 * The {@link #getApplication()} and {@link #getWindow()} methods might
	 * return <code>null</code> after this method is called.
	 * </p>
	 * 
	 * <p>
	 * The caller of this method is {@link #setParent(Component)} if the parent
	 * is in the application. When the parent is detached from the application
	 * it is its response to call {@link #detach()} for all the children and to
	 * detach itself from the terminal.
	 * </p>
	 */
	public void detach();

	/**
	 * Gets the locale of this component.
	 * 
	 * @return This component's locale. If this component does not have a
	 *         locale, the locale of its parent is returned. Eventually locale
	 *         of application is returned. If application does not have its own
	 *         locale the locale is determined by
	 *         <code>Locale.getDefautlt</code>. Returns null if the component
	 *         does not have its own locale and has not yet been added to a
	 *         containment hierarchy such that the locale can be determined from
	 *         the containing parent.
	 */
	public Locale getLocale();

	/**
	 * The children must call this method when they need repainting. The call
	 * must be made event in the case the children sent the repaint request
	 * themselves.
	 * 
	 * @param alreadyNotified
	 *            the collection of repaint request listeners that have been
	 *            already notified by the child. This component should not
	 *            renotify the listed listeners again. The container given as
	 *            parameter must be modifiable as the component might modify it
	 *            and pass it forwards. Null parameter is interpreted as empty
	 *            collection.
	 */
	public void childRequestedRepaint(Collection alreadyNotified);

	/* Component event framework *************************************** */

	/**
	 * Superclass of all component originated <code>Event</code>s.
	 */
	public class Event extends EventObject {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 4048791277653274933L;

		/**
		 * Constructs a new event with a specified source component.
		 * 
		 * @param source
		 *            the source component of the event.
		 */
		public Event(Component source) {
			super(source);
		}
	}

	/**
	 * Listener interface for receiving <code>Component.Event</code>s.
	 */
	public interface Listener extends EventListener {

		/**
		 * Notifies the listener of a component event.
		 * 
		 * @param event
		 *            the event that has occured.
		 */
		public void componentEvent(Component.Event event);
	}

	/**
	 * Registers a new component event listener for this component.
	 * 
	 * @param listener
	 *            the new Listener to be registered.
	 */
	public void addListener(Component.Listener listener);

	/**
	 * Removes a previously registered component event listener from this
	 * component.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeListener(Component.Listener listener);

	/**
	 * Class of all component originated <code>ErrorEvent</code>s.
	 */
	public class ErrorEvent extends Event {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 4051323457293857333L;

		private ErrorMessage message;

		/**
		 * Constructs a new event with a specified source component.
		 * 
		 * @param message
		 *            the error message.
		 * @param component
		 *            the source component.
		 */
		public ErrorEvent(ErrorMessage message, Component component) {
			super(component);
			this.message = message;
		}

		/**
		 * Gets the error message.
		 * 
		 * @return the error message.
		 */
		public ErrorMessage getErrorMessage() {
			return this.message;
		}
	}

	/**
	 * Listener interface for receiving <code>Component.Errors</code>s.
	 */
	public interface ErrorListener extends EventListener {

		/**
		 * Notifies the listener of a component error.
		 * 
		 * @param event
		 *            the event that has occured.
		 */
		public void componentError(Component.ErrorEvent event);
	}

	/**
	 * Interface implemented by components which can obtain input focus.
	 */
	public interface Focusable {

		/**
		 * Sets the focus to this component.
		 */
		public void focus();

		/**
		 * Gets the Tabulator index of this Focusable component.
		 * 
		 * @return the Positive tab order of this focusable. Negative of zero
		 *         means unspecified tab order.
		 */
		public int getTabIndex();

		/**
		 * Sets the Tabulator index of this Focusable component.
		 * 
		 * @param tabIndex
		 *            the Positive tab order of this focusable. Negative of zero
		 *            means unspecified tab order.
		 */
		public void setTabIndex(int tabIndex);

		/**
		 * Gets the unique ID of focusable. This will be used to move input
		 * focus directly to this component.
		 * 
		 * @return the Unique id of focusable.
		 */
		public long getFocusableId();

	}
}
