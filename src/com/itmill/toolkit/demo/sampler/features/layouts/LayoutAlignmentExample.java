package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.itmill.toolkit.ui.Alignment;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.VerticalLayout;

public class LayoutAlignmentExample extends VerticalLayout {

    @SuppressWarnings("deprecation")
    public LayoutAlignmentExample() {
        // Create a grid layout
        final GridLayout grid = new GridLayout(1, 9);
        grid.setSpacing(true);

        // The style allows us to visualize the cell borders in this example.
        grid.addStyleName("gridexample");

        grid.setWidth("300px");
        grid.setHeight("500px");

        // Put a component in each cell with respective alignment.
        // We'll use different ways to set the alignment: constants, bitmasks,
        // and string-shorthand.

        // Here we use the shorthand constants to set the alignment:
        // Alignment.TOP_LEFT, Alignment.TOP_CENTER, Alignment.TOP_RIGHT
        // Alignment.MIDDLE_LEFT, Alignment.MIDDLE_CENTER,
        // Alignment.MIDDLE_RIGHT
        // Alignment.BOTTOM_LEFT, Alignment.BOTTOM_CENTER,
        // Alignment.BOTTOM_RIGHT

        Button topleft = new Button("Top Left");
        grid.addComponent(topleft);
        grid.setComponentAlignment(topleft, Alignment.TOP_LEFT);

        Button topcenter = new Button("Top Center");
        grid.addComponent(topcenter);
        grid.setComponentAlignment(topcenter, Alignment.TOP_CENTER);

        Button topright = new Button("Top Right");
        grid.addComponent(topright);
        grid.setComponentAlignment(topright, Alignment.TOP_RIGHT);

        // Here we use bit additions to set the alignment:
        // Bits.ALIGNMENT_LEFT, Bits.ALIGNMENT_RIGHT
        // Bits.ALIGNMENT_TOP, Bits.ALIGNMENT_BOTTOM
        // Bits.ALIGNMENT_VERTICAL_CENTER, Bits.ALIGNMENT_HORIZONTAL_CENTER

        Button middleleft = new Button("Middle Left");
        grid.addComponent(middleleft);
        grid.setComponentAlignment(middleleft, new Alignment(
                Bits.ALIGNMENT_VERTICAL_CENTER | Bits.ALIGNMENT_LEFT));

        Button middlecenter = new Button("Middle Center");
        grid.addComponent(middlecenter);
        grid.setComponentAlignment(middlecenter, new Alignment(
                Bits.ALIGNMENT_VERTICAL_CENTER
                        | Bits.ALIGNMENT_HORIZONTAL_CENTER));

        Button middleright = new Button("Middle Right");
        grid.addComponent(middleright);
        grid.setComponentAlignment(middleright, new Alignment(
                Bits.ALIGNMENT_VERTICAL_CENTER | Bits.ALIGNMENT_RIGHT));

        // Here we'll use the convenient string-shorthand:

        Button bottomleft = new Button("Bottom Left");
        grid.addComponent(bottomleft);
        grid.setComponentAlignment(bottomleft, "bottom left");

        Button bottomcenter = new Button("Bottom Center");
        grid.addComponent(bottomcenter);
        grid.setComponentAlignment(bottomcenter, "bottom center");

        Button bottomright = new Button("Bottom Right");
        grid.addComponent(bottomright);
        grid.setComponentAlignment(bottomright, "bottom right");

        // Add the layout to the containing layout.
        addComponent(grid);

        // Align the grid itself within its container layout.
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

    }
}
