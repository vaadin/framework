package com.vaadin.tests.widgetset.client.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.ColumnConfiguration;
import com.vaadin.client.ui.grid.Escalator;
import com.vaadin.client.ui.grid.EscalatorUpdater;
import com.vaadin.client.ui.grid.Row;
import com.vaadin.client.ui.grid.RowContainer;
import com.vaadin.shared.ui.grid.ScrollDestination;

public class VTestGrid extends Composite {

    private static class Data {
        private int columnCounter = 0;
        private int rowCounter = 0;
        private final List<Integer> columns = new ArrayList<Integer>();
        private final List<Integer> rows = new ArrayList<Integer>();

        @SuppressWarnings("boxing")
        public void insertRows(final int offset, final int amount) {
            final List<Integer> newRows = new ArrayList<Integer>();
            for (int i = 0; i < amount; i++) {
                newRows.add(rowCounter++);
            }
            rows.addAll(offset, newRows);
        }

        @SuppressWarnings("boxing")
        public void insertColumns(final int offset, final int amount) {
            final List<Integer> newColumns = new ArrayList<Integer>();
            for (int i = 0; i < amount; i++) {
                newColumns.add(columnCounter++);
            }
            columns.addAll(offset, newColumns);
        }

        public EscalatorUpdater createHeaderUpdater() {
            return new EscalatorUpdater() {
                @Override
                public void updateCells(final Row row,
                        final Iterable<Cell> cellsToUpdate) {
                    for (final Cell cell : cellsToUpdate) {
                        if (cell.getColumn() % 3 == 0) {
                            cell.setColSpan(2);
                        }

                        final Integer columnName = columns
                                .get(cell.getColumn());
                        cell.getElement().setInnerText("Header " + columnName);
                    }
                }
            };
        }

        public EscalatorUpdater createFooterUpdater() {
            return new EscalatorUpdater() {
                @Override
                public void updateCells(final Row row,
                        final Iterable<Cell> cellsToUpdate) {
                    for (final Cell cell : cellsToUpdate) {
                        if (cell.getColumn() % 3 == 1) {
                            cell.setColSpan(2);
                        }

                        final Integer columnName = columns
                                .get(cell.getColumn());
                        cell.getElement().setInnerText("Footer " + columnName);
                    }
                }
            };
        }

        public EscalatorUpdater createBodyUpdater() {
            return new EscalatorUpdater() {
                private int i = 0;

                public void renderCell(final Cell cell) {
                    final Integer columnName = columns.get(cell.getColumn());
                    final Integer rowName = rows.get(cell.getRow());
                    final String cellInfo = columnName + "," + rowName + " ("
                            + i + ")";

                    if (cell.getColumn() > 0) {
                        cell.getElement().setInnerText("Cell: " + cellInfo);
                    } else {
                        cell.getElement().setInnerText(
                                "Row " + cell.getRow() + ": " + cellInfo);
                    }

                    if (cell.getColumn() % 3 == cell.getRow() % 3) {
                        cell.setColSpan(3);
                    }

                    final double c = i * .1;
                    final int r = (int) ((Math.cos(c) + 1) * 128);
                    final int g = (int) ((Math.cos(c / Math.PI) + 1) * 128);
                    final int b = (int) ((Math.cos(c / (Math.PI * 2)) + 1) * 128);
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

                @Override
                public void updateCells(final Row row,
                        final Iterable<Cell> cellsToUpdate) {
                    for (final Cell cell : cellsToUpdate) {
                        renderCell(cell);
                    }
                }
            };
        }

        public void removeRows(final int offset, final int amount) {
            for (int i = 0; i < amount; i++) {
                rows.remove(offset);
            }
        }

        public void removeColumns(final int offset, final int amount) {
            for (int i = 0; i < amount; i++) {
                columns.remove(offset);
            }
        }
    }

    private final Escalator escalator = new Escalator();
    private final Data data = new Data();

    public VTestGrid() {
        initWidget(escalator);
        final RowContainer header = escalator.getHeader();
        header.setEscalatorUpdater(data.createHeaderUpdater());
        header.insertRows(0, 1);

        final RowContainer footer = escalator.getFooter();
        footer.setEscalatorUpdater(data.createFooterUpdater());
        footer.insertRows(0, 1);

        escalator.getBody().setEscalatorUpdater(data.createBodyUpdater());

        insertRows(0, 100);
        insertColumns(0, 10);

        setWidth(TestGridState.DEFAULT_WIDTH);
        setHeight(TestGridState.DEFAULT_HEIGHT);

    }

    public void insertRows(final int offset, final int number) {
        data.insertRows(offset, number);
        escalator.getBody().insertRows(offset, number);
    }

    public void insertColumns(final int offset, final int number) {
        data.insertColumns(offset, number);
        escalator.getColumnConfiguration().insertColumns(offset, number);
    }

    public ColumnConfiguration getColumnConfiguration() {
        return escalator.getColumnConfiguration();
    }

    public void scrollToRow(final int index,
            final ScrollDestination destination, final int padding) {
        escalator.scrollToRow(index, destination, padding);
    }

    public void scrollToColumn(final int index,
            final ScrollDestination destination, final int padding) {
        escalator.scrollToColumn(index, destination, padding);
    }

    public void removeRows(final int offset, final int amount) {
        data.removeRows(offset, amount);
        escalator.getBody().removeRows(offset, amount);
    }

    public void removeColumns(final int offset, final int amount) {
        data.removeColumns(offset, amount);
        escalator.getColumnConfiguration().removeColumns(offset, amount);
    }

    @Override
    public void setWidth(String width) {
        escalator.setWidth(width);
    }

    @Override
    public void setHeight(String height) {
        escalator.setHeight(height);
    }

    public RowContainer getHeader() {
        return escalator.getHeader();
    }

    public RowContainer getBody() {
        return escalator.getBody();
    }

    public RowContainer getFooter() {
        return escalator.getFooter();
    }

    public void calculateColumnWidths() {
        escalator.calculateColumnWidths();
    }
}
