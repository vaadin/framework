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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.terminal.KeyMapper;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * A generic button component.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Button extends AbstractField {

	/* Private members ************************************************* */

	boolean switchMode = false;

	/**
	 * Creates a new push button. The value of the push button is allways false
	 * and they are immediate by default.
	 * 
	 */
	public Button() {
		setSwitchMode(false);
	}

	/**
	 * Creates a new push button.
	 * 
	 * The value of the push button is allways false and they are immediate by
	 * default.
	 * 
	 * @param caption
	 *            the Button caption.
	 */
	public Button(String caption) {
		setCaption(caption);
		setSwitchMode(false);
	}

	/**
	 * Creates a new push button with click listener.
	 * 
	 * @param caption
	 *            the Button caption.
	 * @param listener
	 *            the Button click listener.
	 */
	public Button(String caption, ClickListener listener) {
		this(caption);
		addListener(listener);
	}

	/**
	 * Creates a new push button with a method listening button clicks. The
	 * method must have either no parameters, or only one parameter of
	 * Button.ClickEvent type.
	 * 
	 * @param caption
	 *            the Button caption.
	 * @param target
	 *            the Object having the method for listening button clicks.
	 * @param methodName
	 *            the name of the method in target object, that receives button
	 *            click events.
	 */
	public Button(String caption, Object target, String methodName) {
		this(caption);
		addListener(ClickEvent.class, target, methodName);
	}

	/**
	 * Creates a new switch button with initial value.
	 * 
	 * @param state
	 *            the Initial state of the switch-button.
	 * @param initialState
	 */
	public Button(String caption, boolean initialState) {
		setCaption(caption);
		setValue(new Boolean(initialState));
		setSwitchMode(true);
	}

	/**
	 * Creates a new switch button that is connected to a boolean property.
	 * 
	 * @param state
	 *            the Initial state of the switch-button.
	 * @param dataSource
	 */
	public Button(String caption, Property dataSource) {
		setCaption(caption);
		setSwitchMode(true);
		setPropertyDataSource(dataSource);
	}

	/**
	 * Gets component UIDL tag.
	 * 
	 * @return the Component UIDL tag as string.
	 */
	public String getTag() {
		return "button";
	}

	/**
	 * Paints the content of this component.
	 * 
	 * @param event
	 *            the PaintEvent.
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		if (isSwitchMode())
			target.addAttribute("type", "switch");
		boolean state;
		try {
			state = ((Boolean) getValue()).booleanValue();
		} catch (NullPointerException e) {
			state = false;
		}
		target.addVariable(this, "state", state);

	}

	/**
	 * Invoked when the value of a variable has changed. Button listeners are
	 * notified if the button is clicked.
	 * 
	 * @param source
	 * @param variables
	 */
	public void changeVariables(Object source, Map variables) {
		if (variables.containsKey("state")) {
			// Gets the new and old button states
			Boolean newValue = (Boolean) variables.get("state");
			Boolean oldValue = (Boolean) getValue();

			if (isSwitchMode()) {

				// For switch button, the event is only sent if the
				// switch state is changed
				if (newValue != null && !newValue.equals(oldValue)
						&& !isReadOnly()) {
					setValue(newValue);
					fireClick();
				}
			} else {

				// Only send click event if the button is pushed
				if (newValue.booleanValue())
					fireClick();

				// If the button is true for some reason, release it
				if (oldValue.booleanValue())
					setValue(new Boolean(false));
			}
		}
	}

	/**
	 * Checks if it is switchMode.
	 * 
	 * @return <code>true</code> if it is in Switch Mode, otherwise
	 *         <code>false</code>.
	 */
	public boolean isSwitchMode() {
		return switchMode;
	}

	/**
	 * Sets the switchMode.
	 * 
	 * @param switchMode
	 *            The switchMode to set.
	 */
	public void setSwitchMode(boolean switchMode) {
		this.switchMode = switchMode;
		if (!switchMode) {
			setImmediate(true);
			setValue(new Boolean(false));
		}
	}

	/**
	 * Sets immediate mode. Push buttons can not be set in non-immediate mode.
	 * 
	 * @see com.itmill.toolkit.ui.AbstractComponent#setImmediate(boolean)
	 */
	public void setImmediate(boolean immediate) {
		// Push buttons are allways immediate
		super.setImmediate(!isSwitchMode() || immediate);
	}

	/**
	 * The type of the button as a property.
	 * 
	 * @see com.itmill.toolkit.data.Property#getType()
	 */
	public Class getType() {
		return Boolean.class;
	}

	/* Click event ************************************************ */

	private static final Method BUTTON_CLICK_METHOD;
	static {
		try {
			BUTTON_CLICK_METHOD = ClickListener.class.getDeclaredMethod(
					"buttonClick", new Class[] { ClickEvent.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException();
		}
	}

	/**
	 * Click event. This event is thrown, when the button is clicked.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class ClickEvent extends Component.Event {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3546647602931118393L;

		/**
		 * New instance of text change event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public ClickEvent(Component source) {
			super(source);
		}

		/**
		 * Gets the Button where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public Button getButton() {
			return (Button) getSource();
		}
	}

	/**
	 * Button click listener
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public interface ClickListener {

		/**
		 * Button has been pressed.
		 * 
		 * @param event
		 *            Button click event.
		 */
		public void buttonClick(ClickEvent event);
	}

	/**
	 * Adds the button click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addListener(ClickListener listener) {
		addListener(ClickEvent.class, listener, BUTTON_CLICK_METHOD);
	}

	/**
	 * Removes the button click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeListener(ClickListener listener) {
		removeListener(ClickEvent.class, listener, BUTTON_CLICK_METHOD);
	}

	/**
	 * Emits the options change event.
	 */
	protected void fireClick() {
		fireEvent(new Button.ClickEvent(this));
	}

}
