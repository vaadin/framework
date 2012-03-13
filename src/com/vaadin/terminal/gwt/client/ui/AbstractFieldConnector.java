/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.AbstractFieldState;
import com.vaadin.terminal.gwt.client.ComponentState;

public abstract class AbstractFieldConnector extends AbstractComponentConnector {

    @Override
    public AbstractFieldState getState() {
        return (AbstractFieldState) super.getState();
    }

    @Override
    protected ComponentState createState() {
        return GWT.create(AbstractFieldState.class);
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || getState().isPropertyReadOnly();
    }

}
