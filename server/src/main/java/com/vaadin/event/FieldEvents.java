/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import com.vaadin.server.SerializableConsumer;
import com.vaadin.shared.EventId;
import com.vaadin.shared.Registration;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.util.ReflectTools;

/**
 * Interface that serves as a wrapper for focus and blur events.
 */
public interface FieldEvents {

    /**
     * The interface for adding and removing <code>FocusEvent</code> listeners.
     * By implementing this interface a class explicitly announces that it will
     * generate a <code>FocusEvent</code> when it receives keyboard focus.
     *
     * @since 6.2
     * @see FocusListener
     * @see FocusEvent
     */
    public interface FocusNotifier extends Serializable {
        /**
         * Adds a <code>FocusListener</code> to the Component which gets fired
         * when a <code>Field</code> receives keyboard focus.
         *
         * @param listener
         *            the focus listener to add, not null
         * @return a registration object for removing the listener
         * @see FocusListener
         * @see Registration
         * @since 8.0
         */
        public Registration addFocusListener(FocusListener listener);

    }

    /**
     * The interface for adding and removing <code>BlurEvent</code> listeners.
     * By implementing this interface a class explicitly announces that it will
     * generate a <code>BlurEvent</code> when it loses keyboard focus.
     *
     * @since 6.2
     * @see BlurListener
     * @see BlurEvent
     */
    public interface BlurNotifier extends Serializable {
        /**
         * Adds a <code>BlurListener</code> to the Component which gets fired
         * when a <code>Field</code> loses keyboard focus.
         *
         * @see BlurListener
         * @see Registration
         * @since 8.0
         *
         * @param listener
         *            the blur listener to add, not null
         * @return a registration object for removing the listener
         */
        public Registration addBlurListener(BlurListener listener);

    }

    /**
     * <code>FocusEvent</code> class for holding additional event information.
     * Fired when a <code>Field</code> receives keyboard focus.
     *
     * @since 6.2
     */
    @SuppressWarnings("serial")
    public static class FocusEvent extends Component.Event {

        /**
         * Identifier for event that can be used in {@link EventRouter}
         */
        public static final String EVENT_ID = EventId.FOCUS;

        public FocusEvent(Component source) {
            super(source);
        }
    }

    /**
     * <code>FocusListener</code> interface for listening for
     * <code>FocusEvent</code> fired by a <code>Field</code>.
     *
     * @see FocusEvent
     * @since 6.2
     */
    @FunctionalInterface
    public interface FocusListener extends ConnectorEventListener {

        public static final Method focusMethod = ReflectTools
                .findMethod(FocusListener.class, "focus", FocusEvent.class);

        /**
         * Component has been focused
         *
         * @param event
         *            Component focus event.
         */
        public void focus(FocusEvent event);
    }

    /**
     * <code>BlurEvent</code> class for holding additional event information.
     * Fired when a <code>Field</code> loses keyboard focus.
     *
     * @since 6.2
     */
    @SuppressWarnings("serial")
    public static class BlurEvent extends Component.Event {

        /**
         * Identifier for event that can be used in {@link EventRouter}
         */
        public static final String EVENT_ID = EventId.BLUR;

        public BlurEvent(Component source) {
            super(source);
        }
    }

    /**
     * <code>BlurListener</code> interface for listening for
     * <code>BlurEvent</code> fired by a <code>Field</code>.
     *
     * @see BlurEvent
     * @since 6.2
     */
    @FunctionalInterface
    public interface BlurListener extends ConnectorEventListener {

        public static final Method blurMethod = ReflectTools
                .findMethod(BlurListener.class, "blur", BlurEvent.class);

        /**
         * Component has been blurred
         *
         * @param event
         *            Component blur event.
         */
        public void blur(BlurEvent event);
    }

    public static abstract class FocusAndBlurServerRpcImpl
            implements FocusAndBlurServerRpc {

        private final Component component;

        public FocusAndBlurServerRpcImpl(Component component) {
            this.component = component;
        }

        protected abstract void fireEvent(Event event);

        @Override
        public void blur() {
            fireEvent(new BlurEvent(component));
        }

        @Override
        public void focus() {
            fireEvent(new FocusEvent(component));
        }
    }

    /**
     * Focus and blur server RPC implementation which fires focus or blur event
     * using a provided event handler.
     *
     * @author Vaadin Ltd
     * @since 8.0
     */
    public static class FocusAndBlurServerRpcDecorator
            extends FocusAndBlurServerRpcImpl {

        private final SerializableConsumer<Event> eventHandler;

        /**
         * Create a new decorator instance.
         *
         * @param component
         *            the source events component
         * @param eventHandler
         *            the event handler to delegate event firing
         */
        public FocusAndBlurServerRpcDecorator(Component component,
                SerializableConsumer<Event> eventHandler) {
            super(component);
            this.eventHandler = eventHandler;
        }

        @Override
        protected void fireEvent(Event event) {
            eventHandler.accept(event);
        }
    }

}
