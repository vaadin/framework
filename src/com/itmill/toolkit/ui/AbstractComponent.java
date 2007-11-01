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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.event.EventRouter;
import com.itmill.toolkit.event.MethodEventSource;
import com.itmill.toolkit.terminal.ErrorMessage;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.VariableOwner;

/**
 * An abstract class that defines default implementation for the
 * {@link Component} interface. Basic UI components that are not derived from an
 * external component can inherit this class to easily qualify as a IT Mill
 * Toolkit component. Most components in the toolkit do just that.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public abstract class AbstractComponent implements Component, MethodEventSource {

	/* Private members ************************************************* */

	/**
	 * Style names.
	 */
	private ArrayList styles;

	/**
	 * Caption text.
	 */
	private String caption;

	/**
	 * Application specific data object.
	 */
	private Object applicationData;

	/**
	 * Icon to be shown together with caption.
	 */
	private Resource icon;

	/**
	 * Is the component enable (its normal usage is allowed).
	 */
	private boolean enabled = true;

	/**
	 * Is the component visible (it is rendered).
	 */
	private boolean visible = true;

	/**
	 * Is the component read-only ?
	 */
	private boolean readOnly = false;

	/**
	 * Description of the usage (XML).
	 */
	private String description = null;

	/**
	 * The container this component resides in.
	 */
	private Component parent = null;

	/**
	 * The EventRouter used for the event model.
	 */
	private EventRouter eventRouter = null;

	/**
	 * The internal error message of the component.
	 */
	private ErrorMessage componentError = null;

	/**
	 * Immediate mode: if true, all variable changes are required to be sent
	 * from the terminal immediately.
	 */
	private boolean immediate = false;

	/**
	 * Locale of this component.
	 */
	private Locale locale;

	/**
	 * List of repaint request listeners or null if not listened at all.
	 */
	private LinkedList repaintRequestListeners = null;

	/**
	 * Are all the repaint listeners notified about recent changes ?
	 */
	private boolean repaintRequestListenersNotified = false;

	/* Constructor ***************************************************** */

	/**
	 * Constructs a new Component.
	 */
	public AbstractComponent() {
	}

	/* Get/Set component properties ************************************ */

	/**
	 * Gets the UIDL tag corresponding to the component.
	 * 
	 * @return the component's UIDL tag as <code>String</code>
	 */
	public abstract String getTag();

	/**
	 * Gets style for component. Multiple styles are joined with spaces.
	 * 
	 * @return the component's styleValue of property style.
	 * @deprecated Use getStyleName() instead; renamed for consistency and to
	 *             indicate that "style" should not be used to switch client
	 *             side implementation, only to style the component.
	 */
	public String getStyle() {
		return getStyleName();
	}
	
	/**
	 * Sets and replaces all previous style names of the component. This method
	 * will trigger a
	 * {@link com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param style
	 *            the new style of the component.
	 * @deprecated Use setStyleName() instead; renamed for consistency and to
	 *             indicate that "style" should not be used to switch client
	 *             side implementation, only to style the component.
	 */
	public void setStyle(String style) {
		setStyleName(style);
	}
	
	/*
	 * Gets the component's style. Don't add a JavaDoc comment here, we use the
	 * default documentation from implemented interface.
	 */
	public String getStyleName() {
		String s = "";
		if (styles != null) {
			for (Iterator it = styles.iterator(); it.hasNext();) {
				s += (String) it.next();
				if (it.hasNext())
					s += " ";
			}
		}
		return s;
	}

	/*
	 * Sets the component's style. Don't add a JavaDoc comment here, we use the
	 * default documentation from implemented interface.
	 */
	public void setStyleName(String style) {
		if (style == null || "".equals(style)) {
			styles = null;
			requestRepaint();
			return;
		}
		if (this.styles == null) {
			styles = new ArrayList();
		}
		styles.clear();
		styles.add(style);
		requestRepaint();
	}

	public void addStyleName(String style) {
		if (style == null || "".equals(style))
			return;
		if (this.styles == null) {
			styles = new ArrayList();
		}
		if (!styles.contains(style)) {
			this.styles.add(style);
			requestRepaint();
		}
	}

	public void removeStyleName(String style) {
		styles.remove(style);
		requestRepaint();
	}

	/*
	 * Get's the component's caption. Don't add a JavaDoc comment here, we use
	 * the default documentation from implemented interface.
	 */
	public String getCaption() {
		return this.caption;
	}

	/**
	 * Sets the component's caption <code>String</code>. Caption is the
	 * visible name of the component. This method will trigger a
	 * {@link com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param caption
	 *            the new caption <code>String</code> for the component.
	 */
	public void setCaption(String caption) {
		this.caption = caption;
		requestRepaint();
	}

	/*
	 * Don't add a JavaDoc comment here, we use the default documentation from
	 * implemented interface.
	 */
	public Locale getLocale() {
		if (this.locale != null)
			return this.locale;
		if (this.parent != null)
			return parent.getLocale();
		Application app = this.getApplication();
		if (app != null)
			return app.getLocale();
		return null;
	}

	/**
	 * Sets the locale of this component.
	 * 
	 * @param locale
	 *            the locale to become this component's locale.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/*
	 * Gets the component's icon resource. Don't add a JavaDoc comment here, we
	 * use the default documentation from implemented interface.
	 */
	public Resource getIcon() {
		return this.icon;
	}

	/**
	 * Sets the component's icon. This method will trigger a
	 * {@link com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param icon
	 *            the icon to be shown with the component's caption.
	 */
	public void setIcon(Resource icon) {
		this.icon = icon;
		requestRepaint();
	}

	/*
	 * Tests if the component is enabled or not. Don't add a JavaDoc comment
	 * here, we use the default documentation from implemented interface.
	 */
	public boolean isEnabled() {
		return this.enabled && isVisible();
	}

	/*
	 * Enables or disables the component. Don't add a JavaDoc comment here, we
	 * use the default documentation from implemented interface.
	 */
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			requestRepaint();
		}
	}

	/*
	 * Tests if the component is in the immediate mode. Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public boolean isImmediate() {
		return immediate;
	}

	/**
	 * Sets the component's immediate mode to the specified status. This method
	 * will trigger a
	 * {@link com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param immediate
	 *            the boolean value specifying if the component should be in the
	 *            immediate mode after the call.
	 * @see Component#isImmediate()
	 */
	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
		requestRepaint();
	}

	/*
	 * Tests if the component is visible. Don't add a JavaDoc comment here, we
	 * use the default documentation from implemented interface.
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/*
	 * Sets the components visibility. Don't add a JavaDoc comment here, we use
	 * the default documentation from implemented interface.
	 */
	public void setVisible(boolean visible) {

		if (this.visible != visible) {
			this.visible = visible;
			// Instead of requesting repaint normally we
			// fire the event directly to assure that the
			// event goes through event in the component might
			// now be invisible
			fireRequestRepaintEvent(null);
		}
	}

	/**
	 * <p>
	 * Gets the component's description. The description can be used to briefly
	 * describe the state of the component to the user. The description string
	 * may contain certain XML tags:
	 * </p>
	 * 
	 * <p>
	 * <table border=1>
	 * <tr>
	 * <td width=120><b>Tag</b></td>
	 * <td width=120><b>Description</b></td>
	 * <td width=120><b>Example</b></td>
	 * </tr>
	 * <tr>
	 * <td>&lt;b></td>
	 * <td>bold</td>
	 * <td><b>bold text</b></td>
	 * </tr>
	 * <tr>
	 * <td>&lt;i></td>
	 * <td>italic</td>
	 * <td><i>italic text</i></td>
	 * </tr>
	 * <tr>
	 * <td>&lt;u></td>
	 * <td>underlined</td>
	 * <td><u>underlined text</u></td>
	 * </tr>
	 * <tr>
	 * <td>&lt;br></td>
	 * <td>linebreak</td>
	 * <td>N/A</td>
	 * </tr>
	 * <tr>
	 * <td>&lt;ul><br>
	 * &lt;li>item1<br>
	 * &lt;li>item1<br>
	 * &lt;/ul></td>
	 * <td>item list</td>
	 * <td>
	 * <ul>
	 * <li>item1
	 * <li>item2
	 * </ul>
	 * </td>
	 * </tr>
	 * </table>
	 * </p>
	 * 
	 * <p>
	 * These tags may be nested.
	 * </p>
	 * 
	 * @return component's description <code>String</code>
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the component's description. See {@link #getDescription()} for more
	 * information on what the description is. This method will trigger a
	 * {@link com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param description
	 *            the new description string for the component.
	 */
	public void setDescription(String description) {
		this.description = description;
		requestRepaint();
	}

	/*
	 * Gets the component's parent component. Don't add a JavaDoc comment here,
	 * we use the default documentation from implemented interface.
	 */
	public Component getParent() {
		return this.parent;
	}

	/*
	 * Sets the parent component. Don't add a JavaDoc comment here, we use the
	 * default documentation from implemented interface.
	 */
	public void setParent(Component parent) {

		// If the parent is not changed, dont do nothing
		if (parent == this.parent)
			return;

		// Send detach event if the component have been connected to a window
		if (getApplication() != null) {
			detach();
			this.parent = null;
		}

		// Connect to new parent
		this.parent = parent;

		// Send attach event if connected to a window
		if (getApplication() != null)
			attach();
	}

	/**
	 * Gets the error message for this component.
	 * 
	 * @return ErrorMessage containing the description of the error state of the
	 *         component or null, if the component contains no errors. Extending
	 *         classes should override this method if they support other error
	 *         message types such as validation errors or buffering errors. The
	 *         returned error message contains information about all the errors.
	 */
	public ErrorMessage getErrorMessage() {
		return this.componentError;
	}

	/**
	 * Gets the component's error message.
	 * 
	 * @link Terminal.ErrorMessage#ErrorMessage(String, int)
	 * 
	 * @return the component's error message.
	 */
	public ErrorMessage getComponentError() {
		return this.componentError;
	}

	/**
	 * Sets the component's error message. The message may contain certain XML
	 * tags, for more information see
	 * 
	 * @link Component.ErrorMessage#ErrorMessage(String, int)
	 * 
	 * @param componentError
	 *            the new <code>ErrorMessage</code> of the component.
	 */
	public void setComponentError(ErrorMessage componentError) {
		this.componentError = componentError;
		fireComponentErrorEvent();
		requestRepaint();
	}

	/*
	 * Tests if the component is in read-only mode. Don't add a JavaDoc comment
	 * here, we use the default documentation from implemented interface.
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/*
	 * Sets the component's read-only mode. Don't add a JavaDoc comment here, we
	 * use the default documentation from implemented interface.
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		requestRepaint();
	}

	/*
	 * Gets the parent window of the component. Don't add a JavaDoc comment
	 * here, we use the default documentation from implemented interface.
	 */
	public Window getWindow() {
		if (parent == null)
			return null;
		else
			return parent.getWindow();
	}

	/*
	 * Notify the component that it's attached to a window. Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public void attach() {
		requestRepaint();
	}

	/*
	 * Detach the component from application. Don't add a JavaDoc comment here,
	 * we use the default documentation from implemented interface.
	 */
	public void detach() {
	}

	/*
	 * Gets the parent application of the component. Don't add a JavaDoc comment
	 * here, we use the default documentation from implemented interface.
	 */
	public Application getApplication() {
		if (parent == null)
			return null;
		else
			return parent.getApplication();
	}

	/* Component painting ********************************************** */

	/* Documented in super interface */
	public void requestRepaintRequests() {
		repaintRequestListenersNotified = false;
	}

	/*
	 * Paints the component into a UIDL stream. Don't add a JavaDoc comment
	 * here, we use the default documentation from implemented interface.
	 */
	public final void paint(PaintTarget target) throws PaintException {

		if (!target.startTag(this, this.getTag())
				|| repaintRequestListenersNotified) {

			// Paint the contents of the component

			if (styles != null && styles.size() > 0)
				target.addAttribute("style", getStyle());
			if (isReadOnly())
				target.addAttribute("readonly", true);
			if (!isVisible())
				target.addAttribute("invisible", true);
			if (isImmediate())
				target.addAttribute("immediate", true);
			if (!isEnabled())
				target.addAttribute("disabled", true);
			if (getCaption() != null)
				target.addAttribute("caption", getCaption());
			if (getIcon() != null)
				target.addAttribute("icon", getIcon());

			// Only paint content of visible components.
			if (isVisible()) {
				String desc = getDescription();
				if (desc != null && description.length() > 0) {
					target.addAttribute("description", getDescription());
				}

				paintContent(target);

				ErrorMessage error = getErrorMessage();
				if (error != null)
					error.paint(target);
			}
		} else {

			// Contents have not changed, only cached presentation can be used
			target.addAttribute("cached", true);
		}
		target.endTag(this.getTag());

		repaintRequestListenersNotified = false;
	}

	/**
	 * Paints any needed component-specific things to the given UIDL stream. The
	 * more general {@link #paint(PaintTarget)} method handles all general
	 * attributes common to all components, and it calls this method to paint
	 * any component-specific attributes to the UIDL stream.
	 * 
	 * @param target
	 *            the target UIDL stream where the component should paint itself
	 *            to
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

	}

	/* Documentation copied from interface */
	public void requestRepaint() {

		// The effect of the repaint request is identical to case where a
		// child requests repaint
		childRequestedRepaint(null);
	}

	/* Documentation copied from interface */
	public void childRequestedRepaint(Collection alreadyNotified) {

		// Invisible components do not need repaints
		if (!isVisible())
			return;

		fireRequestRepaintEvent(alreadyNotified);
	}

	/**
	 * Fires the repaint request event.
	 * 
	 * @param alreadyNotified
	 */
	private void fireRequestRepaintEvent(Collection alreadyNotified) {

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
						repaintRequestListenersNotified = true;
					}
				}
			}

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

	/* Component variable changes ************************************** */

	/*
	 * Invoked when the value of a variable has changed. Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public void changeVariables(Object source, Map variables) {

	}

	/* Dependency -framework is deprecated */
	public void dependsOn(VariableOwner depended) {}
	public void removeDirectDependency(VariableOwner depended) {}
	public Set getDirectDependencies() {
		return null;
	}

	/* General event framework *************************************** */

	private static final Method COMPONENT_EVENT_METHOD;

	static {
		try {
			COMPONENT_EVENT_METHOD = Component.Listener.class
					.getDeclaredMethod("componentEvent",
							new Class[] { Component.Event.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			e.printStackTrace();
			throw new java.lang.RuntimeException();
		}
	}

	/**
	 * <p>
	 * Registers a new listener with the specified activation method to listen
	 * events generated by this component. If the activation method does not
	 * have any arguments the event object will not be passed to it when it's
	 * called.
	 * </p>
	 * 
	 * <p>
	 * For more information on the inheritable event mechanism see the
	 * {@link com.itmill.toolkit.event com.itmill.toolkit.event package documentation}.
	 * </p>
	 * 
	 * @param eventType
	 *            the type of the listened event. Events of this type or its
	 *            subclasses activate the listener.
	 * @param object
	 *            the object instance who owns the activation method.
	 * @param method
	 *            the activation method.
	 */
	public void addListener(Class eventType, Object object, Method method) {
		if (eventRouter == null)
			eventRouter = new EventRouter();
		eventRouter.addListener(eventType, object, method);
	}

	/**
	 * <p>
	 * Registers a new listener with the specified activation method to listen
	 * events generated by this component. If the activation method does not
	 * have any arguments the event object will not be passed to it when it's
	 * called.
	 * </p>
	 * 
	 * <p>
	 * This version of <code>addListener</code> gets the name of the
	 * activation method as a parameter. The actual method is reflected from
	 * <code>object</code>, and unless exactly one match is found,
	 * <code>java.lang.IllegalArgumentException</code> is thrown.
	 * </p>
	 * 
	 * <p>
	 * For more information on the inheritable event mechanism see the
	 * {@link com.itmill.toolkit.event com.itmill.toolkit.event package documentation}.
	 * </p>
	 * 
	 * @param eventType
	 *            the type of the listened event. Events of this type or its
	 *            subclasses activate the listener.
	 * @param object
	 *            the object instance who owns the activation method.
	 * @param methodName
	 *            the name of the activation method.
	 */
	public void addListener(Class eventType, Object object, String methodName) {
		if (eventRouter == null)
			eventRouter = new EventRouter();
		eventRouter.addListener(eventType, object, methodName);
	}

	/**
	 * Removes all registered listeners matching the given parameters. Since
	 * this method receives the event type and the listener object as
	 * parameters, it will unregister all <code>object</code>'s methods that
	 * are registered to listen to events of type <code>eventType</code>
	 * generated by this component.
	 * 
	 * <p>
	 * For more information on the inheritable event mechanism see the
	 * {@link com.itmill.toolkit.event com.itmill.toolkit.event package documentation}.
	 * </p>
	 * 
	 * @param eventType
	 *            the exact event type the <code>object</code> listens to.
	 * @param target
	 *            the target object that has registered to listen to events of
	 *            type <code>eventType</code> with one or more methods.
	 */
	public void removeListener(Class eventType, Object target) {
		if (eventRouter != null)
			eventRouter.removeListener(eventType, target);
	}

	/**
	 * Removes one registered listener method. The given method owned by the
	 * given object will no longer be called when the specified events are
	 * generated by this component.
	 * 
	 * <p>
	 * For more information on the inheritable event mechanism see the
	 * {@link com.itmill.toolkit.event com.itmill.toolkit.event package documentation}.
	 * </p>
	 * 
	 * @param eventType
	 *            the exact event type the <code>object</code> listens to.
	 * @param target
	 *            target object that has registered to listen to events of type
	 *            <code>eventType</code> with one or more methods.
	 * @param method
	 *            the method owned by <code>target</code> that's registered to
	 *            listen to events of type <code>eventType</code>.
	 */
	public void removeListener(Class eventType, Object target, Method method) {
		if (eventRouter != null)
			eventRouter.removeListener(eventType, target, method);
	}

	/**
	 * <p>
	 * Removes one registered listener method. The given method owned by the
	 * given object will no longer be called when the specified events are
	 * generated by this component.
	 * </p>
	 * 
	 * <p>
	 * This version of <code>removeListener</code> gets the name of the
	 * activation method as a parameter. The actual method is reflected from
	 * <code>target</code>, and unless exactly one match is found,
	 * <code>java.lang.IllegalArgumentException</code> is thrown.
	 * </p>
	 * 
	 * <p>
	 * For more information on the inheritable event mechanism see the
	 * {@link com.itmill.toolkit.event com.itmill.toolkit.event package documentation}.
	 * </p>
	 * 
	 * @param eventType
	 *            the exact event type the <code>object</code> listens to.
	 * @param target
	 *            the target object that has registered to listen to events of
	 *            type <code>eventType</code> with one or more methods.
	 * @param methodName
	 *            the name of the method owned by <code>target</code> that's
	 *            registered to listen to events of type <code>eventType</code>.
	 */
	public void removeListener(Class eventType, Object target, String methodName) {
		if (eventRouter != null)
			eventRouter.removeListener(eventType, target, methodName);
	}

	/**
	 * Sends the event to all listeners.
	 * 
	 * @param event
	 *            the Event to be sent to all listeners.
	 */
	protected void fireEvent(Component.Event event) {

		if (eventRouter != null)
			eventRouter.fireEvent(event);

	}

	/* Component event framework *************************************** */

	/*
	 * Registers a new listener to listen events generated by this component.
	 * Don't add a JavaDoc comment here, we use the default documentation from
	 * implemented interface.
	 */
	public void addListener(Component.Listener listener) {

		if (eventRouter == null)
			eventRouter = new EventRouter();

		eventRouter.addListener(Component.Event.class, listener,
				COMPONENT_EVENT_METHOD);
	}

	/*
	 * Removes a previously registered listener from this component. Don't add a
	 * JavaDoc comment here, we use the default documentation from implemented
	 * interface.
	 */
	public void removeListener(Component.Listener listener) {

		if (eventRouter != null) {
			eventRouter.removeListener(Component.Event.class, listener,
					COMPONENT_EVENT_METHOD);
		}
	}

	/**
	 * Emits the component event. It is transmitted to all registered listeners
	 * interested in such events.
	 */
	protected void fireComponentEvent() {
		fireEvent(new Component.Event(this));
	}

	/**
	 * Emits the component error event. It is transmitted to all registered
	 * listeners interested in such events.
	 */
	protected void fireComponentErrorEvent() {
		fireEvent(new Component.ErrorEvent(this.getComponentError(), this));
	}

	/**
	 * Sets the application specific data object.
	 * 
	 * @param data
	 *            the Application specific data.
	 * @since 3.1
	 */
	public void setData(Object data) {
		this.applicationData = data;
	}

	/**
	 * Gets the application specific data.
	 * 
	 * @return the Application specific data set with setData function.
	 * @since 3.1
	 */
	public Object getData() {
		return this.applicationData;
	}
}