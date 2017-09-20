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

import java.util.Objects;

import org.jsoup.nodes.Element;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.richtextarea.RichTextAreaClientRpc;
import com.vaadin.shared.ui.richtextarea.RichTextAreaServerRpc;
import com.vaadin.shared.ui.richtextarea.RichTextAreaState;
import com.vaadin.ui.declarative.DesignContext;

import elemental.json.Json;

/**
 * A simple RichTextArea to edit HTML format text.
 */
public class RichTextArea extends AbstractField<String>
        implements HasValueChangeMode {

    private class RichTextAreaServerRpcImpl implements RichTextAreaServerRpc {
        @Override
        public void setText(String text) {
            updateDiffstate("value", Json.create(text));
            if (!setValue(text, true)) {
                // The value was not updated, this could happen if the field has
                // been set to readonly on the server and the client does not
                // know about it yet. Must re-send the correct state back.
                markAsDirty();
            }
        }
    }

    /**
     * Constructs an empty <code>RichTextArea</code> with no caption.
     */
    public RichTextArea() {
        super();
        registerRpc(new RichTextAreaServerRpcImpl());
        setValue("");
    }

    /**
     * Constructs an empty <code>RichTextArea</code> with the given caption.
     *
     * @param caption
     *            the caption for the editor.
     */
    public RichTextArea(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new <code>RichTextArea</code> with the given caption and
     * initial text contents.
     *
     * @param caption
     *            the caption for the editor.
     * @param value
     *            the initial text content of the editor, not {@code null}
     */
    public RichTextArea(String caption, String value) {
        this(caption);
        setValue(value);
    }

    /**
     * Constructs a new {@code RichTextArea} with a value change listener.
     * <p>
     * The listener is called when the value of this {@code TextField} is
     * changed either by the user or programmatically.
     *
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     * @since 8.0
     */
    public RichTextArea(ValueChangeListener<String> valueChangeListener) {
        addValueChangeListener(valueChangeListener);
    }

    /**
     * Constructs a new {@code RichTextArea} with the given caption and a value
     * change listener.
     * <p>
     * The listener is called when the value of this {@code TextField} is
     * changed either by the user or programmatically.
     *
     * @param caption
     *            the caption for the field
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     * @since 8.0
     */
    public RichTextArea(String caption,
            ValueChangeListener<String> valueChangeListener) {
        this(valueChangeListener);
        setCaption(caption);
    }

    /**
     * Constructs a new {@code RichTextArea} with the given caption, initial
     * text contents and a value change listener.
     * <p>
     * The listener is called when the value of this {@code RichTextArea} is
     * changed either by the user or programmatically.
     *
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field, not {@code null}
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     * @since 8.0
     */
    public RichTextArea(String caption, String value,
            ValueChangeListener<String> valueChangeListener) {
        this(caption, value);
        addValueChangeListener(valueChangeListener);
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        setValue(design.html());
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        design.html(getValue());
    }

    @Override
    protected RichTextAreaState getState() {
        return (RichTextAreaState) super.getState();
    }

    @Override
    protected RichTextAreaState getState(boolean markAsDirty) {
        return (RichTextAreaState) super.getState(markAsDirty);
    }

    /**
     * Sets the value of this object. If the new value is not equal to
     * {@code getValue()}, fires a {@link ValueChangeEvent}. Throws
     * {@code NullPointerException} if the value is null.
     *
     * @param value
     *            the new value, not {@code null}
     * @throws NullPointerException
     *             if {@code value} is {@code null}
     */
    @Override
    public void setValue(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        setValue(value, false);
    }

    @Override
    public String getValue() {
        return getState(false).value;
    }

    @Override
    public String getEmptyValue() {
        return "";
    }

    @Override
    protected void doSetValue(String value) {
        getState().value = value;
    }

    /**
     * Selects all text in the rich text area. As a side effect, focuses the
     * rich text area.
     *
     * @since 6.5
     */
    public void selectAll() {
        getRpcProxy(RichTextAreaClientRpc.class).selectAll();
        focus();
    }

    @Override
    public void setValueChangeMode(ValueChangeMode mode) {
        getState().valueChangeMode = mode;
    }

    @Override
    public ValueChangeMode getValueChangeMode() {
        return getState(false).valueChangeMode;
    }

    @Override
    public void setValueChangeTimeout(int timeout) {
        getState().valueChangeTimeout = timeout;

    }

    @Override
    public int getValueChangeTimeout() {
        return getState(false).valueChangeTimeout;
    }

}
