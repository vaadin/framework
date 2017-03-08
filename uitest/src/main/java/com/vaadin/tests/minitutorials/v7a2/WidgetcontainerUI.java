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
package com.vaadin.tests.minitutorials.v7a2;

import java.util.Random;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class WidgetcontainerUI extends UI {
    @Override
    public void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Label label = new Label("Hello Vaadin user");
        layout.addComponent(label);
        final WidgetContainer widgetContainer = new WidgetContainer();
        layout.addComponent(widgetContainer);
        widgetContainer.addComponent(new Label(
                "Click the button to add components to the WidgetContainer."));
        Button button = new Button("Add more components", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Random randomGenerator = new Random();
                int random = randomGenerator.nextInt(3);
                Component component;
                if (random % 3 == 0) {
                    component = new Label("A new label");
                } else if (random % 3 == 1) {
                    component = new Button("A button!");
                } else {
                    component = new CheckBox("A textfield");
                }
                widgetContainer.addComponent(component);
            }
        });
        layout.addComponent(button);
    }

}
