package com.vaadin.util;

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
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import static org.junit.Assert.assertNull;

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

    private void assertCleared() throws SecurityException, NoSuchFieldException,
            IllegalAccessException {
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
                .setCurrent(new SessionStoredInCurrentInstance(
                        EasyMock.createNiceMock(VaadinService.class)));

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

    @Test
    public void testFallbackResolvers() {
        TestFallbackResolver<UI> uiResolver = new TestFallbackResolver<UI>();
        CurrentInstance.setFallbackResolver(UI.class, uiResolver);

        TestFallbackResolver<VaadinSession> sessionResolver = new TestFallbackResolver<VaadinSession>();
        CurrentInstance.setFallbackResolver(VaadinSession.class,
                sessionResolver);

        TestFallbackResolver<VaadinService> serviceResolver = new TestFallbackResolver<VaadinService>();
        CurrentInstance.setFallbackResolver(VaadinService.class,
                serviceResolver);

        UI.getCurrent();
        VaadinSession.getCurrent();
        VaadinService.getCurrent();
        VaadinServlet.getCurrent();

        Assert.assertEquals(
                "The UI fallback resolver should have been called exactly once",
                1, uiResolver.getCalled());

        Assert.assertEquals(
                "The VaadinSession fallback resolver should have been called exactly once",
                1, sessionResolver.getCalled());

        // the VaadinServlet.getCurrent() resolution uses the VaadinService type
        Assert.assertEquals(
                "The VaadinService fallback resolver should have been called exactly twice",
                2, serviceResolver.getCalled());

    }

    public static void waitUntilGarbageCollected(WeakReference<?> ref)
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

    private static class TestFallbackResolver<T>
            implements CurrentInstanceFallbackResolver<T> {

        private int called;

        @Override
        public T resolve() {
            called++;
            return null;
        }

        public int getCalled() {
            return called;
        }
    }
}
