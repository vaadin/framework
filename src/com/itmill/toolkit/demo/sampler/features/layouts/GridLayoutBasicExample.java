package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.ui.Alignment;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.VerticalLayout;

public class GridLayoutBasicExample extends VerticalLayout {

    public GridLayoutBasicExample() {
        // Create a grid layout
        final GridLayout grid = new GridLayout(3, 3);

        // The style allows us to visualize the cell borders in this example.
        grid.addStyleName("example-alignment");

        grid.setWidth(400, Sizeable.UNITS_PIXELS);
        grid.setHeight(400, Sizeable.UNITS_PIXELS);

        // First we insert four components that occupy one cell each
        Button topleft = new Button("Top Left");
        grid.addComponent(topleft, 0, 0);
        grid.setComponentAlignment(topleft, Alignment.MIDDLE_CENTER);

        Button topcenter = new Button("Top Center");
        grid.addComponent(topcenter, 1, 0);
        grid.setComponentAlignment(topcenter, Alignment.MIDDLE_CENTER);

        Button bottomleft = new Button("Bottom Left");
        grid.addComponent(bottomleft, 0, 2);
        grid.setComponentAlignment(bottomleft, Alignment.MIDDLE_CENTER);

        Button bottomcenter = new Button("Bottom Center");
        grid.addComponent(bottomcenter, 1, 2);
        grid.setComponentAlignment(bottomcenter, Alignment.MIDDLE_CENTER);

        // Insert a component that occupies all the rightmost cells
        Button topright = new Button("Extra height");
        grid.addComponent(topright, 2, 0, 2, 2);
        grid.setComponentAlignment(topright, Alignment.MIDDLE_CENTER);

        // Insert a component that occupies two cells in horizontal direction
        Button middleleft = new Button("This is a wide cell in GridLayout");
        grid.addComponent(middleleft, 0, 1, 1, 1);
        grid.setComponentAlignment(middleleft, Alignment.MIDDLE_CENTER);

        // Add the layout to the containing layout.
        addComponent(grid);

        // Align the grid itself within its container layout.
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
    }
}
