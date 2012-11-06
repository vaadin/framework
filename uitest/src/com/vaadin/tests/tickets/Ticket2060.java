package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;

public class Ticket2060 extends LegacyApplication {

    private Button button1;
    private Button button2;
    private Button button3;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setContent(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        button1 = new Button("Button which is 50px wide");
        button1.setWidth("50px");
        button2 = new Button("Button without width");
        button3 = new Button("Click to repaint buttons", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                button1.markAsDirty();
                button2.markAsDirty();
                button3.markAsDirty();

            }

        });

        buttonLayout.addComponent(button1);
        buttonLayout.addComponent(button2);
        buttonLayout.addComponent(button3);

        layout.addComponent(buttonLayout);

    }
}
