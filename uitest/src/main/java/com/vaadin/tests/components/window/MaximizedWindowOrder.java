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
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MaximizedWindowOrder extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Open Maximized Window", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                openWindow(true);
            }
        });
    }

    private void openWindow(boolean maximized) {
        Window window = new Window();
        VerticalLayout layout = new VerticalLayout();

        Label label = new Label(maximized ? "Maximized" : "Normal");

        layout.addComponent(label);
        Button button = new Button("Open Normal Window");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                openWindow(false);
            }

        });

        layout.addComponent(button);

        window.setContent(layout);
        window.setWindowMode(
                maximized ? WindowMode.MAXIMIZED : WindowMode.NORMAL);

        addWindow(window);
    }
}
