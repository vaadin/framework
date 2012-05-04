/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.button;

import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.ui.Button;

/**
 * Shared state for Button and NativeButton.
 * 
 * @see ComponentState
 * 
 * @since 7.0
 */
public class ButtonState extends ComponentState {
    private boolean disableOnClick = false;
    private int clickShortcutKeyCode = 0;
    /**
     * If caption should be rendered in HTML
     */
    private boolean htmlContentAllowed = false;

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
        this.htmlContentAllowed = htmlContentAllowed;
    }

    /**
     * Return HTML rendering setting.
     * 
     * @return <code>true</code> if the caption text is to be rendered as HTML,
     *         <code>false</code> otherwise
     */
    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

}
