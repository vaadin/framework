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
package com.vaadin.tests.elements.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelScroll extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel panel = new Panel();
        panel.setId("mainPanel");
        panel.setWidth("200px");
        panel.setHeight("200px");
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("500px");
        layout.setHeight("500px");
        Button btn = new Button("Button 1");
        Button btn2 = new Button("Button 2");
        layout.addComponent(btn);
        layout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        layout.addComponent(btn2);
        layout.setComponentAlignment(btn2, Alignment.BOTTOM_LEFT);
        panel.setContent(layout);
        addComponent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "Test scroll left and scroll right methods of PanelElement";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14819;
    }
}
