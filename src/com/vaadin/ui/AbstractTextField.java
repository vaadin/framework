package com.vaadin.ui;

import java.text.Format;
import java.util.Map;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

public abstract class AbstractTextField extends AbstractField {

    /**
     * Value formatter used to format the string contents.
     */
    private Format format;

    /**
     * Null representation.
     */
    private String nullRepresentation = "null";
    /**
     * Is setting to null from non-null value allowed by setting with null
     * representation .
     */
    private boolean nullSettingAllowed = false;
    /**
     * Maximum character count in text field.
     */
    private int maxLength = -1;

    public AbstractTextField() {
        super();
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (getMaxLength() >= 0) {
            target.addAttribute("maxLength", getMaxLength());
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
        if (height > 1 && this instanceof TextField) {
            /*
             * In html based terminals we most commonly want to make component
             * to be textarea if height is defined. Setting row field above 0
             * will render component as textarea.
             */

            ((TextField) this).setRows(2);
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

    @Override
    public Class getType() {
        return String.class;
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