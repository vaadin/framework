package com.vaadin.v7.tests.server;

import static org.junit.Assert.fail;

import java.util.EventObject;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.v7.ui.Grid.GridContextClickEvent;
import com.vaadin.v7.ui.Table.TableContextClickEvent;

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

    private static final ContextClickEvent contextClickEvent = EasyMock
            .createMock(ContextClickEvent.class);
    private static final GridContextClickEvent gridContextClickEvent = EasyMock
            .createMock(GridContextClickEvent.class);
    private static final TableContextClickEvent tableContextClickEvent = EasyMock
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
                fail("Expected context click never happened.");
            } else if (error != null) {
                fail(error);
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
