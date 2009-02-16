package com.itmill.toolkit.demo.sampler.features.layouts;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.VerticalLayout;

public class ExpandingComponentExample extends VerticalLayout {

    public ExpandingComponentExample() {
        setSpacing(true);

        { // Basic scenario: single expanded component
            HorizontalLayout layout = new HorizontalLayout();
            layout.setWidth("100%"); // make the layout grow with the window
            // size
            addComponent(layout);

            Button naturalButton = new Button("Natural");
            naturalButton
                    .setDescription("This button does not have an explicit size - instead, it's size depends on it's content - a.k.a <i>natural size.</i>");
            layout.addComponent(naturalButton);

            Button expandedButton = new Button("Expanded");
            expandedButton.setWidth("100%");
            expandedButton
                    .setDescription("This button is set to 100% and expanded, and will thus occupy the space left over by the other components.");
            layout.addComponent(expandedButton);
            layout.setExpandRatio(expandedButton, 1.0f);

            Button sizedButton = new Button("Explicit");
            sizedButton.setWidth("150px");
            sizedButton
                    .setDescription("This button is explicitly set to be 150 pixels wide.");
            layout.addComponent(sizedButton);
        }

        { // Ratio example
            HorizontalLayout layout = new HorizontalLayout();
            layout.setWidth("100%"); // make the layout grow with the window
            // size
            addComponent(layout);

            Button naturalButton = new Button("Natural");
            naturalButton
                    .setDescription("This button does not have an explicit size - instead, it's size depends on it's content - a.k.a <i>natural size.</i>");
            layout.addComponent(naturalButton);

            Button expandedButton1 = new Button("Ratio 1.0");
            expandedButton1.setWidth("100%");
            expandedButton1
                    .setDescription("This button is set to 100% and expanded with a ratio of 1.0, and will in this example occupy 1:2 of the leftover space.");
            layout.addComponent(expandedButton1);
            layout.setExpandRatio(expandedButton1, 1.0f);

            Button expandedButton2 = new Button("Ratio 2.0");
            expandedButton2.setWidth("100%");
            expandedButton2
                    .setDescription("This button is set to 100% and expanded with a ratio of 2.0, and will in this example occupy 2:1 of the leftover space.");
            layout.addComponent(expandedButton2);
            layout.setExpandRatio(expandedButton2, 2.0f);
        }

    }
}