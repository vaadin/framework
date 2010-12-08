/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VTextArea;

/**
 * A text field that supports multi line editing.
 */
@ClientWidget(VTextArea.class)
public class TextArea extends AbstractTextField {

    private static final int DEFAULT_ROWS = 5;

    /**
     * Number of visible rows in the text area.
     */
    private int rows = DEFAULT_ROWS;

    /**
     * Tells if word-wrapping should be used in the text area.
     */
    private boolean wordwrap = true;

    /**
     * Constructs an empty TextArea.
     */
    public TextArea() {
        setValue("");
    }

    /**
     * Constructs an empty TextArea with given caption.
     * 
     * @param caption
     *            the caption for the field.
     */
    public TextArea(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a TextArea with given property data source.
     * 
     * @param dataSource
     *            the data source for the field
     */
    public TextArea(Property dataSource) {
        this();
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a TextArea with given caption and property data source.
     * 
     * @param caption
     *            the caption for the field
     * @param dataSource
     *            the data source for the field
     */
    public TextArea(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a TextArea with given caption and value.
     * 
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field
     */
    public TextArea(String caption, String value) {
        this(caption);
        setValue(value);

    }

    /**
     * Sets the number of rows in the text area.
     * 
     * @param rows
     *            the number of rows for this text area.
     */
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        if (this.rows != rows) {
            this.rows = rows;
            requestRepaint();
        }
    }

    /**
     * Gets the number of rows in the text area.
     * 
     * @return number of explicitly set rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the text area's word-wrap mode on or off.
     * 
     * @param wordwrap
     *            the boolean value specifying if the text area should be in
     *            word-wrap mode.
     */
    public void setWordwrap(boolean wordwrap) {
        if (this.wordwrap != wordwrap) {
            this.wordwrap = wordwrap;
            requestRepaint();
        }
    }

    /**
     * Tests if the text area is in word-wrap mode.
     * 
     * @return <code>true</code> if the component is in word-wrap mode,
     *         <code>false</code> if not.
     */
    public boolean isWordwrap() {
        return wordwrap;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        target.addAttribute("rows", getRows());

        if (!isWordwrap()) {
            // Wordwrap is only painted if turned off to minimize communications
            target.addAttribute("wordwrap", false);
        }

    }
}
