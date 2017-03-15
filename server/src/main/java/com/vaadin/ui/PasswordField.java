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

import com.vaadin.shared.ui.passwordfield.PasswordFieldState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * A field that is used to enter secret text information like passwords. The
 * entered text is not displayed on the screen.
 */
public class PasswordField extends TextField {

    /**
     * Constructs an empty PasswordField.
     */
    public PasswordField() {
        setValue("");
    }

    /**
     * Constructs a PasswordField with given value and caption.
     *
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field, not {@code null}
     */
    public PasswordField(String caption, String value) {
        setValue(value);
        setCaption(caption);
    }

    /**
     * Constructs a PasswordField with given caption.
     *
     * @param caption
     *            the caption for the field
     */
    public PasswordField(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new {@code PasswordField} with a value change listener.
     * <p>
     * The listener is called when the value of this {@code PasswordField} is
     * changed either by the user or programmatically.
     *
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     * @since 8.0
     */
    public PasswordField(ValueChangeListener<String> valueChangeListener) {
        super(valueChangeListener);
    }

    /**
     * Constructs a new {@code PasswordField} with the given caption and a value
     * change listener.
     * <p>
     * The listener is called when the value of this {@code PasswordField} is
     * changed either by the user or programmatically.
     *
     * @param caption
     *            the caption for the field
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     * @since 8.0
     */
    public PasswordField(String caption,
            ValueChangeListener<String> valueChangeListener) {
        super(caption, valueChangeListener);
    }

    /**
     * Constructs a new {@code PasswordField} with the given caption, initial
     * text contents and a value change listener.
     * <p>
     * The listener is called when the value of this {@code PasswordField} is
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
    public PasswordField(String caption, String value,
            ValueChangeListener<String> valueChangeListener) {
        super(caption, value, valueChangeListener);
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        Attributes attr = design.attributes();
        if (attr.hasKey("value")) {
            doSetValue(DesignAttributeHandler.readAttribute("value", attr,
                    String.class));
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

    @Override
    protected PasswordFieldState getState() {
        return (PasswordFieldState) super.getState();
    }

    @Override
    protected PasswordFieldState getState(boolean markAsDirty) {
        return (PasswordFieldState) super.getState(markAsDirty);
    }
}
