package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;

public class GridRecalculateColumnWidthNewItem extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<String> testItems = new ArrayList<>();
        testItems.add("short1");
        testItems.add("short2");

        Grid<String> grid = new Grid<>();
        grid.addColumn(String::toString).setCaption("Name");
        grid.addColumn(item -> "col2").setCaption("Col 2");
        grid.addColumn(item -> "col3").setCaption("Col 3");
        grid.setDataProvider(new ListDataProvider<>(testItems));

        final CheckBox recalculateCheckBox = new CheckBox(
                "Recalculate column widths", true);

        Button addButton = new Button("add row", e -> {
            testItems.add(
                    "Wiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiide");
            grid.getDataProvider().refreshAll();
            if (recalculateCheckBox.getValue()) {
                grid.recalculateColumnWidths();
            }
        });
        addButton.setId("add");

        Button removeButton = new Button("remove row", e -> {
            if (testItems.size() > 0) {
                testItems.remove(testItems.size() - 1);
            }
            grid.getDataProvider().refreshAll();
            if (recalculateCheckBox.getValue()) {
                grid.recalculateColumnWidths();
            }
        });
        removeButton.setId("remove");

        addComponents(grid, addButton, removeButton, recalculateCheckBox);
    }

    @Override
    protected String getTestDescription() {
        return "Adding or removing a row with wider contents should update "
                + "column widths if requested but not otherwise.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9996;
    }
}
