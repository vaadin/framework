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
package com.vaadin.data;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;

import com.vaadin.ui.TextField;

/**
 * A base class for {@code Binder} unit tests.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 */
public abstract class BinderTestBase<BINDER extends Binder<ITEM>, ITEM> implements Serializable {

    protected static final String NEGATIVE_ERROR_MESSAGE = "Value must be non-negative";

    protected static final String NOT_NUMBER_ERROR_MESSAGE = "Value must be a number";

    protected static final String EMPTY_ERROR_MESSAGE = "Value cannot be empty";

    protected BINDER binder;

    protected ITEM item;

    protected TextField nameField;
    protected TextField ageField;

    protected Validator<String> notEmpty = Validator.from(val -> !val.isEmpty(),
            EMPTY_ERROR_MESSAGE);
    protected Converter<String, Integer> stringToInteger = Converter.from(
            Integer::valueOf, String::valueOf, e -> NOT_NUMBER_ERROR_MESSAGE);
    protected Validator<Integer> notNegative = Validator.from(x -> x >= 0,
            NEGATIVE_ERROR_MESSAGE);

    public static void testSerialization(Object toSerialize) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream())) {
            objectOutputStream.writeObject(toSerialize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setUpBase() {
        nameField = new TextField();
        nameField.setLocale(Locale.US);
        ageField = new TextField();
        ageField.setLocale(Locale.US);
    }

    @After
    public void testBinderSerialization() {
        testSerialization(binder);
    }
}
