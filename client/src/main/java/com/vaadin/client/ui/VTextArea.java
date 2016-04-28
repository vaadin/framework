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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.dd.DragImageModifier;

/**
 * This class represents a multiline textfield (textarea).
 * 
 * TODO consider replacing this with a RichTextArea based implementation. IE
 * does not support CSS height for textareas in Strict mode :-(
 * 
 * @author Vaadin Ltd.
 * 
 */
public class VTextArea extends VTextField implements DragImageModifier {

    public static final String CLASSNAME = "v-textarea";
    private boolean wordwrap = true;
    private MaxLengthHandler maxLengthHandler = new MaxLengthHandler();
    private boolean browserSupportsMaxLengthAttribute = browserSupportsMaxLengthAttribute();
    private EnterDownHandler enterDownHandler = new EnterDownHandler();

    public VTextArea() {
        super(DOM.createTextArea());
        setStyleName(CLASSNAME);

        // KeyDownHandler is needed for correct text input on all
        // browsers, not just those that don't support a max length attribute
        addKeyDownHandler(enterDownHandler);

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

    @Override
    public void setSelectionRange(int pos, int length) {
        super.setSelectionRange(pos, length);
        final String value = getValue();
        /*
         * Align position to index inside string value
         */
        int index = pos;
        if (index < 0) {
            index = 0;
        }
        if (index > value.length()) {
            index = value.length();
        }
        // Get pixels count required to scroll textarea vertically
        int scrollTop = getScrollTop(value, index);
        int scrollLeft = -1;
        /*
         * Check if textarea has wrap attribute set to "off". In the latter case
         * horizontal scroll is also required.
         */
        if (!isWordwrap()) {
            // Get pixels count required to scroll textarea horizontally
            scrollLeft = getScrollLeft(value, index);
        }
        // Set back original text if previous methods calls changed it
        if (!isWordwrap() || index < value.length()) {
            setValue(value, false);
        }
        /*
         * Call original method to set cursor position. In most browsers it
         * doesn't lead to scrolling.
         */
        super.setSelectionRange(pos, length);
        /*
         * Align vertical scroll to middle of textarea view (height) if
         * scrolling is reqiured at all.
         */
        if (scrollTop > 0) {
            scrollTop += getElement().getClientHeight() / 2;
        }
        /*
         * Align horizontal scroll to middle of textarea view (widht) if
         * scrolling is reqiured at all.
         */
        if (scrollLeft > 0) {
            scrollLeft += getElement().getClientWidth() / 2;
        }
        /*
         * Scroll if computed scrollTop is greater than scroll after cursor
         * setting
         */
        if (getElement().getScrollTop() < scrollTop) {
            getElement().setScrollTop(scrollTop);
        }
        /*
         * Scroll if computed scrollLeft is greater than scroll after cursor
         * setting
         */
        if (getElement().getScrollLeft() < scrollLeft) {
            getElement().setScrollLeft(scrollLeft);
        }
    }

    /*
     * Get horizontal scroll value required to get position visible. Method is
     * called only when text wrapping is off. There is need to scroll
     * horizontally in case words are wrapped.
     */
    private int getScrollLeft(String value, int index) {
        String beginning = value.substring(0, index);
        // Compute beginning of the current line
        int begin = beginning.lastIndexOf('\n');
        String line = value.substring(begin + 1);
        index = index - begin - 1;
        if (index < line.length()) {
            index++;
        }
        line = line.substring(0, index);
        /*
         * Now <code>line</code> contains current line up to index position
         */
        setValue(line.trim(), false); // Set this line to the textarea.
        /*
         * Scroll textarea up to the end of the line (maximum possible
         * horizontal scrolling value). Now the end line becomes visible.
         */
        getElement().setScrollLeft(getElement().getScrollWidth());
        // Return resulting horizontal scrolling value.
        return getElement().getScrollLeft();
    }

    /*
     * Get vertical scroll value required to get position visible
     */
    private int getScrollTop(String value, int index) {
        /*
         * Trim text after position and set this trimmed text if index is not
         * very end.
         */
        if (index < value.length()) {
            String beginning = value.substring(0, index);
            setValue(beginning, false);
        }
        /*
         * Now textarea contains trimmed text and could be scrolled up to the
         * top. Scroll it to maximum possible value to get end of the text
         * visible.
         */
        getElement().setScrollTop(getElement().getScrollHeight());
        // Return resulting vertical scrolling value.
        return getElement().getScrollTop();
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
        if (info.isFirefox()) {
            return true;
        }
        if (info.isSafari()) {
            return true;
        }
        if (info.isIE10() || info.isIE11() || info.isEdge()) {
            return true;
        }
        if (info.isAndroid()) {
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

    private class EnterDownHandler implements KeyDownHandler {

        @Override
        public void onKeyDown(KeyDownEvent event) {
            // Fix for #12424/13811 - if the key being pressed is enter, we stop
            // propagation of the KeyDownEvents if there were no modifier keys
            // also pressed. This prevents shortcuts that are bound to only the
            // enter key from being processed but allows usage of e.g.
            // shift-enter or ctrl-enter.
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
                    && !event.isAnyModifierKeyDown()) {
                event.stopPropagation();
            }
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
            getElement().getStyle().clearWhiteSpace();
        } else {
            getElement().setAttribute("wrap", "off");
            getElement().getStyle().setOverflow(Overflow.AUTO);
            getElement().getStyle().setWhiteSpace(WhiteSpace.PRE);
        }
        if (BrowserInfo.get().isOpera()
                || (BrowserInfo.get().isWebkit() && wordwrap)) {
            // Opera fails to dynamically update the wrap attribute so we detach
            // and reattach the whole TextArea.
            // Webkit fails to properly reflow the text when enabling wrapping,
            // same workaround
            WidgetUtil.detachAttach(getElement());
        }
        this.wordwrap = wordwrap;
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        // Overridden to avoid submitting TextArea value on enter in IE. This is
        // another reason why widgets should inherit a common abstract
        // class instead of directly each other.
        // This method is overridden only for IE and Firefox.
    }

    @Override
    public void modifyDragImage(Element element) {
        // Fix for #13557 - drag image doesn't show original text area text.
        // It happens because "value" property is not copied into the cloned
        // element
        String value = getElement().getPropertyString("value");
        if (value != null) {
            element.setPropertyString("value", value);
        }
    }
}
