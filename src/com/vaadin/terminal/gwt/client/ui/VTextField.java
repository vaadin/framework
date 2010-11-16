/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;

/**
 * This class represents a basic text input field with one row.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class VTextField extends TextBoxBase implements Paintable, Field,
        ChangeHandler, FocusHandler, BlurHandler, BeforeShortcutActionListener {

    public static final String VAR_CUR_TEXT = "curText";
    /**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "v-textfield";
    /**
     * This CSS classname is added to the input node on hover.
     */
    public static final String CLASSNAME_FOCUS = "focus";

    protected String id;

    protected ApplicationConnection client;

    private String valueBeforeEdit = null;

    private boolean immediate = false;
    private int extraHorizontalPixels = -1;
    private int extraVerticalPixels = -1;
    private int maxLength = -1;

    private static final String CLASSNAME_PROMPT = "prompt";
    private static final String ATTR_INPUTPROMPT = "prompt";
    public static final String ATTR_TEXTCHANGE_TIMEOUT = "iet";
    public static final String VAR_CURSOR = "c";
    public static final String ATTR_TEXTCHANGE_EVENTMODE = "iem";
    private static final String TEXTCHANGE_MODE_EAGER = "EAGER";
    private static final String TEXTCHANGE_MODE_TIMEOUT = "TIMEOUT";

    private String inputPrompt = null;
    private boolean prompting = false;
    private int lastCursorPos = -1;

    public VTextField() {
        this(DOM.createInputText());
    }

    protected VTextField(Element node) {
        super(node);
        if (BrowserInfo.get().getIEVersion() > 0
                && BrowserInfo.get().getIEVersion() < 8) {
            // Fixes IE margin problem (#2058)
            DOM.setStyleAttribute(node, "marginTop", "-1px");
            DOM.setStyleAttribute(node, "marginBottom", "-1px");
        }
        setStyleName(CLASSNAME);
        addChangeHandler(this);
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
    private static final int TEXTCHANGE_EVENTS = Event.ONPASTE
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

    private boolean communicateTextValueToServer() {
        String text = getText();
        if (!text.equals(getLastCommunicatedString())) {
            lastTextChangeString = text;
            client.updateVariable(id, VAR_CUR_TEXT, text, true);
            return true;
        }
        return false;
    }

    private Timer textChangeEventTrigger = new Timer() {

        @Override
        public void run() {
            updateCursorPosition();
            boolean textChanged = communicateTextValueToServer();
            if (textChanged) {
                client.sendPendingVariableChanges();
            }
            scheduled = false;
        }
    };
    private boolean scheduled = false;
    private boolean listenTextChangeEvents;
    private String textChangeEventMode;
    private int textChangeEventTimeout;

    private void deferTextChangeEvent() {
        if (textChangeEventMode.equals(TEXTCHANGE_MODE_TIMEOUT) && scheduled) {
            return;
        } else {
            textChangeEventTrigger.cancel();
        }
        textChangeEventTrigger.schedule(getInputEventTimeout());
        scheduled = true;
    }

    private int getInputEventTimeout() {
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

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        if (uidl.getBooleanAttribute("readonly")) {
            setReadOnly(true);
        } else {
            setReadOnly(false);
        }

        inputPrompt = uidl.getStringAttribute(ATTR_INPUTPROMPT);

        setMaxLength(uidl.hasAttribute("maxLength") ? uidl
                .getIntAttribute("maxLength") : -1);

        immediate = uidl.getBooleanAttribute("immediate");

        listenTextChangeEvents = client.hasEventListeners(this, "ie");
        if (listenTextChangeEvents) {
            textChangeEventMode = uidl
                    .getStringAttribute(ATTR_TEXTCHANGE_EVENTMODE);
            if (textChangeEventMode.equals(TEXTCHANGE_MODE_EAGER)) {
                textChangeEventTimeout = 1;
            } else {
                textChangeEventTimeout = uidl
                        .getIntAttribute(ATTR_TEXTCHANGE_TIMEOUT);
            }
            sinkEvents(TEXTCHANGE_EVENTS);
            attachCutEventListener(getElement());
        }

        if (uidl.hasAttribute("cols")) {
            setColumns(new Integer(uidl.getStringAttribute("cols")).intValue());
        }

        final String text = uidl.hasVariable("text") ? uidl
                .getStringVariable("text") : null;
        setPrompting(inputPrompt != null && focusedTextField != this
                && (text == null || text.equals("")));

        if (BrowserInfo.get().isGecko()) {
            /*
             * Gecko is really sluggish when updating input attached to dom.
             * Some optimizations seems to work much better in Gecko if we
             * update the actual content lazily when the rest of the DOM has
             * stabilized. In tests, about ten times better performance is
             * achieved with this optimization. See for eg. #2898
             */
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    String fieldValue;
                    if (prompting) {
                        fieldValue = isReadOnly() ? "" : inputPrompt;
                        addStyleDependentName(CLASSNAME_PROMPT);
                    } else {
                        fieldValue = text;
                        removeStyleDependentName(CLASSNAME_PROMPT);
                    }
                    /*
                     * Avoid resetting the old value. Prevents cursor flickering
                     * which then again happens due to this Gecko hack.
                     */
                    if (!getText().equals(fieldValue)) {
                        setText(fieldValue);
                    }
                }
            });
        } else {
            String fieldValue;
            if (prompting) {
                fieldValue = isReadOnly() ? "" : inputPrompt;
                addStyleDependentName(CLASSNAME_PROMPT);
            } else {
                fieldValue = text;
                removeStyleDependentName(CLASSNAME_PROMPT);
            }
            setText(fieldValue);
        }

        lastTextChangeString = valueBeforeEdit = uidl.getStringVariable("text");

        if (uidl.hasAttribute("selpos")) {
            final int pos = uidl.getIntAttribute("selpos");
            final int length = uidl.getIntAttribute("sellen");
            /*
             * Gecko defers setting the text so we need to defer the selection.
             */
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    setSelectionRange(pos, length);
                }
            });
        }
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
            me.@com.vaadin.terminal.gwt.client.ui.VTextField::onCut()();
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
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (listenTextChangeEvents) {
            detachCutEventListener(getElement());
        }
    }

    private void setMaxLength(int newMaxLength) {
        if (newMaxLength > 0) {
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
                getElement().setAttribute("maxlength", "");
            }
            maxLength = -1;
        }

    }

    protected int getMaxLength() {
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
        if (client != null && id != null) {
            boolean sendBlurEvent = false;
            boolean sendValueChange = false;

            if (blurred && client.hasEventListeners(this, EventId.BLUR)) {
                sendBlurEvent = true;
                client.updateVariable(id, EventId.BLUR, "", false);
            }

            String newText = getText();
            if (!prompting && newText != null
                    && !newText.equals(valueBeforeEdit)) {
                sendValueChange = immediate;
                client.updateVariable(id, "text", getText(), false);
                valueBeforeEdit = newText;
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
        int cursorPos = getCursorPos();
        if (lastCursorPos != cursorPos) {
            client.updateVariable(id, VAR_CURSOR, cursorPos, false);
            lastCursorPos = cursorPos;
            return true;
        } else {
            return false;
        }
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
            if (BrowserInfo.get().isIE6()) {
                // IE6 does not show the cursor when tabbing into the field
                setCursorPos(0);
            }
        }
        focusedTextField = this;
        if (client.hasEventListeners(this, EventId.FOCUS)) {
            client.updateVariable(client.getPid(this), EventId.FOCUS, "", true);
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

    /**
     * @return space used by components paddings and borders
     */
    private int getExtraHorizontalPixels() {
        if (extraHorizontalPixels < 0) {
            detectExtraSizes();
        }
        return extraHorizontalPixels;
    }

    /**
     * @return space used by components paddings and borders
     */
    private int getExtraVerticalPixels() {
        if (extraVerticalPixels < 0) {
            detectExtraSizes();
        }
        return extraVerticalPixels;
    }

    /**
     * Detects space used by components paddings and borders. Used when
     * relational size are used.
     */
    private void detectExtraSizes() {
        Element clone = Util.cloneNode(getElement(), false);
        DOM.setElementAttribute(clone, "id", "");
        DOM.setStyleAttribute(clone, "visibility", "hidden");
        DOM.setStyleAttribute(clone, "position", "absolute");
        // due FF3 bug set size to 10px and later subtract it from extra pixels
        DOM.setStyleAttribute(clone, "width", "10px");
        DOM.setStyleAttribute(clone, "height", "10px");
        DOM.appendChild(DOM.getParent(getElement()), clone);
        extraHorizontalPixels = DOM.getElementPropertyInt(clone, "offsetWidth") - 10;
        extraVerticalPixels = DOM.getElementPropertyInt(clone, "offsetHeight") - 10;

        DOM.removeChild(DOM.getParent(getElement()), clone);
    }

    @Override
    public void setHeight(String height) {
        if (height.endsWith("px")) {
            int h = Integer.parseInt(height.substring(0, height.length() - 2));
            h -= getExtraVerticalPixels();
            if (h < 0) {
                h = 0;
            }

            super.setHeight(h + "px");
        } else {
            super.setHeight(height);
        }
    }

    @Override
    public void setWidth(String width) {
        if (width.endsWith("px")) {
            int w = Integer.parseInt(width.substring(0, width.length() - 2));
            w -= getExtraHorizontalPixels();
            if (w < 0) {
                w = 0;
            }

            super.setWidth(w + "px");
        } else {
            super.setWidth(width);
        }
    }

    public void onBeforeShortcutAction(Event e) {
        valueChange(false);
    }

}
