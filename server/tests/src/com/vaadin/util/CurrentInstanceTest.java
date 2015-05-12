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
package com.vaadin.util;

import static org.junit.Assert.assertNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public class CurrentInstanceTest {

    @Before
    public void clearExistingThreadLocals() {
        // Ensure no previous test left some thread locals hanging
        CurrentInstance.clearAll();
    }

    @Test
    public void testInitiallyCleared() throws Exception {
        assertCleared();
    }

    @Test
    public void testClearedAfterRemove() throws Exception {
        CurrentInstance.set(CurrentInstanceTest.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(CurrentInstanceTest.class));
        CurrentInstance.set(CurrentInstanceTest.class, null);

        assertCleared();
    }

    @Test
    public void testClearedAfterRemoveInheritable() throws Exception {
        CurrentInstance.clearAll();

        CurrentInstance.setInheritable(CurrentInstanceTest.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(CurrentInstanceTest.class));
        CurrentInstance.setInheritable(CurrentInstanceTest.class, null);

        assertCleared();
    }

    @Test
    public void testInheritableThreadLocal() throws Exception {
        final AtomicBoolean threadFailed = new AtomicBoolean(true);

        CurrentInstance.setInheritable(CurrentInstanceTest.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(CurrentInstanceTest.class));
        Thread t = new Thread() {
            @Override
            public void run() {
                Assert.assertEquals(CurrentInstanceTest.this,
                        CurrentInstance.get(CurrentInstanceTest.class));
                threadFailed.set(false);
            }
        };
        t.start();
        CurrentInstance.set(CurrentInstanceTest.class, null);

        assertCleared();
        while (t.isAlive()) {
            Thread.sleep(1000);
        }
        Assert.assertFalse("Thread failed", threadFailed.get());

    }

    @Test
    public void testClearedAfterRemoveInSeparateThread() throws Exception {
        final AtomicBoolean threadFailed = new AtomicBoolean(true);

        CurrentInstance.setInheritable(CurrentInstanceTest.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(CurrentInstanceTest.class));
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Assert.assertEquals(CurrentInstanceTest.this,
                            CurrentInstance.get(CurrentInstanceTest.class));
                    CurrentInstance.set(CurrentInstanceTest.class, null);
                    assertCleared();

                    threadFailed.set(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

        while (t.isAlive()) {
            Thread.sleep(1000);
        }
        Assert.assertFalse("Thread failed", threadFailed.get());

        // Clearing the threadlocal in the thread should not have cleared it
        // here
        Assert.assertEquals(this,
                CurrentInstance.get(CurrentInstanceTest.class));

        // Clearing the only remaining threadlocal should free all memory
        CurrentInstance.set(CurrentInstanceTest.class, null);
        assertCleared();
    }

    @Test
    public void testClearedWithClearAll() throws Exception {
        CurrentInstance.set(CurrentInstanceTest.class, this);
        Assert.assertEquals(this,
                CurrentInstance.get(CurrentInstanceTest.class));
        CurrentInstance.clearAll();

        assertCleared();
    }

    private void assertCleared() throws SecurityException,
            NoSuchFieldException, IllegalAccessException {
        Assert.assertNull(getInternalCurrentInstanceVariable().get());
    }

    private InheritableThreadLocal<Map<Class<?>, CurrentInstance>> getInternalCurrentInstanceVariable()
            throws SecurityException, NoSuchFieldException,
            IllegalAccessException {
        Field f = CurrentInstance.class.getDeclaredField("instances");
        f.setAccessible(true);
        return (InheritableThreadLocal<Map<Class<?>, CurrentInstance>>) f
                .get(null);
    }

    public void testInheritedClearedAfterRemove() {

    }

    private static class UIStoredInCurrentInstance extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    private static class SessionStoredInCurrentInstance extends VaadinSession {
        public SessionStoredInCurrentInstance(VaadinService service) {
            super(service);
        }
    }

    @Test
    public void testRestoringNullUIWorks() throws Exception {
        // First make sure current instance is empty
        CurrentInstance.clearAll();

        // Then store a new UI in there
        Map<Class<?>, CurrentInstance> old = CurrentInstance
                .setCurrent(new UIStoredInCurrentInstance());

        // Restore the old values and assert that the UI is null again
        CurrentInstance.restoreInstances(old);
        assertNull(CurrentInstance.get(UI.class));
    }

    @Test
    public void testRestoringNullSessionWorks() throws Exception {
        // First make sure current instance is empty
        CurrentInstance.clearAll();

        // Then store a new session in there
        Map<Class<?>, CurrentInstance> old = CurrentInstance
                .setCurrent(new SessionStoredInCurrentInstance(EasyMock
                        .createNiceMock(VaadinService.class)));

        // Restore the old values and assert that the session is null again
        CurrentInstance.restoreInstances(old);
        assertNull(CurrentInstance.get(VaadinSession.class));
        assertNull(CurrentInstance.get(VaadinService.class));
    }

    @Test
    public void testRestoreWithGarbageCollectedValue()
            throws InterruptedException {
        VaadinSession session1 = new VaadinSession(null) {
            @Override
            public String toString() {
                return "First session";
            }
        };
        VaadinSession session2 = new VaadinSession(null) {
            @Override
            public String toString() {
                return "Second session";
            }
        };

        VaadinSession.setCurrent(session1);
        Map<Class<?>, CurrentInstance> previous = CurrentInstance
                .setCurrent(session2);

        // Use weak ref to verify object is collected
        WeakReference<VaadinSession> ref = new WeakReference<VaadinSession>(
                session1);

        session1 = null;
        waitUntilGarbageCollected(ref);

        CurrentInstance.restoreInstances(previous);

        Assert.assertNull(VaadinSession.getCurrent());
    }

    private static void waitUntilGarbageCollected(WeakReference<?> ref)
            throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            System.gc();
            if (ref.get() == null) {
                return;
            }
            Thread.sleep(100);
        }
        Assert.fail("Value was not garbage collected.");
    }
}
