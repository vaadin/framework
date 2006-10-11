/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.enably.tk.ui;

import com.enably.tk.Application;
import com.enably.tk.event.EventRouter;
import com.enably.tk.event.MethodEventSource;
import com.enably.tk.terminal.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Method;

/** An abstract class that defines default implementation for the
 * {@link Component} interface. Basic UI components that are not derived
 * from an external component can inherit this class to easily qualify as a
 * MillStone component. Most components in the MillStone base UI package do
 * just that.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public abstract class AbstractComponent
	implements Component, MethodEventSource {

	/* Private members ************************************************* */

	/** Look-and-feel style of the component. */
	private String style;

	/** Caption text. */
	private String caption;
    
    /** Application specific data object. */
    private Object applicationData;

	/** Icon to be shown together with caption. */
	private Resource icon;

	/** Is the component enable (its normal usage is allowed). */
	private boolean enabled = true;

	/** Is the component visible (it is rendered). */
	private boolean visible = true;

	/** Is the component read-only ? */
	private boolean readOnly = false;

	/** Description of the usage (XML). */
	private String description = null;

	/** The container this component resides in. */
	private Component parent = null;

	/** The EventRouter used for the MillStone event model. */
	private EventRouter eventRouter = null;

	/** The internal error message of the component. */
	private ErrorMessage componentError = null;

	/** List of event variable change event handling dependencies */
	private Set dependencies = null;

	/** Immediate mode: if true, all variable changes are required to be sent
	 * from the terminal immediately
	 */
	private boolean immediate = false;

	/** Debug mode: if true, the component may output visual debug information
	 */
	private boolean debug = false;

	/** Locale of this component. */
	private Locale locale;

	/** List of repaint request listeners or null if not listened at all */
	private LinkedList repaintRequestListeners = null;

	/** Are all the repaint listeners notified about recent changes ? */
	private boolean repaintRequestListenersNotified = false;

	/* Constructor ***************************************************** */

	/** Constructs a new Component */
	public AbstractComponent() {
	}

	/* Get/Set component properties ************************************ */

	/** Gets the UIDL tag corresponding to the component.
	 * 
	 * @return component's UIDL tag as <code>String</code>
	 */
	public abstract String getTag();

	/* Gets the component's style.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public String getStyle() {
		return this.style;
	}

	/* Sets the component's style.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void setStyle(String style) {
		this.style = style;
		requestRepaint();
	}

	/* Get's the component's caption.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public String getCaption() {
		return this.caption;
	}

	/** Sets the component's caption <code>String</code>. Caption is the
	 * visible name of the component. This method will trigger a
	 * {@link com.enably.tk.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param caption new caption <code>String</code> for the component
	 */
	public void setCaption(String caption) {
		this.caption = caption;
		requestRepaint();
	}

	/* Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
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

	/** Sets the locale of this component.
	 * @param locale The locale to become this component's locale.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/* Gets the component's icon resource.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Resource getIcon() {
		return this.icon;
	}

	/** Sets the component's icon.  This method will trigger a
	  * {@link com.enably.tk.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	  * 
	  * @param icon the icon to be shown with the component's caption
	  */
	public void setIcon(Resource icon) {
		this.icon = icon;
		requestRepaint();
	}

	/* Tests if the component is enabled or not.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isEnabled() {
		return this.enabled && isVisible();
	}

	/* Enables or disables the component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			requestRepaint();
		}
	}

	/* Tests if the component is in the immediate mode.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isImmediate() {
		return immediate;
	}

	/** Sets the component's immediate mode to the specified status. This
	 * method will trigger a 
	 * {@link com.enably.tk.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 * 
	 * @param immediate boolean value specifying if the component should
	 * be in the immediate mode after the call.
	 * @see Component#isImmediate()
	 */
	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
		requestRepaint();
	}

	/* Tests if the component is visible.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/* Sets the components visibility.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
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

	/** <p>Gets the component's description. The description can be used to
	 * briefly describe the state of the component to the user. The
	 * description string may contain certain XML tags:</p>
	 * 
	 * <p><table border=1>
	 * <tr><td width=120><b>Tag</b></td>
	 *     <td width=120><b>Description</b></td>
	 *     <td width=120><b>Example</b></td>
	 * </tr>
	 * <tr><td>&lt;b></td>
	 *     <td>bold</td>
	 *     <td><b>bold text</b></td>
	 * </tr>
	 * <tr><td>&lt;i></td>
	 *     <td>italic</td>
	 *     <td><i>italic text</i></td>
	 * </tr>
	 * <tr><td>&lt;u></td>
	 *     <td>underlined</td>
	 *     <td><u>underlined text</u></td>
	 * </tr>
	 * <tr><td>&lt;br></td>
	 *     <td>linebreak</td>
	 *     <td>N/A</td>
	 * </tr>
	 * <tr><td>&lt;ul><br>&lt;li>item1<br>&lt;li>item1<br>&lt;/ul></td>
	 *      <td>item list</td>
	 *      <td><ul><li>item1 <li>item2</ul></td>
	 * </tr>
	 * </table></p>
	 * 
	 * <p>These tags may be nested.</p>
	 * 
	 * @return component's description <code>String</code>
	 */
	public String getDescription() {
		return this.description;
	}

	/** Sets the component's description. See {@link #getDescription()} for
	 * more information on what the description is. This method will trigger
	 * a {@link com.enably.tk.terminal.Paintable.RepaintRequestEvent RepaintRequestEvent}.
	 *
	 * @param description new description string for the component
	 */
	public void setDescription(String description) {
		this.description = description;
		requestRepaint();
	}

	/* Gets the component's parent component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Component getParent() {
		return this.parent;
	}

	/* Set the parent component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
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

	/** Get the error message for this component.
	 * 
	 * @return ErrorMessage containing the description of the error state
	 * of the component or null, if the component contains no errors. Extending
	 * classes should override this method if they support other error message
	 * types such as validation errors or buffering errors. The returned error
	 * message contains information about all the errors.
	 */
	public ErrorMessage getErrorMessage() {
		return this.componentError;
	}

	/** Gets the component's error message. 
	 * @link Terminal.ErrorMessage#ErrorMessage(String, int)
	 *
	 * @return component's error message
	 */
	public ErrorMessage getComponentError() {
		return this.componentError;
	}

	/** Sets the component's error message. The message may contain certain
	 * XML tags, for more information see
	 * @link Component.ErrorMessage#ErrorMessage(String, int)
	 * 
	 * @param errorMessage new <code>ErrorMessage</code> of the component
	 */
	public void setComponentError(ErrorMessage componentError) {
		this.componentError = componentError;
		fireComponentErrorEvent();
		requestRepaint();
	}

	/* Tests if the component is in read-only mode.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/* Set the component's read-only mode.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		requestRepaint();
	}

	/* Get the parent window of the component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Window getWindow() {
		if (parent == null)
			return null;
		else
			return parent.getWindow();
	}

	/* Notify the component that it's attached to a window.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void attach() {
	}

	/* Detach the component from application. 
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void detach() {
	}

	/* Get the parent application of the component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
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

	/* Paints the component into a UIDL stream. 
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public final void paint(PaintTarget target) throws PaintException {

		if (!target.startTag(this, this.getTag())) {
			if (getStyle() != null && getStyle().length() > 0)
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
				paintContent(target);

				String desc = getDescription();
				if (desc != null && description.length() > 0) {
					target.startTag("description");
					target.addUIDL(getDescription());
					target.endTag("description");
				}

				ErrorMessage error = getErrorMessage();
				if (error != null)
					error.paint(target);
			}
		}
		target.endTag(this.getTag());

		repaintRequestListenersNotified = false;
	}

	/** Paints any needed component-specific things to the given UIDL
	 * stream. The more general {@link #paint(PaintTarget)} method handles
	 * all general attributes common to all components, and it calls this
	 * method to paint any component-specific attributes to the UIDL stream.
	 * 
	 * @param target target UIDL stream where the component should paint
	 * itself to
	 * @throws PaintException if the operation failed
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

	/** Fire repaint request event */
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
						(
							(
								RepaintRequestListener) listeners[i])
									.repaintRequested(
							event);
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

	/* Invoked when the value of a variable has changed.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void changeVariables(Object source, Map variables) {

	}

	/* Adds a variable-change dependency to this component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void dependsOn(VariableOwner depended) {

		// Assure that the list exists
		if (dependencies == null)
			dependencies = new HashSet();

		// Add to the list of dependencies
		if (depended != null)
			dependencies.add(depended);
	}

	/* Removes a dependency from the component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void removeDirectDependency(VariableOwner depended) {

		// Remove the listener if necessary
		if (dependencies != null && depended != null)
			dependencies.remove(depended);
	}

	/* Gets the set of depended components.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Set getDirectDependencies() {
		return dependencies;
	}

	/* General event framework *************************************** */

	private static final Method COMPONENT_EVENT_METHOD;

	static {
		try {
			COMPONENT_EVENT_METHOD =
				Component.Listener.class.getDeclaredMethod(
					"componentEvent",
					new Class[] { Component.Event.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			e.printStackTrace();
			throw new java.lang.RuntimeException();
		}
	}

	/** <p>Registers a new listener with the specified activation method to
	 * listen events generated by this component. If the activation method
	 * does not have any arguments the event object will not be passed to it
	 * when it's called.</p>
	 * 
	 * <p>For more information on the MillStone inheritable event mechanism
	 * see the 
	 * {@link com.enably.tk.event com.enably.tk.event package documentation}.</p>
	 * 
	 * @param eventType type of the listened event. Events of this type or
	 * its subclasses activate the listener.
	 * @param object the object instance who owns the activation method
	 * @param method the activation method
	 * @throws java.lang.IllegalArgumentException unless <code>method</code>
	 * has exactly one match in <code>object</code>
	 */
	public void addListener(Class eventType, Object object, Method method) {
		if (eventRouter == null)
			eventRouter = new EventRouter();
		eventRouter.addListener(eventType, object, method);
	}

	/** <p>Registers a new listener with the specified activation method to
	 * listen events generated by this component. If the activation method
	 * does not have any arguments the event object will not be passed to it
	 * when it's called.</p>
	 * 
	 * <p>This version of <code>addListener</code> gets the name of the
	 * activation method as a parameter. The actual method is reflected from
	 * <code>object</code>, and unless exactly one match is found,
	 * <code>java.lang.IllegalArgumentException</code> is thrown.</p>
	 * 
	 * <p>For more information on the MillStone inheritable event mechanism
	 * see the 
	 * {@link com.enably.tk.event com.enably.tk.event package documentation}.</p>
	 * 
	 * @param eventType type of the listened event. Events of this type or
	 * its subclasses activate the listener.
	 * @param object the object instance who owns the activation method
	 * @param methodName the name of the activation method
	 * @throws java.lang.IllegalArgumentException unless <code>method</code>
	 * has exactly one match in <code>object</code>
	 */
	public void addListener(
		Class eventType,
		Object object,
		String methodName) {
		if (eventRouter == null)
			eventRouter = new EventRouter();
		eventRouter.addListener(eventType, object, methodName);
	}

	/** Removes all registered listeners matching the given parameters.
	 * Since this method receives the event type and the listener object as
	 * parameters, it will unregister all <code>object</code>'s methods that
	 * are registered to listen to events of type <code>eventType</code>
	 * generated by this component.
	 * 
	 * <p>For more information on the MillStone inheritable event mechanism
	 * see the 
	 * {@link com.enably.tk.event com.enably.tk.event package documentation}.</p>
	 * 
	 * @param eventType exact event type the <code>object</code> listens to
	 * @param target target object that has registered to listen to events
	 * of type <code>eventType</code> with one or more methods
	 */
	public void removeListener(Class eventType, Object target) {
		if (eventRouter != null)
			eventRouter.removeListener(eventType, target);
	}

	/** Removes one registered listener method. The given method owned by
	 * the given object will no longer be called when the specified events
	 * are generated by this component.
	 * 
	 * <p>For more information on the MillStone inheritable event mechanism
	 * see the 
	 * {@link com.enably.tk.event com.enably.tk.event package documentation}.</p>
	 * 
	 * @param eventType exact event type the <code>object</code> listens to
	 * @param target target object that has registered to listen to events
	 * of type <code>eventType</code> with one or more methods
	 * @param method the method owned by <code>target</code> that's
	 * registered to listen to events of type <code>eventType</code>
	 */
	public void removeListener(Class eventType, Object target, Method method) {
		if (eventRouter != null)
			eventRouter.removeListener(eventType, target, method);
	}

	/** <p>Removes one registered listener method. The given method owned by
	 * the given object will no longer be called when the specified events
	 * are generated by this component.</p>
	 * 
	 * <p>This version of <code>removeListener</code> gets the name of the
	 * activation method as a parameter. The actual method is reflected from
	 * <code>target</code>, and unless exactly one match is found,
	 * <code>java.lang.IllegalArgumentException</code> is thrown.</p>
	 * 
	 * <p>For more information on the MillStone inheritable event mechanism
	 * see the 
	 * {@link com.enably.tk.event com.enably.tk.event package documentation}.</p>
	 * 
	 * @param eventType exact event type the <code>object</code> listens to
	 * @param target target object that has registered to listen to events
	 * of type <code>eventType</code> with one or more methods
	 * @param methodName name of the method owned by <code>target</code>
	 * that's registered to listen to events of type <code>eventType</code>
	 */
	public void removeListener(
		Class eventType,
		Object target,
		String methodName) {
		if (eventRouter != null)
			eventRouter.removeListener(eventType, target, methodName);
	}

	/** Send event to all listeners
	 * @param event Event to be sent to all listeners
	 */
	protected void fireEvent(Component.Event event) {

		if (eventRouter != null)
			eventRouter.fireEvent(event);

	}

	/* Component event framework *************************************** */

	/* Registers a new listener to listen events generated by this
	 * component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void addListener(Component.Listener listener) {

		if (eventRouter == null)
			eventRouter = new EventRouter();

		eventRouter.addListener(
			Component.Event.class,
			listener,
			COMPONENT_EVENT_METHOD);
	}

	/* Removes a previously registered listener from this component.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void removeListener(Component.Listener listener) {

		if (eventRouter != null) {
			eventRouter.removeListener(
				Component.Event.class,
				listener,
				COMPONENT_EVENT_METHOD);
		}
	}

	/** Emits a component event. It is transmitted to all registered
	 * listeners interested in such events.
	 */
	protected void fireComponentEvent() {
		fireEvent(new Component.Event(this));
	}

	/** Emits a component error event. It is transmitted to all registered
	 * listeners interested in such events.
	 */
	protected void fireComponentErrorEvent() {
		fireEvent(new Component.ErrorEvent(this.getComponentError(),this));
	}

	/** Sets application specific data object.
     * 
     * @param data Application specific data.
     * @since 3.1
	 */
    public void setData(Object data) {
        this.applicationData = data;
    }
    
    /** Gets application specific data.
     * 
     * @return Application specific data set with setData function.
     * @since 3.1
     */
    public Object getData() {
        return this.applicationData;
    }
}