package com.vaadin.tests.application;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class PaintableIdsShouldBeUnique extends TestBase {

    @Override
    protected void setup() {
        setMainWindow(new MyWindow());
    }

    @Override
    protected String getDescription() {
        return "Two Paintables attached to different windows with the same debug id should have unique paintable ids";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5109;
    }

    @Override
    public Window getWindow(String name) {
        Window win = super.getWindow(name);
        if (win == null) {
            win = new MyWindow();
            addWindow(win);
            win.open(new ExternalResource(win.getURL()));
        }
        return win;
    }

    private class MyWindow extends Window {
        int counter = 0;
        Label labelWithDebugId = new Label("A label with a debug id.");
        Label labelWithoutDebugId = new Label("A label without a debug id.");
        Button button = new Button("Click me.");
        Button newwin = new Button("New window");

        MyWindow() {
            labelWithDebugId.setDebugId("MyLabel");

            button.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    counter++;
                    labelWithDebugId.setValue("Button clicked " + counter
                            + " times.");
                    labelWithoutDebugId.setValue("Button clicked " + counter
                            + " times.");
                }
            });
            button.setDebugId("MyButton");

            newwin.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Window win = new MyWindow();
                    PaintableIdsShouldBeUnique.this.addWindow(win);
                    open(new ExternalResource(win.getURL()));
                }
            });

            addComponent(labelWithDebugId);
            addComponent(labelWithoutDebugId);
            addComponent(button);
            addComponent(newwin);
        };
    }

}
