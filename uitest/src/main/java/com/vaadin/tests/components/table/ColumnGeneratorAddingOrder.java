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
import com.vaadin.ui.Component;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class ColumnGeneratorAddingOrder extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2457;
    }

    @Override
    protected String getDescription() {
        return "Column generator must be allowed to be added both before and after data source setting and overriding should work. Bugs in 5.3-rc7 if added after DS.";
    }

    @Override
    protected void setup() {
        Table t = new Table();

        t.addGeneratedColumn("col2", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Button("generated b c2");
            }
        });

        IndexedContainer c = new IndexedContainer();
        c.addContainerProperty("col1", String.class, "col1 ds data");
        c.addContainerProperty("col2", String.class, "col2 ds data");
        c.addContainerProperty("col3", String.class, "col3 ds data");
        for (int i = 0; i < 100; i++) {
            c.addItem();
        }
        t.setContainerDataSource(c);

        t.addGeneratedColumn("col1", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Button("generated b c1");
            }
        });

        getLayout().addComponent(t);

    }

}
