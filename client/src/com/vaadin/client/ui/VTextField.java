/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.textfield.TextFieldConstants;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author Vaadin Ltd.
 * 
 */
public class VTextField extends TextBoxBase implements Field, ChangeHandler,
        FocusHandler, BlurHandler, KeyDownHandler {

    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "v-textfield";
    /**
     * This CSS classname is added to the input node on hover.
     */
    public static final String CLASSNAME_FOCUS = "focus";

    /** For internal use only. May be removed or replaced in the future. */
    public String paintableId;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public String valueBeforeEdit = null;

    /**
     * Set to false if a text change event has been sent since the last value
     * change event. This means that {@link #valueBeforeEdit} should not be
     * trusted when determining whether a text change even should be sent.
     */
    private boolean valueBeforeEditIsSynced = true;

    private boolean immediate = false;
    private int maxLength = -1;

    private static final String CLASSNAME_PROMPT = "prompt";
    private static final String TEXTCHANGE_MODE_TIMEOUT = "TIMEOUT";

    private String inputPrompt = null;
    private boolean prompting = false;
    private int lastCursorPos = -1;

    // used while checking if FF has set input prompt as value
    private boolean possibleInputError = false;

    public VTextField() {
        this(DOM.createInputText());
    }

    protected VTextField(Element node) {
        super(node);
        setStyleName(CLASSNAME);
        addChangeHandler(this);
        if (BrowserInfo.get().isIE() || BrowserInfo.get().isFirefox()) {
            addKeyDownHandler(this);
        }
        addFocusHandler(this);
        addBlurHandler(this);
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     * <p>
     * TODO When GWT adds ONCUT, add it there and remove workaround. See
     * http://code.google.com/p/google-web-toolkit/issues/detail?id=4030
     * <p>
     * Also note that the cut/paste are not totally crossbrowsers compatible.
     * E.g. in Opera mac works via context menu, but on via File->Paste/Cut.
     * Opera might need the polling method for 100% working textchanceevents.
     * Eager polling for a change is bit dum and heavy operation, so I guess we
     * should first try to survive without.
     */
    public static final int TEXTCHANGE_EVENTS = Event.ONPASTE | Event.KEYEVENTS
            | Event.ONMOUSEUP;

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

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
                client.updateVariable(paintableId,
                        TextFieldConstants.VAR_CUR_TEXT, text, true);

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

    /** For internal use only. May be removed or replaced in the future. */
    public boolean listenTextChangeEvents;
    /** For internal use only. May be removed or replaced in the future. */
    public String textChangeEventMode;
    public int textChangeEventTimeout;

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

    /** For internal use only. May be removed or replaced in the future. */
    public void updateFieldContent(final String text) {
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

    /** For internal use only. May be removed or replaced in the future. */
    public native void attachCutEventListener(Element el)
    /*-{
        var me = this;
        el.oncut = $entry(function() {
            me.@com.vaadin.client.ui.VTextField::onCut()();
        });
    }-*/;

    protected native void detachCutEventListener(Element el)
    /*-{
        el.oncut = null;
    }-*/;

    private void onDrop() {
        if (focusedTextField == this) {
            return;
        }
        updateText(false);
    }

    private void updateText(boolean blurred) {
        String text = getText();
        setPrompting(inputPrompt != null && (text == null || text.isEmpty()));
        if (prompting) {
            setText(isReadOnly() ? "" : inputPrompt);
            if (blurred) {
                addStyleDependentName(CLASSNAME_PROMPT);
            }
        }

        valueChange(blurred);
    }

    private void scheduleOnDropEvent() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                onDrop();
            }
        });
    }

    private native void attachDropEventListener(Element el)
    /*-{
        var me = this;
        el.ondrop = $entry(function() {
            me.@com.vaadin.client.ui.VTextField::scheduleOnDropEvent()();
        });
    }-*/;

    private native void detachDropEventListener(Element el)
    /*-{
        el.ondrop = null;
    }-*/;

    @Override
    protected void onDetach() {
        super.onDetach();
        detachCutEventListener(getElement());
        if (focusedTextField == this) {
            focusedTextField = null;
        }
        if (BrowserInfo.get().isFirefox()) {
            removeOnInputListener(getElement());
            detachDropEventListener(getElement());
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (listenTextChangeEvents) {
            detachCutEventListener(getElement());
        }
        if (BrowserInfo.get().isFirefox()) {
            // Workaround for FF setting input prompt as the value if esc is
            // pressed while the field is focused and empty (#8051).
            addOnInputListener(getElement());
            // Workaround for FF updating component's internal value after
            // having drag-and-dropped text from another element (#14056)
            attachDropEventListener(getElement());
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setMaxLength(int newMaxLength) {
        if (newMaxLength == maxLength) {
            return;
        }
        maxLength = newMaxLength;
        updateMaxLength(maxLength);
    }

    /**
     * This method is responsible for updating the DOM or otherwise ensuring
     * that the given max length is enforced. Called when the max length for the
     * field has changed.
     * 
     * @param maxLength
     *            The new max length
     */
    protected void updateMaxLength(int maxLength) {
        if (maxLength >= 0) {
            getElement().setPropertyInt("maxLength", maxLength);
        } else {
            getElement().removeAttribute("maxLength");

        }
        setMaxLengthToElement(maxLength);
    }

    protected void setMaxLengthToElement(int newMaxLength) {
        if (newMaxLength >= 0) {
            getElement().setPropertyInt("maxLength", newMaxLength);
        } else {
            getElement().removeAttribute("maxLength");
        }
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
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
                client.updateVariable(paintableId, "text", newText, false);
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
                client.updateVariable(paintableId,
                        TextFieldConstants.VAR_CURSOR, cursorPos, false);
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

    @Override
    public void onFocus(FocusEvent event) {
        addStyleDependentName(CLASSNAME_FOCUS);
        if (prompting) {
            setText("");
            removeStyleDependentName(CLASSNAME_PROMPT);
            setPrompting(false);
        }
        focusedTextField = this;
        if (client != null && client.hasEventListeners(this, EventId.FOCUS)) {
            client.updateVariable(paintableId, EventId.FOCUS, "", true);
        }
    }

    @Override
    public void onBlur(BlurEvent event) {
        // this is called twice on Chrome when e.g. changing tab while prompting
        // field focused - do not change settings on the second time
        if (focusedTextField != this) {
            return;
        }
        removeStyleDependentName(CLASSNAME_FOCUS);
        focusedTextField = null;
        updateText(true);
    }

    private void setPrompting(boolean prompting) {
        this.prompting = prompting;
    }

    public void setColumns(int columns) {
        if (columns <= 0) {
            return;
        }

        setWidth(columns + "em");
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (BrowserInfo.get().isIE()
                && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            // IE does not send change events when pressing enter in a text
            // input so we handle it using a key listener instead
            valueChange(false);
        } else if (BrowserInfo.get().isFirefox()
                && event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE
                && getText().equals("")) {
            // check after onInput event if inputPrompt has appeared as the
            // value of the field
            possibleInputError = true;
        }
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
    }

    protected boolean isWordwrap() {
        String wrap = getElement().getAttribute("wrap");
        return !"off".equals(wrap);
    }

    private native void addOnInputListener(Element el)
    /*-{
        var self = this; 
        el.oninput = $entry(function() {
            self.@com.vaadin.client.ui.VTextField::checkForInputError()();
        }); 
    }-*/;

    private native void removeOnInputListener(Element el)
    /*-{
        el.oninput = null;
    }-*/;

    private void checkForInputError() {
        if (possibleInputError && getText().equals(inputPrompt)) {
            setText("");
        }
        possibleInputError = false;
    }
}
