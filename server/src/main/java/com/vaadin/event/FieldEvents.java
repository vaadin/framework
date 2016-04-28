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

import com.vaadin.shared.EventId;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Field;
import com.vaadin.ui.Field.ValueChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.util.ReflectTools;

/**
 * Interface that serves as a wrapper for {@link Field} related events.
 */
public interface FieldEvents {

    /**
     * The interface for adding and removing <code>FocusEvent</code> listeners.
     * By implementing this interface a class explicitly announces that it will
     * generate a <code>FocusEvent</code> when it receives keyboard focus.
     * <p>
     * Note: The general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
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
         * @see FocusListener
         * @since 6.2
         */
        public void addFocusListener(FocusListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #addFocusListener(FocusListener)}
         **/
        @Deprecated
        public void addListener(FocusListener listener);

        /**
         * Removes a <code>FocusListener</code> from the Component.
         * 
         * @param listener
         * @see FocusListener
         * @since 6.2
         */
        public void removeFocusListener(FocusListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #removeFocusListener(FocusListener)}
         **/
        @Deprecated
        public void removeListener(FocusListener listener);
    }

    /**
     * The interface for adding and removing <code>BlurEvent</code> listeners.
     * By implementing this interface a class explicitly announces that it will
     * generate a <code>BlurEvent</code> when it loses keyboard focus.
     * <p>
     * Note: The general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
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
         * @param listener
         * @see BlurListener
         * @since 6.2
         */
        public void addBlurListener(BlurListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #addBlurListener(BlurListener)}
         **/
        @Deprecated
        public void addListener(BlurListener listener);

        /**
         * Removes a <code>BlurListener</code> from the Component.
         * 
         * @param listener
         * @see BlurListener
         * @since 6.2
         */
        public void removeBlurListener(BlurListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #removeBlurListener(BlurListener)}
         **/
        @Deprecated
        public void removeListener(BlurListener listener);
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
    public interface FocusListener extends ConnectorEventListener {

        public static final Method focusMethod = ReflectTools.findMethod(
                FocusListener.class, "focus", FocusEvent.class);

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
    public interface BlurListener extends ConnectorEventListener {

        public static final Method blurMethod = ReflectTools.findMethod(
                BlurListener.class, "blur", BlurEvent.class);

        /**
         * Component has been blurred
         * 
         * @param event
         *            Component blur event.
         */
        public void blur(BlurEvent event);
    }

    /**
     * TextChangeEvents are fired when the user is editing the text content of a
     * field. Most commonly text change events are triggered by typing text with
     * keyboard, but e.g. pasting content from clip board to a text field also
     * triggers an event.
     * <p>
     * TextChangeEvents differ from {@link ValueChangeEvent}s so that they are
     * triggered repeatedly while the end user is filling the field.
     * ValueChangeEvents are not fired until the user for example hits enter or
     * focuses another field. Also note the difference that TextChangeEvents are
     * only fired if the change is triggered from the user, while
     * ValueChangeEvents are also fired if the field value is set by the
     * application code.
     * <p>
     * The {@link TextChangeNotifier}s implementation may decide when exactly
     * TextChangeEvents are fired. TextChangeEvents are not necessary fire for
     * example on each key press, but buffered with a small delay. The
     * {@link TextField} component supports different modes for triggering
     * TextChangeEvents.
     * 
     * @see TextChangeListener
     * @see TextChangeNotifier
     * @see TextField#setTextChangeEventMode(com.vaadin.ui.TextField.TextChangeEventMode)
     * @since 6.5
     */
    public static abstract class TextChangeEvent extends Component.Event {

        public TextChangeEvent(Component source) {
            super(source);
        }

        /**
         * @return the text content of the field after the
         *         {@link TextChangeEvent}
         */
        public abstract String getText();

        /**
         * @return the cursor position during after the {@link TextChangeEvent}
         */
        public abstract int getCursorPosition();
    }

    /**
     * A listener for {@link TextChangeEvent}s.
     * 
     * @since 6.5
     */
    public interface TextChangeListener extends ConnectorEventListener {

        public static String EVENT_ID = "ie";
        public static Method EVENT_METHOD = ReflectTools.findMethod(
                TextChangeListener.class, "textChange", TextChangeEvent.class);

        /**
         * This method is called repeatedly while the text is edited by a user.
         * 
         * @param event
         *            the event providing details of the text change
         */
        public void textChange(TextChangeEvent event);
    }

    /**
     * An interface implemented by a {@link Field} supporting
     * {@link TextChangeEvent}s. An example a {@link TextField} supports
     * {@link TextChangeListener}s.
     */
    public interface TextChangeNotifier extends Serializable {
        public void addTextChangeListener(TextChangeListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #addTextChangeListener(TextChangeListener)}
         **/
        @Deprecated
        public void addListener(TextChangeListener listener);

        public void removeTextChangeListener(TextChangeListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #removeTextChangeListener(TextChangeListener)}
         **/
        @Deprecated
        public void removeListener(TextChangeListener listener);
    }

    public static abstract class FocusAndBlurServerRpcImpl implements
            FocusAndBlurServerRpc {

        private Component component;

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

}
