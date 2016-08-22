package com.vaadin.tests.components.grid.basics;

import java.util.Date;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Grid;

public class GridBasics extends AbstractTestUIWithLog {

    private Grid<DataObject> grid;

    @Override
    protected void setup(VaadinRequest request) {
        List<DataObject> data = DataObject.generateObjects();

        // Create grid
        grid = new Grid<>();
        grid.setItems(data);

        grid.addColumn("Row Number", Integer.class, DataObject::getRowNumber);
        grid.addColumn("Date", Date.class, DataObject::getDate);
        grid.addColumn("HTML String", String.class, DataObject::getHtmlString);
        grid.addColumn("Big Random", Integer.class, DataObject::getBigRandom);
        grid.addColumn("Small Random", Integer.class,
                DataObject::getSmallRandom);

        addComponent(grid);
    }

}
