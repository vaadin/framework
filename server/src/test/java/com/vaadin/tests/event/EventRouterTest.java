package com.vaadin.tests.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.event.EventRouter;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.shared.Registration;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Listener;
import com.vaadin.util.ReflectTools;

/**
 * Test EventRouter and related error handling.
 */
public class EventRouterTest {

    private static final Method COMPONENT_EVENT_METHOD = ReflectTools
            .findMethod(Component.Listener.class, "componentEvent",
                    Component.Event.class);

    private EventRouter router;
    private Component component;
    private ErrorHandler errorHandler;
    private Listener listener;

    @Before
    public void createMocks() {
        router = new EventRouter();
        component = EasyMock.createNiceMock(Component.class);
        errorHandler = EasyMock.createMock(ErrorHandler.class);
        listener = EasyMock.createMock(Component.Listener.class);
        router.addListener(Component.Event.class, listener,
                COMPONENT_EVENT_METHOD);
    }

    @Test
    public void fireEvent_noException_eventReceived() {
        listener.componentEvent(EasyMock.anyObject());

        EasyMock.replay(component, listener, errorHandler);
        router.fireEvent(new Component.Event(component), errorHandler);
        EasyMock.verify(listener, errorHandler);
    }

    @Test
    public void fireEvent_exceptionFromListenerAndNoHandler_exceptionPropagated() {
        listener.componentEvent(EasyMock.anyObject());
        EasyMock.expectLastCall()
                .andThrow(new RuntimeException("listener failed"));

        EasyMock.replay(component, listener);
        try {
            router.fireEvent(new Component.Event(component));
            fail("Did not receive expected exception from listener");
        } catch (RuntimeException e) {
            // e is a ListenerMethod@MethodException
            assertEquals("listener failed", e.getCause().getMessage());
        }
        EasyMock.verify(listener);
    }

    @Test
    public void fireEvent_exceptionFromListener_errorHandlerCalled() {
        listener.componentEvent(EasyMock.anyObject());
        EasyMock.expectLastCall()
                .andThrow(new RuntimeException("listener failed"));
        errorHandler.error(EasyMock.anyObject());

        EasyMock.replay(component, listener, errorHandler);
        router.fireEvent(new Component.Event(component), errorHandler);
        EasyMock.verify(listener, errorHandler);
    }

    @Test
    public void fireEvent_multipleListenersAndException_errorHandlerCalled() {
        Listener listener2 = EasyMock.createMock(Component.Listener.class);
        router.addListener(Component.Event.class, listener2,
                COMPONENT_EVENT_METHOD);

        listener.componentEvent(EasyMock.anyObject());
        EasyMock.expectLastCall()
                .andThrow(new RuntimeException("listener failed"));
        errorHandler.error(EasyMock.anyObject());
        // second listener should be called despite an error in the first
        listener2.componentEvent(EasyMock.anyObject());

        EasyMock.replay(component, listener, listener2, errorHandler);
        router.fireEvent(new Component.Event(component), errorHandler);
        EasyMock.verify(listener, listener2, errorHandler);
    }

    @Test
    public void registrationToRemoveRegisteredEventListener() {
        SharedState state = new SharedState();
        Listener listener2 = EasyMock.createMock(Component.Listener.class);
        Registration registration = router.addListener(ClickEvent.class,
                listener2, COMPONENT_EVENT_METHOD, "click", state);
        assertTrue(!state.registeredEventListeners.isEmpty());
        registration.remove();
        assertNull(state.registeredEventListeners);
    }
}
