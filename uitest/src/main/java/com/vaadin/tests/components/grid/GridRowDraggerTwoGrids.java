package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.DropIndexCalculator;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.components.grid.SourceDataProviderUpdater;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridRowDraggerTwoGrids extends AbstractGridDnD {

    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        // Drag source Grid
        Grid<Person> left = createGridAndFillWithData(50);

        // Drop target Grid
        Grid<Person> right = createGridAndFillWithData(0);

        GridRowDragger<Person> gridDragger = new GridRowDragger<>(left, right);

        CheckBox addItemsToEnd = new CheckBox("Add Items To End", false);
        addItemsToEnd.addValueChangeListener(
                event -> gridDragger.setDropIndexCalculator(
                        event.getValue() ? DropIndexCalculator.alwaysDropToEnd()
                                : null));
        CheckBox removeItemsFromSource = new CheckBox(
                "Remove items from source grid", true);
        removeItemsFromSource.addValueChangeListener(event -> gridDragger
                .setSourceDataProviderUpdater(event.getValue() ? null
                        : SourceDataProviderUpdater.NOOP));

        controls.addComponents(addItemsToEnd, removeItemsFromSource);

        initializeTestFor(gridDragger);
    }

}
