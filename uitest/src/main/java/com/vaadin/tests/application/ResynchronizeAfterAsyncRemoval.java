package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class ResynchronizeAfterAsyncRemoval extends AbstractTestUIWithLog {

    @Override
    public void setup(VaadinRequest vaadinRequest) {
        final Window window = new Window("Asynchronously removed window");
        window.center();

        // The window will enqueue a non-immediate message reporting its current
        // position.
        addWindow(window);

        // Remove window immediately when the current response is sent
        runAfterResponse(new Runnable() {
            @Override
            public void run() {
                removeWindow(window);
            }
        });

        // Clicking the button will trigger sending the window coordinates, but
        // the window is already removed at that point.
        addComponent(new Button("Am I dirty?", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Window removed: " + (window.getParent() == null));

                boolean dirty = getUI().getConnectorTracker()
                        .isDirty(event.getButton());
                log("Dirty: " + dirty);
            }
        }));
    }
}