package com.vaadin.tests.server.component.abstractsinglecomponentcontainer;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class RemoveFromParentLockingTest {

    @Before
    @After
    public void cleanCurrentSession() {
        VaadinSession.setCurrent(null);
    }

    private static VerticalLayout createTestComponent() {
        VaadinSession session = new MockVaadinSession(null);

        session.getLockInstance().lock();
        try {
            UI ui = new UI() {
                @Override
                protected void init(VaadinRequest request) {
                }
            };
            ui.setSession(session);

            VerticalLayout layout = new VerticalLayout();
            ui.setContent(layout);
            return layout;
        } finally {
            session.getLockInstance().unlock();
        }

    }

    @Test
    public void attachNoSessionLocked() {
        VerticalLayout testComponent = createTestComponent();

        VerticalLayout target = new VerticalLayout();

        try {
            target.addComponent(testComponent);
            throw new AssertionError(
                    "Moving component when not holding its sessions's lock should throw");
        } catch (IllegalStateException e) {
            assertEquals(
                    "Cannot remove from parent when the session is not locked.",
                    e.getMessage());
        }
    }

    @Test
    public void attachSessionLocked() {
        VerticalLayout testComponent = createTestComponent();

        VerticalLayout target = new VerticalLayout();

        testComponent.getUI().getSession().getLockInstance().lock();

        target.addComponent(testComponent);
        // OK if we get here without any exception
    }

    @Test
    public void crossAttachOtherSessionLocked() {
        VerticalLayout notLockedComponent = createTestComponent();

        VerticalLayout lockedComponent = createTestComponent();

        // Simulate the situation when attaching cross sessions
        lockedComponent.getUI().getSession().getLockInstance().lock();
        VaadinSession.setCurrent(lockedComponent.getUI().getSession());

        try {
            lockedComponent.addComponent(notLockedComponent);
            throw new AssertionError(
                    "Moving component when not holding its sessions's lock should throw");
        } catch (IllegalStateException e) {
            assertEquals(
                    "Cannot remove from parent when the session is not locked."
                            + " Furthermore, there is another locked session, indicating that the component might be about to be moved from one session to another.",
                    e.getMessage());
        }
    }

    @Test
    public void crossAttachThisSessionLocked() {
        VerticalLayout notLockedComponent = createTestComponent();

        VerticalLayout lockedComponent = createTestComponent();

        // Simulate the situation when attaching cross sessions
        lockedComponent.getUI().getSession().getLockInstance().lock();
        VaadinSession.setCurrent(lockedComponent.getUI().getSession());

        try {
            notLockedComponent.addComponent(lockedComponent);
        } catch (AssertionError e) {
            // All is fine, don't care about the exact wording in this case
        }
    }

}
