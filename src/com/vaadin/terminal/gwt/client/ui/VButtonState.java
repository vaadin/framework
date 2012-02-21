package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ComponentState;

public class VButtonState extends ComponentState {
    private boolean disableOnClick = false;
    private int clickShortcutKeyCode = 0;

    public boolean isDisableOnClick() {
        return disableOnClick;
    }

    public void setDisableOnClick(boolean disableOnClick) {
        this.disableOnClick = disableOnClick;
    }

    public int getClickShortcutKeyCode() {
        return clickShortcutKeyCode;
    }

    public void setClickShortcutKeyCode(int clickShortcutKeyCode) {
        this.clickShortcutKeyCode = clickShortcutKeyCode;
    }

}
