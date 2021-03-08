package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridDetailsUpdateItems extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createExamplleLayout());
    }

    private VerticalLayout createExamplleLayout() {
        Collection<String> firstCollection = Arrays.asList("Hello", ",",
                "world!");
        Collection<String> secondCollection = Arrays.asList("My", "name", "is",
                "Sarah");
        Collection<String> thirdCollection = Arrays.asList("red", "blue");
        Collection<String> fourthCollection = Arrays.asList("spring", "summer",
                "autumn", "winter");

        VerticalLayout mainLayout = new VerticalLayout();
        Grid<Collection<String>> grid = new Grid<>();
        grid.setDetailsGenerator(collection -> {
            VerticalLayout detailLayout = new VerticalLayout();
            collection.forEach(
                    item -> detailLayout.addComponent(new Label(item)));
            return detailLayout;
        });
        ValueProvider<Collection<String>, String> valueProvider = collection -> String
                .join(" ", collection);
        grid.addColumn(valueProvider).setCaption("Header");

        List<Collection<String>> itemsInitial = Arrays.asList(firstCollection,
                secondCollection, thirdCollection, fourthCollection);
        grid.setItems(itemsInitial);
        for (Collection<String> tmp : itemsInitial) {
            grid.setDetailsVisible(tmp, true);
        }
        mainLayout.addComponent(grid);

        Button clickButton = new Button("Change items", event -> {
            List<Collection<String>> itemsOverwrite = Arrays
                    .asList(secondCollection, fourthCollection);
            grid.setItems(itemsOverwrite);
            for (Collection<String> tmp : itemsOverwrite) {
                grid.setDetailsVisible(tmp, true);
            }
        });
        mainLayout.addComponent(clickButton);

        return mainLayout;
    }

    @Override
    protected Integer getTicketNumber() {
        return 12211;
    }

    @Override
    protected String getTestDescription() {
        return "Details should update and not break the positioning "
                + "when the item set is changed.";
    }
}
