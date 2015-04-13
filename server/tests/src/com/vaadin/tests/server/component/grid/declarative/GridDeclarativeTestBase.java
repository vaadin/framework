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
package com.vaadin.tests.server.component.grid.declarative;

import java.util.List;

import org.junit.Assert;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

public class GridDeclarativeTestBase extends DeclarativeTestBase<Grid> {

    @Override
    public Grid testRead(String design, Grid expected) {
        return testRead(design, expected, false);
    }

    public Grid testRead(String design, Grid expected, boolean retestWrite) {
        Grid readGrid = super.testRead(design, expected);

        compareGridColumns(expected, readGrid);

        if (retestWrite) {
            testWrite(design, readGrid);
        }

        return readGrid;
    }

    private void compareGridColumns(Grid expected, Grid actual) {
        List<Column> columns = expected.getColumns();
        List<Column> actualColumns = actual.getColumns();
        Assert.assertEquals("Different amount of columns", columns.size(),
                actualColumns.size());
        for (int i = 0; i < columns.size(); ++i) {
            Column col1 = columns.get(i);
            Column col2 = actualColumns.get(i);
            String baseError = "Error when comparing columns for property "
                    + col1.getPropertyId() + ": ";
            assertEquals(baseError + "Property id", col1.getPropertyId(),
                    col2.getPropertyId());
            assertEquals(baseError + "Width", col1.getWidth(), col2.getWidth());
            assertEquals(baseError + "Maximum width", col1.getMaximumWidth(),
                    col2.getMaximumWidth());
            assertEquals(baseError + "Minimum width", col1.getMinimumWidth(),
                    col2.getMinimumWidth());
            assertEquals(baseError + "Expand ratio", col1.getExpandRatio(),
                    col2.getExpandRatio());
            assertEquals(baseError + "Sortable", col1.isSortable(),
                    col2.isSortable());
            assertEquals(baseError + "Editable", col1.isEditable(),
                    col2.isEditable());

        }
    }
}
