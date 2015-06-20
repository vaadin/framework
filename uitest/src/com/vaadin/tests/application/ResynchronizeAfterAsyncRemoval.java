package com.vaadin.tests.application;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ConnectorTracker;
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

                boolean dirty = getUI().getConnectorTracker().isDirty(
                        event.getButton());
                log("Dirty: " + dirty);
            }
        }));
        addComponent(new Button("Log unregistered connector count",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        logUnregisteredConnectorCount();
                    }
                }));
    }

    private void logUnregisteredConnectorCount() {
        int count = 0;

        Map<Integer, Set<String>> unregisterIdMap = getUnregisterIdMap();
        for (Set<String> set : unregisterIdMap.values()) {
            count += set.size();
        }
        log("syncId: " + getConnectorTracker().getCurrentSyncId());
        log("Unregistered connector count: " + count);
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Set<String>> getUnregisterIdMap() {
        try {
            ConnectorTracker tracker = getConnectorTracker();
            Field field = tracker.getClass().getDeclaredField(
                    "syncIdToUnregisteredConnectorIds");
            field.setAccessible(true);
            return (Map<Integer, Set<String>>) field.get(tracker);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}