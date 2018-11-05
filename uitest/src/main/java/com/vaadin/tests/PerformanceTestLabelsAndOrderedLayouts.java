package com.vaadin.tests;

import java.util.Date;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class PerformanceTestLabelsAndOrderedLayouts extends CustomComponent {
    private final AbstractOrderedLayout main;

    private final AbstractOrderedLayout testContainer = new VerticalLayout();

    private Date startTime;

    private final Label result;

    private static final String DESCRIPTION = "Simple test that renders n labels into ordered layout.";

    private static final int INITIAL_COMPONENTS = 1000;

    public PerformanceTestLabelsAndOrderedLayouts() {
        main = new VerticalLayout();
        setCompositionRoot(main);
        addInfo();

        result = new Label();
        main.addComponent(result);

        main.addComponent(
                new Button("click when rendered", event -> endTest()));

        main.addComponent(
                new Button("Click for layout repaint (cached components)",
                        event -> testContainer.markAsDirty()));

        for (int i = 0; i < INITIAL_COMPONENTS; i++) {
            Label l = new Label("foo" + i);
            testContainer.addComponent(l);
        }

        main.addComponent(testContainer);
        startTest();
    }

    public void startTest() {
        startTime = new Date();
    }

    public void endTest() {
        final long millis = (new Date()).getTime() - startTime.getTime();
        final Float f = new Float(millis / 1000.0);
        result.setValue("Test completed in " + f + " seconds");
    }

    private void addInfo() {
        main.addComponent(new Label(DESCRIPTION, ContentMode.HTML));
    }

}
