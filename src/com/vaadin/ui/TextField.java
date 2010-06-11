/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.text.Format;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
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
public class TextField extends AbstractField implements
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    /* Private members */

    /**
     * Value formatter used to format the string contents.
     */
    private Format format;

    /**
     * Number of visible columns in the TextField.
     */
    private int columns = 0;

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
     * Tells if input is used to enter sensitive information that is not echoed
     * to display. Typically passwords.
     */
    private boolean secret = false;

    /**
     * Null representation.
     */
    private String nullRepresentation = "null";

    /**
     * Is setting to null from non-null value allowed by setting with null
     * representation .
     */
    private boolean nullSettingAllowed = false;

    private String inputPrompt = null;

    /**
     * Maximum character count in text field.
     */
    private int maxLength = -1;

    /* Constructors */

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
        setValue("");
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

    /* Component basic features */

    /*
     * Paints this component. Don't add a JavaDoc comment here, we use the
     * default documentation from implemented interface.
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        // Sets the secret attribute
        if (isSecret()) {
            target.addAttribute("secret", true);
        }

        if (getMaxLength() >= 0) {
            target.addAttribute("maxLength", getMaxLength());
        }

        if (inputPrompt != null) {
            target.addAttribute("prompt", inputPrompt);
        }

        // Adds the number of column and rows
        final int columns = getColumns();
        final int rows = getRows();
        if (columns != 0) {
            target.addAttribute("cols", String.valueOf(columns));
        }
        if (rows != 0) {
            target.addAttribute("rows", String.valueOf(rows));
            target.addAttribute("multiline", true);
            if (!wordwrap) {
                target.addAttribute("wordwrap", false);
            }
        }

        // Adds the content as variable
        String value = getFormattedValue();
        if (value == null) {
            value = getNullRepresentation();
        }
        if (value == null) {
            throw new IllegalStateException(
                    "Null values are not allowed if the null-representation is null");
        }
        target.addVariable(this, "text", value);
    }

    /**
     * Gets the formatted string value. Sets the field value by using the
     * assigned Format.
     * 
     * @return the Formatted value.
     * @see #setFormat(Format)
     * @see Format
     * @deprecated
     */
    @Deprecated
    protected String getFormattedValue() {
        Object v = getValue();
        if (v == null) {
            return null;
        }
        return v.toString();
    }

    /*
     * Gets the value of the field, but uses formatter is given. Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Object getValue() {
        Object v = super.getValue();
        if (format == null || v == null) {
            return v;
        }
        try {
            return format.format(v);
        } catch (final IllegalArgumentException e) {
            return v;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractField#changeVariables(java.lang.Object,
     * java.util.Map)
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

        super.changeVariables(source, variables);

        // Sets the text
        if (variables.containsKey("text") && !isReadOnly()) {

            // Only do the setting if the string representation of the value
            // has been updated
            String newValue = (String) variables.get("text");

            // server side check for max length
            if (getMaxLength() != -1 && newValue.length() > getMaxLength()) {
                newValue = newValue.substring(0, getMaxLength());
            }
            final String oldValue = getFormattedValue();
            if (newValue != null
                    && (oldValue == null || isNullSettingAllowed())
                    && newValue.equals(getNullRepresentation())) {
                newValue = null;
            }
            if (newValue != oldValue
                    && (newValue == null || !newValue.equals(oldValue))) {
                boolean wasModified = isModified();
                setValue(newValue, true);

                // If the modified status changes, or if we have a formatter,
                // repaint is needed after all.
                if (format != null || wasModified != isModified()) {
                    requestRepaint();
                }
            }
        }

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }
        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }

    }

    /* Text field configuration */

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
     * Gets the number of rows in the editor. If the number of rows is set to 0,
     * the actual number of displayed rows is determined implicitly by the
     * adapter.
     * 
     * @return number of explicitly set rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows in the editor.
     * 
     * @param rows
     *            the number of rows for this editor.
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
     * Sets the height of the {@link TextField} instance.
     * 
     * <p>
     * Setting height for {@link TextField} also has a side-effect that puts
     * {@link TextField} into multiline mode (aka "textarea"). Multiline mode
     * can also be achieved by calling {@link #setRows(int)}. The height value
     * overrides the number of rows set by {@link #setRows(int)}.
     * <p>
     * If you want to set height of single line {@link TextField}, call
     * {@link #setRows(int)} with value 0 after setting the height. Setting rows
     * to 0 resets the side-effect.
     * 
     * @see com.vaadin.ui.AbstractComponent#setHeight(float, int)
     */
    @Override
    public void setHeight(float height, int unit) {
        super.setHeight(height, unit);
        if (height > 1) {
            /*
             * In html based terminals we most commonly want to make component
             * to be textarea if height is defined. Setting row field above 0
             * will render component as textarea.
             */
            rows = 2;
        }
    }

    /**
     * Sets the height of the {@link TextField} instance.
     * 
     * <p>
     * Setting height for {@link TextField} also has a side-effect that puts
     * {@link TextField} into multiline mode (aka "textarea"). Multiline mode
     * can also be achieved by calling {@link #setRows(int)}. The height value
     * overrides the number of rows set by {@link #setRows(int)}.
     * <p>
     * If you want to set height of single line {@link TextField}, call
     * {@link #setRows(int)} with value 0 after setting the height. Setting rows
     * to 0 resets the side-effect.
     * 
     * @see com.vaadin.ui.AbstractComponent#setHeight(java.lang.String)
     */
    @Override
    public void setHeight(String height) {
        // will call setHeight(float, int) the actually does the magic. Method
        // is overridden just to document side-effects.
        super.setHeight(height);
    }

    /**
     * Tests if the editor is in word-wrap mode.
     * 
     * @return <code>true</code> if the component is in the word-wrap mode,
     *         <code>false</code> if not.
     */
    public boolean isWordwrap() {
        return wordwrap;
    }

    /**
     * Sets the editor's word-wrap mode on or off.
     * 
     * @param wordwrap
     *            the boolean value specifying if the editor should be in
     *            word-wrap mode after the call or not.
     */
    public void setWordwrap(boolean wordwrap) {
        if (this.wordwrap != wordwrap) {
            this.wordwrap = wordwrap;
            requestRepaint();
        }
    }

    /* Property features */

    /*
     * Gets the edited property's type. Don't add a JavaDoc comment here, we use
     * the default documentation from implemented interface.
     */
    @Override
    public Class getType() {
        return String.class;
    }

    /**
     * Gets the secret property on and off. If a field is used to enter
     * secretinformation the information is not echoed to display.
     * 
     * @return <code>true</code> if the field is used to enter secret
     *         information, <code>false</code> otherwise.
     */
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
     */
    public void setSecret(boolean secret) {
        if (this.secret != secret) {
            this.secret = secret;
            requestRepaint();
        }
    }

    /**
     * Gets the null-string representation.
     * 
     * <p>
     * The null-valued strings are represented on the user interface by
     * replacing the null value with this string. If the null representation is
     * set null (not 'null' string), painting null value throws exception.
     * </p>
     * 
     * <p>
     * The default value is string 'null'.
     * </p>
     * 
     * @return the String Textual representation for null strings.
     * @see TextField#isNullSettingAllowed()
     */
    public String getNullRepresentation() {
        return nullRepresentation;
    }

    /**
     * Is setting nulls with null-string representation allowed.
     * 
     * <p>
     * If this property is true, writing null-representation string to text
     * field always sets the field value to real null. If this property is
     * false, null setting is not made, but the null values are maintained.
     * Maintenance of null-values is made by only converting the textfield
     * contents to real null, if the text field matches the null-string
     * representation and the current value of the field is null.
     * </p>
     * 
     * <p>
     * By default this setting is false
     * </p>
     * 
     * @return boolean Should the null-string represenation be always converted
     *         to null-values.
     * @see TextField#getNullRepresentation()
     */
    public boolean isNullSettingAllowed() {
        return nullSettingAllowed;
    }

    /**
     * Sets the null-string representation.
     * 
     * <p>
     * The null-valued strings are represented on the user interface by
     * replacing the null value with this string. If the null representation is
     * set null (not 'null' string), painting null value throws exception.
     * </p>
     * 
     * <p>
     * The default value is string 'null'
     * </p>
     * 
     * @param nullRepresentation
     *            Textual representation for null strings.
     * @see TextField#setNullSettingAllowed(boolean)
     */
    public void setNullRepresentation(String nullRepresentation) {
        this.nullRepresentation = nullRepresentation;
    }

    /**
     * Sets the null conversion mode.
     * 
     * <p>
     * If this property is true, writing null-representation string to text
     * field always sets the field value to real null. If this property is
     * false, null setting is not made, but the null values are maintained.
     * Maintenance of null-values is made by only converting the textfield
     * contents to real null, if the text field matches the null-string
     * representation and the current value of the field is null.
     * </p>
     * 
     * <p>
     * By default this setting is false.
     * </p>
     * 
     * @param nullSettingAllowed
     *            Should the null-string represenation be always converted to
     *            null-values.
     * @see TextField#getNullRepresentation()
     */
    public void setNullSettingAllowed(boolean nullSettingAllowed) {
        this.nullSettingAllowed = nullSettingAllowed;
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
     * Gets the value formatter of TextField.
     * 
     * @return the Format used to format the value.
     * @deprecated replaced by {@link com.vaadin.data.util.PropertyFormatter}
     */
    @Deprecated
    public Format getFormat() {
        return format;
    }

    /**
     * Gets the value formatter of TextField.
     * 
     * @param format
     *            the Format used to format the value. Null disables the
     *            formatting.
     * @deprecated replaced by {@link com.vaadin.data.util.PropertyFormatter}
     */
    @Deprecated
    public void setFormat(Format format) {
        this.format = format;
        requestRepaint();
    }

    @Override
    protected boolean isEmpty() {
        return super.isEmpty() || toString().length() == 0;
    }

    /**
     * Returns the maximum number of characters in the field. Value -1 is
     * considered unlimited. Terminal may however have some technical limits.
     * 
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum number of characters in the field. Value -1 is
     * considered unlimited. Terminal may however have some technical limits.
     * 
     * @param maxLength
     *            the maxLength to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        requestRepaint();
    }

    public void addListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    public void removeListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    public void addListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    public void removeListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

}
