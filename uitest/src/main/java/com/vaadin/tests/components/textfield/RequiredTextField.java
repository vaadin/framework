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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.TextField;

/**
 * Test for required text field.
 *
 * @author Vaadin Ltd
 */
public class RequiredTextField extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TextField field = new TextField();

        addComponent(field);

        Button button = new Button("Set/unset required",
                event -> field.setRequired(!field.isRequired()));
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Add .v-required style when setRequired() is used";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10201;
    }
}
