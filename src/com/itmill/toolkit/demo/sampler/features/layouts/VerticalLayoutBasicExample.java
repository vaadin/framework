package com.itmill.toolkit.demo.sampler.features.layouts;

import java.util.Date;

import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.PopupDateField;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;

public class VerticalLayoutBasicExample extends VerticalLayout {

    @SuppressWarnings("deprecation")
    public VerticalLayoutBasicExample() {
        // Create a vertical layout.
        final VerticalLayout vertical = new VerticalLayout();
        
        // Populate the layout with components.
        vertical.addComponent(new TextField("Name"));
        vertical.addComponent(new PopupDateField("Registration date", new Date()));
        vertical.addComponent(new CheckBox("Registration confirmed"));
        
        // Add the layout to the containing layout.
        addComponent(vertical);
    }
}
