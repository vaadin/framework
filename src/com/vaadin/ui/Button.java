/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.VButton;
import com.vaadin.ui.ClientWidget.LoadStyle;
import com.vaadin.ui.themes.BaseTheme;

/**
 * A generic button component.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(value = VButton.class, loadStyle = LoadStyle.EAGER)
public class Button extends AbstractField implements FieldEvents.BlurNotifier,
        FieldEvents.FocusNotifier {

    /* Private members */

    boolean switchMode = false;

    boolean disableOnClick = false;

    /**
     * If caption is rendered as HTML
     */
    private boolean htmlContentAllowed = false;

    /**
     * Creates a new push button. The value of the push button is false and it
     * is immediate by default.
     * 
     */
    public Button() {
        setValue(Boolean.FALSE);
    }

    /**
     * Creates a new push button.
     * 
     * The value of the push button is false and it is immediate by default.
     * 
     * @param caption
     *            the Button caption.
     */
    public Button(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new push button with click listener.
     * 
     * @param caption
     *            the Button caption.
     * @param listener
     *            the Button click listener.
     */
    public Button(String caption, ClickListener listener) {
        this(caption);
        addListener(listener);
    }

    /**
     * Creates a new push button with a method listening button clicks. Using
     * this method is discouraged because it cannot be checked during
     * compilation. Use
     * {@link #Button(String, com.vaadin.ui.Button.ClickListener)} instead. The
     * method must have either no parameters, or only one parameter of
     * Button.ClickEvent type.
     * 
     * @param caption
     *            the Button caption.
     * @param target
     *            the Object having the method for listening button clicks.
     * @param methodName
     *            the name of the method in target object, that receives button
     *            click events.
     */
    public Button(String caption, Object target, String methodName) {
        this(caption);
        addListener(ClickEvent.class, target, methodName);
    }

    /**
     * Creates a new switch button with initial value.
     * 
     * @param state
     *            the Initial state of the switch-button.
     * @param initialState
     * @deprecated use {@link CheckBox} instead of Button in "switchmode"
     */
    @Deprecated
    public Button(String caption, boolean initialState) {
        setCaption(caption);
        setValue(Boolean.valueOf(initialState));
        setSwitchMode(true);
    }

    /**
     * Creates a new switch button that is connected to a boolean property.
     * 
     * @param state
     *            the Initial state of the switch-button.
     * @param dataSource
     * @deprecated use {@link CheckBox} instead of Button in "switchmode"
     */
    @Deprecated
    public Button(String caption, Property dataSource) {
        setCaption(caption);
        setSwitchMode(true);
        setPropertyDataSource(dataSource);
    }

    /**
     * Paints the content of this component.
     * 
     * @param event
     *            the PaintEvent.
     * @throws IOException
     *             if the writing failed due to input/output error.
     * @throws PaintException
     *             if the paint operation failed.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (isSwitchMode()) {
            target.addAttribute("type", "switch");
        }
        target.addVariable(this, "state", booleanValue());

        if (isDisableOnClick()) {
            target.addAttribute(VButton.ATTR_DISABLE_ON_CLICK, true);
        }

        if (isHtmlContentAllowed()) {
            target.addAttribute("html-caption", true);
        }
    }

    /**
     * Invoked when the value of a variable has changed. Button listeners are
     * notified if the button is clicked.
     * 
     * @param source
     * @param variables
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey("disabledOnClick")) {
            // Could be optimized so the button is not repainted because of this
            // (client side has already disabled the button)
            setEnabled(false);
        }

        if (!isReadOnly() && variables.containsKey("state")) {
            // Gets the new and old button states
            final Boolean newValue = (Boolean) variables.get("state");
            final Boolean oldValue = (Boolean) getValue();

            if (isSwitchMode()) {

                // For switch button, the event is only sent if the
                // switch state is changed
                if (newValue != null && !newValue.equals(oldValue)
                        && !isReadOnly()) {
                    setValue(newValue);
                    if (variables.containsKey("mousedetails")) {
                        fireClick(MouseEventDetails
                                .deSerialize((String) variables
                                        .get("mousedetails")));
                    } else {
                        // for compatibility with custom implementations which
                        // don't send mouse details
                        fireClick();
                    }
                }
            } else {

                // Only send click event if the button is pushed
                if (newValue.booleanValue()) {
                    if (variables.containsKey("mousedetails")) {
                        fireClick(MouseEventDetails
                                .deSerialize((String) variables
                                        .get("mousedetails")));
                    } else {
                        // for compatibility with custom implementations which
                        // don't send mouse details
                        fireClick();
                    }
                }

                // If the button is true for some reason, release it
                if (null == oldValue || oldValue.booleanValue()) {
                    setValue(Boolean.FALSE);
                }
            }
        }

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }
        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
    }

    /**
     * Checks if it is switchMode.
     * 
     * @return <code>true</code> if it is in Switch Mode, otherwise
     *         <code>false</code>.
     * @deprecated the {@link CheckBox} component should be used instead of
     *             Button in switch mode
     */
    @Deprecated
    public boolean isSwitchMode() {
        return switchMode;
    }

    /**
     * Sets the switchMode.
     * 
     * @param switchMode
     *            The switchMode to set.
     * @deprecated the {@link CheckBox} component should be used instead of
     *             Button in switch mode
     */
    @Deprecated
    public void setSwitchMode(boolean switchMode) {
        this.switchMode = switchMode;
        if (!switchMode) {
            setImmediate(true);
            if (booleanValue()) {
                setValue(Boolean.FALSE);
            }
        }
    }

    /**
     * Get the boolean value of the button state.
     * 
     * @return True iff the button is pressed down or checked.
     */
    public boolean booleanValue() {
        Boolean value = (Boolean) getValue();
        return (null == value) ? false : value.booleanValue();
    }

    /**
     * Sets immediate mode. Push buttons can not be set in non-immediate mode.
     * 
     * @see com.vaadin.ui.AbstractComponent#setImmediate(boolean)
     */
    @Override
    public void setImmediate(boolean immediate) {
        // Push buttons are always immediate
        super.setImmediate(!isSwitchMode() || immediate);
    }

    /**
     * The type of the button as a property.
     * 
     * @see com.vaadin.data.Property#getType()
     */
    @Override
    public Class getType() {
        return Boolean.class;
    }

    /* Click event */

    private static final Method BUTTON_CLICK_METHOD;

    /**
     * Button style with no decorations. Looks like a link, acts like a button
     * 
     * @deprecated use {@link BaseTheme#BUTTON_LINK} instead.
     */
    @Deprecated
    public static final String STYLE_LINK = "link";

    static {
        try {
            BUTTON_CLICK_METHOD = ClickListener.class.getDeclaredMethod(
                    "buttonClick", new Class[] { ClickEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in Button");
        }
    }

    /**
     * Click event. This event is thrown, when the button is clicked.
     * 
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public class ClickEvent extends Component.Event {

        private final MouseEventDetails details;

        /**
         * New instance of text change event.
         * 
         * @param source
         *            the Source of the event.
         */
        public ClickEvent(Component source) {
            super(source);
            details = null;
        }

        /**
         * Constructor with mouse details
         * 
         * @param source
         *            The source where the click took place
         * @param details
         *            Details about the mouse click
         */
        public ClickEvent(Component source, MouseEventDetails details) {
            super(source);
            this.details = details;
        }

        /**
         * Gets the Button where the event occurred.
         * 
         * @return the Source of the event.
         */
        public Button getButton() {
            return (Button) getSource();
        }

        /**
         * Returns the mouse position (x coordinate) when the click took place.
         * The position is relative to the browser client area.
         * 
         * @return The mouse cursor x position or -1 if unknown
         */
        public int getClientX() {
            if (null != details) {
                return details.getClientX();
            } else {
                return -1;
            }
        }

        /**
         * Returns the mouse position (y coordinate) when the click took place.
         * The position is relative to the browser client area.
         * 
         * @return The mouse cursor y position or -1 if unknown
         */
        public int getClientY() {
            if (null != details) {
                return details.getClientY();
            } else {
                return -1;
            }
        }

        /**
         * Returns the relative mouse position (x coordinate) when the click
         * took place. The position is relative to the clicked component.
         * 
         * @return The mouse cursor x position relative to the clicked layout
         *         component or -1 if no x coordinate available
         */
        public int getRelativeX() {
            if (null != details) {
                return details.getRelativeX();
            } else {
                return -1;
            }
        }

        /**
         * Returns the relative mouse position (y coordinate) when the click
         * took place. The position is relative to the clicked component.
         * 
         * @return The mouse cursor y position relative to the clicked layout
         *         component or -1 if no y coordinate available
         */
        public int getRelativeY() {
            if (null != details) {
                return details.getRelativeY();
            } else {
                return -1;
            }
        }

        /**
         * Checks if the Alt key was down when the mouse event took place.
         * 
         * @return true if Alt was down when the event occured, false otherwise
         *         or if unknown
         */
        public boolean isAltKey() {
            if (null != details) {
                return details.isAltKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Ctrl key was down when the mouse event took place.
         * 
         * @return true if Ctrl was pressed when the event occured, false
         *         otherwise or if unknown
         */
        public boolean isCtrlKey() {
            if (null != details) {
                return details.isCtrlKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Meta key was down when the mouse event took place.
         * 
         * @return true if Meta was pressed when the event occured, false
         *         otherwise or if unknown
         */
        public boolean isMetaKey() {
            if (null != details) {
                return details.isMetaKey();
            } else {
                return false;
            }
        }

        /**
         * Checks if the Shift key was down when the mouse event took place.
         * 
         * @return true if Shift was pressed when the event occured, false
         *         otherwise or if unknown
         */
        public boolean isShiftKey() {
            if (null != details) {
                return details.isShiftKey();
            } else {
                return false;
            }
        }
    }

    /**
     * Interface for listening for a {@link ClickEvent} fired by a
     * {@link Component}.
     * 
     * @author Vaadin Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    public interface ClickListener extends Serializable {

        /**
         * Called when a {@link Button} has been clicked. A reference to the
         * button is given by {@link ClickEvent#getButton()}.
         * 
         * @param event
         *            An event containing information about the click.
         */
        public void buttonClick(ClickEvent event);

    }

    /**
     * Adds the button click listener.
     * 
     * @param listener
     *            the Listener to be added.
     */
    public void addListener(ClickListener listener) {
        addListener(ClickEvent.class, listener, BUTTON_CLICK_METHOD);
    }

    /**
     * Removes the button click listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(ClickListener listener) {
        removeListener(ClickEvent.class, listener, BUTTON_CLICK_METHOD);
    }

    /**
     * Simulates a button click, notifying all server-side listeners.
     * 
     * No action is taken is the button is disabled.
     */
    public void click() {
        if (isEnabled() && !isReadOnly()) {
            fireClick();
        }
    }

    /**
     * Fires a click event to all listeners without any event details.
     * 
     * In subclasses, override {@link #fireClick(MouseEventDetails)} instead of
     * this method.
     */
    protected void fireClick() {
        fireEvent(new Button.ClickEvent(this));
    }

    /**
     * Fires a click event to all listeners.
     * 
     * @param details
     *            MouseEventDetails from which keyboard modifiers and other
     *            information about the mouse click can be obtained. If the
     *            button was clicked by a keyboard event, some of the fields may
     *            be empty/undefined.
     */
    protected void fireClick(MouseEventDetails details) {
        fireEvent(new Button.ClickEvent(this, details));
    }

    @Override
    protected void setInternalValue(Object newValue) {
        // Make sure only booleans get through
        if (null != newValue && !(newValue instanceof Boolean)) {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + " only accepts Boolean values");
        }
        super.setInternalValue(newValue);
    }

    public void addListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    public void removeListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    public void addListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    public void removeListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);

    }

    /*
     * Actions
     */

    protected ClickShortcut clickShortcut;

    /**
     * Makes it possible to invoke a click on this button by pressing the given
     * {@link KeyCode} and (optional) {@link ModifierKey}s.<br/>
     * The shortcut is global (bound to the containing Window).
     * 
     * @param keyCode
     *            the keycode for invoking the shortcut
     * @param modifiers
     *            the (optional) modifiers for invoking the shortcut, null for
     *            none
     */
    public void setClickShortcut(int keyCode, int... modifiers) {
        if (clickShortcut != null) {
            removeShortcutListener(clickShortcut);
        }
        clickShortcut = new ClickShortcut(this, keyCode, modifiers);
        addShortcutListener(clickShortcut);
    }

    /**
     * Removes the keyboard shortcut previously set with
     * {@link #setClickShortcut(int, int...)}.
     */
    public void removeClickShortcut() {
        if (clickShortcut != null) {
            removeShortcutListener(clickShortcut);
            clickShortcut = null;
        }
    }

    /**
     * A {@link ShortcutListener} specifically made to define a keyboard
     * shortcut that invokes a click on the given button.
     * 
     */
    public static class ClickShortcut extends ShortcutListener {
        protected Button button;

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * shorthand notation defined in {@link ShortcutAction}.
         * 
         * @param button
         *            to be clicked when the shortcut is invoked
         * @param shorthandCaption
         *            the caption with shortcut keycode and modifiers indicated
         */
        public ClickShortcut(Button button, String shorthandCaption) {
            super(shorthandCaption);
            this.button = button;
        }

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode} and {@link ModifierKey}s.
         * 
         * @param button
         *            to be clicked when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         * @param modifiers
         *            optional modifiers for shortcut
         */
        public ClickShortcut(Button button, int keyCode, int... modifiers) {
            super(null, keyCode, modifiers);
            this.button = button;
        }

        /**
         * Creates a keyboard shortcut for clicking the given button using the
         * given {@link KeyCode}.
         * 
         * @param button
         *            to be clicked when the shortcut is invoked
         * @param keyCode
         *            KeyCode to react to
         */
        public ClickShortcut(Button button, int keyCode) {
            this(button, keyCode, null);
        }

        @Override
        public void handleAction(Object sender, Object target) {
            button.click();
        }
    }

    /**
     * Determines if a button is automatically disabled when clicked. See
     * {@link #setDisableOnClick(boolean)} for details.
     * 
     * @return true if the button is disabled when clicked, false otherwise
     */
    public boolean isDisableOnClick() {
        return disableOnClick;
    }

    /**
     * Determines if a button is automatically disabled when clicked. If this is
     * set to true the button will be automatically disabled when clicked,
     * typically to prevent (accidental) extra clicks on a button.
     * 
     * @param disableOnClick
     *            true to disable button when it is clicked, false otherwise
     */
    public void setDisableOnClick(boolean disableOnClick) {
        this.disableOnClick = disableOnClick;
        requestRepaint();
    }

    /**
     * Set whether the caption text is rendered as HTML or not. You might need
     * to retheme button to allow higher content than the original text style.
     * 
     * If set to true, the captions are passed to the browser as html and the
     * developer is responsible for ensuring no harmful html is used. If set to
     * false, the content is passed to the browser as plain text.
     * 
     * @param htmlContentAllowed
     *            <code>true</code> if caption is rendered as HTML,
     *            <code>false</code> otherwise
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        if (this.htmlContentAllowed != htmlContentAllowed) {
            this.htmlContentAllowed = htmlContentAllowed;
            requestRepaint();
        }
    }

    /**
     * Return HTML rendering setting
     * 
     * @return <code>true</code> if the caption text is to be rendered as HTML,
     *         <code>false</code> otherwise
     */
    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

}
