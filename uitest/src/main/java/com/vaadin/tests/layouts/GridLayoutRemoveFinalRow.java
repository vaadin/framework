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
package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

/**
 * Tests removing rows from a GridLayout
 */
@SuppressWarnings("serial")
public class GridLayoutRemoveFinalRow extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSpacing(true);

        final GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);
        layout.addComponent(new Label("Label1"));
        layout.addComponent(new Label("Label2"));
        layout.addComponent(new Label("Label3"));
        layout.addComponent(new Label("Label4"));
        addComponent(layout);

        Button removeRowBtn = new Button("Remove row",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        layout.removeRow(0);
                    }
                });
        addComponent(removeRowBtn);
    }

    @Override
    protected String getDescription() {
        return "Removing last row of a GridLayout throws a IllegalArgumentException";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4542;
    }

}
