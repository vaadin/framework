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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.table.TableState;
import com.vaadin.ui.Table;

/**
 * Tests for Table State.
 * 
 */
public class TableStateTest {

    @Test
    public void getState_tableHasCustomState() {
        TestTable table = new TestTable();
        TableState state = table.getState();
        Assert.assertEquals("Unexpected state class", TableState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_tableHasCustomPrimaryStyleName() {
        Table table = new Table();
        TableState state = new TableState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, table.getPrimaryStyleName());
    }

    @Test
    public void tableStateHasCustomPrimaryStyleName() {
        TableState state = new TableState();
        Assert.assertEquals("Unexpected primary style name", "v-table",
                state.primaryStyleName);
    }

    private static class TestTable extends Table {

        @Override
        public TableState getState() {
            return super.getState();
        }
    }
}
