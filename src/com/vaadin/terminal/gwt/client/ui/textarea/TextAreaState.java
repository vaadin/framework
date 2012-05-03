package com.vaadin.terminal.gwt.client.ui.textarea;

import com.vaadin.terminal.gwt.client.ui.textfield.AbstractTextFieldState;

public class TextAreaState extends AbstractTextFieldState {

    /**
     * Number of visible rows in the text area. The default is 5.
     */
    private int rows = 5;

    /**
     * Tells if word-wrapping should be used in the text area.
     */
    private boolean wordwrap = true;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public boolean isWordwrap() {
        return wordwrap;
    }

    public void setWordwrap(boolean wordwrap) {
        this.wordwrap = wordwrap;
    }

}
