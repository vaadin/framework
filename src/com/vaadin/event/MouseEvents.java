package com.vaadin.event;

import java.lang.reflect.Method;

import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

public interface MouseEvents {

    /**
     * defines the clicked mouse button for a ComponentClickEvents
     */
    public enum MouseButton {
        LEFT, RIGHT, MIDDLE
    }

    public class ClickEvent extends Component.Event {

        private MouseButton mouseButton;

        private static final long serialVersionUID = -7644184999481404162L;

        public ClickEvent(Component source, String mouseButton) {
            super(source);
            if (mouseButton.equals("left")) {
                this.mouseButton = MouseButton.LEFT;
            } else if (mouseButton.equals("right")) {
                this.mouseButton = MouseButton.RIGHT;
            } else {
                this.mouseButton = MouseButton.MIDDLE;
            }
        }

        public MouseButton getMouseButton() {
            return mouseButton;
        }

    }

    public interface ClickListener extends ComponentEventListener {

        public static final Method clickMethod = ReflectTools.findMethod(
                ClickListener.class, "click", ClickEvent.class);

        /**
         * Component has been clicked
         * 
         * @param event
         *            Component click event.
         */
        public void click(ClickEvent event);
    }

    /*
     * component double click event
     */

    public class DoubleClickEvent extends Component.Event {

        private static final long serialVersionUID = -7644184999481404162L;

        public DoubleClickEvent(Component source) {
            super(source);
        }
    }

    public interface DoubleClickListener extends ComponentEventListener {

        public static final Method doubleClickMethod = ReflectTools.findMethod(
                DoubleClickListener.class, "doubleClick",
                DoubleClickEvent.class);

        /**
         * Component value was changed
         * 
         * @param event
         *            Component change event.
         */
        public void doubleClick(DoubleClickEvent event);
    }

}
