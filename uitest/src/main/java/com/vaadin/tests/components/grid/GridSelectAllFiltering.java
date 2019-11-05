package com.vaadin.tests.components.grid;

import java.util.Iterator;
import java.util.Set;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridSelectAllFiltering extends SimpleGridUI {
    private String filterText = "Johannes";

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setHeightByRows(3);
        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getAge, new NumberRenderer());

        ListDataProvider<Person> dataProvider = DataProvider
                .ofCollection(createPersons());
        dataProvider.setFilter(person -> {
            if (person.getFirstName().contains(filterText)) {
                return false;
            }
            return true;
        });
        grid.setDataProvider(dataProvider);

        Button toggleButton = new Button("Toggle filter", e -> {
            if ("Johannes".equals(filterText)) {
                filterText = "Galileo";
            } else {
                filterText = "Johannes";
            }
            dataProvider.refreshAll();
        });
        toggleButton.setId("toggle");

        Button checkButton = new Button("Check selection", e -> {
            Set<Person> selected = grid.getSelectedItems();
            Iterator<Person> i = selected.iterator();
            log("selected " + selected.size()
                    + (i.hasNext() ? ": " + i.next().getFirstName() : "")
                    + (i.hasNext() ? ", " + i.next().getFirstName() : "")
                    + (i.hasNext() ? ", " + i.next().getFirstName() : "")
                    + (i.hasNext() ? "... " : ""));
        });
        checkButton.setId("check");

        addComponents(grid, toggleButton, checkButton);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11479;
    }

    @Override
    protected String getTestDescription() {
        return "Selecting all does not select items that have been "
                + "filtered out, they should not be shown selected "
                + "after the filter changes.";
    }
}
