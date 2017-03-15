/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui;

import org.jsoup.nodes.Element;

import com.vaadin.shared.ui.textarea.TextAreaServerRpc;
import com.vaadin.shared.ui.textarea.TextAreaState;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;

/**
 * A text field that supports multi line editing.
 */
public class TextArea extends AbstractTextField {

    /**
     * Constructs an empty TextArea.
     */
    public TextArea() {
        registerRpc(new TextAreaServerRpc() {

            @Override
            public void setHeight(String height) {
                TextArea.this.setHeight(height);
            }

            @Override
            public void setWidth(String width) {
                TextArea.this.setWidth(width);
            }
        });
        clear();
    }

    /**
     * Constructs an empty TextArea with given caption.
     *
     * @param caption
     *            the caption for the field.
     */
    public TextArea(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a TextArea with given caption and value.
     *
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field, not {@code null}
     */
    public TextArea(String caption, String value) {
        this(caption);
        setValue(value);
    }

    /**
     * Constructs a new {@code TextArea} with a value change listener.
     * <p>
     * The listener is called when the value of this {@code TextArea} is changed
     * either by the user or programmatically.
     *
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     * @since 8.0
     */
    public TextArea(ValueChangeListener<String> valueChangeListener) {
        addValueChangeListener(valueChangeListener);
    }

    /**
     * Constructs a new {@code TextArea} with the given caption and a value
     * change listener.
     * <p>
     * The listener is called when the value of this {@code TextArea} is changed
     * either by the user or programmatically.
     *
     * @param caption
     *            the caption for the field
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     * @since 8.0
     */
    public TextArea(String caption,
            ValueChangeListener<String> valueChangeListener) {
        this(valueChangeListener);
        setCaption(caption);
    }

    /**
     * Constructs a new {@code TextArea} with the given caption, initial text
     * contents and a value change listener.
     * <p>
     * The listener is called when the value of this {@code TextArea} is changed
     * either by the user or programmatically.
     *
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field, not {@code null}
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     * @since 8.0
     */
    public TextArea(String caption, String value,
            ValueChangeListener<String> valueChangeListener) {
        this(caption, value);
        addValueChangeListener(valueChangeListener);
    }

    @Override
    protected TextAreaState getState() {
        return (TextAreaState) super.getState();
    }

    @Override
    protected TextAreaState getState(boolean markAsDirty) {
        return (TextAreaState) super.getState(markAsDirty);
    }

    /**
     * Sets the number of rows in the text area.
     *
     * @param rows
     *            the number of rows for this text area.
     */
    public void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        getState().rows = rows;
    }

    /**
     * Gets the number of rows in the text area.
     *
     * @return number of explicitly set rows.
     */
    public int getRows() {
        return getState(false).rows;
    }

    /**
     * Sets the text area's word-wrap mode on or off.
     *
     * @param wordWrap
     *            <code>true</code> to use word-wrap mode <code>false</code>
     *            otherwise.
     */
    public void setWordWrap(boolean wordWrap) {
        getState().wordWrap = wordWrap;
    }

    /**
     * Tests if the text area is in word-wrap mode.
     *
     * @return <code>true</code> if the component is in word-wrap mode,
     *         <code>false</code> if not.
     */
    public boolean isWordWrap() {
        return getState(false).wordWrap;
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        doSetValue(DesignFormatter.decodeFromTextNode(design.html()));
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        design.html(DesignFormatter.encodeForTextNode(getValue()));
    }
}
