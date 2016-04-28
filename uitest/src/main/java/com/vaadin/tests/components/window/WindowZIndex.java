package com.vaadin.tests.components.window;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowZIndex extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "Windows should stack correctly when adding new windows, closing opened ones and switching focus.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13174;
    }

    int windowCount = 0;
    Queue<Window> windows = new ArrayDeque<Window>();

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Button("Add window", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Window window = new Window();
                window.setPositionX(100 + 20 * windowCount);
                window.setPositionY(100 + 20 * windowCount);
                window.setHeight(200, Unit.PIXELS);
                window.setWidth(200, Unit.PIXELS);
                window.setContent(new Label("Window " + ++windowCount));
                addWindow(window);
                windows.add(window);
            }
        }));
        addComponent(new Button("Close window", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    windows.remove().close();
                } catch (NoSuchElementException e) {
                }
            }
        }));
    }

}
