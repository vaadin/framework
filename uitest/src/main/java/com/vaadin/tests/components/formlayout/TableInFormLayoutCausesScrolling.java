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
package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class TableInFormLayoutCausesScrolling extends AbstractReindeerTestUI {

    @Override
    public void setup(VaadinRequest request) {

        final FormLayout fl = new FormLayout();
        addComponent(fl);

        for (int i = 20; i-- > 0;) {
            fl.addComponent(new TextField());
        }

        final Table table = new Table();
        table.setSelectable(true);
        table.addContainerProperty("item", String.class, "");
        for (int i = 50; i-- > 0;) {
            table.addItem(new String[] { "item" + i }, i);
        }

        fl.addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking in the Table should not cause the page to scroll";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7309;
    }
}
