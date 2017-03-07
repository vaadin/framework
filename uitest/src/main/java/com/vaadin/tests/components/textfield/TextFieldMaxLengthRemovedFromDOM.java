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
package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.TextField;

public class TextFieldMaxLengthRemovedFromDOM extends TestBase {

    @Override
    protected void setup() {
        final TextField tf = new TextField();
        tf.setMaxLength(11);
        tf.setRequired(true);
        tf.setImmediate(true);
        addComponent(tf);

        tf.addFocusListener(new FieldEvents.FocusListener() {

            @Override
            public void focus(FocusEvent event) {
                // Resetting Max length should not remove maxlength attribute
                tf.setMaxLength(11);
            }
        });
    }

    @Override
    protected String getDescription() {
        return "Maxlength attribute should not dissappear from the DOM when I focus the text field.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9940;
    }

}
