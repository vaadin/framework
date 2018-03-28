package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.grid.basics.DataObject;
import com.vaadin.tests.components.grid.basics.GridBasics;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridDragSelectionWhileScrolled extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Layout layout = new VerticalLayout();

        HorizontalLayout spacer = new HorizontalLayout();
        spacer.setHeight("1000px");
        layout.addComponent(spacer);

        Grid<DataObject> grid = new Grid<>();
        grid.setItems(DataObject.generateObjects());
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 0)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[0]);
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 1)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[1]);
        grid.addColumn(dataObj -> "(" + dataObj.getRowNumber() + ", 2)")
                .setCaption(GridBasics.COLUMN_CAPTIONS[2]);

        grid.addColumn(DataObject::getRowNumber, new NumberRenderer())
                .setCaption(GridBasics.COLUMN_CAPTIONS[3]);
        grid.addColumn(DataObject::getDate, new DateRenderer())
                .setCaption(GridBasics.COLUMN_CAPTIONS[4]);
        grid.addColumn(DataObject::getHtmlString, new HtmlRenderer())
                .setCaption(GridBasics.COLUMN_CAPTIONS[5]);
        grid.addColumn(DataObject::getBigRandom, new NumberRenderer())
                .setCaption(GridBasics.COLUMN_CAPTIONS[6]);
        grid.setSelectionMode(SelectionMode.MULTI);

        layout.addComponent(grid);

        addComponent(layout);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17895;
    }

    @Override
    protected String getTestDescription() {
        return "Drag selecting rows in Grid malfunctions if page is scrolled";
    }
}
