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
package com.vaadin.tests.components.abstractfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.validator.StringLengthValidator;
import com.vaadin.v7.ui.TextField;

public class AbstractFieldCommitWithInvalidValues extends TestBase {

    private TextField tf;

    @Override
    protected String getDescription() {
        return "Commiting a field with invalid values should throw an exception";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2532;
    }

    @Override
    protected void setup() {
        tf = new TextField("A field, must contain 1-2 chars",
                new ObjectProperty<>("a"));
        tf.addValidator(
                new StringLengthValidator("Invalid length", 1, 2, false));
        tf.setBuffered(true);
        tf.setRequired(true);

        Button b = new Button("Commit", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    tf.commit();
                    if (tf.isValid()) {
                        getMainWindow().showNotification(
                                "OK! Form validated and no error was thrown",
                                Notification.TYPE_HUMANIZED_MESSAGE);
                    } else {
                        getMainWindow().showNotification(
                                "Form is invalid but no exception was thrown",
                                Notification.TYPE_ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    if (tf.isValid()) {
                        getMainWindow().showNotification(
                                "Form is valid but an exception was thrown",
                                Notification.TYPE_ERROR_MESSAGE);
                    } else {
                        getMainWindow().showNotification(
                                "OK! Error was thrown for an invalid input",
                                Notification.TYPE_HUMANIZED_MESSAGE);

                    }
                }
            }

        });

        addComponent(tf);
        addComponent(b);
    }
}
