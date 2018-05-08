package com.vaadin.tests.components;

import java.time.LocalDate;

import com.vaadin.ui.DateField;

/**
 * @author Vaadin Ltd
 *
 */
public class TestDateField extends DateField {

    /**
     * Constructs an empty <code>DateField</code> with no caption.
     */
    public TestDateField() {
    }

    /**
     * Constructs an empty <code>DateField</code> with caption.
     *
     * @param caption
     *            the caption of the datefield.
     */
    public TestDateField(String caption) {
        setCaption(caption);
    }

    /**
     * Constructs a new <code>DateField</code> with the given caption and
     * initial text contents.
     *
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the {@link LocalDate} value.
     */
    public TestDateField(String caption, LocalDate value) {
        setValue(value);
        setCaption(caption);
    }
}
