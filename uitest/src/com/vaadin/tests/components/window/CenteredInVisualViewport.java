package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class CenteredInVisualViewport extends TestBase {

    @Override
    protected String getDescription() {
        return "Should open centered, even if zoomed in on one button (e.g zoom in iOS)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11614;
    }

    @Override
    protected void setup() {
        GridLayout layout = new GridLayout(3, 3);
        layout.setWidth("1000px");
        layout.setHeight("1000px");
        addComponent(layout);

        Button b = new Button("Open", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Window centered = new Window("A window", new Label(
                        "Centered window"));
                centered.center();
                getMainWindow().addWindow(centered);
            }
        });
        layout.addComponent(b, 0, 0);

        b = new Button("Open", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Window centered = new Window("A window", new Label(
                        "Centered window"));
                centered.center();
                getMainWindow().addWindow(centered);
            }
        });
        layout.addComponent(b, 1, 1);

        b = new Button("Open", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Window centered = new Window("A window", new Label(
                        "Centered window"));
                centered.center();
                getMainWindow().addWindow(centered);
            }
        });
        layout.addComponent(b, 2, 2);

    }
}
