package com.vaadin.tests.tickets;

import java.util.UUID;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket2014 extends LegacyApplication {

    private HorizontalLayout innerLayout1;
    private Button b1;
    private Panel panel;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getName());
        setMainWindow(w);
        // setTheme("tests-ticket");
        GridLayout layout = new GridLayout(10, 10);
        w.setContent(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        createPanel(layout);

        layout.addComponent(new Button("Change class name",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        b1.setStyleName(UUID.randomUUID().toString());
                    }

                }));

    }

    private void createPanel(GridLayout layout) {
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        panel = new Panel("panel caption", panelLayout);
        layout.addComponent(panel);

        innerLayout1 = new HorizontalLayout();
        innerLayout1.setSpacing(true);
        panelLayout.addComponent(innerLayout1);

        b1 = new Button("Button inside orderedLayout", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                System.out.println("Clicked " + event.getButton().getCaption());
            }

        });

        innerLayout1.addComponent(b1);

    }
}
