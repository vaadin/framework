package com.itmill.toolkit.automatedtests.featurebrowser;

import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;

/**
 * Accordion is a derivative of TabSheet, a vertical tabbed layout that places
 * the tab contents between the vertical tabs.
 */
@SuppressWarnings("serial")
public class AccordionExample extends CustomComponent {

    public AccordionExample() {
        // Create a new accordion
        final Accordion accordion = new Accordion();
        setCompositionRoot(accordion);

        // Add a few tabs to the accordion.
        for (int i = 0; i < 5; i++) {
            // Create a root component for a accordion tab
            VerticalLayout layout = new VerticalLayout();
            accordion.addComponent(layout);

            // The accordion tab label is taken from the caption of the root
            // component. Notice that layouts can have a caption too.
            layout.setCaption("Tab " + (i + 1));

            // Add some components in each accordion tab
            Label label = new Label("These are the contents of Tab " + (i + 1)
                    + ".");
            layout.addComponent(label);

            TextField textfield = new TextField("Some text field");
            layout.addComponent(textfield);
        }
    }
}
