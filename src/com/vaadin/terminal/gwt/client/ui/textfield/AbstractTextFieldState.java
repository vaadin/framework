package com.vaadin.terminal.gwt.client.ui.textfield;

import com.vaadin.terminal.gwt.client.AbstractFieldState;

public class AbstractTextFieldState extends AbstractFieldState {
    /**
     * Maximum character count in text field.
     */
    private int maxLength = -1;

    /**
     * Number of visible columns in the TextField.
     */
    private int columns = 0;

    /**
     * The prompt to display in an empty field. Null when disabled.
     */
    private String inputPrompt = null;

    /**
     * The text in the field
     */
    private String text = null;

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String getInputPrompt() {
        return inputPrompt;
    }

    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
