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
package com.vaadin.tests.components.label;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.Label;

public class LabelPropertySourceValue extends AbstractReindeerTestUI {
    private Label label;

    @Override
    public void setup(VaadinRequest request) {
        label = new Label("Hello Vaadin user");
        addComponent(label);
        Button button = new Button("Give label a new property data source...");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ObjectProperty<String> p = new ObjectProperty<>(
                        "This text should appear on the label after clicking the button.");

                label.setPropertyDataSource(p);
            }
        });
        addComponent(button);
        button = new Button("Remove data source", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                label.setPropertyDataSource(null);
            }
        });
        addComponent(button);

        button = new Button("Set label value to 'foo'", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                label.setValue("foo");
            }
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "The value should change by clicking the button";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9618;
    }

}
