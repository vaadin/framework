/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VTextField;
import com.vaadin.ui.ClientWidget.LoadStyle;

/**
 * <p>
 * A text editor component that can be bound to any bindable Property. The text
 * editor supports both multiline and single line modes, default is one-line
 * mode.
 * </p>
 * 
 * <p>
 * Since <code>TextField</code> extends <code>AbstractField</code> it implements
 * the {@link com.vaadin.data.Buffered} interface. A <code>TextField</code> is
 * in write-through mode by default, so
 * {@link com.vaadin.ui.AbstractField#setWriteThrough(boolean)} must be called
 * to enable buffering.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
@ClientWidget(value = VTextField.class, loadStyle = LoadStyle.EAGER)
public class TextField extends AbstractTextField implements
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    /**
     * Tells if input is used to enter sensitive information that is not echoed
     * to display. Typically passwords.
     */
    private boolean secret = false;

    /**
     * Number of visible rows in a multiline TextField. Value 0 implies a
     * single-line text-editor.
     */
    private int rows = 0;

    /**
     * Tells if word-wrapping should be used in multiline mode.
     */
    private boolean wordwrap = true;

    /**
     * Number of visible columns in the TextField.
     */
    private int columns = 0;

    private String inputPrompt = null;

    private int selectionPosition = -1;
    private int selectionLength;

    /**
     * Constructs an empty <code>TextField</code> with no caption.
     */
    public TextField() {
        setValue("");
    }

    /**
     * Constructs an empty <code>TextField</code> with given caption.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     */
    public TextField(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new <code>TextField</code> that's bound to the specified
     * <code>Property</code> and has no caption.
     * 
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public TextField(Property dataSource) {
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a new <code>TextField</code> that's bound to the specified
     * <code>Property</code> and has the given caption <code>String</code>.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public TextField(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a new <code>TextField</code> with the given caption and
     * initial text contents. The editor constructed this way will not be bound
     * to a Property unless
     * {@link com.vaadin.data.Property.Viewer#setPropertyDataSource(Property)}
     * is called to bind it.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param text
     *            the initial text content of the editor.
     */
    public TextField(String caption, String value) {
        setValue(value);
        setCaption(caption);
    }

    /**
     * Gets the secret property on and off. If a field is used to enter
     * secretinformation the information is not echoed to display.
     * 
     * @return <code>true</code> if the field is used to enter secret
     *         information, <code>false</code> otherwise.
     * 
     * @deprecated use {@link PasswordField} instead
     */
    @Deprecated
    public boolean isSecret() {
        return secret;
    }

    /**
     * Sets the secret property on and off. If a field is used to enter
     * secretinformation the information is not echoed to display.
     * 
     * @param secret
     *            the value specifying if the field is used to enter secret
     *            information.
     * @deprecated use {@link PasswordField} instead
     */
    @Deprecated
    public void setSecret(boolean secret) {
        if (this.secret != secret) {
            this.secret = secret;
            requestRepaint();
        }
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (isSecret()) {
            target.addAttribute("secret", true);
        }
        // Adds the number of column and rows
        final int columns = getColumns();
        if (columns != 0) {
            target.addAttribute("cols", String.valueOf(columns));
        }
        final int rows = getRows();
        if (rows != 0) {
            target.addAttribute("rows", String.valueOf(rows));
            target.addAttribute("multiline", true);
            if (!isWordwrap()) {
                target.addAttribute("wordwrap", false);
            }
        }

        if (getInputPrompt() != null) {
            target.addAttribute("prompt", getInputPrompt());
        }

        if (selectionPosition != -1) {
            target.addAttribute("selpos", selectionPosition);
            target.addAttribute("sellen", selectionLength);
            selectionPosition = -1;
        }

        super.paintContent(target);
    }

    /**
     * Gets the number of rows in the editor. If the number of rows is set to 0,
     * the actual number of displayed rows is determined implicitly by the
     * adapter.
     * 
     * @return number of explicitly set rows.
     * @deprecated use {@link TextArea} component and the same method there.
     *             This method will be removed from TextField that is to be used
     *             for one line text input only in the next versions.
     * 
     */
    @Deprecated
    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows in the editor.
     * 
     * @param rows
     *            the number of rows for this editor.
     * 
     * @deprecated use {@link TextArea} component and the same method there.
     *             This method will be removed from TextField that is to be used
     *             for one line text input only in the next versions.
     */
    @Deprecated
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
     * Tests if the editor is in word-wrap mode.
     * 
     * @return <code>true</code> if the component is in the word-wrap mode,
     *         <code>false</code> if not.
     * @deprecated use {@link TextArea} component and the same method there.
     *             This method will be removed from TextField that is to be used
     *             for one line text input only in the next versions.
     */
    @Deprecated
    public boolean isWordwrap() {
        return wordwrap;
    }

    /**
     * Sets the editor's word-wrap mode on or off.
     * 
     * @param wordwrap
     *            the boolean value specifying if the editor should be in
     *            word-wrap mode after the call or not.
     * 
     * @deprecated use {@link TextArea} component and the same method there.
     *             This method will be removed from TextField that is to be used
     *             for one line text input only in the next versions.
     */
    @Deprecated
    public void setWordwrap(boolean wordwrap) {
        if (this.wordwrap != wordwrap) {
            this.wordwrap = wordwrap;
            requestRepaint();
        }
    }

    /**
     * Gets the number of columns in the editor. If the number of columns is set
     * 0, the actual number of displayed columns is determined implicitly by the
     * adapter.
     * 
     * @return the number of columns in the editor.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets the number of columns in the editor. If the number of columns is set
     * 0, the actual number of displayed columns is determined implicitly by the
     * adapter.
     * 
     * @param columns
     *            the number of columns to set.
     */
    public void setColumns(int columns) {
        if (columns < 0) {
            columns = 0;
        }
        this.columns = columns;
        requestRepaint();
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

    /**
     * Selects all text in the field.
     * 
     * @since 6.4
     */
    public void selectAll() {
        String text = getValue() == null ? "" : getValue().toString();
        setSelectionRange(0, text.length());
    }

    /**
     * Sets the range of text to be selected.
     * 
     * As a side effect the field will become focused.
     * 
     * @since 6.4
     * 
     * @param pos
     *            the position of the first character to be selected
     * @param length
     *            the number of characters to be selected
     */
    public void setSelectionRange(int pos, int length) {
        selectionPosition = pos;
        selectionLength = length;
        focus();
        requestRepaint();
    }

    /**
     * Sets the cursor position in the field. As a side effect the field will
     * become focused.
     * 
     * @since 6.4
     * 
     * @param pos
     *            the position for the cursor
     * */
    public void setCursorPosition(int pos) {
        setSelectionRange(pos, 0);
    }

}
