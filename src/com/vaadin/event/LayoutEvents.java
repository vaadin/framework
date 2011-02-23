/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.event;

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