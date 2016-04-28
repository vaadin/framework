/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.event;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.shared.Connector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.util.ReflectTools;

public interface LayoutEvents {

    public interface LayoutClickListener extends ConnectorEventListener {

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
        public void addLayoutClickListener(LayoutClickListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #addLayoutClickListener(LayoutClickListener)}
         **/
        @Deprecated
        public void addListener(LayoutClickListener listener);

        /**
         * Removes an LayoutClickListener.
         * 
         * @param listener
         *            LayoutClickListener to be removed
         */
        public void removeLayoutClickListener(LayoutClickListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #removeLayoutClickListener(LayoutClickListener)}
         **/
        @Deprecated
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

        public static LayoutClickEvent createEvent(ComponentContainer layout,
                MouseEventDetails mouseDetails, Connector clickedConnector) {
            Component clickedComponent = (Component) clickedConnector;
            Component childComponent = clickedComponent;
            while (childComponent != null
                    && childComponent.getParent() != layout) {
                childComponent = childComponent.getParent();
            }

            return new LayoutClickEvent(layout, mouseDetails, clickedComponent,
                    childComponent);
        }
    }
}
