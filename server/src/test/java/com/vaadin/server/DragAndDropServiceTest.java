package com.vaadin.server;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.ui.AbstractComponent;

/**
 * Tests for {@link DragAndDropService}.
 *
 * @author Vaadin Ltd
 */
public class DragAndDropServiceTest {

    @Test
    public void changeVariables_isSourceConnectorEnabledCalled() {
        final List<Level> levels = new ArrayList<>();
        Logger.getLogger(DragAndDropService.class.getName())
                .addHandler(new StreamHandler() {
                    @Override
                    public synchronized void publish(LogRecord record) {
                        levels.add(record.getLevel());
                    }
                });
        Map<String, Object> variables = new HashMap<>();
        final boolean[] isConnectorEnabledCalled = new boolean[1];
        AbstractComponent component = new AbstractComponent() {
            @Override
            public boolean isConnectorEnabled() {
                isConnectorEnabledCalled[0] = true;
                return false;
            }
        };
        variables.put("component", component);

        DragAndDropService service = new DragAndDropService(
                EasyMock.createMock(VaadinSession.class));
        service.changeVariables(null, variables);

        assertTrue("isConnectorEnabled() method is not called",
                isConnectorEnabledCalled[0]);
        assertTrue("No warning on drop from disabled source",
                levels.contains(Level.WARNING));

    }

    @Test
    public void changeVariables_isTargetConnectorEnabledCalled() {
        final List<Level> levels = new ArrayList<>();
        Logger.getLogger(DragAndDropService.class.getName())
                .addHandler(new StreamHandler() {
                    @Override
                    public void publish(LogRecord record) {
                        levels.add(record.getLevel());
                    }
                });
        Map<String, Object> variables = new HashMap<>();
        TestDropTarget target = new TestDropTarget();
        variables.put("dhowner", target);

        DragAndDropService service = new DragAndDropService(
                EasyMock.createMock(VaadinSession.class));
        service.changeVariables(null, variables);

        assertTrue("isConnectorEnabled() method is not called",
                target.isConnectorEnabledCalled());
        assertTrue("No warning on drop to disabled target",
                levels.contains(Level.WARNING));

    }

    private static class TestDropTarget extends AbstractComponent
            implements com.vaadin.event.dd.DropTarget {
        @Override
        public boolean isConnectorEnabled() {
            isConnectorEnabledCalled = true;
            return false;
        }

        @Override
        public DropHandler getDropHandler() {
            return null;
        }

        @Override
        public TargetDetails translateDropTargetDetails(
                Map<String, Object> clientVariables) {
            return null;
        }

        boolean isConnectorEnabledCalled() {
            return isConnectorEnabledCalled;
        }

        private boolean isConnectorEnabledCalled;

    }
}
