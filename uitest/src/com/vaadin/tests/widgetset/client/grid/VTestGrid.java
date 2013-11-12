package com.vaadin.tests.widgetset.client.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.CellRenderer;
import com.vaadin.client.ui.grid.ColumnConfiguration;
import com.vaadin.client.ui.grid.Escalator;
import com.vaadin.client.ui.grid.RowContainer;
import com.vaadin.client.ui.grid.ScrollDestination;

public class VTestGrid extends Composite {

    private static class Data {
        private int columnCounter = 0;
        private int rowCounter = 0;
        private final List<Integer> columns = new ArrayList<Integer>();
        private final List<Integer> rows = new ArrayList<Integer>();

        public void insertRows(int offset, int amount) {
            List<Integer> newRows = new ArrayList<Integer>();
            for (int i = 0; i < amount; i++) {
                newRows.add(rowCounter++);
            }
            rows.addAll(offset, newRows);
        }

        public void insertColumns(int offset, int amount) {
            List<Integer> newColumns = new ArrayList<Integer>();
            for (int i = 0; i < amount; i++) {
                newColumns.add(columnCounter++);
            }
            columns.addAll(offset, newColumns);
        }

        public CellRenderer createHeaderRenderer() {
            return new CellRenderer() {
                @Override
                public void renderCell(Cell cell) {
                    int columnName = columns.get(cell.getColumn());
                    cell.getElement().setInnerText("Header " + columnName);
                }
            };
        }

        public CellRenderer createFooterRenderer() {
            return new CellRenderer() {
                @Override
                public void renderCell(Cell cell) {
                    int columnName = columns.get(cell.getColumn());
                    cell.getElement().setInnerText("Footer " + columnName);
                }
            };
        }

        public CellRenderer createBodyRenderer() {
            return new CellRenderer() {
                int i = 0;

                @Override
                public void renderCell(Cell cell) {
                    int columnName = columns.get(cell.getColumn());
                    int rowName = rows.get(cell.getRow());
                    String cellInfo = columnName + "," + rowName + " (" + i
                            + ")";

                    if (cell.getColumn() > 0) {
                        cell.getElement().setInnerText("Cell: " + cellInfo);
                    } else {
                        cell.getElement().setInnerText(
                                "Row " + cell.getRow() + ": " + cellInfo);
                    }

                    double c = i * .1;
                    int r = (int) ((Math.cos(c) + 1) * 128);
                    int g = (int) ((Math.cos(c / Math.PI) + 1) * 128);
                    int b = (int) ((Math.cos(c / (Math.PI * 2)) + 1) * 128);
                    cell.getElement()
                            .getStyle()
                            .setBackgroundColor(
                                    "rgb(" + r + "," + g + "," + b + ")");
                    if ((r * .8 + g * 1.3 + b * .9) / 3 < 127) {
                        cell.getElement().getStyle().setColor("white");
                    } else {
                        cell.getElement().getStyle().clearColor();
                    }

                    i++;
                }
            };
        }

        public void removeRows(int offset, int amount) {
            for (int i = 0; i < amount; i++) {
                rows.remove(offset);
            }
        }

        public void removeColumns(int offset, int amount) {
            for (int i = 0; i < amount; i++) {
                columns.remove(offset);
            }
        }
    }

    private Escalator escalator = new Escalator();
    private Data data = new Data();

    public VTestGrid() {
        initWidget(escalator);
        RowContainer header = escalator.getHeader();
        header.setCellRenderer(data.createHeaderRenderer());
        header.insertRows(0, 1);

        RowContainer footer = escalator.getFooter();
        footer.setCellRenderer(data.createFooterRenderer());
        footer.insertRows(0, 1);

        escalator.getBody().setCellRenderer(data.createBodyRenderer());

        insertRows(0, 100);
        insertColumns(0, 10);

        setWidth(TestGridState.DEFAULT_WIDTH);
        setHeight(TestGridState.DEFAULT_HEIGHT);

    }

    public void insertRows(int offset, int number) {
        data.insertRows(offset, number);
        escalator.getBody().insertRows(offset, number);
    }

    public void insertColumns(int offset, int number) {
        data.insertColumns(offset, number);
        escalator.getColumnConfiguration().insertColumns(offset, number);
    }

    public ColumnConfiguration getColumnConfiguration() {
        return escalator.getColumnConfiguration();
    }

    public void scrollToRow(int index, ScrollDestination destination,
            int padding) {
        if (padding != 0) {
            escalator.scrollToRow(index, destination, padding);
        } else {
            escalator.scrollToRow(index, destination);
        }
    }

    public void scrollToColumn(int index, ScrollDestination destination,
            int padding) {
        if (padding != 0) {
            escalator.scrollToColumn(index, destination, padding);
        } else {
            escalator.scrollToColumn(index, destination);
        }
    }

    public void removeRows(int offset, int amount) {
        data.removeRows(offset, amount);
        escalator.getBody().removeRows(offset, amount);
    }

    public void removeColumns(int offset, int amount) {
        data.removeColumns(offset, amount);
        escalator.getColumnConfiguration().removeColumns(offset, amount);
    }
}
