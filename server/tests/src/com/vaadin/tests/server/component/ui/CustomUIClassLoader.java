package com.vaadin.tests.server.component.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServiceSession;
import com.vaadin.ui.UI;

public class CustomUIClassLoader extends TestCase {

    /**
     * Stub root
     */
    public static class MyUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
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
        VaadinServiceSession application = createStubApplication();
        application.setConfiguration(createConfigurationMock());

        DefaultUIProvider uiProvider = new DefaultUIProvider();
        Class<? extends UI> uiClass = uiProvider
                .getUIClass(new UIClassSelectionEvent(createRequestMock(null)));

        assertEquals(MyUI.class, uiClass);
    }

    private static DeploymentConfiguration createConfigurationMock() {
        Properties properties = new Properties();
        properties.put(VaadinServiceSession.UI_PARAMETER, MyUI.class.getName());
        return new DefaultDeploymentConfiguration(CustomUIClassLoader.class,
                properties);
    }

    private static VaadinRequest createRequestMock(ClassLoader classloader) {
        // Mock a VaadinService to give the passed classloader
        VaadinService configurationMock = EasyMock
                .createMock(VaadinService.class);
        EasyMock.expect(configurationMock.getDeploymentConfiguration())
                .andReturn(createConfigurationMock());
        EasyMock.expect(configurationMock.getClassLoader()).andReturn(
                classloader);

        // Mock a VaadinRequest to give the mocked vaadin service
        VaadinRequest requestMock = EasyMock.createMock(VaadinRequest.class);
        EasyMock.expect(requestMock.getService()).andReturn(configurationMock);
        EasyMock.expect(requestMock.getService()).andReturn(configurationMock);
        EasyMock.expect(requestMock.getService()).andReturn(configurationMock);

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

        DefaultUIProvider uiProvider = new DefaultUIProvider();
        Class<? extends UI> uiClass = uiProvider
                .getUIClass(new UIClassSelectionEvent(
                        createRequestMock(loggingClassLoader)));

        assertEquals(MyUI.class, uiClass);
        assertEquals(1, loggingClassLoader.requestedClasses.size());
        assertEquals(MyUI.class.getName(),
                loggingClassLoader.requestedClasses.get(0));

    }

    private VaadinServiceSession createStubApplication() {
        return new VaadinServiceSession(null) {
            @Override
            public DeploymentConfiguration getConfiguration() {
                return createConfigurationMock();
            }
        };
    }
}
