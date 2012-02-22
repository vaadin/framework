/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.ui.Button;

/**
 * Shared state for Button and NativeButton.
 * 
 * @see ComponentState
 * 
 * @since 7.0
 */
public class VButtonState extends ComponentState {
    private boolean disableOnClick = false;
    private int clickShortcutKeyCode = 0;

    /**
     * Checks whether the button should be disabled on the client side on next
     * click.
     * 
     * @return true if the button should be disabled on click
     */
    public boolean isDisableOnClick() {
        return disableOnClick;
    }

    /**
     * Sets whether the button should be disabled on the client side on next
     * click.
     * 
     * @param disableOnClick
     *            true if the button should be disabled on click
     */
    public void setDisableOnClick(boolean disableOnClick) {
        this.disableOnClick = disableOnClick;
    }

    /**
     * Returns the key code for activating the button via a keyboard shortcut.
     * 
     * See {@link Button#setClickShortcut(int, int...)} for more information.
     * 
     * @return key code or 0 for none
     */
    public int getClickShortcutKeyCode() {
        return clickShortcutKeyCode;
    }

    /**
     * Sets the key code for activating the button via a keyboard shortcut.
     * 
     * See {@link Button#setClickShortcut(int, int...)} for more information.
     * 
     * @param clickShortcutKeyCode
     *            key code or 0 for none
     */
    public void setClickShortcutKeyCode(int clickShortcutKeyCode) {
        this.clickShortcutKeyCode = clickShortcutKeyCode;
    }

}
