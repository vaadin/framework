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

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.themes.BaseTheme;

@Theme("tests-tickets")
public class TableRowHeight2 extends AbstractReindeerTestUI {

    @Override
    protected String getTestDescription() {
        return "The table contains 2 rows, which both should be shown completely as the table height is undefined.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2747;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout vl = new HorizontalLayout();
        vl.setSizeFull();

        Table table = new Table();

        int COL_TITLE_W = 200;
        int COL_TEST_W = 98;

        table.setPageLength(0);
        table.setColumnWidth("title", COL_TITLE_W);
        table.setColumnWidth("test", COL_TEST_W);
        table.addContainerProperty("title", Button.class, "");
        table.addContainerProperty("test", Button.class, "");
        for (int i = 0; i < 2; i++) {
            Item item = table.addItem(new Object());

            Button b = new Button();
            b.setWidth("100%");
            b.setStyleName(BaseTheme.BUTTON_LINK);
            b.addStyleName("nowraplink");

            b.setCaption(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ullamcorper, elit quis elementum iaculis, dui est rutrum risus, at cursus sem leo eget arcu. Proin vel eros ut tortor luctus pretium. Nulla facilisi. Donec in dui. Proin ac diam vitae massa tempus faucibus. Fusce eu risus. Nunc ac risus. Cras libero.");

            item.getItemProperty("title").setValue(b);

            Button c = new Button("test");
            item.getItemProperty("test").setValue(c);
        }

        vl.addComponent(table);

        addComponent(vl);
    }
}
