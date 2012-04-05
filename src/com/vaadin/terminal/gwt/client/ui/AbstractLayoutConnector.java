/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ComponentState;

public abstract class AbstractLayoutConnector extends
        AbstractComponentContainerConnector {

    public static class AbstractLayoutState extends ComponentState {
        private int marginsBitmask;

        public int getMarginsBitmask() {
            return marginsBitmask;
        }

        public void setMarginsBitmask(int marginsBitmask) {
            this.marginsBitmask = marginsBitmask;
        }

    }

    @Override
    public AbstractLayoutState getState() {
        return (AbstractLayoutState) super.getState();
    }
}
