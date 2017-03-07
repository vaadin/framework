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
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class RepaintWindowContents extends AbstractReindeerTestUI {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("serial")
    @Override
    protected void setup(VaadinRequest request) {
        final Window window = new Window("Test window");
        addWindow(window);

        final Layout layout1 = new VerticalLayout();
        Button button1 = new Button("Button 1");
        layout1.addComponent(button1);

        final Layout layout2 = new VerticalLayout();
        Button button2 = new Button("Button 2");
        layout2.addComponent(button2);

        window.setContent(layout1);

        button1.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                window.setContent(layout2);
            }
        });

        button2.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                window.setContent(layout1);
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Clicking the button switches the content between content1 and content2";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8832;
    }

}
