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

package com.vaadin.tokka.ui.components.fields;

import java.util.Collection;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.shared.tokka.ui.components.fields.TextFieldServerRpc;
import com.vaadin.shared.tokka.ui.components.fields.TextFieldState;
import com.vaadin.tokka.event.EventListener;
import com.vaadin.tokka.event.Registration;
import com.vaadin.tokka.ui.components.HasValue;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Abstract base class for text input components.
 * 
 * @author Vaadin Ltd.
 */
public abstract class AbstractTextField extends AbstractComponent
        implements HasValue<String> {

    public static class TextChange extends ValueChange<String> {
        public TextChange(AbstractTextField source, boolean userOriginated) {
            super(source, userOriginated);
        }
    }

    protected AbstractTextField() {
        registerRpc(new TextFieldServerRpc() {

            @Override
            public void blur() {
                fireEvent(new BlurEvent(AbstractTextField.this));
            }

            @Override
            public void focus() {
                fireEvent(new FocusEvent(AbstractTextField.this));
            }

            @Override
            public void setText(String text) {
                if (!isReadOnly()) {
                    setValue(text);
                    fireEvent(new TextChange(AbstractTextField.this, true));
                }
            }
        });
    }

    @Override
    protected TextFieldState getState() {
        return (TextFieldState) super.getState();
    }

    @Override
    protected TextFieldState getState(boolean markAsDirty) {
        return (TextFieldState) super.getState(markAsDirty);
    }

    /**
     * Returns the maximum number of characters in the field. Value -1 is
     * considered unlimited. Terminal may however have some technical limits.
     * 
     * @return the maxLength
     */
    public int getMaxLength() {
        return getState(false).maxLength;
    }

    /**
     * Sets the maximum number of characters in the field. Value -1 is
     * considered unlimited. Terminal may however have some technical limits.
     * 
     * @param maxLength
     *            the maxLength to set
     */
    public void setMaxLength(int maxLength) {
        getState().maxLength = maxLength;
    }

    /**
     * Returns the current placeholder text.
     * 
     * @see #setPlaceholder(String)
     * @return the placeholder text
     */
    public String getPlaceholder() {
        return getState(false).placeholder;
    }

    /**
     * Sets the placeholder text. The placeholder is text that is displayed when
     * the field would otherwise be empty, to prompt the user for input.
     * 
     * @param placeholder
     *            the placeholder text to set
     */
    public void setPlaceholder(String placeholder) {
        getState().placeholder = placeholder;
    }

    @Override
    public void setValue(String text) {
        // TODO Accept nulls or not?
        getState().text = text;
    }

    @Override
    public String getValue() {
        return getState(false).text;
    }

    @Override
    public Registration addValueChangeListener(EventListener<ValueChange<String>> listener) {
        return addListener(TextChange.class, listener);
    }

    /**
     * Selects all text in the field.
     * 
     */
    public void selectAll() {
        // TODO Do we need this API? Or provide as extension?
        setSelection(0, getValue().length());
    }

    /**
     * Sets the range of text to be selected.
     * 
     * As a side effect the field will become focused.
     * 
     * @param pos
     *            the position of the first character to be selected
     * @param length
     *            the number of characters to be selected
     */
    public void setSelection(int start, int length) {
        // TODO Do we need this API? Or provide as extension?
        getState().selectionStart = start;
        getState().selectionLength = length;
        focus();
    }

    /**
     * Sets the cursor position in the field. As a side effect the field will
     * become focused.
     * 
     * @param pos
     *            the position for the cursor
     */
    public void setCursorPosition(int pos) {
        setSelection(pos, 0);
    }

    @Deprecated
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    @Deprecated
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    @Deprecated
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Deprecated
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        Attributes attr = design.attributes();
        if (attr.hasKey("maxlength")) {
            setMaxLength(DesignAttributeHandler.readAttribute("maxlength", attr,
                    Integer.class));
        }
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> customAttributes = super.getCustomAttributes();
        customAttributes.add("maxlength");
        customAttributes.add("max-length"); // to prevent this appearing in
                                            // output
        customAttributes.add("cursor-position");
        return customAttributes;
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        AbstractTextField def = (AbstractTextField) designContext
                .getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("maxlength", attr, getMaxLength(),
                def.getMaxLength(), Integer.class);
    }
}
