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
package com.vaadin.tests.components;

import java.time.LocalDate;

import com.vaadin.ui.AbstractLocalDateField;

/**
 * @author Vaadin Ltd
 *
 */
public class TestDateField extends AbstractLocalDateField {

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
