package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridReorderColumns extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);

        Grid<String> emptyGrid = createGrid(false);
        emptyGrid.setId("emptyGrid");

        Grid<String> contentGrid = createGrid(true);
        contentGrid.setId("contentGrid");

        addComponents(emptyGrid, contentGrid);
    }

    private Grid<String> createGrid(boolean includeItem) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(t -> t + "1").setCaption("caption1");
        grid.addColumn(t -> t + "2").setCaption("caption2");
        grid.addColumn(t -> t + "3").setCaption("caption3");
        grid.addColumn(t -> t + "4").setCaption("caption4");
        grid.setColumnReorderingAllowed(true);
        grid.setHeight("100px");

        List<String> items = new ArrayList<>();
        if (includeItem) {
            items.add("content");
        }
        ListDataProvider<String> dataProvider = DataProvider
                .ofCollection(items);
        grid.setDataProvider(dataProvider);
        return grid;
    }

    @Override
    protected Integer getTicketNumber() {
        return 10699;
    }

    @Override
    protected String getTestDescription() {
        return "Column reordering without changing initial focus should complete "
                + "without errors on all browsers even if the Grid is empty.";
    }
}
