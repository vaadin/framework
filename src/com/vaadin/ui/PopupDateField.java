/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

/**
 * <p>
 * A date entry component, which displays the actual date selector as a popup.
 * 
 * </p>
 * 
 * @see DateField
 * @see InlineDateField
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
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
        requestRepaint();
    }

}
