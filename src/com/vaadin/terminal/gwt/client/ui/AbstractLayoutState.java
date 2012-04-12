/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ComponentState;

public class AbstractLayoutState extends ComponentState {
    private int marginsBitmask;

    public int getMarginsBitmask() {
        return marginsBitmask;
    }

    public void setMarginsBitmask(int marginsBitmask) {
        this.marginsBitmask = marginsBitmask;
    }

}