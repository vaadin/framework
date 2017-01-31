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

import java.time.LocalDateTime;

import com.vaadin.shared.ui.datefield.InlineDateTimeFieldState;

/**
 * A date time entry component, which displays the actual date selector inline.
 *
 * @see AbstractLocalDateTimeField
 * @see DateTimeField
 * @author Vaadin Ltd.
 * @since 8.0
 */
public class InlineDateTimeField extends AbstractLocalDateTimeField {

    /**
     * Constructs an empty <code>InlineDateTimeField</code> with no caption.
     */
    public InlineDateTimeField() {
        super();
    }

    /**
     * Constructs a new <code>InlineDateTimeField</code> with the given caption
     * and initial text contents.
     *
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the LocalDate value.
     */
    public InlineDateTimeField(String caption, LocalDateTime value) {
        super(caption, value);
    }

    /**
     * Constructs an empty <code>InlineDateTimeField</code> with caption.
     *
     * @param caption
     *            the caption of the datefield.
     */
    public InlineDateTimeField(String caption) {
        super(caption);
    }

    /**
     * Constructs a new {@code InlineDateTimeField} with a value change
     * listener.
     * <p>
     * The listener is called when the value of this {@code InlineDateTimeField}
     * is changed either by the user or programmatically.
     *
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     */
    public InlineDateTimeField(
            ValueChangeListener<LocalDateTime> valueChangeListener) {
        super();
        addValueChangeListener(valueChangeListener);
    }

    /**
     * Constructs a new {@code InlineDateTimeField} with the given caption and a
     * value change listener.
     * <p>
     * The listener is called when the value of this {@code InlineDateTimeField}
     * is changed either by the user or programmatically.
     *
     * @param caption
     *            the caption for the field
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     */
    public InlineDateTimeField(String caption,
            ValueChangeListener<LocalDateTime> valueChangeListener) {
        this(valueChangeListener);
        setCaption(caption);
    }

    /**
     * Constructs a new {@code InlineDateTimeField} with the given caption,
     * initial text contents and a value change listener.
     * <p>
     * The listener is called when the value of this {@code InlineDateTimeField}
     * is changed either by the user or programmatically.
     *
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field, not {@code null}
     * @param valueChangeListener
     *            the value change listener, not {@code null}
     */
    public InlineDateTimeField(String caption, LocalDateTime value,
            ValueChangeListener<LocalDateTime> valueChangeListener) {
        this(caption, value);
        addValueChangeListener(valueChangeListener);
    }

    @Override
    protected InlineDateTimeFieldState getState() {
        return (InlineDateTimeFieldState) super.getState();
    }

    @Override
    protected InlineDateTimeFieldState getState(boolean markAsDirty) {
        return (InlineDateTimeFieldState) super.getState(markAsDirty);
    }
}
