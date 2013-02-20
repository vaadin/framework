/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.UIDL;

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

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Call parent renderer explicitly
        super.updateFromUIDL(uidl, client);

        if (uidl.hasAttribute("rows")) {
            setRows(uidl.getIntAttribute("rows"));
        }

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

        public void onKeyUp(KeyUpEvent event) {
            enforceMaxLength();
        }

        public void onPaste(Event event) {
            enforceMaxLength();
        }

        public void onChange(ChangeEvent event) {
            // Opera does not support paste events so this enforces max length
            // for Opera.
            enforceMaxLength();
        }

    }

    protected void enforceMaxLength() {
        if (getMaxLength() >= 0) {
            Scheduler.get().scheduleDeferred(new Command() {
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

    public void setRows(int rows) {
        setRows(getElement(), rows);
    }

    private native void setRows(Element e, int r)
    /*-{
    try {
        if(e.tagName.toLowerCase() == "textarea")
                e.rows = r;
    } catch (e) {}
    }-*/;

    @Override
    public int getCursorPos() {
        // This is needed so that TextBoxImplIE6 is used to return the correct
        // position for old Internet Explorer versions where it has to be
        // detected in a different way.
        return getImpl().getTextAreaCursorPos(getElement());
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        // Overridden to avoid submitting TextArea value on enter in IE. This is
        // another reason why widgets should inherit a common abstract
        // class instead of directly each other.
    }
}
