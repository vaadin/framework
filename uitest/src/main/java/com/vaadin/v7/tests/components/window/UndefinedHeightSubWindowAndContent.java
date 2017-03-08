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
package com.vaadin.v7.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.Form;
import com.vaadin.v7.ui.TextField;

public class UndefinedHeightSubWindowAndContent extends TestBase {

    @Override
    protected void setup() {
        Window subWindow = new Window("No scrollbars!");
        subWindow.setWidth("300px");
        subWindow.center();
        subWindow.setModal(true);
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        subWindow.setContent(layout);

        final Form form = new Form();
        form.setFooter(null);
        form.setImmediate(true);
        form.setValidationVisible(true);
        form.setCaption("This is a form");
        form.setDescription("How do you do?");
        final TextField field1 = new TextField("Write here");
        field1.setImmediate(true);
        field1.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (!isValid(value)) {
                    throw new InvalidValueException("FAIL!");
                }
            }

            public boolean isValid(Object value) {
                return field1.getValue().equals("valid");
            }
        });
        form.addField("Field 1", field1);
        layout.addComponent(form);

        getMainWindow().addWindow(subWindow);
        subWindow.bringToFront();
    }

    @Override
    protected String getDescription() {
        return "When both window and its content have undefined height, window must not reserve space for a scroll bar when it is not needed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8852;
    }

}
