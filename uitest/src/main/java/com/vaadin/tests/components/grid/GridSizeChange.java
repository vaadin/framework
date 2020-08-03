package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class GridSizeChange extends AbstractTestUI {

    private Grid<Integer> grid;
    private List<Integer> data;
    private ListDataProvider<Integer> dataProvider;
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        grid = new Grid<>();
        data = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            data.add(i);
            ++counter;
        }

        dataProvider = DataProvider.ofCollection(data);
        grid.setDataProvider(dataProvider);

        // create column and fill rows
        grid.addColumn(item -> "row_" + item).setCaption("Item");

        // set height mode and height
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(8);
        grid.setWidth(100, Unit.PIXELS);

        // create a tabsheet with one tab and place grid inside
        VerticalLayout tab = new VerticalLayout();
        tab.setSpacing(false);
        tab.setMargin(false);
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthUndefined();
        tabSheet.addTab(tab, "Tab");
        tab.addComponent(grid);

        GridLayout layout = new GridLayout(3, 2);
        layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);

        layout.addComponent(new Button("Reduce height", e -> {
            double newHeight = grid.getHeightByRows() - 1;
            grid.setHeightByRows(newHeight);
        }));

        layout.addComponent(new Button("Remove row", e -> {
            removeRow();
            dataProvider.refreshAll();
        }));

        layout.addComponent(new Button("Reduce width", e -> {
            grid.setWidth(grid.getWidth() - 30, Unit.PIXELS);
        }));

        layout.addComponent(new Button("Increase height", e -> {
            double newHeight = grid.getHeightByRows() + 1;
            grid.setHeightByRows(newHeight);
        }));

        layout.addComponent(new Button("Add row", e -> {
            addRow();
            dataProvider.refreshAll();
        }));

        layout.addComponent(new Button("Increase width", e -> {
            grid.setWidth(grid.getWidth() + 30, Unit.PIXELS);
        }));

        addComponent(tabSheet);
        addComponent(layout);

        getLayout().setSpacing(true);
    }

    private void removeRow() {
        data.remove(0);
        dataProvider.refreshAll();
    }

    private void addRow() {
        ++counter;
        data.add(counter);
        dataProvider.refreshAll();
    }

    @Override
    protected String getTestDescription() {
        return "Changing Grid size should resize the TabSheet accordingly "
                + "even if scrollbar(s) appear or disappear.";
    }
}
