package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridColumnFrozenColumn extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label(
                "Frozen columns can be reordered with unhidden columns with: "
                        + com.vaadin.shared.Version.getFullVersion()));
        Label issueLabel = new Label(
                "Demonstrate problem in <a href=\"https://github.com/vaadin/framework/issues/10546\">grid column frozen column reorder issue with SelectionMode.MULTI</a>");
        issueLabel.setContentMode(ContentMode.HTML);
        layout.addComponent(issueLabel);

        // Create new Grid
        Grid<HashMap<String, String>> grid = new Grid<>(
                "My test grid to reorder columns");

        // Fill the grid with data to sort
        List<HashMap<String, String>> rows = new ArrayList<>();
        String FIRST = "Frozen Column (Should not be reordered)";
        String LAST = "Last Name Column";

        // Grid for Vaadin 8 without bean class from
        // https://vaadin.com/forum/#!/thread/16038356/16816582
        for (int i = 0; i < 20; i++) {
            HashMap<String, String> fakeBean = new HashMap<>();
            fakeBean.put(FIRST, "first" + i);
            fakeBean.put(LAST, "last" + i);
            rows.add(fakeBean);
        }

        grid.setItems(rows);

        // Add the columns based on the first row
        HashMap<String, String> s = rows.get(0);
        for (Map.Entry<String, String> entry : s.entrySet()) {
            grid.addColumn(h -> h.get(entry.getKey()))
                    .setCaption(entry.getKey()).setId(entry.getKey());
        }
        grid.getColumn(LAST).setHidable(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        // without the selector column the issue cannot be observed
        // grid.setSelectionMode(SelectionMode.NONE);
        grid.setFrozenColumnCount(1);
        grid.setColumnReorderingAllowed(true);
        grid.setSizeFull();

        layout.addComponent(grid);
        layout.setMargin(true);
        layout.setSpacing(true);

        addComponent(layout);
    }
}
