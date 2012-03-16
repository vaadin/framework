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

    /**
     * Checks whether the required indicator should be shown for the field.
     * 
     * Required indicators are hidden if the field or its data source is
     * read-only.
     * 
     * @return true if required indicator should be shown
     */
    public boolean isRequired() {
        return getState().isRequired() && !isReadOnly();
    }

}
