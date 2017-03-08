/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.server.component.ui;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.UI;

public class CustomUIClassLoaderTest {

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

        private final List<String> requestedClasses = new ArrayList<>();

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
    @Test
    public void testWithDefaultClassLoader() throws Exception {
        VaadinSession application = createStubApplication();
        application.setConfiguration(createConfigurationMock());

        DefaultUIProvider uiProvider = new DefaultUIProvider();
        Class<? extends UI> uiClass = uiProvider
                .getUIClass(new UIClassSelectionEvent(
                        createRequestMock(getClass().getClassLoader())));

        assertEquals(MyUI.class, uiClass);
    }

    private static DeploymentConfiguration createConfigurationMock() {
        Properties properties = new Properties();
        properties.put(VaadinSession.UI_PARAMETER, MyUI.class.getName());
        return new DefaultDeploymentConfiguration(CustomUIClassLoaderTest.class,
                properties);
    }

    private static VaadinRequest createRequestMock(ClassLoader classloader) {
        // Mock a VaadinService to give the passed classloader
        VaadinService configurationMock = EasyMock
                .createMock(VaadinService.class);
        EasyMock.expect(configurationMock.getDeploymentConfiguration())
                .andReturn(createConfigurationMock());
        EasyMock.expect(configurationMock.getClassLoader())
                .andReturn(classloader);

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
    @Test
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

    private VaadinSession createStubApplication() {
        return new AlwaysLockedVaadinSession(null) {
            @Override
            public DeploymentConfiguration getConfiguration() {
                return createConfigurationMock();
            }
        };
    }
}
