/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Locale;

import com.vaadin.Application;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.VariableOwner;

/**
 * The top-level component interface which must be implemented by all UI
 * components that use IT Mill Toolkit.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Component extends Paintable, VariableOwner, Sizeable,
        Serializable {

    /**
     * Gets style for component. Multiple styles are joined with spaces.
     * 
     * @return the component's styleValue of property style.
     */
    public String getStyleName();

    /**
     * Sets and replaces all previous style names of the component. This method
     * will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param style
     *            the new style of the component.
     */
    public void setStyleName(String style);

    /**
     * Adds style name to component. Handling additional style names is terminal
     * specific, but in web browser environment they will most likely become CSS
     * classes as given on server side.
     * 
     * This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param style
     *            the new style to be added to the component
     */
    public void addStyleName(String style);

    /**
     * Removes given style name from component.
     * 
     * @param style
     *            the style to be removed
     */
    public void removeStyleName(String style);

    /**
     * <p>
     * Tests if the component is enabled or not. All the variable change events
     * are blocked from disabled components. Also the component should visually
     * indicate that it is disabled (by shading the component for example). All
     * hidden (isVisible() == false) components must return false.
     * </p>
     * 
     * <p>
     * <b>Note</b> The component is considered disabled if it's parent is
     * disabled.
     * </p>
     * 
     * <p>
     * Components should be enabled by default.
     * </p>
     * 
     * @return <code>true</code> if the component, and it's parent, is enabled
     *         <code>false</code> otherwise.
     * @see VariableOwner#isEnabled()
     */
    public boolean isEnabled();

    /**
     * Enables or disables the component. Being enabled means that the component
     * can be edited. This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * <p>
     * <b>Note</b> that after enabling a component, {@link #isEnabled()} might
     * still return false if the parent is disabled.
     * </p>
     * 
     * <p>
     * <b>Also note</b> that if the component contains child-components, it
     * should recursively call requestRepaint() for all descendant components.
     * </p>
     * 
     * @param enabled
     *            the boolean value specifying if the component should be
     *            enabled after the call or not
     */
    public void setEnabled(boolean enabled);

    /**
     * Tests the components visibility. Visibility defines if the component is
     * drawn when updating UI. Default is <code>true</code>.
     * 
     * <p>
     * <b>Note</b> that to return true, this component and all its parents must
     * be visible.
     * 
     * <p>
     * <b>Also note</b> that this method does not check if component is attached
     * and shown to user. Component and all its parents may be visible, but not
     * necessary attached to application. To test if component will be drawn,
     * check its visibility and that {@link Component#getApplication()} does not
     * return <code>null</code>.
     * 
     * @return <code>true</code> if the component is visible in the UI,
     *         <code>false</code> if not
     */
    public boolean isVisible();

    /**
     * Sets this components visibility status. Visibility defines if the
     * component is shown in the UI or not.
     * <p>
     * <b>Note</b> that to be shown in UI this component and all its parents
     * must be visible.
     * 
     * @param visible
     *            the boolean value specifying if the component should be
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
     * <code>parent</code> is set <code>null</code>, but the component was in
     * the application.
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
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
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
     * Sets the component's caption <code>String</code>. Caption is the visible
     * name of the component. This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param caption
     *            the new caption <code>String</code> for the component.
     */
    public void setCaption(String caption);

    /**
     * Gets the component's icon. A component may have a graphical icon
     * associated with it, this method retrieves it if it is defined.
     * 
     * @return the component's icon or <code>null</code> if it not defined.
     */
    public Resource getIcon();

    /**
     * Sets the component's icon. This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param icon
     *            the icon to be shown with the component's caption.
     */
    public void setIcon(Resource icon);

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
     * <code>getWindow</code> methods might return <code>null</code> before this
     * method is called.
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

    /* Component event framework */

    /**
     * Superclass of all component originated <code>Event</code>s.
     */
    public class Event extends EventObject {

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
    public interface Listener extends EventListener, Serializable {

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
    @SuppressWarnings("serial")
    public class ErrorEvent extends Event {

        private final ErrorMessage message;

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
            return message;
        }
    }

    /**
     * Listener interface for receiving <code>Component.Errors</code>s.
     */
    public interface ErrorListener extends EventListener, Serializable {

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
    public interface Focusable extends Component {

        /**
         * Sets the focus to this component.
         */
        public void focus();

        /**
         * Gets the Tabulator index of this Focusable component.
         * 
         * @return tab index set for this Focusable component
         */
        public int getTabIndex();

        /**
         * Sets the tab index of this field. The tab index property is used to
         * specify the natural tab order of fields.
         * 
         * @param tabIndex
         *            the tab order of this component. Indexes usually start
         *            from 1. Negative value means that field is not wanted to
         *            tabbing sequence.
         */
        public void setTabIndex(int tabIndex);

    }
}
