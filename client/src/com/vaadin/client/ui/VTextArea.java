/*
 * Copyright 2011 Vaadin Ltd.
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
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;

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
    private MaxLengthHandler maxLengthHandler = new MaxLengthHandler();
    private boolean browserSupportsMaxLengthAttribute = browserSupportsMaxLengthAttribute();

    public VTextArea() {
        super(DOM.createTextArea());
        setStyleName(CLASSNAME);
        if (!browserSupportsMaxLengthAttribute) {
            addKeyUpHandler(maxLengthHandler);
            addChangeHandler(maxLengthHandler);
            sinkEvents(Event.ONPASTE);
        }
    }

    public TextAreaElement getTextAreaElement() {
        return super.getElement().cast();
    }

    public void setRows(int rows) {
        getTextAreaElement().setRows(rows);
    }

    private class MaxLengthHandler implements KeyUpHandler, ChangeHandler {

        @Override
        public void onKeyUp(KeyUpEvent event) {
            enforceMaxLength();
        }

        public void onPaste(Event event) {
            enforceMaxLength();
        }

        @Override
        public void onChange(ChangeEvent event) {
            // Opera does not support paste events so this enforces max length
            // for Opera.
            enforceMaxLength();
        }

    }

    protected void enforceMaxLength() {
        if (getMaxLength() >= 0) {
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    if (getText().length() > getMaxLength()) {
                        setText(getText().substring(0, getMaxLength()));
                    }
                }
            });
        }
    }

    protected boolean browserSupportsMaxLengthAttribute() {
        BrowserInfo info = BrowserInfo.get();
        if (info.isFirefox() && info.isBrowserVersionNewerOrEqual(4, 0)) {
            return true;
        }
        if (info.isSafari() && info.isBrowserVersionNewerOrEqual(5, 0)) {
            return true;
        }
        if (info.isIE() && info.isBrowserVersionNewerOrEqual(10, 0)) {
            return true;
        }
        if (info.isAndroid() && info.isBrowserVersionNewerOrEqual(2, 3)) {
            return true;
        }
        return false;
    }

    @Override
    protected void updateMaxLength(int maxLength) {
        if (browserSupportsMaxLengthAttribute) {
            super.updateMaxLength(maxLength);
        } else {
            // Events handled by MaxLengthHandler. This call enforces max length
            // when the max length value has changed
            enforceMaxLength();
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONPASTE) {
            maxLengthHandler.onPaste(event);
        }
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
