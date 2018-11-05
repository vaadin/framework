package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridRowDragger;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridRowDraggerOneGrid extends AbstractGridDnD {

    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        Grid<Person> grid = createGridAndFillWithData(50);

        GridRowDragger<Person> gridDragger = new GridRowDragger<>(grid);

        initializeTestFor(gridDragger);
    }

}
