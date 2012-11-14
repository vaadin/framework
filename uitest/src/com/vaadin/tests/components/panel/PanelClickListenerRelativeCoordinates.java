package com.vaadin.tests.components.panel;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelClickListenerRelativeCoordinates extends TestBase {

    @Override
    protected void setup() {
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel panel = new Panel("Panel's caption", panelLayout);
        panel.addListener(new ClickListener() {

            @Override
            public void click(ClickEvent event) {
                getMainWindow()
                        .showNotification(
                                "" + event.getRelativeX() + ", "
                                        + event.getRelativeY());
            }
        });
        addComponent(panel);

    }

    @Override
    protected String getDescription() {
        return "Click the panel to get coordinates relative to the top-left corder of the panel.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
