package com.vaadin.tests.components.progressindicator;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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

        Button hideProgressIndicator = new Button("Hide progress indicator",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        pi.setVisible(!pi.isVisible());
                        event.getButton().setCaption(
                                (pi.isVisible() ? "Hide" : "Show")
                                        + " progress indicator");

                    }
                });
        addComponent(hideProgressIndicator);

        Button disableProgressIndicator = new Button(
                "Disable progress indicator", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        pi.setEnabled(!pi.isEnabled());
                        event.getButton().setCaption(
                                (pi.isEnabled() ? "Disable" : "Enable")
                                        + " progress indicator");

                    }
                });

        addComponent(disableProgressIndicator);
        Button removeProgressIndicator = new Button(
                "Remove progress indicator", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (pi.getParent() != null) {
                            lo.removeComponent(pi);
                            event.getButton().setCaption(
                                    "Add progress indicator");
                        } else {
                            lo.addComponent(pi);
                            event.getButton().setCaption(
                                    "Remove progress indicator");
                        }

                    }
                });

        addComponent(removeProgressIndicator);
        final Button b = new Button("Hide container of progress indicator");
        addComponent(b);

        b.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                lo.setVisible(!lo.isVisible());
                b.setCaption((lo.isVisible() ? "Hide" : "Show")
                        + " container of progress indicator");

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
