/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.server;

import java.io.IOException;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
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
        VaadinService service = control.createMock(VaadinService.class);

        EasyMock.expect(request.getPathInfo())
                .andReturn("/APP/connector/0/1/2");

        control.replay();

        session = new MockVaadinSession(service);

        ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
            }
        };
        ui.doInit(request, 0, "");

        session.lock();
        try {
            session.setCommunicationManager(new LegacyCommunicationManager(
                    session));
            ui.setSession(session);
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
        Assert.assertTrue(handler.handleRequest(session, request, response));

        EasyMock.verify(errorHandler);
    }
}
