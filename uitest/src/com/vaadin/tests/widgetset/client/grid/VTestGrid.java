package com.vaadin.tests.widgetset.client.grid;

import com.google.gwt.user.client.ui.Composite;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.CellRenderer;
import com.vaadin.client.ui.grid.ColumnConfiguration;
import com.vaadin.client.ui.grid.Escalator;
import com.vaadin.client.ui.grid.RowContainer;

public class VTestGrid extends Composite {
    public static class HeaderRenderer implements CellRenderer {
        private int i = 0;

        @Override
        public void renderCell(final Cell cell) {
            cell.getElement().setInnerText("Header " + (i++));
        }
    }

    public static class BodyRenderer implements CellRenderer {
        private int i = 0;
        private int ri = 0;

        @Override
        public void renderCell(final Cell cell) {
            if (cell.getColumn() != 0) {
                cell.getElement().setInnerText("Cell #" + (i++));
            } else {
                cell.getElement().setInnerText(
                        "Logical row " + cell.getRow() + "/" + (ri++));
            }

            double c = i * .1;
            int r = (int) ((Math.cos(c) + 1) * 128);
            int g = (int) ((Math.cos(c / Math.PI) + 1) * 128);
            int b = (int) ((Math.cos(c / (Math.PI * 2)) + 1) * 128);
            cell.getElement().getStyle()
                    .setBackgroundColor("rgb(" + r + "," + g + "," + b + ")");
            if ((r * .8 + g * 1.3 + b * .9) / 3 < 127) {
                cell.getElement().getStyle().setColor("white");
            } else {
                cell.getElement().getStyle().clearColor();
            }
        }
    }

    public static class FooterRenderer implements CellRenderer {
        private int i = 0;

        @Override
        public void renderCell(final Cell cell) {
            cell.getElement().setInnerText("Footer " + (i++));
        }
    }

    private Escalator escalator = new Escalator();

    public VTestGrid() {
        initWidget(escalator);
        final ColumnConfiguration cConf = escalator.getColumnConfiguration();
        cConf.insertColumns(cConf.getColumnCount(), 5);

        final RowContainer h = escalator.getHeader();
        h.setCellRenderer(new HeaderRenderer());
        h.insertRows(0, 1);

        final RowContainer b = escalator.getBody();
        b.setCellRenderer(new BodyRenderer());
        b.insertRows(0, 10);

        final RowContainer f = escalator.getFooter();
        f.setCellRenderer(new FooterRenderer());
        f.insertRows(0, 1);

        setWidth(TestGridState.DEFAULT_WIDTH);
        setHeight(TestGridState.DEFAULT_HEIGHT);

    }

    public RowContainer getBody() {
        return escalator.getBody();
    }

    public ColumnConfiguration getColumnConfiguration() {
        return escalator.getColumnConfiguration();
    }
}
