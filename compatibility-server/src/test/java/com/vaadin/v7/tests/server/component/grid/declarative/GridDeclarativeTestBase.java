package com.vaadin.v7.tests.server.component.grid.declarative;

import java.util.List;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.Grid.FooterCell;
import com.vaadin.v7.ui.Grid.FooterRow;
import com.vaadin.v7.ui.Grid.HeaderCell;
import com.vaadin.v7.ui.Grid.HeaderRow;

public class GridDeclarativeTestBase extends DeclarativeTestBase<Grid> {

    @Override
    public Grid testRead(String design, Grid expected) {
        return testRead(design, expected, false);
    }

    public Grid testRead(String design, Grid expected, boolean retestWrite) {
        return testRead(design, expected, retestWrite, false);
    }

    public Grid testRead(String design, Grid expected, boolean retestWrite,
            boolean writeData) {
        Grid actual = super.testRead(design, expected);

        compareGridColumns(expected, actual);
        compareHeaders(expected, actual);
        compareFooters(expected, actual);

        if (retestWrite) {
            testWrite(design, actual, writeData);
        }

        return actual;
    }

    private void compareHeaders(Grid expected, Grid actual) {
        assertEquals("Different header row count", expected.getHeaderRowCount(),
                actual.getHeaderRowCount());
        for (int i = 0; i < expected.getHeaderRowCount(); ++i) {
            HeaderRow expectedRow = expected.getHeaderRow(i);
            HeaderRow actualRow = actual.getHeaderRow(i);

            if (expectedRow.equals(expected.getDefaultHeaderRow())) {
                assertEquals("Different index for default header row",
                        actual.getDefaultHeaderRow(), actualRow);
            }

            for (Column c : expected.getColumns()) {
                String baseError = "Difference when comparing cell for " + c
                        + " on header row " + i + ": ";
                Object propertyId = c.getPropertyId();
                HeaderCell expectedCell = expectedRow.getCell(propertyId);
                HeaderCell actualCell = actualRow.getCell(propertyId);

                switch (expectedCell.getCellType()) {
                case TEXT:
                    assertEquals(baseError + "Text content",
                            expectedCell.getText(), actualCell.getText());
                    break;
                case HTML:
                    assertEquals(baseError + "HTML content",
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

    private void compareFooters(Grid expected, Grid actual) {
        assertEquals("Different footer row count", expected.getFooterRowCount(),
                actual.getFooterRowCount());
        for (int i = 0; i < expected.getFooterRowCount(); ++i) {
            FooterRow expectedRow = expected.getFooterRow(i);
            FooterRow actualRow = actual.getFooterRow(i);

            for (Column c : expected.getColumns()) {
                String baseError = "Difference when comparing cell for " + c
                        + " on footer row " + i + ": ";
                Object propertyId = c.getPropertyId();
                FooterCell expectedCell = expectedRow.getCell(propertyId);
                FooterCell actualCell = actualRow.getCell(propertyId);

                switch (expectedCell.getCellType()) {
                case TEXT:
                    assertEquals(baseError + "Text content",
                            expectedCell.getText(), actualCell.getText());
                    break;
                case HTML:
                    assertEquals(baseError + "HTML content",
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

    private void compareGridColumns(Grid expected, Grid actual) {
        List<Column> columns = expected.getColumns();
        List<Column> actualColumns = actual.getColumns();
        assertEquals("Different amount of columns", columns.size(),
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
