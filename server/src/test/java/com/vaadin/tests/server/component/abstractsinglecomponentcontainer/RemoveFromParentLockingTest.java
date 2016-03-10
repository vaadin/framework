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
package com.vaadin.tests.server.component.abstractsinglecomponentcontainer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class RemoveFromParentLockingTest {

    private static VerticalLayout createTestComponent() {
        VaadinSession session = new VaadinSession(null) {
            private final ReentrantLock lock = new ReentrantLock();

            @Override
            public Lock getLockInstance() {
                return lock;
            }
        };

        session.getLockInstance().lock();

        UI ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
            }
        };
        ui.setSession(session);

        VerticalLayout layout = new VerticalLayout();
        ui.setContent(layout);

        session.getLockInstance().unlock();
        return layout;
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
            Assert.assertEquals(
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
            Assert.assertEquals(
                    "Cannot remove from parent when the session is not locked."
                            + " Furthermore, there is another locked session, indicating that the component might be about to be moved from one session to another.",
                    e.getMessage());
        } finally {
            VaadinSession.setCurrent(null);
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
        } finally {
            VaadinSession.setCurrent(null);
        }
    }

}
