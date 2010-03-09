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
import com.vaadin.data.Property;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.VariableOwner;

/**
 * {@code Component} is the top-level interface that is and must be implemented
 * by all Vaadin components. {@code Component} is paired with
 * {@link AbstractComponent}, which provides a default implementation for all
 * the methods defined in this interface.
 * 
 * <p>
 * Components are laid out in the user interface hierarchically. The layout is
 * managed by layout components, or more generally by components that implement
 * the {@link ComponentContainer} interface. Such a container is the
 * <i>parent</i> of the contained components.
 * </p>
 * 
 * <p>
 * The {@link #getParent()} method allows retrieving the parent component of a
 * component. While there is a {@link #setParent(Component) setParent()}, you
 * rarely need it as you usually add components with the
 * {@link ComponentContainer#addComponent(Component) addComponent()} method of
 * the {@code ComponentContainer} interface, which automatically sets the
 * parent.
 * </p>
 * 
 * <p>
 * A component becomes <i>attached</i> to an application (and the
 * {@link #attach()} is called) when it or one of its parents is attached to the
 * main window of the application through its containment hierarchy.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Component extends Paintable, VariableOwner, Sizeable,
        Serializable {

    /**
     * Gets all user-defined CSS style names of a component. If the component
     * has multiple style names defined, the return string is a space-separated
     * list of style names. Built-in style names defined in Vaadin or GWT are
     * not returned.
     * 
     * <p>
     * The style names are returned only in the basic form in which they were
     * added; each user-defined style name shows as two CSS style class names in
     * the rendered HTML: one as it was given and one prefixed with the
     * component-specific style name. Only the former is returned.
     * </p>
     * 
     * @return the style name or a space-separated list of user-defined style
     *         names of the component
     * @see #setStyleName(String)
     * @see #addStyleName(String)
     * @see #removeStyleName(String)
     */
    public String getStyleName();

    /**
     * Sets one or more user-defined style names of the component, replacing any
     * previous user-defined styles. Multiple styles can be specified as a
     * space-separated list of style names. The style names must be valid CSS
     * class names and should not conflict with any built-in style names in
     * Vaadin or GWT.
     * 
     * <pre>
     * Label label = new Label(&quot;This text has a lot of style&quot;);
     * label.setStyleName(&quot;myonestyle myotherstyle&quot;);
     * </pre>
     * 
     * <p>
     * Each style name will occur in two versions: one as specified and one that
     * is prefixed with the style name of the component. For example, if you
     * have a {@code Button} component and give it "{@code mystyle}" style, the
     * component will have both "{@code mystyle}" and "{@code v-button-mystyle}"
     * styles. You could then style the component either with:
     * </p>
     * 
     * <pre>
     * .myonestyle {background: blue;}
     * </pre>
     * 
     * <p>
     * or
     * </p>
     * 
     * <pre>
     * .v-button-myonestyle {background: blue;}
     * </pre>
     * 
     * <p>
     * It is normally a good practice to use {@link #addStyleName(String)
     * addStyleName()} rather than this setter, as different software
     * abstraction layers can then add their own styles without accidentally
     * removing those defined in other layers.
     * </p>
     * 
     * <p>
     * This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * </p>
     * 
     * @param style
     *            the new style or styles of the component as a space-separated
     *            list
     * @see #getStyleName()
     * @see #addStyleName(String)
     * @see #removeStyleName(String)
     */
    public void setStyleName(String style);

    /**
     * Adds a style name to component. The style name will be rendered as a HTML
     * class name, which can be used in a CSS definition.
     * 
     * <pre>
     * Label label = new Label(&quot;This text has style&quot;);
     * label.addStyleName(&quot;mystyle&quot;);
     * </pre>
     * 
     * <p>
     * Each style name will occur in two versions: one as specified and one that
     * is prefixed wil the style name of the component. For example, if you have
     * a {@code Button} component and give it "{@code mystyle}" style, the
     * component will have both "{@code mystyle}" and "{@code v-button-mystyle}"
     * styles. You could then style the component either with:
     * </p>
     * 
     * <pre>
     * .mystyle {font-style: italic;}
     * </pre>
     * 
     * <p>
     * or
     * </p>
     * 
     * <pre>
     * .v-button-mystyle {font-style: italic;}
     * </pre>
     * 
     * <p>
     * This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * </p>
     * 
     * @param style
     *            the new style to be added to the component
     * @see #getStyleName()
     * @see #setStyleName(String)
     * @see #removeStyleName(String)
     */
    public void addStyleName(String style);

    /**
     * Removes the given style name from component.
     * 
     * <p>
     * The parameter must be a valid CSS style name. Only user-defined style
     * names added with {@link #addStyleName(String) addStyleName()} or
     * {@link #setStyleName(String) setStyleName()} can be removed; built-in
     * style names defined in Vaadin or GWT can not be removed.
     * </p>
     * 
     * @param style
     *            the style name to be removed
     * @see #getStyleName()
     * @see #setStyleName(String)
     * @see #addStyleName(String)
     */
    public void removeStyleName(String style);

    /**
     * Tests whether the component is enabled or not. A user can not interact
     * with disabled components. Disabled components are rendered in a style
     * that indicates the status, usually in gray color. Children of a disabled
     * component are also disabled. Components are enabled by default.
     * 
     * <p>
     * As a security feature, all variable change events for disabled components
     * are blocked on the server-side.
     * </p>
     * 
     * @return <code>true</code> if the component and its parent are enabled,
     *         <code>false</code> otherwise.
     * @see VariableOwner#isEnabled()
     */
    public boolean isEnabled();

    /**
     * Enables or disables the component. The user can not interact disabled
     * components, which are shown with a style that indicates the status,
     * usually shaded in light gray color. Components are enabled by default.
     * Children of a disabled component are automatically disabled; if a child
     * component is explicitly set as disabled, changes in the disabled status
     * of its parents do not change its status.
     * 
     * <pre>
     * Button enabled = new Button(&quot;Enabled&quot;);
     * enabled.setEnabled(true); // The default
     * layout.addComponent(enabled);
     * 
     * Button disabled = new Button(&quot;Disabled&quot;);
     * disabled.setEnabled(false);
     * layout.addComponent(disabled);
     * </pre>
     * 
     * <p>
     * This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent} for the component and, if it is a
     * {@link ComponentContainer}, for all its children recursively.
     * </p>
     * 
     * @param enabled
     *            a boolean value specifying if the component should be enabled
     *            or not
     */
    public void setEnabled(boolean enabled);

    /**
     * Tests the <i>visibility</i> property of the component.
     * 
     * <p>
     * Visible components are drawn in the user interface, while invisible ones
     * are not. The effect is not merely a cosmetic CSS change, but the entire
     * HTML element will be empty. Making a component invisible through this
     * property can alter the positioning of other components.
     * </p>
     * 
     * <p>
     * A component is visible only if all its parents are also visible. Notice
     * that if a child component is explicitly set as invisible, changes in the
     * visibility status of its parents do not change its status.
     * </p>
     * 
     * <p>
     * This method does not check whether the component is attached (see
     * {@link #attach()}). The component and all its parents may be considered
     * "visible", but not necessarily attached to application. To test if
     * component will actually be drawn, check both its visibility and that
     * {@link #getApplication()} does not return {@code null}.
     * </p>
     * 
     * @return <code>true</code> if the component is visible in the user
     *         interface, <code>false</code> if not
     * @see #setVisible(boolean)
     * @see #attach()
     */
    public boolean isVisible();

    /**
     * Sets the visibility of the component.
     * 
     * <p>
     * Visible components are drawn in the user interface, while invisible ones
     * are not. The effect is not merely a cosmetic CSS change, but the entire
     * HTML element will be empty.
     * </p>
     * 
     * <pre>
     * TextField readonly = new TextField(&quot;Read-Only&quot;);
     * readonly.setValue(&quot;You can't see this!&quot;);
     * readonly.setVisible(false);
     * layout.addComponent(readonly);
     * </pre>
     * 
     * <p>
     * A component is visible only if all of its parents are also visible. If a
     * component is explicitly set to be invisible, changes in the visibility of
     * its parents will not change the visibility of the component.
     * </p>
     * 
     * @param visible
     *            the boolean value specifying if the component should be
     *            visible after the call or not.
     * @see #isVisible()
     */
    public void setVisible(boolean visible);

    /**
     * Gets the parent component of the component.
     * 
     * <p>
     * Components can be nested but a component can have only one parent. A
     * component that contains other components, that is, can be a parent,
     * should usually inherit the {@link ComponentContainer} interface.
     * </p>
     * 
     * @return the parent component
     * @see #setParent(Component)
     */
    public Component getParent();

    /**
     * Sets the parent component of the component.
     * 
     * <p>
     * This method automatically calls {@link #attach()} if the parent becomes
     * attached to the application, regardless of whether it was attached
     * previously. Conversely, if the parent is {@code null} and the component
     * is attached to the application, {@link #detach()} is called for the
     * component.
     * </p>
     * 
     * <p>
     * This method is rarely called directly. The
     * {@link ComponentContainer#addComponent(Component)} method is normally
     * used for adding components to a container and it will call this method
     * implicitly.
     * </p>
     * 
     * <p>
     * It is not possible to change the parent without first setting the parent
     * to {@code null}.
     * </p>
     * 
     * @param parent
     *            the parent component
     * @throws IllegalStateException
     *             if a parent is given even though the component already has a
     *             parent
     */
    public void setParent(Component parent);

    /**
     * Tests whether the component is in the read-only mode. The user can not
     * change the value of a read-only component. As only {@link Field}
     * components normally have a value that can be input or changed by the
     * user, this is mostly relevant only to field components, though not
     * restricted to them.
     * 
     * <p>
     * Notice that the read-only mode only affects whether the user can change
     * the <i>value</i> of the component; it is possible to, for example, scroll
     * a read-only table.
     * </p>
     * 
     * <p>
     * The read-only status affects only the user; the value can still be
     * changed programmatically, for example, with
     * {@link Property#setValue(Object)}.
     * </p>
     * 
     * <p>
     * The method will return {@code true} if the component or any of its
     * parents is in the read-only mode.
     * </p>
     * 
     * @return <code>true</code> if the component or any of its parents is in
     *         read-only mode, <code>false</code> if not.
     * @see #setReadOnly(boolean)
     */
    public boolean isReadOnly();

    /**
     * Sets the read-only mode of the component to the specified mode. The user
     * can not change the value of a read-only component.
     * 
     * <p>
     * As only {@link Field} components normally have a value that can be input
     * or changed by the user, this is mostly relevant only to field components,
     * though not restricted to them.
     * </p>
     * 
     * <p>
     * Notice that the read-only mode only affects whether the user can change
     * the <i>value</i> of the component; it is possible to, for example, scroll
     * a read-only table.
     * </p>
     * 
     * <p>
     * The read-only status affects only the user; the value can still be
     * changed programmatically, for example, with
     * {@link Property#setValue(Object)}.
     * </p>
     * 
     * <p>
     * This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * </p>
     * 
     * @param readOnly
     *            a boolean value specifying whether the component is put
     *            read-only mode or not
     */
    public void setReadOnly(boolean readOnly);

    /**
     * Gets the caption of the component. See {@link #setCaption(String)} for a
     * detailed description of the caption.
     * 
     * @return the caption of the component or {@code null} if the caption is
     *         not set.
     */
    public String getCaption();

    /**
     * Sets the caption of the component.
     * 
     * <p>
     * A <i>caption</i> is an explanatory textual label accompanying a user
     * interface component, usually shown above, left of, or inside the
     * component. <i>Icon</i> (see {@link #setIcon(Resource) setIcon()} is
     * closely related to caption and is usually displayed horizontally before
     * or after it, depending on the component and the containing layout.
     * </p>
     * 
     * <p>
     * The caption can usually also be given as the first parameter to a
     * constructor, though some components do not support it.
     * </p>
     * 
     * <pre>
     * RichTextArea area = new RichTextArea();
     * area.setCaption(&quot;You can edit stuff here&quot;);
     * area.setValue(&quot;&lt;h1&gt;Helpful Heading&lt;/h1&gt;&quot; + &quot;&lt;p&gt;All this is for you to edit.&lt;/p&gt;&quot;);
     * </pre>
     * 
     * <p>
     * The contents of a caption are automatically quoted, so no raw XHTML can
     * be rendered in a caption. The validity of the used character encoding,
     * usually UTF-8, is not checked.
     * </p>
     * 
     * <p>
     * The caption of a component is, by default, managed and displayed by the
     * layout component or component container in which the component is placed.
     * For example, the {@link VerticalLayout} component shows the captions
     * left-aligned above the contained components, while the {@link FormLayout}
     * component shows the captions on the left side of the vertically laid
     * components, with the captions and their associated components
     * left-aligned in their own columns. The {@link CustomComponent} does not
     * manage the caption of its composition root, so if the root component has
     * a caption, it will not be rendered. Some components, such as
     * {@link Button} and {@link Panel}, manage the caption themselves and
     * display it inside the component.
     * </p>
     * 
     * This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}. A reimplementation should call the superclass
     * implementation.
     * 
     * @param caption
     *            the new caption for the component. If the caption is {@code
     *            null}, no caption is shown and it does not normally take any
     *            space
     */
    public void setCaption(String caption);

    /**
     * Gets the icon of the component. See {@link #setIcon(Resource)} for a
     * detailed description of the icon.
     * 
     * @return the icon resource of the component or {@code null} if the
     *         component has no icon
     * @see #setIcon(Resource)
     */
    public Resource getIcon();

    /**
     * Sets the icon of the component.
     * 
     * <p>
     * An icon is an explanatory graphical label accompanying a user interface
     * component, usually shown above, left of, or inside the component. Icon is
     * closely related to caption (see {@link #setCaption(String) setCaption()})
     * and is usually displayed horizontally before or after it, depending on
     * the component and the containing layout.
     * </p>
     * 
     * <p>
     * The image is loaded by the browser from a resource, typically a
     * {@link com.vaadin.terminal.ThemeResource}.
     * </p>
     * 
     * <pre>
     * // Component with an icon from a custom theme
     * TextField name = new TextField(&quot;Name&quot;);
     * name.setIcon(new ThemeResource(&quot;icons/user.png&quot;));
     * layout.addComponent(name);
     * 
     * // Component with an icon from another theme ('runo')
     * Button ok = new Button(&quot;OK&quot;);
     * ok.setIcon(new ThemeResource(&quot;../runo/icons/16/ok.png&quot;));
     * layout.addComponent(ok);
     * </pre>
     * 
     * <p>
     * The icon of a component is, by default, managed and displayed by the
     * layout component or component container in which the component is placed.
     * For example, the {@link VerticalLayout} component shows the icons
     * left-aligned above the contained components, while the {@link FormLayout}
     * component shows the icons on the left side of the vertically laid
     * components, with the icons and their associated components left-aligned
     * in their own columns. The {@link CustomComponent} does not manage the
     * icon of its composition root, so if the root component has an icon, it
     * will not be rendered.
     * </p>
     * 
     * <p>
     * An icon will be rendered inside an HTML element that has the {@code
     * v-icon} CSS style class. The containing layout may enclose an icon and a
     * caption inside elements related to the caption, such as {@code v-caption}
     * .
     * </p>
     * 
     * This method will trigger a
     * {@link com.vaadin.terminal.Paintable.RepaintRequestEvent
     * RepaintRequestEvent}.
     * 
     * @param icon
     *            the icon of the component. If null, no icon is shown and it
     *            does not normally take any space.
     * @see #getIcon()
     * @see #setCaption(String)
     */
    public void setIcon(Resource icon);

    /**
     * Gets the parent window of the component.
     * 
     * <p>
     * If the component is not attached to a window through a component
     * containment hierarchy, <code>null</code> is returned.
     * </p>
     * 
     * <p>
     * The window can be either an application-level window or a sub-window. If
     * the component is itself a window, it returns a reference to itself, not
     * to its containing window (of a sub-window).
     * </p>
     * 
     * @return the parent window of the component or <code>null</code> if it is
     *         not attached to a window or is itself a window
     */
    public Window getWindow();

    /**
     * Gets the application object to which the component is attached.
     * 
     * <p>
     * The method will return {@code null} if the component has not yet been
     * attached to an application.
     * </p>
     * 
     * @return the parent application of the component or <code>null</code>.
     * @see #attach()
     */
    public Application getApplication();

    /**
     * Notifies the component that it is connected to an application.
     * 
     * <p>
     * The caller of this method is {@link #setParent(Component)} if the parent
     * is itself already attached to the application. If not, the parent will
     * call the {@link #attach()} for all its children when it is attached to
     * the application. This method is always called before the component is
     * painted for the first time.
     * </p>
     * 
     * <p>
     * Reimplementing the {@code attach()} method is useful for tasks that need
     * to get a reference to the parent, window, or application object with the
     * {@link #getParent()}, {@link #getWindow()}, and {@link #getApplication()}
     * methods. A component does not yet know these objects in the constructor,
     * so in such case, the methods will return {@code null}. For example, the
     * following is invalid:
     * </p>
     * 
     * <pre>
     * public class AttachExample extends CustomComponent {
     *     public AttachExample() {
     *         // ERROR: We can't access the application object yet.
     *         ClassResource r = new ClassResource(&quot;smiley.jpg&quot;, getApplication());
     *         Embedded image = new Embedded(&quot;Image:&quot;, r);
     *         setCompositionRoot(image);
     *     }
     * }
     * </pre>
     * 
     * <p>
     * Adding a component to an application triggers calling the
     * {@link #attach()} method for the component. Correspondingly, removing a
     * component from a container triggers calling the {@link #detach()} method.
     * If the parent of an added component is already connected to the
     * application, the {@code attach()} is called immediately from
     * {@link #setParent(Component)}.
     * </p>
     * 
     * <pre>
     * public class AttachExample extends CustomComponent {
     *     public AttachExample() {
     *     }
     * 
     *     &#064;Override
     *     public void attach() {
     *         super.attach(); // Must call.
     * 
     *         // Now we know who ultimately owns us.
     *         ClassResource r = new ClassResource(&quot;smiley.jpg&quot;, getApplication());
     *         Embedded image = new Embedded(&quot;Image:&quot;, r);
     *         setCompositionRoot(image);
     *     }
     * }
     * </pre>
     * 
     * <p>
     * The attachment logic is implemented in {@link AbstractComponent}.
     * </p>
     */
    public void attach();

    /**
     * Notifies the component that it is detached from the application.
     * 
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
    public void childRequestedRepaint(
                                      Collection<RepaintRequestListener> alreadyNotified);

    /* Component event framework */

    /**
     * Superclass of all component originated <code>Event</code>s.
     */
    @SuppressWarnings("serial")
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

        /**
         * Gets the Component where the event occurred.
         * 
         * @return the Source of the event.
         */
        public Component getComponent() {
            return (Component) getSource();
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
