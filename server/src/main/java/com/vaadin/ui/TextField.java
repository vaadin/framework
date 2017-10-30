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

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.shared.ui.textfield.TextFieldState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * A component for editing textual data that fits on a single line. For a
 * multi-line textarea, see the {@link TextArea} component.
 *
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class TextField extends AbstractTextField {

    /**
     * Constructs an empty <code>TextField</code> with no caption.
     */
    public TextField() {
        clear();
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
     * Constructs a new <code>TextField</code> with the given caption and
     * initial text contents.
     *
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the initial text content of the editor, not {@code null}
     */
    public TextField(String caption, String value) {
        setValue(value);
        setCaption(caption);
    }

    /**
     * Constructs a new {@code TextField} with a value change listener. The
     * listener is called when the value of this {@code TextField} is changed
     * either by the user or programmatically.
     * 
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     */
    public TextField(ValueChangeListener<String> valueChangeListener) {
        addValueChangeListener(valueChangeListener);
    }

    /**
     * Constructs a new {@code TextField} with the given caption and a value
     * change listener.
     * <p>
     * The listener is called when the value of this {@code TextField} is
     * changed either by the user or programmatically.
     *
     * @param caption
     *            the caption {@code String} for the editor.
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     */
    public TextField(String caption,
            ValueChangeListener<String> valueChangeListener) {
        this(valueChangeListener);
        setCaption(caption);
    }

    /**
     * Constructs a new {@code TextField} with the given caption, initial text
     * contents and a value change listener.
     * <p>
     * The listener is called when the value of this {@code TextField} is
     * changed either by the user or programmatically.
     * 
     * @param caption
     *            the caption {@code String} for the editor.
     * @param value
     *            the initial text content of the editor, not {@code null}
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     */
    public TextField(String caption, String value,
            ValueChangeListener<String> valueChangeListener) {
        this(caption, value);
        addValueChangeListener(valueChangeListener);
    }

    @Override
    protected TextFieldState getState() {
        return (TextFieldState) super.getState();
    }

    @Override
    protected TextFieldState getState(boolean markAsDirty) {
        return (TextFieldState) super.getState(markAsDirty);
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        Attributes attr = design.attributes();
        if (attr.hasKey("value")) {
            String text = DesignAttributeHandler.readAttribute("value", attr,
                    String.class);
            doSetValue(text);
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        AbstractTextField def = designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("value", attr, getValue(),
                def.getValue(), String.class, designContext);
    }
}
