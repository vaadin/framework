package com.vaadin.tests.server.component.root;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.Application;
import com.vaadin.Application.ApplicationStartEvent;
import com.vaadin.RootRequiresMoreInformationException;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

public class CustomRootClassLoader extends TestCase {

    /**
     * Stub root
     */
    public static class MyRoot extends Root {
        @Override
        protected void init(WrappedRequest request) {
            // Nothing to see here
        }
    }

    /**
     * Dummy ClassLoader that just saves the name of the requested class before
     * delegating to the default implementation.
     */
    public class LoggingClassLoader extends ClassLoader {

        private List<String> requestedClasses = new ArrayList<String>();

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            requestedClasses.add(name);
            return super.loadClass(name, resolve);
        }
    }

    /**
     * Tests that a Root class can be loaded even if no classloader has been
     * provided.
     * 
     * @throws Exception
     *             if thrown
     */
    public void testWithNullClassLoader() throws Exception {
        Application application = createStubApplication();
        application.start(new ApplicationStartEvent(null, new Properties(),
                null, false));

        Root root = application.getRootForRequest(createRequestMock(null));
        assertTrue(root instanceof MyRoot);
    }

    private static WrappedRequest createRequestMock(ClassLoader classloader) {
        // Mock a DeploymentConfiguration to give the passed classloader
        DeploymentConfiguration configurationMock = EasyMock
                .createMock(DeploymentConfiguration.class);
        EasyMock.expect(configurationMock.getClassLoader()).andReturn(
                classloader);

        // Mock a WrappedRequest to give the mocked deployment configuration
        WrappedRequest requestMock = EasyMock.createMock(WrappedRequest.class);
        EasyMock.expect(requestMock.getDeploymentConfiguration()).andReturn(
                configurationMock);

        EasyMock.replay(configurationMock, requestMock);
        return requestMock;
    }

    /**
     * Tests that the ClassLoader passed in the ApplicationStartEvent is used to
     * load Root classes.
     * 
     * @throws Exception
     *             if thrown
     */
    public void testWithClassLoader() throws Exception {
        LoggingClassLoader loggingClassLoader = new LoggingClassLoader();

        Application application = createStubApplication();
        application.start(new ApplicationStartEvent(null, new Properties(),
                null, false));

        Root root = application
                .getRootForRequest(createRequestMock(loggingClassLoader));
        assertTrue(root instanceof MyRoot);
        assertEquals(1, loggingClassLoader.requestedClasses.size());
        assertEquals(MyRoot.class.getName(),
                loggingClassLoader.requestedClasses.get(0));

    }

    private Application createStubApplication() {
        return new Application() {
            @Override
            protected String getRootClassName(WrappedRequest request) {
                // Always use the same root class
                return MyRoot.class.getName();
            }

            @Override
            public Root getRootForRequest(WrappedRequest request)
                    throws RootRequiresMoreInformationException {
                // Always create a new root for testing (can't directly use
                // getRoot as it's protected)
                return getRoot(request);
            }
        };
    }
}
