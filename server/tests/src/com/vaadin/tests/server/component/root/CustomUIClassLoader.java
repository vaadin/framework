package com.vaadin.tests.server.component.root;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.Application;
import com.vaadin.Application.ApplicationStartEvent;
import com.vaadin.DefaultApplicationConfiguration;
import com.vaadin.server.ApplicationConfiguration;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

public class CustomUIClassLoader extends TestCase {

    /**
     * Stub root
     */
    public static class MyUI extends UI {
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

        DefaultUIProvider uiProvider = new DefaultUIProvider();
        Class<? extends UI> uiClass = uiProvider.getUIClass(application,
                createRequestMock(null));

        assertEquals(MyUI.class, uiClass);
    }

    private static ApplicationConfiguration createConfigurationMock() {
        Properties properties = new Properties();
        properties.put(Application.UI_PARAMETER, MyUI.class.getName());
        return new DefaultApplicationConfiguration(CustomUIClassLoader.class,
                properties);
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

        DefaultUIProvider uiProvider = new DefaultUIProvider();
        Class<? extends UI> uiClass = uiProvider.getUIClass(application,
                createRequestMock(loggingClassLoader));

        assertEquals(MyUI.class, uiClass);
        assertEquals(1, loggingClassLoader.requestedClasses.size());
        assertEquals(MyUI.class.getName(),
                loggingClassLoader.requestedClasses.get(0));

    }

    private Application createStubApplication() {
        return new Application() {
            @Override
            public ApplicationConfiguration getConfiguration() {
                return createConfigurationMock();
            }
        };
    }
}
