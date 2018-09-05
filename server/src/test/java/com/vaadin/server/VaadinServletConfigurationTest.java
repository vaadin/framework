package com.vaadin.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.MockUIContainingServlet.ServletInUI;
import com.vaadin.ui.UI;

public class VaadinServletConfigurationTest {

    @Test
    public void testEnclosingUIClass() throws Exception {
        ServletInUI servlet = new MockUIContainingServlet.ServletInUI();
        servlet.init(new MockServletConfig());

        Class<? extends UI> uiClass = new DefaultUIProvider()
                .getUIClass(new UIClassSelectionEvent(new VaadinServletRequest(
                        EasyMock.createMock(HttpServletRequest.class),
                        servlet.getService())));
        assertEquals(MockUIContainingServlet.class, uiClass);
    }

    @Test
    public void testValuesFromAnnotation() throws ServletException {
        TestServlet servlet = new TestServlet();
        servlet.init(new MockServletConfig());
        DeploymentConfiguration configuration = servlet.getService()
                .getDeploymentConfiguration();

        assertTrue(configuration.isProductionMode());
        assertTrue(configuration.isCloseIdleSessions());
        assertEquals(1234, configuration.getHeartbeatInterval());
        assertEquals(4321, configuration.getResourceCacheTime());

        Class<? extends UI> uiClass = new DefaultUIProvider()
                .getUIClass(new UIClassSelectionEvent(new VaadinServletRequest(
                        EasyMock.createMock(HttpServletRequest.class),
                        servlet.getService())));
        assertEquals(MockUIContainingServlet.class, uiClass);
    }

    @Test
    public void testValuesOverriddenForServlet() throws ServletException {
        Properties servletInitParams = new Properties();
        servletInitParams.setProperty("productionMode", "false");
        servletInitParams.setProperty("heartbeatInterval", "1111");

        TestServlet servlet = new TestServlet();
        servlet.init(new MockServletConfig(servletInitParams));
        DeploymentConfiguration configuration = servlet.getService()
                .getDeploymentConfiguration();

        // Values from servlet init params take precedence
        assertEquals(1111, configuration.getHeartbeatInterval());
        assertFalse(configuration.isProductionMode());

        // Other params are as defined in the annotation
        assertTrue(configuration.isCloseIdleSessions());
        assertEquals(4321, configuration.getResourceCacheTime());

        Class<? extends UI> uiClass = new DefaultUIProvider()
                .getUIClass(new UIClassSelectionEvent(new VaadinServletRequest(
                        EasyMock.createMock(HttpServletRequest.class),
                        servlet.getService())));
        assertEquals(MockUIContainingServlet.class, uiClass);
    }
}

@VaadinServletConfiguration(productionMode = true, ui = MockUIContainingServlet.class, closeIdleSessions = true, heartbeatInterval = 1234, resourceCacheTime = 4321)
class TestServlet extends VaadinServlet {

}
