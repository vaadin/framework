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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.lang.reflect.Method;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.terminal.KeyMapper;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.event.Action.Handler;


/** A generic button component.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Button extends AbstractField implements Action.Container {

	/* Private members ************************************************* */

	boolean switchMode = false;
	
	/** List of action handlers */
	private LinkedList actionHandlers = null;

	/** Action mapper */
	private KeyMapper actionMapper = null;


	/** Creates a new push button.
	 *
	 * The value of the push button is allways false and they are
	 * immediate by default.
	 *
	 */
	public Button() {
		setSwitchMode(false);
	}

	/** Creates a new push button.
	 * 
	 * The value of the push button is allways false and they are 
	 * immediate by default.
	 * 
	 *  @param caption Button caption
	 */
	public Button(String caption) {
		setCaption(caption);
		setSwitchMode(false);
	}

	/** Creates a new push button with click listener.
	 *  @param caption Button caption
	 *  @param listener Button click listener
	 */
	public Button(String caption, ClickListener listener) {
		this(caption);
		addListener(listener);
	}

	/** Creates a new push button with a method listening button clicks.
	 * The method must have either no parameters, or only one parameter of
	 * Button.ClickEvent type.
	 *  @param caption Button caption
	 *  @param target Object having the method for listening button clicks
	 *  @param methodName The name of the method in target object, that
	 *   receives button click events.
	 */
	public Button(String caption, Object target, String methodName) {
		this(caption);
		addListener(ClickEvent.class, target, methodName);
	}

	/** Creates new switch button with initial value.
	 * @param state Initial state of the switch-button.
	 */
	public Button(String caption, boolean initialState) {
		setCaption(caption);
		setValue(new Boolean(initialState));
		setSwitchMode(true);
	}

	/** Creates new switch button that is connected to a boolean property.
	 * @param state Initial state of the switch-button.
	 */
	public Button(String caption, Property dataSource) {
		setCaption(caption);
		setSwitchMode(true);
		setPropertyDataSource(dataSource);
	}

	/** Get component UIDL tag.
	 * @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "button";
	}

	/** Paint the content of this component.
	 * @param event PaintEvent.
	 * @throws IOException Passed from the UIDLStream.
	 * @throws PaintException The paint operation failed.
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
		
		// Actions
		if (actionHandlers != null) {
			Set actionSet = new LinkedHashSet();
			for (Iterator ahi = actionHandlers.iterator();
				ahi.hasNext();
				) {
				Action[] aa =
					((Action.Handler) ahi.next()).getActions(
						this,
						this);
				if (aa != null)
					for (int ai = 0; ai < aa.length; ai++) {
						actionSet.add(aa[ai]);
					}
			}
			
			target.startTag("actions");
			target.addVariable(this, "action", "");
			for (Iterator i = actionSet.iterator(); i.hasNext();) {
				try {
					ShortcutAction a = (ShortcutAction) i.next();
					target.startTag("action");
					if (a.getCaption() != null)
						target.addAttribute("caption", a.getCaption());
					if (a.getIcon() != null)
						target.addAttribute("icon", a.getIcon());
					target.addAttribute("key", actionMapper.key(a));
					target.addAttribute("keycode", a.getKeyCode());
					if(a.getModifiers() != null) {
						int[] modifiers = a.getModifiers();
						target.addAttribute("modifiers", modifiers.length);
						for(int j = 0; j < modifiers.length; j++) {
							target.addAttribute("modifier" + j, modifiers[j]);
						}
					}
					target.endTag("action");
				} catch( Exception e ){
					// ignore non-shorcut actions for button
				}
			}
			target.endTag("actions");
		}
	}

	/** Invoked when the value of a variable has changed. Button
	 * listeners are notified if the button is clicked.
	 * @param event Variable change event.
	 */
	public void changeVariables(Object source, Map variables) {
		if (variables.containsKey("state")) {
			// Get the new and old button states
			Boolean newValue = (Boolean) variables.get("state");
			Boolean oldValue = (Boolean) getValue();

			if (isSwitchMode()) {

				// For switch button, the event is only sent if the 
				// switch state is changed
				if (newValue != null
					&& !newValue.equals(oldValue)
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
		// Actions
		// TODO this is pretty much copy-pasted from tree, may be simplified
		if (variables.containsKey("action")) {

			StringTokenizer st =
				new StringTokenizer((String) variables.get("action"), ",");
			if (st.countTokens() == 2) {
				Action action = (Action) actionMapper.get(st.nextToken());
				if (action != null
					&& actionHandlers != null)
					for (Iterator i = actionHandlers.iterator();
						i.hasNext();
						)
						((Action.Handler) i.next()).handleAction(
							action,
							this,
							this);
			}
		}

	}

	/**
	 * Returns the switchMode.
	 * @return boolean
	 */
	public boolean isSwitchMode() {
		return switchMode;
	}

	/**
	 * Sets the switchMode.
	 * @param switchMode The switchMode to set
	 */
	public void setSwitchMode(boolean switchMode) {
		this.switchMode = switchMode;
		if (!switchMode) {
			setImmediate(true);
			setValue(new Boolean(false));
		}
	}

	/** Set immediate mode. 
	* @see com.itmill.toolkit.ui.AbstractComponent#setImmediate(boolean)
	* 
	* Push buttons can not be set in non-immediate mode.
	*/
	public void setImmediate(boolean immediate) {
		// Push buttons are allways immediate
		super.setImmediate(!isSwitchMode() || immediate);
	}

	/** The type of the button as a property.
	* @see com.itmill.toolkit.data.Property#getType()
	*/
	public Class getType() {
		return Boolean.class;
	}

	/* Click event ************************************************ */

	private static final Method BUTTON_CLICK_METHOD;
	static {
		try {
			BUTTON_CLICK_METHOD =
				ClickListener.class.getDeclaredMethod(
					"buttonClick",
					new Class[] { ClickEvent.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException();
		}
	}

	/** Click event. This event is thrown, when the button is clicked. 
	 * @author IT Mill Ltd.
		 * @version @VERSION@
		 * @since 3.0
	 */
	public class ClickEvent extends Component.Event {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3546647602931118393L;

        /** New instance of text change event
		* @param source Source of the event.
		*/
		public ClickEvent(Component source) {
			super(source);
		}

		/** Button where the event occurred
		 * @return Source of the event.
		 */
		public Button getButton() {
			return (Button) getSource();
		}
	}

	/** Button click listener
	 * @author IT Mill Ltd.
		 * @version @VERSION@
		 * @since 3.0
	 */
	public interface ClickListener {

		/** Button has been pressed.
		 * @param event Button click event.
		 */
		public void buttonClick(ClickEvent event);
	}

	/** Add button click listener
	* @param listener Listener to be added.
	*/
	public void addListener(ClickListener listener) {
		addListener(ClickEvent.class, listener, BUTTON_CLICK_METHOD);
	}

	/** Remove button click listener
	* @param listener Listener to be removed.
	*/
	public void removeListener(ClickListener listener) {
		removeListener(ClickEvent.class, listener, BUTTON_CLICK_METHOD);
	}

	/** Emit options change event. */
	protected void fireClick() {
		fireEvent(new Button.ClickEvent(this));
	}

	/** Adds an action handler.
	 * @see com.itmill.toolkit.event.Action.Container#addActionHandler(Action.Handler)
	 */
	public void addActionHandler(Action.Handler actionHandler) {

		if (actionHandler != null) {

			if (actionHandlers == null) {
				actionHandlers = new LinkedList();
				actionMapper = new KeyMapper();
			}

             if(!actionHandlers.contains(actionHandler)){ 
                 actionHandlers.add(actionHandler); 
                 requestRepaint(); 
             }
		}
	}

	/** Removes an action handler.
	 * @see com.itmill.toolkit.event.Action.Container#removeActionHandler(Action.Handler)
	 */
	public void removeActionHandler(Action.Handler actionHandler) {

		if (actionHandlers != null && actionHandlers.contains(actionHandler)) {
            
            actionHandlers.remove(actionHandler);
            
            if (actionHandlers.isEmpty()) {
                actionHandlers = null;
                actionMapper = null;
            }

            requestRepaint();        
		}
	}
}
