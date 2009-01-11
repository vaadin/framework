package com.itmill.toolkit.demo.sampler.features.layouts;

import java.util.Date;

import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.itmill.toolkit.ui.AbstractComponent;
import com.itmill.toolkit.ui.Alignment;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.PopupDateField;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;

public class LayoutAlignmentExample extends VerticalLayout {

    @SuppressWarnings("deprecation")
    public LayoutAlignmentExample() {
        // Create a grid layout
        final GridLayout grid = new GridLayout(3, 3);

        // The style allows us to visualize the cell borders in this example.
        grid.addStyleName("example-alignment");

        grid.setWidth(400, Sizeable.UNITS_PIXELS);
        grid.setHeight(400, Sizeable.UNITS_PIXELS);
        
        ///////////////////////////////////////////////////////////
        // Put a component in each cell with respective alignment.
        // (We use different ways to set the alignment.)

        // Here we use the shorthand constants to set the alignment:
        //   Alignment.TOP_LEFT,    Alignment.TOP_CENTER,    Alignment.TOP_RIGHT
        //   Alignment.MIDDLE_LEFT, Alignment.MIDDLE_CENTER, Alignment.MIDDLE_RIGHT
        //   Alignment.BOTTOM_LEFT, Alignment.BOTTOM_CENTER, Alignment.BOTTOM_RIGHT

        Button topleft = new Button("Top Left");
        grid.addComponent(topleft, 0, 0);
        grid.setComponentAlignment(topleft, Alignment.TOP_LEFT);
        
        Button topcenter = new Button("Top Center");
        grid.addComponent(topcenter, 1, 0);
        grid.setComponentAlignment(topcenter, Alignment.TOP_CENTER);

        Button topright = new Button("Top Right");
        grid.addComponent(topright, 2, 0);
        grid.setComponentAlignment(topright, Alignment.TOP_RIGHT);
        
        // Here we use bit additions to set the alignment:
        //   Bits.ALIGNMENT_LEFT, Bits.ALIGNMENT_RIGHT
        //   Bits.ALIGNMENT_TOP, Bits.ALIGNMENT_BOTTOM
        //   Bits.ALIGNMENT_VERTICAL_CENTER, Bits.ALIGNMENT_HORIZONTAL_CENTER
        
        Button middleleft = new Button("Middle Left");
        grid.addComponent(middleleft, 0, 1);
        grid.setComponentAlignment(middleleft, new Alignment(Bits.ALIGNMENT_VERTICAL_CENTER | Bits.ALIGNMENT_LEFT));
        
        Button middlecenter = new Button("Middle Center");
        grid.addComponent(middlecenter, 1, 1);
        grid.setComponentAlignment(middlecenter, new Alignment(Bits.ALIGNMENT_VERTICAL_CENTER | Bits.ALIGNMENT_HORIZONTAL_CENTER));

        Button middleright = new Button("Middle Right");
        grid.addComponent(middleright, 2, 1);
        grid.setComponentAlignment(middleright, new Alignment(Bits.ALIGNMENT_VERTICAL_CENTER | Bits.ALIGNMENT_RIGHT));

        // Here we again use constants:
        
        Button bottomleft = new Button("Bottom Left");
        grid.addComponent(bottomleft, 0, 2);
        grid.setComponentAlignment(bottomleft, Alignment.BOTTOM_LEFT);
        
        Button bottomcenter = new Button("Bottom Center");
        grid.addComponent(bottomcenter, 1, 2);
        grid.setComponentAlignment(bottomcenter, Alignment.BOTTOM_CENTER);

        Button bottomright = new Button("Bottom Right");
        grid.addComponent(bottomright, 2, 2);
        grid.setComponentAlignment(bottomright, Alignment.BOTTOM_RIGHT);

        // Add the layout to the containing layout.
        addComponent(grid);

        // Align the grid itself within its container layout.
        setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

    }
}
