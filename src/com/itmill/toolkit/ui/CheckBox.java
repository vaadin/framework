/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.lang.reflect.Method;

import com.itmill.toolkit.data.Property;

public class CheckBox extends Button {
    /**
     * Creates a new switch button.
     */
    public CheckBox() {
        setSwitchMode(true);
    }

    /**
     * Creates a new switch button with a caption and a set initial state.
     * 
     * @param caption
     *                the caption of the switch button
     * @param initialState
     *                the initial state of the switch button
     */
    public CheckBox(String caption, boolean initialState) {
        setCaption(caption);
        setValue(new Boolean(initialState));
        setSwitchMode(true);
    }

    /**
     * Creates a new switch button with a caption and a click listener.
     * 
     * @param caption
     *                the caption of the switch button
     * @param listener
     *                the click listener
     */
    public CheckBox(String caption, ClickListener listener) {
        setCaption(caption);
        addListener(listener);
        setSwitchMode(true);
    }

    /**
     * Convenience method for creating a new switch button with a method
     * listening button clicks. Using this method is discouraged because it
     * cannot be checked during compilation. Use
     * {@link #addListener(Class, Object, Method)} or
     * {@link #addListener(com.itmill.toolkit.ui.Component.Listener)} instead.
     * The method must have either no parameters, or only one parameter of
     * Button.ClickEvent type.
     * 
     * @param caption
     *                the Button caption.
     * @param target
     *                the Object having the method for listening button clicks.
     * @param methodName
     *                the name of the method in target object, that receives
     *                button click events.
     */
    public CheckBox(String caption, Object target, String methodName) {
        setCaption(caption);
        addListener(ClickEvent.class, target, methodName);
        setSwitchMode(true);
    }

    /**
     * Creates a new switch button that is connected to a boolean property.
     * 
     * @param state
     *                the Initial state of the switch-button.
     * @param dataSource
     */
    public CheckBox(String caption, Property dataSource) {
        setCaption(caption);
        setPropertyDataSource(dataSource);
        setSwitchMode(true);
    }

    /**
     * Creates a new push button with a set caption.
     * 
     * The value of the push button is always false and they are immediate by
     * default.
     * 
     * @param caption
     *                the Button caption.
     */

    public CheckBox(String caption) {
        setCaption(caption);
        setSwitchMode(true);
    }

    public void setSwitchMode(boolean switchMode)
            throws UnsupportedOperationException {
        if (this.switchMode && !switchMode) {
            throw new UnsupportedOperationException(
                    "CheckBox is always in switch mode (consider using a Button)");
        }
        this.switchMode = true;
    }

}
