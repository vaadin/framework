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
package com.vaadin.v7.tests.server.validation;

import org.junit.Test;

import com.vaadin.v7.data.validator.IntegerValidator;
import com.vaadin.v7.ui.TextField;

public class ReadOnlyValidationTest {

    @Test
    public void testIntegerValidation() {
        TextField field = new TextField();
        field.addValidator(new IntegerValidator("Enter a Valid Number"));
        field.setValue(String.valueOf(10));
        field.validate();
    }
}
