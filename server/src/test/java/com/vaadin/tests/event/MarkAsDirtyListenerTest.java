package com.vaadin.tests.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.event.MarkedAsDirtyConnectorEvent;
import com.vaadin.event.MarkedAsDirtyListener;
import com.vaadin.server.ClientConnector;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentTest;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

/**
 * Test for mark as dirty listener functionality.
 */
public class MarkAsDirtyListenerTest {

    @Test
    public void fire_event_when_ui_marked_dirty() {
        UI ui = new MockUI();

        final AtomicReference<MarkedAsDirtyConnectorEvent> events = new AtomicReference<MarkedAsDirtyConnectorEvent>();
        ui.getConnectorTracker()
                .addMarkedAsDirtyListener(new MarkedAsDirtyListener() {
                    @Override
                    public void connectorMarkedAsDirty(
                            MarkedAsDirtyConnectorEvent event) {
                        Assert.assertTrue(
                                "No reference should have been registered",
                                events.compareAndSet(null, event));
                    }
                });

        // UI is marked dirty on creation and when adding a listener
        ComponentTest.syncToClient(ui);

        ui.getConnectorTracker().markDirty(ui);

        Assert.assertNotNull("Mark as dirty event should have fired",
                events.get());
        Assert.assertEquals("Event contains wrong ui", ui,
                events.get().getUi());
        Assert.assertEquals("Found wrong connector in event", ui,
                events.get().getConnector());
    }

    @Test
    public void fire_event_for_setContent() {
        final List<MarkedAsDirtyConnectorEvent> events = new ArrayList<MarkedAsDirtyConnectorEvent>();
        UI ui = new MockUI() {
            {
                getConnectorTracker()
                        .addMarkedAsDirtyListener(new MarkedAsDirtyListener() {

                            @Override
                            public void connectorMarkedAsDirty(
                                    MarkedAsDirtyConnectorEvent event) {
                                events.add(event);
                            }
                        });
            }
        };
        ComponentTest.syncToClient(ui);

        Button button = new Button("Button");
        ui.setContent(button);

        Assert.assertEquals("Mark as dirty events should have fired", 2,
                events.size());
        Assert.assertEquals("Expected button to inform first for creation",
                button, events.get(0).getConnector());
        Assert.assertEquals("Expected UI marked as dirty for setContent", ui,
                events.get(1).getConnector());
    }

    @Test
    public void fire_event_for_component_stateChange() {
        UI ui = new MockUI();
        Button button = new Button("empty");
        ui.setContent(button);
        ComponentTest.syncToClient(button);

        final AtomicReference<MarkedAsDirtyConnectorEvent> events = new AtomicReference<MarkedAsDirtyConnectorEvent>();
        ui.getConnectorTracker()
                .addMarkedAsDirtyListener(new MarkedAsDirtyListener() {

                    @Override
                    public void connectorMarkedAsDirty(
                            MarkedAsDirtyConnectorEvent event) {
                        Assert.assertTrue(
                                "No reference should have been registered",
                                events.compareAndSet(null, event));
                    }
                });

        button.setIconAlternateText("alternate");

        Assert.assertNotNull("Mark as dirty event should have fired",
                events.get());
        Assert.assertEquals("Event contains wrong ui", ui,
                events.get().getUi());
        Assert.assertEquals("Found wrong connector in event", button,
                events.get().getConnector());
    }

    @Test
    public void fire_events_for_each_component() {
        final List<MarkedAsDirtyConnectorEvent> events = new ArrayList<MarkedAsDirtyConnectorEvent>();
        UI ui = new MockUI() {
            {
                getConnectorTracker()
                        .addMarkedAsDirtyListener(new MarkedAsDirtyListener() {

                            @Override
                            public void connectorMarkedAsDirty(
                                    MarkedAsDirtyConnectorEvent event) {
                                events.add(event);
                            }
                        });
            }
        };

        HorizontalLayout layout = new HorizontalLayout();
        // UI initially marked as dirty so should not show as event.
        ui.setContent(layout);
        TextField field = new TextField("Name");
        Button button = new Button("say hello");
        layout.addComponents(field, button);

        Assert.assertFalse("Mark as dirty event should have fired",
                events.isEmpty());
        Assert.assertEquals("Unexpected amount of connector events", 3,
                events.size());

        Set<ClientConnector> connectors = new HashSet<ClientConnector>();
        for (MarkedAsDirtyConnectorEvent event : events) {
            connectors.add(event.getConnector());
        }

        Assert.assertTrue(
                "HorizontalLayout should have fired an markedAsDirty event",
                connectors.contains(layout));
        Assert.assertTrue("TextField should have fired an markedAsDirty event",
                connectors.contains(field));
        Assert.assertTrue("Button should have fired an markedAsDirty event",
                connectors.contains(button));
    }

    @Test
    public void event_should_only_fire_once_for_an_connector_per_roundtrip() {
        UI ui = new MockUI();
        Button button = new Button("empty");
        ui.setContent(button);
        ComponentTest.syncToClient(button);

        final AtomicReference<MarkedAsDirtyConnectorEvent> events = new AtomicReference<MarkedAsDirtyConnectorEvent>();
        ui.getConnectorTracker()
                .addMarkedAsDirtyListener(new MarkedAsDirtyListener() {
                    @Override
                    public void connectorMarkedAsDirty(
                            MarkedAsDirtyConnectorEvent event) {
                        Assert.assertTrue(
                                "Only one event should have registered",
                                events.compareAndSet(null, event));
                    }
                });

        button.setIconAlternateText("alternate");
        button.setCaption("Update");
        button.setDisableOnClick(true);

        Assert.assertNotNull("Mark as dirty event should have fired",
                events.get());
        Assert.assertEquals("Event contains wrong ui", ui,
                events.get().getUi());
        Assert.assertEquals("Found wrong connector in event", button,
                events.get().getConnector());

        events.set(null);
        ComponentTest.syncToClient(button);

        button.setCaption("new caption");

        Assert.assertNotNull("Mark as dirty event should have fired",
                events.get());
        Assert.assertEquals("Found wrong connector in event", button,
                events.get().getConnector());
    }

}
