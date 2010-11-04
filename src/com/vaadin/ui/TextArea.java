package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.gwt.client.ui.VTextArea;

@ClientWidget(VTextArea.class)
public class TextArea extends TextField {

    private static final int DEFAULT_ROWS = 5;

    public TextArea() {
        setRows(DEFAULT_ROWS);
    }

    public TextArea(String caption) {
        super(caption);
        setRows(DEFAULT_ROWS);
    }

    public TextArea(Property dataSource) {
        super(dataSource);
        setRows(DEFAULT_ROWS);
    }

    public TextArea(String caption, Property dataSource) {
        super(caption, dataSource);
        setRows(DEFAULT_ROWS);
    }

    public TextArea(String caption, String value) {
        super(caption, value);
        setRows(DEFAULT_ROWS);
    }

    /**
     * Sets the number of rows in the editor.
     * 
     * @param rows
     *            the number of rows for this editor.
     */
    @Override
    public void setRows(int rows) {
        // TODO implement here once the API from TextField is removed
        super.setRows(rows);
    }

    /**
     * Gets the number of rows in the editor. If the number of rows is set to 0,
     * the actual number of displayed rows is determined implicitly by the
     * adapter.
     * 
     * @return number of explicitly set rows.
     */
    @Override
    public int getRows() {
        // TODO implement here once the API from TextField is removed
        return super.getRows();
    }

    /**
     * Sets the editor's word-wrap mode on or off.
     * 
     * @param wordwrap
     *            the boolean value specifying if the editor should be in
     *            word-wrap mode after the call or not.
     */
    @Override
    public void setWordwrap(boolean wordwrap) {
        // TODO implement here once the API from TextField is removed
        super.setWordwrap(wordwrap);
    }

    /**
     * Tests if the editor is in word-wrap mode.
     * 
     * @return <code>true</code> if the component is in the word-wrap mode,
     *         <code>false</code> if not.
     */
    @Override
    public boolean isWordwrap() {
        // TODO implement here once the API from TextField is removed
        return super.isWordwrap();
    }

}
