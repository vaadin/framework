package com.vaadin.v7.tests.server.component.treetable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;

import com.vaadin.v7.shared.ui.treetable.TreeTableState;
import com.vaadin.v7.ui.Table.RowHeaderMode;
import com.vaadin.v7.ui.TreeTable;

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

        assertFalse("Row headers are enabled for Icon header mode",
                tree.rowHeadersAreEnabled());
    }

    @Test
    public void rowHeadersAreEnabled_hiddenRowHeaderMode_rowHeadersAreDisabled() {
        TestTreeTable tree = new TestTreeTable();
        tree.setRowHeaderMode(RowHeaderMode.HIDDEN);

        assertFalse("Row headers are enabled for Hidden header mode",
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
            assertTrue("Row headers are disabled for " + mode + " header mode",
                    tree.rowHeadersAreEnabled());
        }
    }

    @Test
    public void getState_treeTableHasCustomState() {
        TestTreeTable table = new TestTreeTable();
        TreeTableState state = table.getState();
        assertEquals("Unexpected state class", TreeTableState.class,
                state.getClass());
    }

    @Test
    public void getPrimaryStyleName_treeTableHasCustomPrimaryStyleName() {
        TreeTable table = new TreeTable();
        TreeTableState state = new TreeTableState();
        assertEquals("Unexpected primary style name", state.primaryStyleName,
                table.getPrimaryStyleName());
    }

    @Test
    public void treeTableStateHasCustomPrimaryStyleName() {
        TreeTableState state = new TreeTableState();
        assertEquals("Unexpected primary style name", "v-table",
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
