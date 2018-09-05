package com.vaadin.tests.components.button;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ButtonClick extends AbstractReindeerTestUI {

    public static final String SUCCESS_TEXT = "Click received successfully!";
    public static final String WRONG_BUTTON_TEXT = "Wrong button clicked.";

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout rootLayout = new VerticalLayout();
        final Label statusLabel = new Label("Test initialized");
        rootLayout.addComponent(new Button("Click here",
                event -> statusLabel.setValue(SUCCESS_TEXT)));
        Button visitLocation = new Button("Drag here",
                event -> statusLabel.setValue(WRONG_BUTTON_TEXT));
        rootLayout.addComponent(statusLabel);
        rootLayout.addComponent(visitLocation);
        rootLayout.setComponentAlignment(visitLocation, Alignment.BOTTOM_RIGHT);
        rootLayout.setSizeFull();
        rootLayout.setMargin(true);
        setContent(rootLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Verify button click logic";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13550;
    }

}
