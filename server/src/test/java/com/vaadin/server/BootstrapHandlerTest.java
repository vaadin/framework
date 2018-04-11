package com.vaadin.server;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.BootstrapHandler.BootstrapContext;
import com.vaadin.server.BootstrapHandler.BootstrapUriResolver;

public class BootstrapHandlerTest {

    private static final String VAADIN_URL = "http://host/VAADIN/";

    public static class ES5Browser extends WebBrowser {
        @Override
        public boolean isEs6Supported() {
            return false;
        }
    }

    public static class ES6Browser extends WebBrowser {
        @Override
        public boolean isEs6Supported() {
            return true;
        }
    }

    @Test
    public void resolveFrontendES5() {
        testResolveFrontEnd("frontend://foobar.html",
                "http://host/VAADIN/frontend/es5/foobar.html",
                new ES5Browser());

    }

    @Test
    public void resolveFrontendES6() {
        testResolveFrontEnd("frontend://foobar.html",
                "http://host/VAADIN/frontend/es6/foobar.html",
                new ES6Browser());

    }

    @Test
    public void resolveFrontendES5CustomUrl() {
        Properties properties = new Properties();
        properties.setProperty("frontend.url.es5",
                "https://cdn.somewhere.com/5");
        testResolveFrontEnd("frontend://foobar.html",
                "https://cdn.somewhere.com/5/foobar.html", new ES5Browser(),
                properties);

    }

    @Test
    public void resolveFrontendES6CustomUrl() {
        Properties properties = new Properties();
        properties.setProperty("frontend.url.es6",
                "https://cdn.somewhere.com/6");
        testResolveFrontEnd("frontend://foobar.html",
                "https://cdn.somewhere.com/6/foobar.html", new ES6Browser(),
                properties);

    }

    private static void testResolveFrontEnd(String frontendUrl,
            String expectedUrl, WebBrowser browser) {
        testResolveFrontEnd(frontendUrl, expectedUrl, browser,
                new Properties());
    }

    @SuppressWarnings("deprecation")
    private static void testResolveFrontEnd(String frontendUrl,
            String expectedUrl, WebBrowser browser,
            Properties customProperties) {

        BootstrapContext context = Mockito.mock(BootstrapContext.class);
        BootstrapUriResolver resolver = new BootstrapUriResolver(context) {
            @Override
            protected String getVaadinDirUrl() {
                return VAADIN_URL;
            }
        };
        VaadinSession session = Mockito.mock(VaadinSession.class);
        Mockito.when(context.getSession()).thenReturn(session);
        DeploymentConfiguration configuration = new DefaultDeploymentConfiguration(
                BootstrapHandlerTest.class, customProperties);
        Mockito.when(session.getBrowser()).thenReturn(browser);
        Mockito.when(session.getConfiguration()).thenReturn(configuration);

        assertEquals(expectedUrl, resolver.resolveVaadinUri(frontendUrl));
    }
}
