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
package com.vaadin.tests.server.component.treetable;

import java.util.EnumSet;

import org.junit.Assert;
import org.junit.Test;

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
            Assert.assertTrue("Row headers are disabled for " + mode
                    + " header mode", tree.rowHeadersAreEnabled());
        }
    }

    private static class TestTreeTable extends TreeTable {

        @Override
        protected boolean rowHeadersAreEnabled() {
            return super.rowHeadersAreEnabled();
        }
    }
}
