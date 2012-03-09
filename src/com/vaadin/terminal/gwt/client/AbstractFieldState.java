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

}
