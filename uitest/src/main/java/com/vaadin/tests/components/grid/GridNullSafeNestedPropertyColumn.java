package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.TextRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridNullSafeNestedPropertyColumn extends AbstractTestUI {

    private List<Person> personList = new ArrayList<>();
    private ListDataProvider<Person> listDataProvider;

    @Override
    protected void setup(VaadinRequest request) {
        setErrorHandler(null);

        Grid<Person> grid = new Grid<>(Person.class);
        grid.setSizeFull();

        personList.add(new Person("person", "with", "an address", 0,
                Sex.UNKNOWN, new Address("street", 0, "", Country.FINLAND)));
        listDataProvider = new ListDataProvider<>(personList);
        grid.setDataProvider(listDataProvider);

        Button addPersonButton = new Button("add person with a null address",
                event -> {
                    Address address = null;
                    Person person = new Person("person", "without", "address",
                            42, Sex.UNKNOWN, address);
                    personList.add(person);
                    listDataProvider.refreshAll();
                });
        addPersonButton.setId("add");

        Button addSafeColumnButton = new Button(
                "add 'address.streetAddress' as a null-safe column", event -> {
                    grid.addColumn("address.streetAddress", new TextRenderer(),
                            Grid.Column.NestedNullBehavior.ALLOW_NULLS);
                });
        addSafeColumnButton.setId("safe");

        Button addUnsafeColumnButton = new Button(
                "add 'address.streetAddress' column without nested null safety",
                event -> {
                    grid.addColumn("address.streetAddress");
                });
        addUnsafeColumnButton.setId("unsafe");

        addComponents(grid, addPersonButton, addSafeColumnButton,
                addUnsafeColumnButton);
    }
}
