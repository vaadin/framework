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

import java.util.Collection;
import java.util.Objects;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.shared.Registration;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.textfield.AbstractTextFieldClientRpc;
import com.vaadin.shared.ui.textfield.AbstractTextFieldServerRpc;
import com.vaadin.shared.ui.textfield.AbstractTextFieldState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

import elemental.json.Json;

/**
 * Abstract base class for text input components.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
public abstract class AbstractTextField extends AbstractField<String>
        implements HasValueChangeMode {

    private final class AbstractTextFieldServerRpcImpl
            implements AbstractTextFieldServerRpc {

        @Override
        public void setText(String text, int cursorPosition) {
            updateDiffstate("text", Json.create(text));

            lastKnownCursorPosition = cursorPosition;
            setValue(text, true);
        }
    }

    private final class AbstractTextFieldFocusAndBlurRpcImpl
            implements FocusAndBlurServerRpc {
        @Override
        public void blur() {
            fireEvent(new BlurEvent(AbstractTextField.this));
        }

        @Override
        public void focus() {
            fireEvent(new FocusEvent(AbstractTextField.this));
        }
    }

    private int lastKnownCursorPosition = -1;

    /**
     * Creates a new instance.
     */
    protected AbstractTextField() {
        registerRpc(new AbstractTextFieldServerRpcImpl());
        registerRpc(new AbstractTextFieldFocusAndBlurRpcImpl());
    }

    /**
     * Sets the value of this text field. If the new value is not equal to
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
     * @since 8.0
     */
    public void setPlaceholder(String placeholder) {
        getState().placeholder = placeholder;
    }

    @Override
    public String getValue() {
        return getState(false).text;
    }

    /**
     * Selects all text in the field.
     * <p>
     * As a side effect the field will become focused.
     */
    public void selectAll() {
        getRpcProxy(AbstractTextFieldClientRpc.class).selectAll();
        focus();
    }

    /**
     * Sets the range of text to be selected.
     * <p>
     * As a side effect the field will become focused.
     *
     * @param start
     *            the position of the first character to be selected
     * @param length
     *            the number of characters to be selected
     */
    public void setSelection(int start, int length) {
        getRpcProxy(AbstractTextFieldClientRpc.class).selectRange(start,
                length);
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

    /**
     * Returns the last known cursor position of the field.
     *
     * @return the last known cursor position
     */
    public int getCursorPosition() {
        return lastKnownCursorPosition;
    }

    /**
     * Adds a {@link FocusListener} to this component, which gets fired when
     * this component receives keyboard focus.
     *
     * @param listener
     *            the focus listener
     * @return a registration for the listener
     *
     * @see Registration
     */
    public Registration addFocusListener(FocusListener listener) {
        return addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    /**
     * Adds a {@link BlurListener} to this component, which gets fired when this
     * component loses keyboard focus.
     *
     * @param listener
     *            the blur listener
     * @return a registration for the listener
     *
     * @see Registration
     */
    public Registration addBlurListener(BlurListener listener) {
        return addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
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
        if (timeout < 0) {
            throw new IllegalArgumentException(
                    "Timeout must be greater than 0");
        }
        getState().valueChangeTimeout = timeout;
    }

    @Override
    public int getValueChangeTimeout() {
        return getState(false).valueChangeTimeout;
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
    protected AbstractTextFieldState getState() {
        return (AbstractTextFieldState) super.getState();
    }

    @Override
    protected AbstractTextFieldState getState(boolean markAsDirty) {
        return (AbstractTextFieldState) super.getState(markAsDirty);
    }

    @Override
    protected void doSetValue(String value) {
        getState().text = value;
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        AbstractTextField def = designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("maxlength", attr, getMaxLength(),
                def.getMaxLength(), Integer.class, designContext);
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
    public String getEmptyValue() {
        return "";
    }
}
