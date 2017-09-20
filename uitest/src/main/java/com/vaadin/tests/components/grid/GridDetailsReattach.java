package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridDetailsReattach extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout verticalMain = new VerticalLayout();

        final VerticalLayout layoutWithGrid = new VerticalLayout();

        Grid<String> grid = new Grid<>("Grid");
        grid.addColumn(String::toString).setCaption("Foo");
        grid.setHeight("150px");
        grid.setItems("Foo");
        grid.setDetailsGenerator(str -> new Label("AnyDetails"));
        grid.setDetailsVisible("Foo", true);
        layoutWithGrid.addComponent(grid);

        Button addCaptionToLayoutWithGridButton = new Button(
                "Add caption to 'layoutWithGrid' layout");
        addCaptionToLayoutWithGridButton.addClickListener(e -> layoutWithGrid
                .setCaption("Caption added to 'layoutWithGrid' layout"));
        layoutWithGrid.addComponent(addCaptionToLayoutWithGridButton);

        verticalMain.addComponent(layoutWithGrid);

        addComponent(verticalMain);

    }
}
