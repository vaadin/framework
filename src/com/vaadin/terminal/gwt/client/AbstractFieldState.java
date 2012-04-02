/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.ui.TabIndexState;
import com.vaadin.ui.AbstractField;

/**
 * Shared state for {@link AbstractField}.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public class AbstractFieldState extends ComponentState implements TabIndexState {
    private boolean propertyReadOnly = false;
    private boolean hideErrors = false;
    private boolean required = false;
    private boolean modified = false;

    /**
     * The tab order number of this field.
     */
    private int tabIndex = 0;

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

    /**
     * Is the field required. Required fields must filled by the user.
     * 
     * See AbstractField#isRequired() for more information.
     * 
     * @return <code>true</code> if the field is required, otherwise
     *         <code>false</code>.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the field required. Required fields must filled by the user.
     * 
     * See AbstractField#setRequired(boolean) for more information.
     * 
     * @param required
     *            Is the field required.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Has the contents of the field been modified, i.e. has the value been
     * updated after it was read from the data source.
     * 
     * @return true if the field has been modified, false otherwise
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Setter for the modified flag, toggled when the contents of the field is
     * modified by the user.
     * 
     * @param modified
     *            the new modified state
     * 
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ComponentState#getTabIndex()
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ui.TabIndexState#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

}
