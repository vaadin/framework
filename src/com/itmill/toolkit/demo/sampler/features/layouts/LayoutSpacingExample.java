package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class LayoutSpacingExample extends VerticalLayout {

    public LayoutSpacingExample() {
        // Create a grid layout.
        final GridLayout grid = new GridLayout(3, 3);

        // Add the layout to the containing layout.
        addComponent(grid);

        // Add a style to allow customization of the layout.
        grid.addStyleName("spacingexample");

        // Populate the layout with components.
        for (int i = 0; i < 9; i++) {
            grid.addComponent(new Button("Component " + (i + 1)));
        }

        // CheckBox for toggling spacing on and off
        final CheckBox spacing = new CheckBox("Spacing enabled");
        spacing.setImmediate(true);
        spacing.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                grid.setSpacing(spacing.booleanValue());
            }
        });
        addComponent(spacing);

        setSpacing(true); // enable spacing for the example itself
    }
}
