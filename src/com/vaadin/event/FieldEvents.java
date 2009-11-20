package com.vaadin.event;

import java.lang.reflect.Method;

import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

public interface FieldEvents {

    /*
     * component focus event and listener
     */

    public class FocusEvent extends Component.Event {

        private static final long serialVersionUID = -7644184999481404162L;

        public FocusEvent(Component source) {
            super(source);
        }
    }

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

    /*
     * component blur event and listener
     */

    public class BlurEvent extends Component.Event {

        private static final long serialVersionUID = -7644184999481404162L;

        public BlurEvent(Component source) {
            super(source);
        }
    }

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

    /*
     * component value change event
     */

    public class ValueChangeEvent extends Component.Event {

        private static final long serialVersionUID = -7644184999481404162L;

        public ValueChangeEvent(Component source) {
            super(source);
        }
    }

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
