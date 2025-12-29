package com.vaadin.server;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.BootstrapHandler.BootstrapContext;
import com.vaadin.server.BootstrapHandler.BootstrapUriResolver;
import com.vaadin.server.communication.ServletBootstrapHandler;

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

    @Test
    public void synchronizedHandleRequest_requestTargetVAADINFolder_noUiCreated() throws IOException {
        final BootstrapHandler bootstrapHandler = new ServletBootstrapHandler();

        final VaadinServletRequest request = Mockito.mock(
                VaadinServletRequest.class);
        Mockito.doAnswer(invocation -> "/VAADIN").when(request).getPathInfo();

        final TestVaadinServletResponse response = new TestVaadinServletResponse();

        final boolean value = bootstrapHandler.synchronizedHandleRequest(
                Mockito.mock(VaadinSession.class), request, response);
        Assert.assertTrue("No further request handlers should be called",
                value);

        Assert.assertEquals("Invalid status code reported", 400,
                response.getErrorCode());
        Assert.assertEquals("Invalid message reported",
                "Invalid UI location: VAADIN is for static files",
                response.getErrorMessage());
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

    public static class TestVaadinServletResponse
            extends VaadinServletResponse {
        private int errorCode;
        private String errorMessage;

        private TestVaadinServletResponse() {
            super(Mockito.mock(HttpServletResponse.class), Mockito.mock(
                    VaadinServletService.class));
        }

        @Override
        public void sendError(int errorCode, String message) {
            this.errorCode = errorCode;
            errorMessage = message;
        }

        @Override
        public void sendError(int sc) {
            errorCode = sc;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public void setStatus(int sc) {
            errorCode = sc;
        }
    }
}
