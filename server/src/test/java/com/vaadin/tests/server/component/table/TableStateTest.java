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
