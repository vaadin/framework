package com.vaadin.tests.components.window;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WindowClickEvents extends TestBase {

    private Log log;

    @Override
    protected String getDescription() {
        return "Both the sub window and the main window has a click listener. Clicking produces a row in the log below.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5038;
    }

    @Override
    protected void setup() {
        VerticalLayout layout = new VerticalLayout();
        layout.addLayoutClickListener(event -> WindowClickEvents.this
                .click("Sub window layout", event));

        ((VerticalLayout) getMainWindow().getContent())
                .addLayoutClickListener(event -> WindowClickEvents.this
                        .click("Main window layout", event));
        layout.setMargin(true);
        Window centered = new Window("A window with a click listener", layout);
        centered.addClickListener(
                event -> WindowClickEvents.this.click("Sub window", event));
        centered.setSizeUndefined();
        layout.setSizeUndefined();
        centered.center();

        Label l = new Label("This window is centered");
        l.setSizeUndefined();
        Button b = new Button(
                "Clicking here should not produce a layout click event");
        b.addClickListener(event -> log.log("Click on button"));
        layout.addComponent(l);
        layout.addComponent(b);

        getMainWindow().addWindow(centered);
        log = new Log(5);
        addComponent(log);
        getMainWindow().addClickListener(
                event -> WindowClickEvents.this.click("Main window", event));
    }

    private void click(String target, ClickEvent event) {
        log.log("Click using " + event.getButtonName() + " on " + target);
        // + " at " + event.getClientX() + "," + event.getClientY());
    }
}
