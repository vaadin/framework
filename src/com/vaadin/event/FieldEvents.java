/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.event;

import java.lang.reflect.Method;

import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

public interface FieldEvents {

    /**
     * <code>FocusEvent</code> class for holding additional event information.
     * Fired when a <code>Field</code> receives keyboard focus.
     * 
     * @since 6.2
     */
    public class FocusEvent extends Component.Event {

        private static final long serialVersionUID = -7644184999481404162L;

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
    public class BlurEvent extends Component.Event {

        private static final long serialVersionUID = -7644184999481404162L;

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

    /**
     * <code>ValueChangeEvent</code> class for holding additional event
     * information. Fired when the value of a <code>Field</code> changes.
     * 
     * @since 6.2
     */
    public class ValueChangeEvent extends Component.Event {

        private static final long serialVersionUID = -7644184999481404162L;

        public ValueChangeEvent(Component source) {
            super(source);
        }
    }

    /**
     * <code>ValueChangeListener</code> interface for listening for
     * <code>ValueChangeEvent</code> fired by a <code>Field</code>.
     * 
     * @see ValueChangeEvent
     * @since 6.2
     */
    public interface ValueChangeListener extends ComponentEventListener {

        public static final Method valueChangeMethod = ReflectTools.findMethod(
                ValueChangeListener.class, "valueChange",
                ValueChangeEvent.class);

        /**
         * Component value was changed
         * 
         * @param event
         *            Component change event.
         */
        public void valueChange(ValueChangeEvent event);
    }

}
