/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.server.clientconnector;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.event.ConnectorEvent;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;

public class AttachDetachListenersTest {

    private IMocksControl control;

    private VaadinSession session;
    private UI ui;
    private Layout content;
    private Component component;

    AttachListener attachListener;
    DetachListener detachListener;

    @Before
    public void setUp() {
        control = EasyMock.createStrictControl();

        session = new AlwaysLockedVaadinSession(
                control.createMock(VaadinService.class));

        ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
            }
        };
        content = new CssLayout();
        component = new Label();

        attachListener = control.createMock(AttachListener.class);
        detachListener = control.createMock(DetachListener.class);
    }

    @Test
    public void attachListeners_setSessionLast() {
        setupAttachListeners();

        ui.setContent(content);
        content.addComponent(component);
        ui.setSession(session);

        control.verify();
    }

    @Test
    public void attachListeners_setSessionFirst() {
        setupAttachListeners();

        ui.setSession(session);
        ui.setContent(content);
        content.addComponent(component);

        control.verify();
    }

    @Test
    public void attachListeners_setSessionBetween() {
        setupAttachListeners();

        ui.setContent(content);
        ui.setSession(session);
        content.addComponent(component);

        control.verify();
    }

    @Test
    public void detachListeners_setSessionNull() {
        setupDetachListeners();

        ui.setContent(content);
        content.addComponent(component);
        ui.setSession(null);

        control.verify();
    }

    @Test
    public void detachListeners_removeComponent() {
        setupDetachListeners();

        ui.setContent(content);
        content.addComponent(component);
        content.removeAllComponents();
        ui.setSession(null);

        control.verify();
    }

    @Test
    public void detachListeners_setContentNull() {
        setupDetachListeners();

        ui.setContent(content);
        content.addComponent(component);
        ui.setContent(null);
        ui.setSession(null);

        control.verify();
    }

    public static class EventEquals<E extends ConnectorEvent> implements
            IArgumentMatcher {

        private E expected;

        public EventEquals(E expected) {
            this.expected = expected;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            buffer.append("EventEquals(");
            buffer.append("expected " + expected.getClass().getSimpleName()
                    + " with connector " + expected.getConnector());
            buffer.append(")");
        }

        @Override
        public boolean matches(Object argument) {
            return expected.getClass().isInstance(argument)
                    && ((ConnectorEvent) argument).getConnector() == expected
                            .getConnector();
        }
    }

    public static <E extends ConnectorEvent> E eventEquals(E expected) {
        EasyMock.reportMatcher(new EventEquals<E>(expected));
        return null;
    }

    private void setupDetachListeners() {
        detachListener.detach(eventEquals(new DetachEvent(component)));
        detachListener.detach(eventEquals(new DetachEvent(content)));
        detachListener.detach(eventEquals(new DetachEvent(ui)));

        control.replay();

        ui.addDetachListener(detachListener);
        content.addDetachListener(detachListener);
        component.addDetachListener(detachListener);

        ui.setSession(session);
    }

    private void setupAttachListeners() {
        attachListener.attach(eventEquals(new AttachEvent(ui)));
        attachListener.attach(eventEquals(new AttachEvent(content)));
        attachListener.attach(eventEquals(new AttachEvent(component)));

        control.replay();

        ui.addAttachListener(attachListener);
        content.addAttachListener(attachListener);
        component.addAttachListener(attachListener);
    }
}
