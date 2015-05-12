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
package com.vaadin.tests.server.component.table;

import static org.junit.Assert.assertTrue;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Table;

public abstract class TableDeclarativeTestBase extends
        DeclarativeTestBase<Table> {

    @Override
    public Table testRead(String design, Table expected) {
        Table read = super.testRead(design, expected);
        compareColumns(read, expected);
        compareBody(read, expected);
        return read;
    }

    protected Table getTable() {
        return new Table();
    }

    protected String getTag() {
        return "v-table";
    }

    protected void compareBody(Table read, Table expected) {
        assertEquals("number of items", expected.getItemIds().size(), read
                .getItemIds().size());
        for (Object rowId : expected.getItemIds()) {
            assertTrue(read.containsId(rowId));
            for (Object propertyId : read.getVisibleColumns()) {
                Object expectedItem = expected.getContainerProperty(rowId,
                        propertyId);
                Object readItem = read.getContainerProperty(rowId, propertyId);
                assertEquals("property '" + propertyId + "'", expectedItem,
                        readItem);
            }
        }
    }

    protected void compareColumns(Table read, Table expected) {
        for (Object pid : expected.getVisibleColumns()) {
            String col = "column '" + pid + "'";
            assertEquals(col + " width", expected.getColumnWidth(pid),
                    read.getColumnWidth(pid));
            assertEquals(col + " expand ratio",
                    expected.getColumnExpandRatio(pid),
                    read.getColumnExpandRatio(pid));
            assertEquals(col + " collapsible",
                    expected.isColumnCollapsible(pid),
                    read.isColumnCollapsible(pid));
            assertEquals(col + " collapsed", expected.isColumnCollapsed(pid),
                    read.isColumnCollapsed(pid));
            assertEquals(col + " footer", expected.getColumnFooter(pid),
                    read.getColumnFooter(pid));
        }
    }
}
