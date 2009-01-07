package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class HorizontalLayoutBasicExample extends VerticalLayout {

    public HorizontalLayoutBasicExample() {
        // Create a horizontal layout.
        final HorizontalLayout horizontal = new HorizontalLayout();
        
        // Populate the layout with components.
        horizontal.addComponent(new TextField("Name"));
        horizontal.addComponent(new TextField("Street address"));
        horizontal.addComponent(new TextField("Postal code"));
        
        // Add the layout to the containing layout.
        addComponent(horizontal);
    }
}
