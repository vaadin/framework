/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;


public abstract class AbstractLayoutConnector extends
        AbstractComponentContainerConnector {

    @Override
    public AbstractLayoutState getState() {
        return (AbstractLayoutState) super.getState();
    }
}
