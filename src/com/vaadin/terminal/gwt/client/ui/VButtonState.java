package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ComponentState;

public class VButtonState extends ComponentState {
    private boolean disableOnClick = false;

    public boolean isDisableOnClick() {
        return disableOnClick;
    }

    public void setDisableOnClick(boolean disableOnClick) {
        this.disableOnClick = disableOnClick;
    }

}
