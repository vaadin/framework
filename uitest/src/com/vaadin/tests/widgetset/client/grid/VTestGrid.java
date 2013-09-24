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

        @Override
        public void renderCell(final Cell cell) {
            cell.getElement().setInnerText("Cell #" + (i++));

            double c = i * .1;
            int r = (int) ((Math.cos(c) + 1) * 128);
            int g = (int) ((Math.cos(c / Math.PI) + 1) * 128);
            int b = (int) ((Math.cos(c / (Math.PI * 2)) + 1) * 128);
            cell.getElement().getStyle()
                    .setBackgroundColor("rgb(" + r + "," + g + "," + b + ")");
            if ((r + g + b) / 3 < 127) {
                cell.getElement().getStyle().setColor("white");
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
        cConf.insertColumns(0, 1);
        cConf.insertColumns(0, 1); // prepend one column
        cConf.insertColumns(cConf.getColumnCount(), 1); // append one column
        // cConf.insertColumns(cConf.getColumnCount(), 10); // append 10 columns

        final RowContainer h = escalator.getHeader();
        h.setCellRenderer(new HeaderRenderer());
        h.insertRows(0, 1);

        final RowContainer b = escalator.getBody();
        b.setCellRenderer(new BodyRenderer());
        b.insertRows(0, 5);

        final RowContainer f = escalator.getFooter();
        f.setCellRenderer(new FooterRenderer());
        f.insertRows(0, 1);

        b.removeRows(3, 2);
        // iterative transformations for testing.
        // step2();
        // step3();
        // step4();
        // step5();
        // step6();

        setWidth(TestGridState.DEFAULT_WIDTH);
        setHeight(TestGridState.DEFAULT_HEIGHT);
    }

    private void step2() {
        RowContainer b = escalator.getBody();
        b.insertRows(0, 5); // prepend five rows
        b.insertRows(b.getRowCount(), 5); // append five rows
    }

    private void step3() {
        ColumnConfiguration cConf = escalator.getColumnConfiguration();
        cConf.insertColumns(0, 1); // prepend one column
        cConf.insertColumns(cConf.getColumnCount(), 1); // append one column
    }

    private void step4() {
        final ColumnConfiguration cConf = escalator.getColumnConfiguration();
        cConf.removeColumns(0, 1);
        cConf.removeColumns(1, 1);
        cConf.removeColumns(cConf.getColumnCount() - 1, 1);
    }

    private void step5() {
        final RowContainer b = escalator.getBody();
        b.removeRows(0, 1);
        b.removeRows(b.getRowCount() - 1, 1);
    }

    private void step6() {
        RowContainer b = escalator.getBody();
        b.refreshRows(0, b.getRowCount());
    }

}
