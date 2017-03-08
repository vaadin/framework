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
package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class WrapTabSheetInTabSheet extends TestBase {
    @Override
    protected void setup() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponent(new Label("This is main layout"));
        addComponent(mainLayout);

        Button b = new Button("Wrap main layout in a TabSheet");
        b.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                TabSheet tabsheet = new TabSheet();
                ComponentContainer mainParent = (ComponentContainer) mainLayout
                        .getParent();
                mainParent.replaceComponent(mainLayout, tabsheet);
                tabsheet.addTab(mainLayout, "Default tab");
            }
        });
        mainLayout.addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Click the button to add a TabSheet and move the window content into the TabSheet. Every click should wrap the contents with a new TabSheet and the contents should remain visible.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8238;
    }
}
