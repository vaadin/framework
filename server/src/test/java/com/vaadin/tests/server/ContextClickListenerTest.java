/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.server;

import java.util.EventObject;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid.GridContextClickEvent;
import com.vaadin.ui.Table.TableContextClickEvent;

/**
 * Server-side unit tests to see that context click events are sent to listeners
 * correctly.
 *
 * If a listener is listening to a super type of an event, it should get the
 * event. i.e. Listening to ContextClickEvent, it should get the specialized
 * GridContextClickEvent as well.
 *
 * If a listener is listening to a sub-type of an event, it should not get the
 * super version. i.e. Listening to GridContextClickEvent, it should not get a
 * plain ContextClickEvent.
 */
public class ContextClickListenerTest extends AbstractComponent {

    private final static ContextClickEvent contextClickEvent = EasyMock
            .createMock(ContextClickEvent.class);
    private final static GridContextClickEvent gridContextClickEvent = EasyMock
            .createMock(GridContextClickEvent.class);
    private final static TableContextClickEvent tableContextClickEvent = EasyMock
            .createMock(TableContextClickEvent.class);

    private final AssertListener contextListener = new AssertListener();
    private final AssertListener ctxtListener2 = new AssertListener();

    public static class AssertListener implements ContextClickListener {

        private Class<?> expected = null;
        private String error = null;

        @Override
        public void contextClick(ContextClickEvent event) {
            if (expected == null) {
                error = "Unexpected context click event.";
                return;
            }

            if (!expected.isAssignableFrom(event.getClass())) {
                error = "Expected event type did not match the actual event.";
            }

            expected = null;
        }

        public <T extends ContextClickEvent> void expect(Class<T> clazz) {
            validate();
            expected = clazz;
        }

        public void validate() {
            if (expected != null) {
                Assert.fail("Expected context click never happened.");
            } else if (error != null) {
                Assert.fail(error);
            }
        }
    }

    @Test
    public void testListenerGetsASubClass() {
        addContextClickListener(contextListener);
        contextListener.expect(GridContextClickEvent.class);
        fireEvent(gridContextClickEvent);
    }

    @Test
    public void testListenerGetsExactClass() {
        addContextClickListener(contextListener);
        contextListener.expect(ContextClickEvent.class);
        fireEvent(contextClickEvent);
    }

    /**
     * Multiple listeners should get fitting events.
     */
    @Test
    public void testMultipleListenerGetEvents() {
        addContextClickListener(ctxtListener2);
        addContextClickListener(contextListener);

        ctxtListener2.expect(GridContextClickEvent.class);
        contextListener.expect(GridContextClickEvent.class);

        fireEvent(gridContextClickEvent);
    }

    @Test
    public void testAddAndRemoveListener() {
        addContextClickListener(contextListener);
        contextListener.expect(ContextClickEvent.class);

        fireEvent(contextClickEvent);

        removeContextClickListener(contextListener);

        fireEvent(contextClickEvent);
    }

    @Test
    public void testAddAndRemoveMultipleListeners() {
        addContextClickListener(ctxtListener2);
        addContextClickListener(contextListener);

        ctxtListener2.expect(GridContextClickEvent.class);
        contextListener.expect(GridContextClickEvent.class);
        fireEvent(gridContextClickEvent);

        removeContextClickListener(ctxtListener2);

        contextListener.expect(GridContextClickEvent.class);
        fireEvent(gridContextClickEvent);
    }

    @Test(expected = AssertionError.class)
    public void testExpectedEventNotReceived() {
        addContextClickListener(contextListener);
        contextListener.expect(GridContextClickEvent.class);
        fireEvent(contextClickEvent);
    }

    @Test(expected = AssertionError.class)
    public void testUnexpectedEventReceived() {
        addContextClickListener(contextListener);
        fireEvent(gridContextClickEvent);
    }

    @Override
    protected void fireEvent(EventObject event) {
        super.fireEvent(event);

        // Validate listeners automatically.
        ctxtListener2.validate();
        contextListener.validate();
    }
}
