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
package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateTimeField;

/**
 * Test UI for testing the functionality of the popup button.
 */
public class DateTimeFieldPopup extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        DateTimeField dateTimeField = new DateTimeField();
        dateTimeField.setValue(LocalDateTime.of(1999, 12, 1, 12, 00));

        addComponent(dateTimeField);
    }
}
