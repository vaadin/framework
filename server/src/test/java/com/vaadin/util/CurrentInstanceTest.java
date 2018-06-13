package com.vaadin.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public class CurrentInstanceTest {

    @Before
    public void clearExistingThreadLocals() {
        // Ensure no previous test left some thread locals hanging
        CurrentInstance.clearAll();
    }

    @Before
    @After
    public void clearExistingFallbackResolvers() throws Exception {
        // Removes all static fallback resolvers
        Field field = CurrentInstance.class
                .getDeclaredField("fallbackResolvers");
        field.setAccessible(true);
        Map<?, ?> map = (Map<?, ?>) field.get(null);
        map.clear();
    }

    @Test
    public void testInitiallyCleared() throws Exception {
        assertCleared();
    }

    @Test
    public void testClearedAfterRemove() throws Exception {
        CurrentInstance.set(CurrentInstanceTest.class, this);
        assertEquals(this, CurrentInstance.get(CurrentInstanceTest.class));
        CurrentInstance.set(CurrentInstanceTest.class, null);

        assertCleared();
    }

    @Test
    public void testClearedWithClearAll() throws Exception {
        CurrentInstance.set(CurrentInstanceTest.class, this);
        assertEquals(this, CurrentInstance.get(CurrentInstanceTest.class));
        CurrentInstance.clearAll();

        assertCleared();
    }

    private void assertCleared() throws SecurityException, NoSuchFieldException,
            IllegalAccessException {
        assertNull(getInternalCurrentInstanceVariable().get());
    }

    private ThreadLocal<Map<Class<?>, CurrentInstance>> getInternalCurrentInstanceVariable()
            throws SecurityException, NoSuchFieldException,
            IllegalAccessException {
        Field f = CurrentInstance.class.getDeclaredField("INSTANCES");
        f.setAccessible(true);
        return (ThreadLocal<Map<Class<?>, CurrentInstance>>) f.get(null);
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
        WeakReference<VaadinSession> ref = new WeakReference<>(session1);

        session1 = null;
        waitUntilGarbageCollected(ref);

        CurrentInstance.restoreInstances(previous);

        assertNull(VaadinSession.getCurrent());
    }
    
    @Test
    public void testFallbackResolvers() throws Exception {
        TestFallbackResolver<UI> uiResolver = new TestFallbackResolver<UI>(
                new FakeUI());
        CurrentInstance.defineFallbackResolver(UI.class, uiResolver);

        TestFallbackResolver<VaadinSession> sessionResolver = new TestFallbackResolver<VaadinSession>(
                new FakeSession());
        CurrentInstance.defineFallbackResolver(VaadinSession.class,
                sessionResolver);

        TestFallbackResolver<VaadinService> serviceResolver = new TestFallbackResolver<VaadinService>(
                new FakeService(new FakeServlet()));
        CurrentInstance.defineFallbackResolver(VaadinService.class,
                serviceResolver);

        assertThat(UI.getCurrent(),
                CoreMatchers.instanceOf(FakeUI.class));
        assertThat(VaadinSession.getCurrent(),
                CoreMatchers.instanceOf(FakeSession.class));
        assertThat(VaadinService.getCurrent(),
                CoreMatchers.instanceOf(FakeService.class));

        assertEquals(
                "The UI fallback resolver should have been called exactly once",
                1, uiResolver.getCalled());

        assertEquals(
                "The VaadinSession fallback resolver should have been called exactly once",
                1, sessionResolver.getCalled());

        assertEquals(
                "The VaadinService fallback resolver should have been called exactly once",
                1, serviceResolver.getCalled());

        // the VaadinServlet.getCurrent() resolution uses the VaadinService type
        assertThat(VaadinServlet.getCurrent(),
                CoreMatchers.instanceOf(FakeServlet.class));
        assertEquals(
                "The VaadinService fallback resolver should have been called exactly twice",
                2, serviceResolver.getCalled());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFallbackResolversWithAlreadyDefinedResolver() {
        TestFallbackResolver<UI> uiResolver = new TestFallbackResolver<UI>(
                new FakeUI());
        CurrentInstance.defineFallbackResolver(UI.class, uiResolver);
        CurrentInstance.defineFallbackResolver(UI.class, uiResolver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFallbackResolversWithNullResolver() {
        CurrentInstance.defineFallbackResolver(UI.class, null);
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
        fail("Value was not garbage collected.");
    }

    @Test
    public void nonInheritableThreadLocals()
            throws InterruptedException, ExecutionException {
        CurrentInstance.clearAll();
        CurrentInstance.set(CurrentInstanceTest.class, this);

        assertNotNull(CurrentInstance.get(CurrentInstanceTest.class));

        Callable<Void> runnable = () -> {
            assertNull(CurrentInstance.get(CurrentInstanceTest.class));
            return null;
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Void> future = service.submit(runnable);
        future.get();
    }

    private static class TestFallbackResolver<T>
            implements CurrentInstanceFallbackResolver<T> {

        private int called;
        private final T instance;

        public TestFallbackResolver(T instance) {
            this.instance = instance;
        }

        @Override
        public T resolve() {
            called++;
            return instance;
        }

        public int getCalled() {
            return called;
        }
    }

    private static class FakeUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    private static class FakeServlet extends VaadinServlet {
    }

    private static class FakeService extends VaadinServletService {
        public FakeService(VaadinServlet servlet) throws ServiceException {
            super(servlet, new DefaultDeploymentConfiguration(FakeService.class,
                    new Properties()));
        }
    }

    private static class FakeSession extends VaadinSession {
        public FakeSession() {
            super(null);
        }

    }

}
