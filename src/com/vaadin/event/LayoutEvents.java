/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.event;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

public interface LayoutEvents {

    public interface LayoutClickListener extends ComponentEventListener {

        public static final Method clickMethod = ReflectTools.findMethod(
                LayoutClickListener.class, "layoutClick",
                LayoutClickEvent.class);

        /**
         * Layout has been clicked
         * 
         * @param event
         *            Component click event.
         */
        public void layoutClick(LayoutClickEvent event);
    }

    /**
     * The interface for adding and removing <code>LayoutClickEvent</code>
     * listeners. By implementing this interface a class explicitly announces
     * that it will generate a <code>LayoutClickEvent</code> when a component
     * inside it is clicked and a <code>LayoutClickListener</code> is
     * registered.
     * <p>
     * Note: The general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
     * 
     * @since 6.5.2
     * @see LayoutClickListener
     * @see LayoutClickEvent
     */
    public interface LayoutClickNotifier extends Serializable {
        /**
         * Add a click listener to the layout. The listener is called whenever
         * the user clicks inside the layout. An event is also triggered when
         * the click targets a component inside a nested layout or Panel,
         * provided the targeted component does not prevent the click event from
         * propagating. A caption is not considered part of a component.
         * 
         * The child component that was clicked is included in the
         * {@link LayoutClickEvent}.
         * 
         * Use {@link #removeListener(LayoutClickListener)} to remove the
         * listener.
         * 
         * @param listener
         *            The listener to add
         */
        public void addListener(LayoutClickListener listener);

        /**
         * Removes an LayoutClickListener.
         * 
         * @param listener
         *            LayoutClickListener to be removed
         */
        public void removeListener(LayoutClickListener listener);
    }

    /**
     * An event fired when the layout has been clicked. The event contains
     * information about the target layout (component) and the child component
     * that was clicked. If no child component was found it is set to null.
     */
    public static class LayoutClickEvent extends ClickEvent {

        private final Component clickedComponent;
        private final Component childComponent;

        public LayoutClickEvent(Component source,
                MouseEventDetails mouseEventDetails,
                Component clickedComponent, Component childComponent) {
            super(source, mouseEventDetails);
            this.clickedComponent = clickedComponent;
            this.childComponent = childComponent;
        }

        /**
         * Returns the component that was clicked, which is somewhere inside the
         * parent layout on which the listener was registered.
         * 
         * For the direct child component of the layout, see
         * {@link #getChildComponent()}.
         * 
         * @return clicked {@link Component}, null if none found
         */
        public Component getClickedComponent() {
            return clickedComponent;
        }

        /**
         * Returns the direct child component of the layout which contains the
         * clicked component.
         * 
         * For the clicked component inside that child component of the layout,
         * see {@link #getClickedComponent()}.
         * 
         * @return direct child {@link Component} of the layout which contains
         *         the clicked Component, null if none found
         */
        public Component getChildComponent() {
            return childComponent;
        }

    }
}