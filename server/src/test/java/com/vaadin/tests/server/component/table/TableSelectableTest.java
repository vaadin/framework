package com.vaadin.tests.server.component.table;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;

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

        Assert.assertTrue(table.isSelectable());
    }

    @Test
    public void addValueChangeListener_explicitSelectable_tableIsSelectable() {
        TestTable table = new TestTable();
        table.addValueChangeListener(
                EasyMock.createMock(ValueChangeListener.class));

        Assert.assertTrue(table.isSelectable());
        Assert.assertTrue(table.markAsDirtyCalled);
    }

    @Test
    public void tableIsNotSelectableByDefult() {
        Table table = new Table();

        Assert.assertFalse(table.isSelectable());
    }

    @Test
    public void setSelectable_explicitNotSelectable_tableIsNotSelectable() {
        Table table = new Table();
        table.setSelectable(false);
        table.addValueChangeListener(
                EasyMock.createMock(ValueChangeListener.class));

        Assert.assertFalse(table.isSelectable());
    }

    private static final class TestTable extends Table {
        @Override
        public void markAsDirty() {
            markAsDirtyCalled = true;
        }

        private boolean markAsDirtyCalled;
    }
}
