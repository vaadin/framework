package com.vaadin.tests.components.progressindicator;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;

public class ProgressIndicatorInvisible extends TestBase {

    @Override
    protected void setup() {
        final VerticalLayout lo = new VerticalLayout();

        addComponent(lo);

        final ProgressIndicator pi = new ProgressIndicator();
        pi.setPollingInterval(400);
        lo.addComponent(pi);

        final Button b = new Button("Hide container of progress indicator");
        addComponent(b);

        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                // If we skip hiding the layout, hiding the ProgressIndicator
                // will stop the polling
                lo.setVisible(!lo.isVisible());
                // Not even this works
                pi.setVisible(!lo.isVisible());
                if (!lo.isVisible()) {
                    b.setCaption("Still polling");
                } else {
                    b.setCaption("Hide container of progress indicator");
                }

            }

        });
    }

    @Override
    protected String getDescription() {
        return "Progress indicator does not stop polling when its parent layout is made invisible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4014;
    }

}
