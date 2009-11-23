/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.event;

import java.lang.reflect.Method;

import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

public interface MouseEvents {

    /**
     * defines the clicked mouse button for ClickEvents
     */
    public enum MouseButton {
        LEFT, RIGHT, MIDDLE
    }

    /**
     * <code>ClickEvent</code> class for holding additional event information.
     * Fired when the user clicks on a <code>Component</code>.
     * 
     * @since 6.2
     */
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

    /**
     * <code>ClickListener</code> interface for listening for
     * <code>ClickEvent</code> fired by a <code>Component</code>.
     * 
     * @see ClickEvent
     * @since 6.2
     */
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

    /**
     * <code>DoubleClickEvent</code> class for holding additional event
     * information. Fired when the user double-clicks on a
     * <code>Component</code>.
     * 
     * @since 6.2
     */
    public class DoubleClickEvent extends Component.Event {

        private static final long serialVersionUID = -7644184999481404162L;

        public DoubleClickEvent(Component source) {
            super(source);
        }
    }

    /**
     * <code>DoubleClickListener</code> interface for listening for
     * <code>DoubleClickEvent</code> fired by a <code>Component</code>.
     * 
     * @see DoubleClickEvent
     * @since 6.2
     */
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
