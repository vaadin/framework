package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class LayoutSpacingExample extends VerticalLayout {

    public LayoutSpacingExample() {
        // Create a horizontal layout.
        final HorizontalLayout horizontal = new HorizontalLayout();
        
        // Add a style to allow customization of the layout.
        horizontal.addStyleName("spacingexample");
        
        // Populate the layout with components.
        horizontal.addComponent(new Button("Component 1"));
        horizontal.addComponent(new Button("Component 2"));
        horizontal.addComponent(new Button("Component 3"));
        
        // Add the layout to the containing layout.
        addComponent(horizontal);

        // CheckBox for toggling spacing on and off
        final CheckBox spacing = new CheckBox("Click here to enable/disable spacing");
        spacing.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                horizontal.setSpacing(((Boolean)spacing.getValue()).booleanValue());
            }
        });
        spacing.setImmediate(true);
        addComponent(spacing);
        setSpacing(true);
    }
}
