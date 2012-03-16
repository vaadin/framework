/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.vaadin.ui.AbstractField;

/**
 * Shared state for {@link AbstractField}.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public class AbstractFieldState extends ComponentState {
    private boolean propertyReadOnly = false;
    private boolean hideErrors = false;

    /**
     * Checks if the property data source for the Field is in read only mode.
     * This affects the read only state of the field itself.
     * 
     * @return true if there is a property data source and it is set to read
     *         only, false otherwise
     */
    public boolean isPropertyReadOnly() {
        return propertyReadOnly;
    }

    /**
     * Sets the read only state of the property data source.
     * 
     * @param propertyReadOnly
     *            true if the property data source if read only, false otherwise
     */
    public void setPropertyReadOnly(boolean propertyReadOnly) {
        this.propertyReadOnly = propertyReadOnly;
    }

    /**
     * Returns true if the component will hide any errors even if the error
     * message is set.
     * 
     * @return true if error messages are disabled
     */
    public boolean isHideErrors() {
        return hideErrors;
    }

    /**
     * Sets whether the component should hide any errors even if the error
     * message is set.
     * 
     * This is used e.g. on forms to hide error messages for invalid fields
     * before the first user actions.
     * 
     * @param hideErrors
     *            true if error messages should be hidden
     */
    public void setHideErrors(boolean hideErrors) {
        this.hideErrors = hideErrors;
    }

}
