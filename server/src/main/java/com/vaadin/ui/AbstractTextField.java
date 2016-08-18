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

package com.vaadin.ui;

import java.util.Collection;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.Registration;
import com.vaadin.shared.ui.textfield.TextFieldServerRpc;
import com.vaadin.shared.ui.textfield.TextFieldState;
import com.vaadin.shared.ui.textfield.ValueChangeMode;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Abstract base class for text input components.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
public abstract class AbstractTextField extends AbstractField<String> {

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
            public void setText(String text, int cursorPosition) {
                getUI().getConnectorTracker()
                        .getDiffState(AbstractTextField.this).put("text", text);
                getUI().getConnectorTracker()
                        .getDiffState(AbstractTextField.this)
                        .put("cursorPosition", cursorPosition);
                getState(false).cursorPosition = cursorPosition;
                setValue(text, true);
            }
        });
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            setValue("", false);
        } else {
            setValue(value, false);
        }
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
    public String getValue() {
        return getState(false).text;
    }

    /**
     * Selects all text in the field.
     */
    public void selectAll() {
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
        getState().cursorPosition = pos;
        focus();
    }

    /**
     * Returns the last known cursor position of the field.
     *
     */
    public int getCursorPosition() {
        return getState(false).cursorPosition;
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
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
        return () -> removeListener(FocusEvent.EVENT_ID, FocusEvent.class,
                listener);
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
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
        return () -> removeListener(BlurEvent.EVENT_ID, BlurEvent.class,
                listener);
    }

    /**
     * Gets the number of columns in the editor. If the number of columns is set
     * 0, the actual number of displayed columns is determined implicitly by the
     * adapter.
     *
     * @return the number of columns in the editor.
     */
    public int getColumns() {
        return getState(false).columns;
    }

    /**
     * Sets the number of columns in the editor. If the number of columns is set
     * 0, the actual number of displayed columns is determined implicitly by the
     * adapter.
     *
     * @param columns
     *            the number of columns to set.
     */
    public void setColumns(int columns) {
        if (columns < 0) {
            columns = 0;
        }
        getState().columns = columns;
    }

    /**
     * Sets the mode how the TextField triggers {@link ValueChange}s.
     *
     * @param mode
     *            the new mode
     *
     * @see ValueChangeMode
     */
    public void setValueChangeMode(ValueChangeMode mode) {
        getState().valueChangeMode = mode;
    }

    /**
     * Returns the currently set {@link ValueChangeMode}.
     *
     * @return the mode used to trigger {@link ValueChange}s.
     *
     * @see ValueChangeMode
     */
    public ValueChangeMode getValueChangeMode() {
        return getState(false).valueChangeMode;
    }

    /**
     * Sets how often {@link ValueChange}s are triggered when the
     * {@link ValueChangeMode} is set to either {@link ValueChangeMode#LAZY} or
     * {@link ValueChangeMode#TIMEOUT}.
     *
     * @param timeout
     *            timeout in milliseconds, must be greater or equal to 0
     * @throws IllegalArgumentException
     *             if given timeout is smaller than 0
     *
     * @see ValueChangeMode
     */
    public void setValueChangeTimeout(int timeout) {
        if (timeout < 0)
            throw new IllegalArgumentException(
                    "Timeout must be greater than 0");
        getState().valueChangeTimeout = timeout;
    }

    /**
     * Returns the currently set timeout, in milliseconds, for how often
     * {@link ValueChange}s are triggered if the current {@link ValueChangeMode}
     * is set to either {@link ValueChangeMode#LAZY} or
     * {@link ValueChangeMode#TIMEOUT}.
     *
     * @return the timeout in milliseconds of how often {@link ValueChange}s are
     *         triggered.
     */
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
    protected TextFieldState getState() {
        return (TextFieldState) super.getState();
    }

    @Override
    protected TextFieldState getState(boolean markAsDirty) {
        return (TextFieldState) super.getState(markAsDirty);
    }

    @Override
    protected void doSetValue(String value) {
        getState().text = value;
    }

    /**
     * Clears the value of this field.
     */
    public void clear() {
        setValue("");
    }

    /**
     * Checks if the field is empty.
     *
     * @return true if the field value is an empty string, false otherwise
     */
    public boolean isEmpty() {
        return "".equals(getValue());
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

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> customAttributes = super.getCustomAttributes();
        customAttributes.add("maxlength");
        customAttributes.add("max-length"); // to prevent this appearing in
                                            // output
        customAttributes.add("cursor-position");
        return customAttributes;
    }
}
