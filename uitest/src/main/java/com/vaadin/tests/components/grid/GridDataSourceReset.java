package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class GridDataSourceReset extends AbstractTestUI {

    private List<ComplexPerson> persons;
    private Grid<ComplexPerson> grid;

    @Override
    protected void setup(VaadinRequest request) {
        persons = createPersons(10, new Random(1));

        grid = new Grid<>();
        ListDataProvider<ComplexPerson> provider = DataProvider
                .ofCollection(persons);
        grid.setDataProvider(provider);
        grid.getSelectionModel().select(persons.get(0));
        addComponent(new Button("Remove first", event -> {
            persons.remove(0);

            provider.refreshAll();
            grid.getSelectionModel().select(persons.get(0));
        }));

        addComponent(grid);
    }

    public static List<ComplexPerson> createPersons(int count, Random r) {
        List<ComplexPerson> c = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            c.add(ComplexPerson.create(r));
        }
        return c;
    }
}
