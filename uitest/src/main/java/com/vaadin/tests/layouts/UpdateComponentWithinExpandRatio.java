package com.vaadin.tests.layouts;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ProgressBar;

public class UpdateComponentWithinExpandRatio extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ProgressBar progress = new ProgressBar();
        progress.setWidth(100, Unit.PERCENTAGE);
        Button button = new Button("Progress", e -> {
            float value = progress.getValue();
            value = (value >= 1) ? 0 : value + 0.1f;
            progress.setValue(value);
        });

        HorizontalLayout layout = new HorizontalLayout(progress, button);
        layout.setExpandRatio(progress, 1);
        layout.setWidth(100, Unit.PERCENTAGE);
        getLayout().addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking the button to update the progress bar (expanded) "
                + "shouldn't push the button (fixed width) to the right";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10734;
    }
}
