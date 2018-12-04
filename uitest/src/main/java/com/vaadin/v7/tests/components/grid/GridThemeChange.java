package com.vaadin.v7.tests.components.grid;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.event.SelectionEvent;
import com.vaadin.v7.event.SelectionEvent.SelectionListener;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionMode;

public class GridThemeChange extends AbstractReindeerTestUI {
    private final List<String> themes = Arrays.asList("valo", "reindeer",
            "runo", "chameleon", "base");

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.setSelectionMode(SelectionMode.SINGLE);

        grid.addColumn("Theme");
        for (String theme : themes) {
            Object itemId = grid.addRow(theme);
            if (theme.equals(getTheme())) {
                grid.select(itemId);
            }
        }

        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                Object selectedItemId = grid.getSelectedRow();
                Object theme = grid.getContainerDataSource()
                        .getItem(selectedItemId).getItemProperty("Theme")
                        .getValue();
                setTheme(String.valueOf(theme));
            }
        });

        addComponent(grid);

    }
}
