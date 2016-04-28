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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

public class TableTooManyColumns extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();

        table.setColumnCollapsingAllowed(true);

        for (int i = 0; i < 91; i++) {
            table.addGeneratedColumn("COLUMN " + i, new ColumnGenerator() {

                @Override
                public Object generateCell(Table source, Object itemId,
                        Object columnId) {
                    return columnId;
                }
            });
        }

        addComponent(table);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Table column drop down becomes too large to fit the screen.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14156;
    }

}
