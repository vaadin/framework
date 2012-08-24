package com.vaadin.tests.server.component.root;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.Application;
import com.vaadin.Application.ApplicationStartEvent;
import com.vaadin.RootRequiresMoreInformationException;
import com.vaadin.terminal.DefaultRootProvider;
import com.vaadin.terminal.DeploymentConfiguration;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.UI;

public class CustomRootClassLoader extends TestCase {

    /**
     * Stub root
     */
    public static class MyRoot extends UI {
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
     * Tests that a UI class can be loaded even if no classloader has been
     * provided.
     * 
     * @throws Exception
     *             if thrown
     */
    public void testWithNullClassLoader() throws Exception {
        Application application = createStubApplication();
        application.start(new ApplicationStartEvent(null,
                createConfigurationMock(), null));

        UI uI = application.getRootForRequest(createRequestMock(null));
        assertTrue(uI instanceof MyRoot);
    }

    private static DeploymentConfiguration createConfigurationMock() {
        DeploymentConfiguration configurationMock = EasyMock
                .createMock(DeploymentConfiguration.class);
        EasyMock.expect(configurationMock.isProductionMode()).andReturn(false);
        EasyMock.expect(configurationMock.getInitParameters()).andReturn(
                new Properties());

        EasyMock.replay(configurationMock);
        return configurationMock;
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
     * load UI classes.
     * 
     * @throws Exception
     *             if thrown
     */
    public void testWithClassLoader() throws Exception {
        LoggingClassLoader loggingClassLoader = new LoggingClassLoader();

        Application application = createStubApplication();
        application.start(new ApplicationStartEvent(null,
                createConfigurationMock(), null));

        UI uI = application
                .getRootForRequest(createRequestMock(loggingClassLoader));
        assertTrue(uI instanceof MyRoot);
        assertEquals(1, loggingClassLoader.requestedClasses.size());
        assertEquals(MyRoot.class.getName(),
                loggingClassLoader.requestedClasses.get(0));

    }

    private Application createStubApplication() {
        return new Application() {
            {
                addRootProvider(new DefaultRootProvider());
            }

            @Override
            public String getProperty(String name) {
                if (name.equals(ROOT_PARAMETER)) {
                    return MyRoot.class.getName();
                } else {
                    return super.getProperty(name);
                }
            }

            @Override
            public UI getRootForRequest(WrappedRequest request)
                    throws RootRequiresMoreInformationException {
                // Always create a new root for testing (can't directly use
                // getRoot as it's protected)
                return getRoot(request);
            }
        };
    }
}
