package com.vaadin.v7.tests.server.component.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.Table;

/**
 * Tests for 'selectable' property of {@link Table} class.
 *
 * @author Vaadin Ltd
 */
public class TableSelectableTest {

    @Test
    public void setSelectable_explicitSelectable_tableIsSelectable() {
        Table table = new Table();
        table.setSelectable(true);

        assertTrue(table.isSelectable());
    }

    @Test
    public void addValueChangeListener_explicitSelectable_tableIsSelectable() {
        TestTable table = new TestTable();
        table.addValueChangeListener(
                EasyMock.createMock(ValueChangeListener.class));

        assertTrue(table.isSelectable());
        assertTrue(table.markAsDirtyCalled);
    }

    @Test
    public void tableIsNotSelectableByDefult() {
        Table table = new Table();

        assertFalse(table.isSelectable());
    }

    @Test
    public void setSelectable_explicitNotSelectable_tableIsNotSelectable() {
        Table table = new Table();
        table.setSelectable(false);
        table.addValueChangeListener(
                EasyMock.createMock(ValueChangeListener.class));

        assertFalse(table.isSelectable());
    }

    private static final class TestTable extends Table {
        @Override
        public void markAsDirty() {
            markAsDirtyCalled = true;
        }

        private boolean markAsDirtyCalled;
    }
}
