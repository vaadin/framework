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
package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * For tables that are contained in a layout, a delayed column layouting should
 * not be visible (because it makes the column jump around).
 * 
 * #15189
 * 
 * @author Vaadin Ltd
 */
public class DelayedColumnLayouting extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        Button reset = new Button("Recreate layout with contained table");
        verticalLayout.addComponent(reset);
        reset.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                fillLayout(layout);
            }
        });

        fillLayout(layout);

        verticalLayout.addComponent(layout);
        verticalLayout.setExpandRatio(layout, 1f);

        setContent(verticalLayout);
    }

    private void fillLayout(VerticalLayout layout) {
        layout.removeAllComponents();

        Table table = new Table();
        table.setSizeFull();
        table.addContainerProperty("First", String.class, "");
        table.addContainerProperty("This column jumps", String.class, "");

        layout.addComponent(table);
        layout.setExpandRatio(table, 1f);
    }
}