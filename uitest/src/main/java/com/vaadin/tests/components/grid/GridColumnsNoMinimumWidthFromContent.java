package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

public class GridColumnsNoMinimumWidthFromContent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Random random = new Random();

        List<DummyGridRow> gridRows = new ArrayList<DummyGridRow>();
        gridRows.add(new DummyGridRow(random));

        Grid<DummyGridRow> grid = new Grid<DummyGridRow>();
        for (int i = 0; i < 20; i++) {
            grid.addColumn(DummyGridRow::getValue)
                    .setCaption("[" + i + "] Quite dummy column")
                    .setMinimumWidthFromContent(false);
        }

        grid.setItems(gridRows);
        FooterRow defaultFooter = grid.appendFooterRow();
        grid.getColumns().forEach(column -> defaultFooter.getCell(column)
                .setText(grid.getDefaultHeaderRow().getCell(column).getText()));
        grid.setFooterVisible(true);
        grid.setHeightByRows(gridRows.size());
        grid.setWidthFull();

        getLayout().addComponent(grid);
    }

    class DummyGridRow {
        private Random random = null;

        public DummyGridRow(Random random) {
            this.random = random;
        }

        public int getValue() {
            return random.nextInt(1000000000);
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 12139;
    }

    @Override
    protected String getTestDescription() {
        return "Loading the UI should not get stuck in an eternal loop "
                + "and the columns should be narrow with ellipsis "
                + "until the page is resized small enough that "
                + "the resize handles alone force a scrollbar. "
                + "No overflowing of header cells should occur "
                + "when resized very near to the cutoff point "
                + "between no scrollbar and a scrollbar.";
    }
}
