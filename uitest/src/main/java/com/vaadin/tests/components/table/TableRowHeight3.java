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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.themes.BaseTheme;

public class TableRowHeight3 extends TestBase {

    @Override
    protected String getDescription() {
        return "All rows should be visible and the table height should match the height of the rows (no vertical scrollbar)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2747;
    }

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        HorizontalLayout vl = new HorizontalLayout();
        vl.setSizeFull();

        Table table = new Table();
        table.setWidth("320px");
        table.setPageLength(0);
        table.setColumnWidth("title", 200);
        table.setColumnWidth("test", 98);
        table.addContainerProperty("title", Button.class, "");
        table.addContainerProperty("test", Button.class, "");
        for (int i = 0; i < 6; i++) {
            Item item = table.addItem(new Object());

            Button b = new Button();
            b.setWidth("100%");
            b.setStyleName(BaseTheme.BUTTON_LINK);
            b.addStyleName("nowraplink");
            if (i < 2) {
                b.setCaption(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ullamcorper, elit quis elementum iaculis, dui est rutrum risus, at cursus sem leo eget arcu. Proin vel eros ut tortor luctus pretium. Nulla facilisi. Donec in dui. Proin ac diam vitae massa tempus faucibus. Fusce eu risus. Nunc ac risus. Cras libero.");
            } else if (2 <= i && i < 4) {
                b.setCaption("One line");
            } else {
                b.setCaption("This button caption should use up two lines");
            }
            item.getItemProperty("title").setValue(b);

            Button c = new Button("test");
            item.getItemProperty("test").setValue(c);
        }

        vl.addComponent(table);

        addComponent(vl);

    }

}
