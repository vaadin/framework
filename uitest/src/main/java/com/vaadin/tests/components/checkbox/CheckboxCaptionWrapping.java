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
package com.vaadin.tests.components.checkbox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class CheckboxCaptionWrapping extends TestBase {

    @Override
    protected String getDescription() {
        return "The checkbox caption consists of 10 words which should all be shown. There should be no extra white space between the checkbox caption and the label below it.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3263;
    }

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        VerticalLayout mainLayout = new VerticalLayout();
        CheckBox cb = new CheckBox(
                "Checkbox with some Incididunt ut labore et dolore magna aliqua.");
        cb.setStyleName("wrap");
        cb.setWidth("100%");
        mainLayout.setStyleName("borders");
        mainLayout.setWidth("300px");
        mainLayout.addComponent(cb);
        mainLayout.addComponent(
                new Label("Lorem ipsum dolor sit amet, consectetur adipisicing"
                        + " elit, sed do eiusmod tempor."));

        addComponent(mainLayout);

    }

}
