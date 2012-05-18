/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.textarea;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.textfield.VTextField;

/**
 * This class represents a multiline textfield (textarea).
 * 
 * TODO consider replacing this with a RichTextArea based implementation. IE
 * does not support CSS height for textareas in Strict mode :-(
 * 
 * @author Vaadin Ltd.
 * 
 */
public class VTextArea extends VTextField {
    public static final String CLASSNAME = "v-textarea";
    private boolean wordwrap = true;

    public VTextArea() {
        super(DOM.createTextArea());
        setStyleName(CLASSNAME);
    }

    public TextAreaElement getTextAreaElement() {
        return super.getElement().cast();
    }

    public void setRows(int rows) {
        getTextAreaElement().setRows(rows);
    }

    @Override
    protected void setMaxLength(int newMaxLength) {
        super.setMaxLength(newMaxLength);

        boolean hasMaxLength = (newMaxLength >= 0);

        if (hasMaxLength) {
            sinkEvents(Event.ONKEYUP);
        } else {
            unsinkEvents(Event.ONKEYUP);
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (getMaxLength() >= 0 && event.getTypeInt() == Event.ONKEYUP) {
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    if (getText().length() > getMaxLength()) {
                        setText(getText().substring(0, getMaxLength()));
                    }
                }
            });
        }
        super.onBrowserEvent(event);
    }

    @Override
    public int getCursorPos() {
        // This is needed so that TextBoxImplIE6 is used to return the correct
        // position for old Internet Explorer versions where it has to be
        // detected in a different way.
        return getImpl().getTextAreaCursorPos(getElement());
    }

    @Override
    protected void setMaxLengthToElement(int newMaxLength) {
        // There is no maxlength property for textarea. The maximum length is
        // enforced by the KEYUP handler

    }

    public void setWordwrap(boolean wordwrap) {
        if (wordwrap == this.wordwrap) {
            return; // No change
        }

        if (wordwrap) {
            getElement().removeAttribute("wrap");
            getElement().getStyle().clearOverflow();
        } else {
            getElement().setAttribute("wrap", "off");
            getElement().getStyle().setOverflow(Overflow.AUTO);
        }
        if (BrowserInfo.get().isOpera()) {
            // Opera fails to dynamically update the wrap attribute so we detach
            // and reattach the whole TextArea.
            Util.detachAttach(getElement());
        }
        this.wordwrap = wordwrap;
    }
}
