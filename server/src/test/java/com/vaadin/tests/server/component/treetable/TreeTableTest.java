package com.vaadin.tests.server.component.treetable;

import java.util.EnumSet;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.treetable.TreeTableState;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TreeTable;

/**
 * Tests for {@link TreeTable}
 *
 * @author Vaadin Ltd
 */
public class TreeTableTest {

    @Test
    public void rowHeadersAreEnabled_iconRowHeaderMode_rowHeadersAreDisabled() {
        TestTreeTable tree = new TestTreeTable();
        tree.setRowHeaderMode(RowHeaderMode.ICON_ONLY);

        Assert.assertFalse("Row headers are enabled for Icon header mode",
                tree.rowHeadersAreEnabled());
    }

    @Test
    public void rowHeadersAreEnabled_hiddenRowHeaderMode_rowHeadersAreDisabled() {
        TestTreeTable tree = new TestTreeTable();
        tree.setRowHeaderMode(RowHeaderMode.HIDDEN);

        Assert.assertFalse("Row headers are enabled for Hidden header mode",
                tree.rowHeadersAreEnabled());
    }

    @Test
    public void rowHeadersAreEnabled_otherRowHeaderModes_rowHeadersAreEnabled() {
        TestTreeTable tree = new TestTreeTable();
        EnumSet<RowHeaderMode> modes = EnumSet.allOf(RowHeaderMode.class);
        modes.remove(RowHeaderMode.ICON_ONLY);
        modes.remove(RowHeaderMode.HIDDEN);

        for (RowHeaderMode mode : modes) {
            tree.setRowHeaderMode(mode);
            Assert.assertTrue(
                    "Row headers are disabled for " + mode + " header mode",
                    tree.rowHeadersAreEnabled());
        }
    }

    @Test
    public void getState_treeTableHasCustomState() {
        TestTreeTable table = new TestTreeTable();
        TreeTableState state = table.getState();
        Assert.assertEquals("Unexpected state class", TreeTableState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_treeTableHasCustomPrimaryStyleName() {
        TreeTable table = new TreeTable();
        TreeTableState state = new TreeTableState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, table.getPrimaryStyleName());
    }

    @Test
    public void treeTableStateHasCustomPrimaryStyleName() {
        TreeTableState state = new TreeTableState();
        Assert.assertEquals("Unexpected primary style name", "v-table",
                state.primaryStyleName);
    }

    private static class TestTreeTable extends TreeTable {

        @Override
        protected boolean rowHeadersAreEnabled() {
            return super.rowHeadersAreEnabled();
        }

        @Override
        public TreeTableState getState() {
            return super.getState();
        }
    }
}
