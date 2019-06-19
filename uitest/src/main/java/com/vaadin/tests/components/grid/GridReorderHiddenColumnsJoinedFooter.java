package com.vaadin.tests.components.grid;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;

import java.util.ArrayList;
import java.util.List;

public class GridReorderHiddenColumnsJoinedFooter extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);

        Grid<String> grid = createGrid();

        addComponents(grid);
    }

    private Grid<String> createGrid() {
        Grid<String> grid = new Grid<>();
        grid.addColumn(t -> t + "1").setId("col1").setCaption("caption1")
                .setHidable(true);
        grid.addColumn(t -> t + "2").setId("col2").setCaption("caption2")
                .setHidable(true);
        grid.addColumn(t -> t + "3").setId("col3").setCaption("caption3")
                .setHidable(true);
        grid.addColumn(t -> t + "4").setId("col4").setCaption("caption4")
                .setHidable(true);
        grid.addColumn(t -> t + "5").setId("col5").setCaption("caption5")
                .setHidable(true);
        grid.addColumn(t -> t + "6").setId("col6").setCaption("caption6")
                .setHidable(true);
        grid.addColumn(t -> t + "7").setId("col7").setCaption("caption7")
                .setHidable(true);
        grid.addColumn(t -> t + "8").setId("col8").setCaption("caption8")
                .setHidable(true);

        grid.setColumnReorderingAllowed(true);
        grid.setSizeFull();

        // join FooterRow together
        FooterRow footerRow = grid.appendFooterRow();
        Grid.Column[] columns = grid.getColumns().toArray(new Grid.Column[0]);
        FooterCell footerCell = footerRow.join(columns);
        footerCell.setText("test");

        // hide columns
        grid.getColumn("col2").setHidden(true);
        grid.getColumn("col4").setHidden(true);
        grid.getColumn("col5").setHidden(true);
        grid.getColumn("col6").setHidden(true);

        List<String> items = new ArrayList<>();
        items.add("content");
        ListDataProvider<String> dataProvider = DataProvider
                .ofCollection(items);
        grid.setDataProvider(dataProvider);
        return grid;
    }

    @Override
    protected Integer getTicketNumber() {
        return 11478;
    }

    @Override
    protected String getTestDescription() {
        return "Column reordering with hidden columns and joined footer should not "
                + "be blocked by hidden columns.";
    }
}
