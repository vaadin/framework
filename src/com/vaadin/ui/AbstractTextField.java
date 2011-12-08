/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.text.Format;
import java.util.Map;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.FieldEvents.TextChangeNotifier;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VTextField;

public abstract class AbstractTextField extends AbstractField implements
        BlurNotifier, FocusNotifier, TextChangeNotifier {

    /**
     * Value formatter used to format the string contents.
     */
    @Deprecated
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

    /**
     * Number of visible columns in the TextField.
     */
    private int columns = 0;

    /**
     * The prompt to display in an empty field. Null when disabled.
     */
    private String inputPrompt = null;

    /**
     * The text content when the last messages to the server was sent. Cleared
     * when value is changed.
     */
    private String lastKnownTextContent;

    /**
     * The position of the cursor when the last message to the server was sent.
     */
    private int lastKnownCursorPosition;

    /**
     * Flag indicating that a text change event is pending to be triggered.
     * Cleared by {@link #setInternalValue(Object)} and when the event is fired.
     */
    private boolean textChangeEventPending;

    private TextChangeEventMode textChangeEventMode = TextChangeEventMode.LAZY;

    private final int DEFAULT_TEXTCHANGE_TIMEOUT = 400;

    private int textChangeEventTimeout = DEFAULT_TEXTCHANGE_TIMEOUT;

    /**
     * Temporarily holds the new selection position. Cleared on paint.
     */
    private int selectionPosition = -1;

    /**
     * Temporarily holds the new selection length.
     */
    private int selectionLength;

    /**
     * Flag used to determine whether we are currently handling a state change
     * triggered by a user. Used to properly fire text change event before value
     * change event triggered by the client side.
     */
    private boolean changingVariables;

    protected AbstractTextField() {
        super();
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        if (getMaxLength() >= 0) {
            target.addAttribute("maxLength", getMaxLength());
        }

        // Adds the number of column and rows
        final int columns = getColumns();
        if (columns != 0) {
            target.addAttribute("cols", String.valueOf(columns));
        }

        if (getInputPrompt() != null) {
            target.addAttribute("prompt", getInputPrompt());
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

        if (selectionPosition != -1) {
            target.addAttribute("selpos", selectionPosition);
            target.addAttribute("sellen", selectionLength);
            selectionPosition = -1;
        }

        if (hasListeners(TextChangeEvent.class)) {
            target.addAttribute(VTextField.ATTR_TEXTCHANGE_EVENTMODE,
                    getTextChangeEventMode().toString());
            target.addAttribute(VTextField.ATTR_TEXTCHANGE_TIMEOUT,
                    getTextChangeTimeout());
            if (lastKnownTextContent != null) {
                /*
                 * The field has be repainted for some reason (e.g. caption,
                 * size, stylename), but the value has not been changed since
                 * the last text change event. Let the client side know about
                 * the value the server side knows. Client side may then ignore
                 * the actual value, depending on its state.
                 */
                target.addAttribute(
                        VTextField.ATTR_NO_VALUE_CHANGE_BETWEEN_PAINTS, true);
            }
        }

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
        changingVariables = true;

        try {
            super.changeVariables(source, variables);

            if (variables.containsKey(VTextField.VAR_CURSOR)) {
                Integer object = (Integer) variables.get(VTextField.VAR_CURSOR);
                lastKnownCursorPosition = object.intValue();
            }

            if (variables.containsKey(VTextField.VAR_CUR_TEXT)) {
                /*
                 * NOTE, we might want to develop this further so that on a
                 * value change event the whole text content don't need to be
                 * sent from the client to server. Just "commit" the value from
                 * currentText to the value.
                 */
                handleInputEventTextChange(variables);
            }

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

                    // If the modified status changes, or if we have a
                    // formatter, repaint is needed after all.
                    if (format != null || wasModified != isModified()) {
                        requestRepaint();
                    }
                }
            }
            firePendingTextChangeEvent();

            if (variables.containsKey(FocusEvent.EVENT_ID)) {
                fireEvent(new FocusEvent(this));
            }
            if (variables.containsKey(BlurEvent.EVENT_ID)) {
                fireEvent(new BlurEvent(this));
            }
        } finally {
            changingVariables = false;

        }

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
        requestRepaint();
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
     *            Should the null-string representation always be converted to
     *            null-values.
     * @see TextField#getNullRepresentation()
     */
    public void setNullSettingAllowed(boolean nullSettingAllowed) {
        this.nullSettingAllowed = nullSettingAllowed;
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

    /* ** Text Change Events ** */

    private void firePendingTextChangeEvent() {
        if (textChangeEventPending) {
            textChangeEventPending = false;
            fireEvent(new TextChangeEventImpl(this));
        }
    }

    @Override
    protected void setInternalValue(Object newValue) {
        if (changingVariables && !textChangeEventPending) {

            /*
             * TODO check for possible (minor?) issue (not tested)
             * 
             * -field with e.g. PropertyFormatter.
             * 
             * -TextChangeListener and it changes value.
             * 
             * -if formatter again changes the value, do we get an extra
             * simulated text change event ?
             */

            /*
             * Fire a "simulated" text change event before value change event if
             * change is coming from the client side.
             * 
             * Iff there is both value change and textChangeEvent in same
             * variable burst, it is a text field in non immediate mode and the
             * text change event "flushed" queued value change event. In this
             * case textChangeEventPending flag is already on and text change
             * event will be fired after the value change event.
             */
            if (newValue == null && lastKnownTextContent != null
                    && !lastKnownTextContent.equals(getNullRepresentation())) {
                // Value was changed from something to null representation
                lastKnownTextContent = getNullRepresentation();
                textChangeEventPending = true;
            } else if (newValue != null
                    && !newValue.toString().equals(lastKnownTextContent)) {
                // Value was changed to something else than null representation
                lastKnownTextContent = newValue.toString();
                textChangeEventPending = true;
            }
            firePendingTextChangeEvent();
        }

        /*
         * Reset lastKnownTextContent field on value change. We know the value
         * now.
         */
        lastKnownTextContent = null;
        super.setInternalValue(newValue);
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        super.setValue(newValue);
        /*
         * Make sure w reset lastKnownTextContent field on value change. The
         * clearing must happen here as well because TextChangeListener can
         * revert the original value. Client must respect the value in this
         * case. AbstractField optimizes value change if the existing value is
         * reset. Also we need to force repaint if the flag is on.
         */
        if(lastKnownTextContent != null) {
            lastKnownTextContent = null;
            requestRepaint();
        }
    }

    private void handleInputEventTextChange(Map<String, Object> variables) {
        /*
         * TODO we could vastly optimize the communication of values by using
         * some sort of diffs instead of always sending the whole text content.
         * Also on value change events we could use the mechanism.
         */
        String object = (String) variables.get(VTextField.VAR_CUR_TEXT);
        lastKnownTextContent = object;
        textChangeEventPending = true;
    }

    /**
     * Sets the mode how the TextField triggers {@link TextChangeEvent}s.
     * 
     * @param inputEventMode
     *            the new mode
     * 
     * @see TextChangeEventMode
     */
    public void setTextChangeEventMode(TextChangeEventMode inputEventMode) {
        textChangeEventMode = inputEventMode;
        requestRepaint();
    }

    /**
     * @return the mode used to trigger {@link TextChangeEvent}s.
     */
    public TextChangeEventMode getTextChangeEventMode() {
        return textChangeEventMode;
    }

    /**
     * Different modes how the TextField can trigger {@link TextChangeEvent}s.
     */
    public enum TextChangeEventMode {

        /**
         * An event is triggered on each text content change, most commonly key
         * press events.
         */
        EAGER,
        /**
         * Each text change event in the UI causes the event to be communicated
         * to the application after a timeout. The length of the timeout can be
         * controlled with {@link TextField#setInputEventTimeout(int)}. Only the
         * last input event is reported to the server side if several text
         * change events happen during the timeout.
         * <p>
         * In case of a {@link ValueChangeEvent} the schedule is not kept
         * strictly. Before a {@link ValueChangeEvent} a {@link TextChangeEvent}
         * is triggered if the text content has changed since the previous
         * TextChangeEvent regardless of the schedule.
         */
        TIMEOUT,
        /**
         * An event is triggered when there is a pause of text modifications.
         * The length of the pause can be modified with
         * {@link TextField#setInputEventTimeout(int)}. Like with the
         * {@link #TIMEOUT} mode, an event is forced before
         * {@link ValueChangeEvent}s, even if the user did not keep a pause
         * while entering the text.
         * <p>
         * This is the default mode.
         */
        LAZY
    }

    public void addListener(TextChangeListener listener) {
        addListener(TextChangeListener.EVENT_ID, TextChangeEvent.class,
                listener, TextChangeListener.EVENT_METHOD);
    }

    public void removeListener(TextChangeListener listener) {
        removeListener(TextChangeListener.EVENT_ID, TextChangeEvent.class,
                listener);
    }

    /**
     * The text change timeout modifies how often text change events are
     * communicated to the application when {@link #getTextChangeEventMode()} is
     * {@link TextChangeEventMode#LAZY} or {@link TextChangeEventMode#TIMEOUT}.
     * 
     * 
     * @see #getTextChangeEventMode()
     * 
     * @param timeout
     *            the timeout in milliseconds
     */
    public void setTextChangeTimeout(int timeout) {
        textChangeEventTimeout = timeout;
        requestRepaint();
    }

    /**
     * Gets the timeout used to fire {@link TextChangeEvent}s when the
     * {@link #getTextChangeEventMode()} is {@link TextChangeEventMode#LAZY} or
     * {@link TextChangeEventMode#TIMEOUT}.
     * 
     * @return the timeout value in milliseconds
     */
    public int getTextChangeTimeout() {
        return textChangeEventTimeout;
    }

    public class TextChangeEventImpl extends TextChangeEvent {
        private String curText;
        private int cursorPosition;

        private TextChangeEventImpl(final AbstractTextField tf) {
            super(tf);
            curText = tf.getCurrentTextContent();
            cursorPosition = tf.getCursorPosition();
        }

        @Override
        public AbstractTextField getComponent() {
            return (AbstractTextField) super.getComponent();
        }

        @Override
        public String getText() {
            return curText;
        }

        @Override
        public int getCursorPosition() {
            return cursorPosition;
        }

    }

    /**
     * Gets the current (or the last known) text content in the field.
     * <p>
     * Note the text returned by this method is not necessary the same that is
     * returned by the {@link #getValue()} method. The value is updated when the
     * terminal fires a value change event via e.g. blurring the field or by
     * pressing enter. The value returned by this method is updated also on
     * {@link TextChangeEvent}s. Due to this high dependency to the terminal
     * implementation this method is (at least at this point) not published.
     * 
     * @return the text which is currently displayed in the field.
     */
    private String getCurrentTextContent() {
        if (lastKnownTextContent != null) {
            return lastKnownTextContent;
        } else {
            Object text = getValue();
            if (text == null) {
                return getNullRepresentation();
            }
            return text.toString();
        }
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
        lastKnownCursorPosition = pos;
    }

    /**
     * Returns the last known cursor position of the field.
     * 
     * <p>
     * Note that due to the client server nature or the GWT terminal, Vaadin
     * cannot provide the exact value of the cursor position in most situations.
     * The value is updated only when the client side terminal communicates to
     * TextField, like on {@link ValueChangeEvent}s and {@link TextChangeEvent}
     * s. This may change later if a deep push integration is built to Vaadin.
     * 
     * @return the cursor position
     */
    public int getCursorPosition() {
        return lastKnownCursorPosition;
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