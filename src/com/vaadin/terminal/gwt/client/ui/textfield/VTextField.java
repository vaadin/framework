/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.textfield;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.Field;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author Vaadin Ltd.
 * 
 */
public class VTextField extends TextBoxBase implements Field, ChangeHandler,
        FocusHandler, BlurHandler, KeyDownHandler {

    public static final String VAR_CUR_TEXT = "curText";

    public static final String ATTR_NO_VALUE_CHANGE_BETWEEN_PAINTS = "nvc";
    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "v-textfield";
    /**
     * This CSS classname is added to the input node on hover.
     */
    public static final String CLASSNAME_FOCUS = "focus";

    protected String paintableId;

    protected ApplicationConnection client;

    protected String valueBeforeEdit = null;

    /**
     * Set to false if a text change event has been sent since the last value
     * change event. This means that {@link #valueBeforeEdit} should not be
     * trusted when determining whether a text change even should be sent.
     */
    private boolean valueBeforeEditIsSynced = true;

    protected boolean immediate = false;
    private int maxLength = -1;

    private static final String CLASSNAME_PROMPT = "prompt";
    protected static final String ATTR_INPUTPROMPT = "prompt";
    public static final String ATTR_TEXTCHANGE_TIMEOUT = "iet";
    public static final String VAR_CURSOR = "c";
    public static final String ATTR_TEXTCHANGE_EVENTMODE = "iem";
    protected static final String TEXTCHANGE_MODE_EAGER = "EAGER";
    private static final String TEXTCHANGE_MODE_TIMEOUT = "TIMEOUT";

    protected String inputPrompt = null;
    private boolean prompting = false;
    private int lastCursorPos = -1;
    private boolean wordwrap = true;

    public VTextField() {
        this(DOM.createInputText());
    }

    protected VTextField(Element node) {
        super(node);
        setStyleName(CLASSNAME);
        addChangeHandler(this);
        if (BrowserInfo.get().isIE()) {
            // IE does not send change events when pressing enter in a text
            // input so we handle it using a key listener instead
            addKeyDownHandler(this);
        }
        addFocusHandler(this);
        addBlurHandler(this);
        sinkEvents(VTooltip.TOOLTIP_EVENTS);
    }

    /*
     * TODO When GWT adds ONCUT, add it there and remove workaround. See
     * http://code.google.com/p/google-web-toolkit/issues/detail?id=4030
     * 
     * Also note that the cut/paste are not totally crossbrowsers compatible.
     * E.g. in Opera mac works via context menu, but on via File->Paste/Cut.
     * Opera might need the polling method for 100% working textchanceevents.
     * Eager polling for a change is bit dum and heavy operation, so I guess we
     * should first try to survive without.
     */
    protected static final int TEXTCHANGE_EVENTS = Event.ONPASTE
            | Event.KEYEVENTS | Event.ONMOUSEUP;

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (client != null) {
            client.handleTooltipEvent(event, this);
        }

        if (listenTextChangeEvents
                && (event.getTypeInt() & TEXTCHANGE_EVENTS) == event
                        .getTypeInt()) {
            deferTextChangeEvent();
        }

    }

    /*
     * TODO optimize this so that only changes are sent + make the value change
     * event just a flag that moves the current text to value
     */
    private String lastTextChangeString = null;

    private String getLastCommunicatedString() {
        return lastTextChangeString;
    }

    private void communicateTextValueToServer() {
        String text = getText();
        if (prompting) {
            // Input prompt visible, text is actually ""
            text = "";
        }
        if (!text.equals(getLastCommunicatedString())) {
            if (valueBeforeEditIsSynced && text.equals(valueBeforeEdit)) {
                /*
                 * Value change for the current text has been enqueued since the
                 * last text change event was sent, but we can't know that it
                 * has been sent to the server. Ensure that all pending changes
                 * are sent now. Sending a value change without a text change
                 * will simulate a TextChangeEvent on the server.
                 */
                client.sendPendingVariableChanges();
            } else {
                // Default case - just send an immediate text change message
                client.updateVariable(paintableId, VAR_CUR_TEXT, text, true);

                // Shouldn't investigate valueBeforeEdit to avoid duplicate text
                // change events as the states are not in sync any more
                valueBeforeEditIsSynced = false;
            }
            lastTextChangeString = text;
        }
    }

    private Timer textChangeEventTrigger = new Timer() {

        @Override
        public void run() {
            if (isAttached()) {
                updateCursorPosition();
                communicateTextValueToServer();
                scheduled = false;
            }
        }
    };
    private boolean scheduled = false;
    protected boolean listenTextChangeEvents;
    protected String textChangeEventMode;
    protected int textChangeEventTimeout;

    private void deferTextChangeEvent() {
        if (textChangeEventMode.equals(TEXTCHANGE_MODE_TIMEOUT) && scheduled) {
            return;
        } else {
            textChangeEventTrigger.cancel();
        }
        textChangeEventTrigger.schedule(getTextChangeEventTimeout());
        scheduled = true;
    }

    private int getTextChangeEventTimeout() {
        return textChangeEventTimeout;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        boolean wasReadOnly = isReadOnly();

        if (readOnly) {
            setTabIndex(-1);
        } else if (wasReadOnly && !readOnly && getTabIndex() == -1) {
            /*
             * Need to manually set tab index to 0 since server will not send
             * the tab index if it is 0.
             */
            setTabIndex(0);
        }

        super.setReadOnly(readOnly);
    }

    protected void updateFieldContent(final String text) {
        setPrompting(inputPrompt != null && focusedTextField != this
                && (text.equals("")));

        String fieldValue;
        if (prompting) {
            fieldValue = isReadOnly() ? "" : inputPrompt;
            addStyleDependentName(CLASSNAME_PROMPT);
        } else {
            fieldValue = text;
            removeStyleDependentName(CLASSNAME_PROMPT);
        }
        setText(fieldValue);

        lastTextChangeString = valueBeforeEdit = text;
        valueBeforeEditIsSynced = true;
    }

    protected void onCut() {
        if (listenTextChangeEvents) {
            deferTextChangeEvent();
        }
    }

    protected native void attachCutEventListener(Element el)
    /*-{
        var me = this;
        el.oncut = function() {
            me.@com.vaadin.terminal.gwt.client.ui.textfield.VTextField::onCut()();
        };
    }-*/;

    protected native void detachCutEventListener(Element el)
    /*-{
        el.oncut = null;
    }-*/;

    @Override
    protected void onDetach() {
        super.onDetach();
        detachCutEventListener(getElement());
        if (focusedTextField == this) {
            focusedTextField = null;
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (listenTextChangeEvents) {
            detachCutEventListener(getElement());
        }
    }

    protected void setMaxLength(int newMaxLength) {
        if (newMaxLength >= 0) {
            maxLength = newMaxLength;
            if (getElement().getTagName().toLowerCase().equals("textarea")) {
                // NOP no maxlength property for textarea
            } else {
                getElement().setPropertyInt("maxLength", maxLength);
            }
        } else if (maxLength != -1) {
            if (getElement().getTagName().toLowerCase().equals("textarea")) {
                // NOP no maxlength property for textarea
            } else {
                getElement().removeAttribute("maxLength");
            }
            maxLength = -1;
        }

    }

    public int getMaxLength() {
        return maxLength;
    }

    public void onChange(ChangeEvent event) {
        valueChange(false);
    }

    /**
     * Called when the field value might have changed and/or the field was
     * blurred. These are combined so the blur event is sent in the same batch
     * as a possible value change event (these are often connected).
     * 
     * @param blurred
     *            true if the field was blurred
     */
    public void valueChange(boolean blurred) {
        if (client != null && paintableId != null) {
            boolean sendBlurEvent = false;
            boolean sendValueChange = false;

            if (blurred && client.hasEventListeners(this, EventId.BLUR)) {
                sendBlurEvent = true;
                client.updateVariable(paintableId, EventId.BLUR, "", false);
            }

            String newText = getText();
            if (!prompting && newText != null
                    && !newText.equals(valueBeforeEdit)) {
                sendValueChange = immediate;
                client.updateVariable(paintableId, "text", getText(), false);
                valueBeforeEdit = newText;
                valueBeforeEditIsSynced = true;
            }

            /*
             * also send cursor position, no public api yet but for easier
             * extension
             */
            updateCursorPosition();

            if (sendBlurEvent || sendValueChange) {
                /*
                 * Avoid sending text change event as we will simulate it on the
                 * server side before value change events.
                 */
                textChangeEventTrigger.cancel();
                scheduled = false;
                client.sendPendingVariableChanges();
            }
        }
    }

    /**
     * Updates the cursor position variable if it has changed since the last
     * update.
     * 
     * @return true iff the value was updated
     */
    protected boolean updateCursorPosition() {
        if (Util.isAttachedAndDisplayed(this)) {
            int cursorPos = getCursorPos();
            if (lastCursorPos != cursorPos) {
                client.updateVariable(paintableId, VAR_CURSOR, cursorPos, false);
                lastCursorPos = cursorPos;
                return true;
            }
        }
        return false;
    }

    private static VTextField focusedTextField;

    public static void flushChangesFromFocusedTextField() {
        if (focusedTextField != null) {
            focusedTextField.onChange(null);
        }
    }

    public void onFocus(FocusEvent event) {
        addStyleDependentName(CLASSNAME_FOCUS);
        if (prompting) {
            setText("");
            removeStyleDependentName(CLASSNAME_PROMPT);
            setPrompting(false);
        }
        focusedTextField = this;
        if (client.hasEventListeners(this, EventId.FOCUS)) {
            client.updateVariable(paintableId, EventId.FOCUS, "", true);
        }
    }

    public void onBlur(BlurEvent event) {
        removeStyleDependentName(CLASSNAME_FOCUS);
        focusedTextField = null;
        String text = getText();
        setPrompting(inputPrompt != null && (text == null || "".equals(text)));
        if (prompting) {
            setText(isReadOnly() ? "" : inputPrompt);
            addStyleDependentName(CLASSNAME_PROMPT);
        }

        valueChange(true);
    }

    private void setPrompting(boolean prompting) {
        this.prompting = prompting;
    }

    public void setColumns(int columns) {
        setColumns(getElement(), columns);
    }

    private native void setColumns(Element e, int c)
    /*-{
    try {
    	switch(e.tagName.toLowerCase()) {
    		case "input":
    			//e.size = c;
    			e.style.width = c+"em";
    			break;
    		case "textarea":
    			//e.cols = c;
    			e.style.width = c+"em";
    			break;
    		default:;
    	}
    } catch (e) {}
    }-*/;

    // Here for backward compatibility; to be moved to TextArea
    public void setWordwrap(boolean enabled) {
        if (enabled == wordwrap) {
            return; // No change
        }

        if (enabled) {
            getElement().removeAttribute("wrap");
            getElement().getStyle().clearOverflow();
        } else {
            getElement().setAttribute("wrap", "off");
            getElement().getStyle().setOverflow(Overflow.AUTO);
        }
        if (BrowserInfo.get().isSafari4()) {
            // Force redraw as Safari 4 does not properly update the screen
            Util.forceWebkitRedraw(getElement());
        } else if (BrowserInfo.get().isOpera()) {
            // Opera fails to dynamically update the wrap attribute so we detach
            // and reattach the whole TextArea.
            Util.detachAttach(getElement());
        }
        wordwrap = enabled;
    }

    public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            valueChange(false);
        }
    }
}
