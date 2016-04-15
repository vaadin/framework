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
package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

@Theme("valo")
public class TableWithEmptyCaption extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        table.addContainerProperty("first", String.class, "");
        table.addContainerProperty("last", String.class, "");
        table.addContainerProperty("actions", Component.class, null);

        table.addItem(new Object[] { "Teemu", "Test", new Button("Edit") }, 1);
        table.addItem(new Object[] { "Dummy", "Test", new Button("Edit") }, 2);

        table.setPageLength(0);
        table.setColumnHeaders("First Name", "Last Name", "");

        table.setFooterVisible(true);
        table.setColumnFooter("first", "Footer");
        table.setColumnFooter("last", "");
        table.setColumnFooter("actions", "");
        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "Test that column headers (and footers) work properly with empty captions.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14812;
    }
}
