/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class PrimaryStyle extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Use a set of three common components as a test.
        final Label label = new Label("Test Label");
        label.setPrimaryStyleName("initial");
        label.setStyleName("state");
        addComponent(label);

        final Button button = new Button("Test Button");
        button.setPrimaryStyleName("initial");
        button.setStyleName("state");
        addComponent(button);

        final TextField tf = new TextField("Test TextField");
        tf.setPrimaryStyleName("initial");
        tf.setStyleName("state");
        addComponent(tf);

        Button updateButton = new Button("Update styles",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        label.setPrimaryStyleName("updated");
                        label.setStyleName("correctly");

                        button.setPrimaryStyleName("updated");
                        button.setStyleName("correctly");

                        tf.setPrimaryStyleName("updated");
                        tf.setStyleName("correctly");
                    }
                });
        updateButton.setId("update-button");
        addComponent(updateButton);
    }

    @Override
    protected String getTestDescription() {
        return "Test that setPrimaryStyleName followed by setStyleName results in correct class names.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12190;
    }

}
