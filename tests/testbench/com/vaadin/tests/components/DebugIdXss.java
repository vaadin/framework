/*
 * Copyright 2012 Vaadin Ltd.
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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;

public class DebugIdXss extends TestBase {

    @Override
    protected void setup() {

        getLayout().setWidth("100%");

        final TextField debugIdTextField = new TextField("Enter debug id");
        debugIdTextField.setDebugId("TF");
        debugIdTextField.setValue("\"");

        Button submitButton = new Button("Add a magic button",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        String debugId = (String) debugIdTextField.getValue();

                        final Button magicButton = new Button(
                                "A button with debug id " + debugId);
                        magicButton.addListener(new Button.ClickListener() {
                            public void buttonClick(ClickEvent event) {
                                getLayout().setComponentAlignment(magicButton,
                                        Alignment.TOP_RIGHT);
                                magicButton.setCaption("Aligned to right");
                            }
                        });
                        magicButton.setDebugId(debugId);
                        magicButton
                                .setDescription("Clicking this button should align it to the right."
                                        + " Other things may happen instead...");
                        addComponent(magicButton);
                    }
                });
        addComponent(debugIdTextField);
        addComponent(submitButton);
    }

    @Override
    protected String getDescription() {
        return "Debug IDs should be escaped everywhere";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10873;
    }
}
