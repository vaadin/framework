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
package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.Table;

public class UncollapsedCollumnWidth extends TestBase {

    @Override
    protected void setup() {
        final Table table = new Table();
        table.addContainerProperty("Col1", String.class, "");
        table.addContainerProperty("Col2", String.class, "");
        table.setColumnCollapsingAllowed(true);
        table.setColumnCollapsed("Col2", true);

        table.setColumnWidth("Col1", 150);

        table.setWidth("400px");

        table.addItem(new Object[] { "Cell 1", "Cell 2" }, new Object());

        addComponent(table);
        addComponent(new Button("Uncollapse col2", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setColumnCollapsed("Col2", false);
            }
        }));
    }

    @Override
    protected String getDescription() {
        return "Uncollapsing col2 after resizing col1 should set a reasonable width for col2. Additionally, the width of the header and the cell content should be the same.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7012);
    }

}
