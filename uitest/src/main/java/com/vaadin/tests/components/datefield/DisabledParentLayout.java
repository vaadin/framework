package com.vaadin.tests.components.datefield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Vaadin Ltd
 */
public class DisabledParentLayout extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final VerticalLayout pane = new VerticalLayout();
        AbstractLocalDateField dateField = new TestDateField();
        pane.addComponent(dateField);

        Button button = new Button("Test");
        button.addClickListener(event -> pane.setEnabled(!pane.isEnabled()));

        addComponents(pane, button);
    }

    @Override
    protected String getTestDescription() {
        return "Data field should be functional after enabling disabled parent.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4773;
    }

}
