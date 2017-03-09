/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.tests.components.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelSetScrollTopWithLargeNumber extends AbstractTestUI {

    Panel panel = new Panel();

    @Override
    public String getDescription() {
        return "Click the button to scroll down " + Integer.MAX_VALUE
                + " pixels";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1149;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        panel.setHeight("500px");
        String s = "";
        for (int i = 0; i < 10000; i++) {
            s += i + "<br />";
        }
        Label label = new Label(s, com.vaadin.shared.ui.ContentMode.HTML);
        layout.addComponent(label);
        panel.setContent(layout);
        panel.setScrollTop(Integer.MAX_VALUE);
        addComponent(panel);
    }

}
