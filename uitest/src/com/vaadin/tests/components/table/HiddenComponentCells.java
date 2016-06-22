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

/**
 * 
 */
package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * 
 * @author Vaadin Ltd
 */
public class HiddenComponentCells extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Table tbl = new Table();
        tbl.addContainerProperty("col1", Label.class, null);
        tbl.addContainerProperty("col2", Label.class, null);

        for (int rows = 0; rows < 20; rows++) {

            Item item = tbl.addItem(rows);

            Label cb = new Label("col1");
            cb.setVisible(rows % 2 == 0);
            item.getItemProperty("col1").setValue(cb);

            cb = new Label("col2");
            cb.setVisible((rows + 1) % 2 == 0);
            item.getItemProperty("col2").setValue(cb);
        }

        addComponent(tbl);
    }

    @Override
    protected String getTestDescription() {
        return "Hiding a component in the Table should not cause exceptions. <br/> Every other cell in the table should be hidden.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12119;
    }

}
