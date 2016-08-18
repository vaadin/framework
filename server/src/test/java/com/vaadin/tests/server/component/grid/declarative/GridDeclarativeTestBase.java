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
package com.vaadin.tests.server.component.grid.declarative;

import java.util.List;

import com.vaadin.ui.LegacyGrid;
import org.junit.Assert;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.LegacyGrid.Column;
import com.vaadin.ui.LegacyGrid.FooterCell;
import com.vaadin.ui.LegacyGrid.FooterRow;
import com.vaadin.ui.LegacyGrid.HeaderCell;
import com.vaadin.ui.LegacyGrid.HeaderRow;

public class GridDeclarativeTestBase extends DeclarativeTestBase<LegacyGrid> {

    @Override
    public LegacyGrid testRead(String design, LegacyGrid expected) {
        return testRead(design, expected, false);
    }

    public LegacyGrid testRead(String design, LegacyGrid expected, boolean retestWrite) {
        return testRead(design, expected, retestWrite, false);
    }

    public LegacyGrid testRead(String design, LegacyGrid expected, boolean retestWrite,
            boolean writeData) {
        LegacyGrid actual = super.testRead(design, expected);

        compareGridColumns(expected, actual);
        compareHeaders(expected, actual);
        compareFooters(expected, actual);

        if (retestWrite) {
            testWrite(design, actual, writeData);
        }

        return actual;
    }

    private void compareHeaders(LegacyGrid expected, LegacyGrid actual) {
        Assert.assertEquals("Different header row count",
                expected.getHeaderRowCount(), actual.getHeaderRowCount());
        for (int i = 0; i < expected.getHeaderRowCount(); ++i) {
            HeaderRow expectedRow = expected.getHeaderRow(i);
            HeaderRow actualRow = actual.getHeaderRow(i);

            if (expectedRow.equals(expected.getDefaultHeaderRow())) {
                Assert.assertEquals("Different index for default header row",
                        actual.getDefaultHeaderRow(), actualRow);
            }

            for (Column c : expected.getColumns()) {
                String baseError = "Difference when comparing cell for "
                        + c.toString() + " on header row " + i + ": ";
                Object propertyId = c.getPropertyId();
                HeaderCell expectedCell = expectedRow.getCell(propertyId);
                HeaderCell actualCell = actualRow.getCell(propertyId);

                switch (expectedCell.getCellType()) {
                case TEXT:
                    Assert.assertEquals(baseError + "Text content",
                            expectedCell.getText(), actualCell.getText());
                    break;
                case HTML:
                    Assert.assertEquals(baseError + "HTML content",
                            expectedCell.getHtml(), actualCell.getHtml());
                    break;
                case WIDGET:
                    assertEquals(baseError + "Component content",
                            expectedCell.getComponent(),
                            actualCell.getComponent());
                    break;
                }
            }
        }
    }

    private void compareFooters(LegacyGrid expected, LegacyGrid actual) {
        Assert.assertEquals("Different footer row count",
                expected.getFooterRowCount(), actual.getFooterRowCount());
        for (int i = 0; i < expected.getFooterRowCount(); ++i) {
            FooterRow expectedRow = expected.getFooterRow(i);
            FooterRow actualRow = actual.getFooterRow(i);

            for (Column c : expected.getColumns()) {
                String baseError = "Difference when comparing cell for "
                        + c.toString() + " on footer row " + i + ": ";
                Object propertyId = c.getPropertyId();
                FooterCell expectedCell = expectedRow.getCell(propertyId);
                FooterCell actualCell = actualRow.getCell(propertyId);

                switch (expectedCell.getCellType()) {
                case TEXT:
                    Assert.assertEquals(baseError + "Text content",
                            expectedCell.getText(), actualCell.getText());
                    break;
                case HTML:
                    Assert.assertEquals(baseError + "HTML content",
                            expectedCell.getHtml(), actualCell.getHtml());
                    break;
                case WIDGET:
                    assertEquals(baseError + "Component content",
                            expectedCell.getComponent(),
                            actualCell.getComponent());
                    break;
                }
            }
        }
    }

    private void compareGridColumns(LegacyGrid expected, LegacyGrid actual) {
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
            assertEquals(baseError + "Hidable", col1.isHidable(),
                    col2.isHidable());
            assertEquals(baseError + "Hidden", col1.isHidden(),
                    col2.isHidden());
            assertEquals(baseError + "HidingToggleCaption",
                    col1.getHidingToggleCaption(),
                    col2.getHidingToggleCaption());
        }
    }
}
