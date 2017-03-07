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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.Table;

public class TableModifcationsWhenScrolledRight extends TestBase {

    @Override
    protected void setup() {
        final Table t = new Table();
        Button btn = new Button("Add row");
        Integer row = 1;

        t.setPageLength(5);
        t.setWidth("400px");
        t.addContainerProperty("name", String.class, "NA");
        t.setColumnCollapsingAllowed(true);

        for (Integer col = 0; col < 10; col++) {
            t.addContainerProperty(col, Integer.class, col);
            t.setColumnWidth(col, 50);
        }
        t.addItem(row).getItemProperty("name").setValue("Row" + row);

        btn.addClickListener(new ClickListener() {
            Integer row = 2;

            @Override
            public void buttonClick(ClickEvent event) {
                t.addItem(row).getItemProperty("name").setValue("Row" + row);
                row++;
            }
        });

        addComponent(t);
        addComponent(btn);
    }

    @Override
    protected String getDescription() {
        return "Scroll right and then click \"Add row\". The table will scroll back left and the headers should also.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5382;
    }

}
