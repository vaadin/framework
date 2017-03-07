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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

public class ErrorMessages extends TestBase {

    @Override
    protected void setup() {
        Button bb = new Button("Button with CompositeError");
        List<UserError> errors = new ArrayList<>();
        errors.add(new UserError("Error 1"));
        errors.add(new UserError("Error 2"));
        bb.setComponentError(new CompositeErrorMessage(errors));
        addComponent(bb);

        TextField tf = new TextField("", "Textfield with UserError");
        tf.setComponentError(new UserError("This is a failure"));
        addComponent(tf);

        ComboBox<String> cb = new ComboBox<>(
                "ComboBox with description and UserError");
        cb.setDescription("This is a combobox");
        cb.setComponentError(new UserError("This is a failure"));
        addComponent(cb);

    }

    @Override
    protected String getDescription() {
        return "The components all have error messages that should appear when hovering them";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3712;
    }

}
