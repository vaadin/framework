/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.event;

import java.lang.reflect.Method;

import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

/**
 * Interface that serves as a wrapper for mouse related events.
 * 
 * @author Vaadin Ltd.
 * @see ClickListener
 * @version
 * @VERSION@
 * @since 6.2
 */
public interface MouseEvents {

    /**
     * Class for holding information about a mouse click event. A
     * {@link ClickEvent} is fired when the user clicks on a
     * <code>Component</code>.
     * 
     * The information available for click events are terminal dependent.
     * Correct values for all event details cannot be guaranteed.
     * 
     * @author Vaadin Ltd.
     * @see ClickListener
     * @version
     * @VERSION@
     * @since 6.2
     */
    public class ClickEvent extends Component.Event {
        public static final int BUTTON_LEFT = MouseEventDetails.BUTTON_LEFT;
        public static final int BUTTON_MIDDLE = MouseEventDetails.BUTTON_MIDDLE;
        public static final int BUTTON_RIGHT = MouseEventDetails.BUTTON_RIGHT;

        private MouseEventDetails details;

        public ClickEvent(Component source, MouseEventDetails mouseEventDetails) {
            super(source);
            details = mouseEventDetails;
        }

        /**
         * Returns an identifier describing which mouse button the user pushed.
         * Compare with {@link #BUTTON_LEFT},{@link #BUTTON_MIDDLE},
         * {@link #BUTTON_RIGHT} to find out which butten it is.
         * 
         * @return one of {@link #BUTTON_LEFT}, {@link #BUTTON_MIDDLE},
         *         {@link #BUTTON_RIGHT}.
         */
        public int getButton() {
            return details.getButton();
        }

        /**
         * Returns the mouse position (x coordinate) when the click took place.
         * The position is relative to the browser client area.
         * 
         * @return The mouse cursor x position
         */
        public int getClientX() {
            return details.getClientX();
        }

        /**
         * Returns the mouse position (y coordinate) when the click took place.
         * The position is relative to the browser client area.
         * 
         * @return The mouse cursor y position
         */
        public int getClientY() {
            return details.getClientY();
        }

        /**
         * Returns the relative mouse position (x coordinate) when the click
         * took place. The position is relative to the clicked component.
         * 
         * @return The mouse cursor x position relative to the clicked layout
         *         component or -1 if no x coordinate available
         */
        public int getRelativeX() {
            return details.getRelativeX();
        }

        /**
         * Returns the relative mouse position (y coordinate) when the click
         * took place. The position is relative to the clicked component.
         * 
         * @return The mouse cursor y position relative to the clicked layout
         *         component or -1 if no y coordinate available
         */
        public int getRelativeY() {
            return details.getRelativeY();
        }

        /**
         * Checks if the event is a double click event.
         * 
         * @return true if the event is a double click event, false otherwise
         */
        public boolean isDoubleClick() {
            return details.isDoubleClick();
        }

        /**
         * Checks if the Alt key was down when the mouse event took place.
         * 
         * @return true if Alt was down when the event occured, false otherwise
         */
        public boolean isAltKey() {
            return details.isAltKey();
        }

        /**
         * Checks if the Ctrl key was down when the mouse event took place.
         * 
         * @return true if Ctrl was pressed when the event occured, false
         *         otherwise
         */
        public boolean isCtrlKey() {
            return details.isCtrlKey();
        }

        /**
         * Checks if the Meta key was down when the mouse event took place.
         * 
         * @return true if Meta was pressed when the event occured, false
         *         otherwise
         */
        public boolean isMetaKey() {
            return details.isMetaKey();
        }

        /**
         * Checks if the Shift key was down when the mouse event took place.
         * 
         * @return true if Shift was pressed when the event occured, false
         *         otherwise
         */
        public boolean isShiftKey() {
            return details.isShiftKey();
        }

        /**
         * Returns a human readable string representing which button has been
         * pushed. This is meant for debug purposes only and the string returned
         * could change. Use {@link #getButton()} to check which button was
         * pressed.
         * 
         * @since 6.3
         * @return A string representation of which button was pushed.
         */
        public String getButtonName() {
            return details.getButtonName();
        }
    }

    /**
     * Interface for listening for a {@link ClickEvent} fired by a
     * {@link Component}.
     * 
     * @see ClickEvent
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 6.2
     */
    public interface ClickListener extends ComponentEventListener {

        public static final Method clickMethod = ReflectTools.findMethod(
                ClickListener.class, "click", ClickEvent.class);

        /**
         * Called when a {@link Component} has been clicked. A reference to the
         * component is given by {@link ClickEvent#getComponent()}.
         * 
         * @param event
         *            An event containing information about the click.
         */
        public void click(ClickEvent event);
    }

    /**
     * Class for holding additional event information for DoubleClick events.
     * Fired when the user double-clicks on a <code>Component</code>.
     * 
     * @see ClickEvent
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 6.2
     */
    public class DoubleClickEvent extends Component.Event {

        public DoubleClickEvent(Component source) {
            super(source);
        }
    }

    /**
     * Interface for listening for a {@link DoubleClickEvent} fired by a
     * {@link Component}.
     * 
     * @see DoubleClickEvent
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 6.2
     */
    public interface DoubleClickListener extends ComponentEventListener {

        public static final Method doubleClickMethod = ReflectTools.findMethod(
                DoubleClickListener.class, "doubleClick",
                DoubleClickEvent.class);

        /**
         * Called when a {@link Component} has been double clicked. A reference
         * to the component is given by {@link DoubleClickEvent#getComponent()}.
         * 
         * @param event
         *            An event containing information about the double click.
         */
        public void doubleClick(DoubleClickEvent event);
    }

}
