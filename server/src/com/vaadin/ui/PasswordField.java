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

import com.vaadin.data.Property;

/**
 * A field that is used to enter secret text information like passwords. The
 * entered text is not displayed on the screen.
 */
public class PasswordField extends AbstractTextField {

    /**
     * Constructs an empty PasswordField.
     */
    public PasswordField() {
        setValue("");
    }

    /**
     * Constructs a PasswordField with given property data source.
     * 
     * @param dataSource
     *            the property data source for the field
     */
    public PasswordField(Property dataSource) {
        setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a PasswordField with given caption and property data source.
     * 
     * @param caption
     *            the caption for the field
     * @param dataSource
     *            the property data source for the field
     */
    public PasswordField(String caption, Property dataSource) {
        this(dataSource);
        setCaption(caption);
    }

    /**
     * Constructs a PasswordField with given value and caption.
     * 
     * @param caption
     *            the caption for the field
     * @param value
     *            the value for the field
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
}
