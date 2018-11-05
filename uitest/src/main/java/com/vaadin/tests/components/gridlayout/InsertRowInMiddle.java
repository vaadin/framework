package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class InsertRowInMiddle extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final GridLayout layout = new GridLayout(1, 2);
        layout.addComponent(new Label("some row"), 0, 0);
        Button newRowButton = new Button("Insert Row");
        newRowButton.addClickListener(event -> {
            layout.insertRow(1);
            layout.addComponent(new Label("some new row"), 0, 1);
        });
        layout.addComponent(newRowButton, 0, 1);
        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "A new row added to the middle of a GridLayout should appear without any exception being thrown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10097);
    }

}
