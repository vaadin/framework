/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier,
        FieldEvents.TextChangeNotifier {

    private static final String VAR_TEXT_CONTENT_DIFF = "tcd";

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

    private int lastKnownCursorPosition;

    private String lastKnownTextContent;

    /**
     * Flag indicating that a text change event is pending to be triggered.
     * Cleared by {@link #setInternalValue(Object)} and when the event is fired.
     */
    private boolean textChangeEventPending;

    /**
     * Flag used to determine wheter we are currently handling a state change
     * triggered by a user. Used to properly fire text change event before value
     * change event triggered by the client side.
     */
    private boolean changingVariables;

    private TextChangeEventMode textChangeEventMode = TextChangeEventMode.LAZY;

    private final int DEFAULT_TEXTCHANGE_TIMEOUT = 400;

    private int textChangeEventTimeout = DEFAULT_TEXTCHANGE_TIMEOUT;

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
     * @deprecated in 6.5 use {@link PasswordField} instead
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
     * @deprecated in 6.5 use {@link PasswordField} instead
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
        if (hasListeners(TextChangeEvent.class)) {
            target.addAttribute(VTextField.ATTR_TEXTCHANGE_EVENTMODE,
                    getTextChangeEventMode().toString());
            target.addAttribute(VTextField.ATTR_TEXTCHANGE_TIMEOUT,
                    getTextChangeTimeout());
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
     * @deprecated in 6.5 use {@link TextArea} component and the same method
     *             there. This method will be removed from TextField that is to
     *             be used for one line text input only in the next versions.
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
     * @deprecated in 6.5 use {@link TextArea} component and the same method
     *             there. This method will be removed from TextField that is to
     *             be used for one line text input only in the next versions.
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
     * @deprecated in 6.5 use {@link TextArea} component and the same method
     *             there. This method will be removed from TextField that is to
     *             be used for one line text input only in the next versions.
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

    /**
     * Gets the current (or the last known) text content in the field.
     * <p>
     * Note the text returned by this method is not necessary the same what is
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

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        if (variables.containsKey(VTextField.VAR_CURSOR)) {
            Integer object = (Integer) variables.get(VTextField.VAR_CURSOR);
            lastKnownCursorPosition = object.intValue();
        }
        if (variables.containsKey(VTextField.VAR_CUR_TEXT)) {
            /*
             * NOTE, we might want to develop this further so that on a value
             * change event the whole text content don't need to be sent from
             * the client to server. Just "commit" the value from currentText to
             * the value.
             */
            textChangeEventPending = true;
            handleInputEventTextChange(variables);
        }

        changingVariables = true;
        try {
            super.changeVariables(source, variables);
        } finally {
            changingVariables = false;
        }
        firePendingTextChangeEvent();
    }

    private void firePendingTextChangeEvent() {
        if (textChangeEventPending) {
            fireEvent(new TextChangeEventImpl(this));
            textChangeEventPending = false;
        }
    }

    @Override
    protected void setInternalValue(Object newValue) {
        if (changingVariables
                && !newValue.toString().equals(lastKnownTextContent)) {
            /*
             * Fire text change event before value change event if change is
             * coming from the client side.
             */
            lastKnownTextContent = newValue.toString();
            textChangeEventPending = true;
            firePendingTextChangeEvent();
        }

        super.setInternalValue(newValue);
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

    /* ** Text Change Events ** */

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

        private TextChangeEventImpl(final TextField tf) {
            super(tf);
            curText = tf.getCurrentTextContent();
            cursorPosition = tf.getCursorPosition();
        }

        @Override
        public TextField getComponent() {
            return (TextField) super.getComponent();
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

}
