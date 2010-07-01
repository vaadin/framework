/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.event;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

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
        public void addListener(FocusListener listener);

        /**
         * Removes a <code>FocusListener</code> from the Component.
         * 
         * @param listener
         * @see FocusListener
         * @since 6.2
         */
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
        public void addListener(BlurListener listener);

        /**
         * Removes a <code>BlurListener</code> from the Component.
         * 
         * @param listener
         * @see BlurListener
         * @since 6.2
         */
        public void removeListener(BlurListener listener);
    }

    /**
     * <code>FocusEvent</code> class for holding additional event information.
     * Fired when a <code>Field</code> receives keyboard focus.
     * 
     * @since 6.2
     */
    @SuppressWarnings("serial")
    public class FocusEvent extends Component.Event {

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
    public interface FocusListener extends ComponentEventListener {

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
    public class BlurEvent extends Component.Event {

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
    public interface BlurListener extends ComponentEventListener {

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

}
