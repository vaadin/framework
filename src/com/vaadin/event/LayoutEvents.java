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
     * 
     */
    public static class LayoutClickEvent extends ClickEvent {

        private Component childComponent;

        public LayoutClickEvent(Component source,
                MouseEventDetails mouseEventDetails, Component childComponent) {
            super(source, mouseEventDetails);
            this.childComponent = childComponent;
        }

        public Component getChildComponent() {
            return childComponent;
        }

    }
}