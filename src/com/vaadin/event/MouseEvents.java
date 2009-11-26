/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.event;

import java.lang.reflect.Method;

import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

public interface MouseEvents {

    /**
     * <code>ClickEvent</code> class for holding additional event information.
     * Fired when the user clicks on a <code>Component</code>.
     * 
     * ClickEvents are rather terminal dependent events. Correct values in event
     * details cannot be guaranteed.
     * 
     * @since 6.2
     */
    public class ClickEvent extends Component.Event {
        public static final int BUTTON_LEFT = MouseEventDetails.BUTTON_LEFT;
        public static final int BUTTON_MIDDLE = MouseEventDetails.BUTTON_MIDDLE;
        public static final int BUTTON_RIGHT = MouseEventDetails.BUTTON_RIGHT;

        private MouseEventDetails details;

        private static final long serialVersionUID = -7644184999481404162L;

        public ClickEvent(Component source, MouseEventDetails mouseEventDetails) {
            super(source);
            this.details = mouseEventDetails;
        }

        public int getButton() {
            return details.getButton();
        }

        public int getClientX() {
            return details.getClientX();
        }

        public int getClientY() {
            return details.getClientY();
        }

        public boolean isDoubleClick() {
            return details.isDoubleClick();
        }

        public boolean isAltKey() {
            return details.isAltKey();
        }

        public boolean isCtrlKey() {
            return details.isCtrlKey();
        }

        public boolean isMetaKey() {
            return details.isMetaKey();
        }

        public boolean isShiftKey() {
            return details.isShiftKey();
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
