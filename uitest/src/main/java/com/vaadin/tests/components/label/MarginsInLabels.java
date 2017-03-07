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
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class MarginsInLabels extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        AbstractLayout layout = new VerticalLayout();
        layout.addComponent(
                new Label("<h1>Vertical layout</h1>", ContentMode.HTML));
        layout.addComponent(new Label("Next row"));
        addComponent(layout);

        layout = new GridLayout(1, 2);
        layout.setWidth("100%");
        layout.addComponent(
                new Label("<h1>Grid layout</h1>", ContentMode.HTML));
        layout.addComponent(new Label("Next row"));
        addComponent(layout);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addTab(new Label("<h1>Tabsheet</h1>", ContentMode.HTML),
                "Label");
        addComponent(tabSheet);

        Accordion accordion = new Accordion();
        accordion.addTab(new Label("<h1>Accordion</h1>", ContentMode.HTML),
                "Label");
        addComponent(accordion);
    }

    @Override
    protected String getTestDescription() {
        return "Margins inside labels should not be allowed to collapse out of the label as it causes problems with layotus measuring the label.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8671);
    }

}
