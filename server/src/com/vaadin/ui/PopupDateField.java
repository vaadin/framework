/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.ui;

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.ui.datefield.PopupDateFieldState;

/**
 * <p>
 * A date entry component, which displays the actual date selector as a popup.
 * 
 * </p>
 * 
 * @see DateField
 * @see InlineDateField
 * @author Vaadin Ltd.
 * @since 5.0
 */
public class PopupDateField extends DateField {

    private String inputPrompt = null;

    public PopupDateField() {
        super();
    }

    public PopupDateField(Property dataSource) throws IllegalArgumentException {
        super(dataSource);
    }

    public PopupDateField(String caption, Date value) {
        super(caption, value);
    }

    public PopupDateField(String caption, Property dataSource) {
        super(caption, dataSource);
    }

    public PopupDateField(String caption) {
        super(caption);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (inputPrompt != null) {
            target.addAttribute("prompt", inputPrompt);
        }
    }

    /**
     * Gets the current input prompt.
     * 
     * @see #setInputPrompt(String)
     * @return the current input prompt, or null if not enabled
     */
    public String getInputPrompt() {
        return inputPrompt;
    }

    /**
     * Sets the input prompt - a textual prompt that is displayed when the field
     * would otherwise be empty, to prompt the user for input.
     * 
     * @param inputPrompt
     */
    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
        markAsDirty();
    }

    @Override
    protected PopupDateFieldState getState() {
        return (PopupDateFieldState) super.getState();
    }

    @Override
    protected PopupDateFieldState getState(boolean markAsDirty) {
        return (PopupDateFieldState) super.getState(markAsDirty);
    }

    /**
     * Checks whether the text field is enabled (default) or not.
     * 
     * @see PopupDateField#setTextFieldEnabled(boolean);
     * 
     * @return <b>true</b> if the text field is enabled, <b>false</b> otherwise.
     */
    public boolean isTextFieldEnabled() {
        return getState(false).textFieldEnabled;
    }

    /**
     * Enables or disables the text field. By default the text field is enabled.
     * Disabling it causes only the button for date selection to be active, thus
     * preventing the user from entering invalid dates.
     * 
     * See {@link http://dev.vaadin.com/ticket/6790}.
     * 
     * @param state
     *            <b>true</b> to enable text field, <b>false</b> to disable it.
     */
    public void setTextFieldEnabled(boolean state) {
        getState().textFieldEnabled = state;
    }

    /**
     * Set a description that explains the usage of the Widget for users of
     * assistive devices.
     * 
     * @param description
     *            String with the description
     */
    public void setAssistiveText(String description) {
        getState().descriptionForAssistiveDevices = description;
    }

    /**
     * Get the description that explains the usage of the Widget for users of
     * assistive devices.
     * 
     * @return String with the description
     */
    public String getAssistiveText() {
        return getState(false).descriptionForAssistiveDevices;
    }
}
