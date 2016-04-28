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
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableColumnAddAndResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String people[][] = {
                { "Galileo", "Liked to go around the Sun" },
                { "Monnier", "Liked star charts" },
                { "VÃ€isÃ€lÃ€", "Liked optics" },
                { "Oterma", "Liked comets" },
                {
                        "Valtaoja",
                        "Likes cosmology and still "
                                + "lives unlike the others above" }, };

        VerticalLayout content = new VerticalLayout();

        final Table table = new Table("Awesome Table");
        table.setSizeFull();
        table.addContainerProperty("Id1", String.class, "TestString");
        table.addContainerProperty("Id2", String.class, "TestString2");

        for (String[] p : people) {
            table.addItem(p);
        }
        table.setColumnWidth("Id1", 100);

        table.setColumnWidth("Id2", 100);

        table.setVisibleColumns("Id1");
        content.addComponent(table);
        Button button = new Button("Add and Resize");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.setVisibleColumns("Id1", "Id2");
                table.setColumnWidth("Id2", 200);

            }
        });
        content.addComponent(button);
        addComponent(content);

    }
}
