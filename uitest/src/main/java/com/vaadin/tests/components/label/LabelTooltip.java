package com.vaadin.tests.components.label;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

public class LabelTooltip extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createLayout());
    }

    private static Layout createLayout() {
        GridLayout layout = new GridLayout(2, 1);
        layout.setCaption("Tooltips");
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("400px");
        layout.setColumnExpandRatio(0, 1);

        Label defaultLabel = new Label("Default");
        defaultLabel.setWidth("100%");
        layout.addComponent(defaultLabel);
        Label tooltip = new Label("Hover over me to see the tooltip");
        tooltip.setDescription("Default tooltip content");
        layout.addComponent(tooltip);

        Label defaultWithError = new Label("Default /w error");
        defaultWithError.setWidth("100%");
        layout.addComponent(defaultWithError);
        tooltip = new Label("Hover over me to see the tooltip");
        tooltip.setDescription("Default tooltip content");
        tooltip.setComponentError(new UserError(
                "Error inside tooltip together with the regular tooltip message."));
        layout.addComponent(tooltip);

        return layout;
    }

    @Override
    protected String getTestDescription() {
        return "The lower label should have an error indicator. The upper should not.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6911;
    }

}
