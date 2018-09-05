package com.vaadin.server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

public class ConnectorResourceHandlerTest {

    VaadinRequest request;
    VaadinResponse response;
    VaadinSession session;
    UI ui;

    @Before
    public void setUp() {
        IMocksControl control = EasyMock.createNiceControl();

        request = control.createMock(VaadinRequest.class);
        response = control.createMock(VaadinResponse.class);
        DeploymentConfiguration dc = control
                .createMock(DeploymentConfiguration.class);
        VaadinService service = control.createMock(VaadinService.class);

        EasyMock.expect(request.getPathInfo())
                .andReturn("/APP/connector/0/1/2");
        EasyMock.expect(request.getParameter("v-loc"))
                .andReturn("http://localhost/");

        control.replay();

        session = new MockVaadinSession(service);

        ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
            }
        };

        session.lock();
        try {
            session.setConfiguration(dc);
            session.setCommunicationManager(
                    new LegacyCommunicationManager(session));
            ui.setSession(session);
            ui.doInit(request, 0, "");
            session.addUI(ui);
        } finally {
            session.unlock();
        }
    }

    @Test
    public void testErrorHandling() throws IOException {

        ErrorHandler errorHandler = EasyMock.createMock(ErrorHandler.class);
        errorHandler.error(EasyMock.anyObject(ErrorEvent.class));
        EasyMock.replay(errorHandler);

        Button button = new Button() {
            @Override
            public boolean handleConnectorRequest(VaadinRequest request,
                    VaadinResponse response, String path) {
                throw new RuntimeException();
            }
        };
        button.setErrorHandler(errorHandler);

        session.lock();
        try {
            ui.setContent(button);
        } finally {
            session.unlock();
        }

        ConnectorResourceHandler handler = new ConnectorResourceHandler();
        assertTrue(handler.handleRequest(session, request, response));

        EasyMock.verify(errorHandler);
    }
}
