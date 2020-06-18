package com.vaadin.tests.components.window;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class CloseWindowOnEscapeMaximizedButtonFocused extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label instructions = new Label("Press Maximise button and then ESC. "
                + " Window should be closed");
        Button openWindow = new Button("Open Window");
        openWindow.setId("openW");
        openWindow.addClickListener(e -> {
            Window win = new Window("Window test", new Label("Some content"));
            win.setWindowMode(WindowMode.NORMAL);
            win.setWidth("300px");
            win.setHeight("300px");
            win.center();
            addWindow(win);
        });
        addComponent(instructions);
        addComponent(openWindow);
    }

    @Override
    public String getTestDescription() {
        return "A window should be closed after the ESC button is pressed, when a maximize button is focused";
    };

    @Override
    public Integer getTicketNumber() {
        return 11838;
    };
}
