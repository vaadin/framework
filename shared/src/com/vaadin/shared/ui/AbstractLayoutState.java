/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui;

import com.vaadin.shared.ComponentState;

public class AbstractLayoutState extends ComponentState {
    private int marginsBitmask;

    public int getMarginsBitmask() {
        return marginsBitmask;
    }

    public void setMarginsBitmask(int marginsBitmask) {
        this.marginsBitmask = marginsBitmask;
    }

}