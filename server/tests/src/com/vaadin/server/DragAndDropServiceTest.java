/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.easymock.EasyMock;
import org.junit.Assert;
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
        final List<Level> levels = new ArrayList<Level>();
        Logger.getLogger(DragAndDropService.class.getName()).addHandler(
                new StreamHandler() {
                    @Override
                    public synchronized void publish(LogRecord record) {
                        levels.add(record.getLevel());
                    }
                });
        Map<String, Object> variables = new HashMap<String, Object>();
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

        Assert.assertTrue("isConnectorEnabled() method is not called",
                isConnectorEnabledCalled[0]);
        Assert.assertTrue("No warning on drop from disabled source",
                levels.contains(Level.WARNING));

    }

    @Test
    public void changeVariables_isTargetConnectorEnabledCalled() {
        final List<Level> levels = new ArrayList<Level>();
        Logger.getLogger(DragAndDropService.class.getName()).addHandler(
                new StreamHandler() {
                    @Override
                    public void publish(LogRecord record) {
                        levels.add(record.getLevel());
                    }
                });
        Map<String, Object> variables = new HashMap<String, Object>();
        TestDropTarget target = new TestDropTarget();
        variables.put("dhowner", target);

        DragAndDropService service = new DragAndDropService(
                EasyMock.createMock(VaadinSession.class));
        service.changeVariables(null, variables);

        Assert.assertTrue("isConnectorEnabled() method is not called",
                target.isConnectorEnabledCalled());
        Assert.assertTrue("No warning on drop to disabled target",
                levels.contains(Level.WARNING));

    }

    private static class TestDropTarget extends AbstractComponent implements
            com.vaadin.event.dd.DropTarget {
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
