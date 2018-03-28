package com.vaadin.tests.components.grid;

import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.grid.basics.DataObject;
import com.vaadin.tests.components.grid.basics.GridBasics;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;

@Widgetset(TestingWidgetSet.NAME)
public class GridCustomSelectionModel extends AbstractTestUI {

    public static class MySelectionModel
            extends MultiSelectionModelImpl<DataObject> {

    }

    private Grid<DataObject> grid;
    private List<DataObject> data;

    @Override
    protected void setup(VaadinRequest request) {
        data = DataObject.generateObjects();

        // Create grid
        grid = new Grid<DataObject>() {
            {
                setSelectionModel(new MySelectionModel());
            }
        };
        grid.setItems(data);
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 0)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[0]);
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 1)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[1]);
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 2)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[2]);
        addComponent(grid);
    }

}
