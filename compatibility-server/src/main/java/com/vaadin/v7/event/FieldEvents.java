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

package com.vaadin.v7.event;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Field.ValueChangeEvent;
import com.vaadin.v7.ui.TextField;

/**
 * Interface that serves as a wrapper for {@link Field} related events.
 */
@Deprecated
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
    @Deprecated
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
         * Removes a <code>FocusListener</code> from the Component.
         *
         * @param listener
         * @see FocusListener
         * @since 6.2
         */
        public void removeFocusListener(FocusListener listener);
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
    @Deprecated
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
         * Removes a <code>BlurListener</code> from the Component.
         *
         * @param listener
         * @see BlurListener
         * @since 6.2
         */
        public void removeBlurListener(BlurListener listener);
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
     * {@code TextField} component supports different modes for triggering
     * TextChangeEvents.
     *
     * @see TextChangeListener
     * @see TextChangeNotifier
     * @see TextField#setTextChangeEventMode(com.vaadin.ui.TextField.TextChangeEventMode)
     * @since 6.5
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public interface TextChangeNotifier extends Serializable {
        public void addTextChangeListener(TextChangeListener listener);

        public void removeTextChangeListener(TextChangeListener listener);

    }

}
