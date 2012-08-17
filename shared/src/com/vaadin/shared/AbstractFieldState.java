/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared;

import com.vaadin.shared.ui.TabIndexState;

/**
 * Shared state for {@link com.vaadin.ui.AbstractField}.
 * 
 * @author Vaadin Ltd
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
     * See {@link com.vaadin.ui.AbstractField#isRequired()} for more
     * information.
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
     * See {@link com.vaadin.ui.AbstractField#setRequired(boolean)} for more
     * information.
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
    @Override
    public int getTabIndex() {
        return tabIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ui.TabIndexState#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

}
