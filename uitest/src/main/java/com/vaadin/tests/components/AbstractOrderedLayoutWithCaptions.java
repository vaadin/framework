package com.vaadin.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

/**
 * Test to see if AbstractOrderedLayout displays captions correctly with
 * expanding ratios.
 *
 * @author Vaadin Ltd
 */
public class AbstractOrderedLayoutWithCaptions extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        TextField textField = new TextField("Input Text:");
        Label label1 = new Label("LABEL 1");
        Label label2 = new Label("LABEL 2");
        label1.setWidth("100%"); // Only to make test backwards compatible
        label2.setWidth("100%"); // Only to make test backwards compatible

        layout.addComponent(textField);

        layout.addComponent(label1);
        layout.setExpandRatio(label1, 1.0f);

        layout.addComponent(label2);

        Panel containingPanel = new Panel(layout);
        containingPanel.setHeight("200px");
        addComponent(containingPanel);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test to see if AbstractOrderedLayout calculates captions correctly.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13741;
    }
}
