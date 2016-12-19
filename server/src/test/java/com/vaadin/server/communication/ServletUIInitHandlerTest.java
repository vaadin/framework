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
package com.vaadin.server.communication;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.MockServletConfig;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.server.VaadinServletService;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.UI;

public class ServletUIInitHandlerTest {

    public static class CommunicationMock {

        public final UI ui;
        public final ServletConfig servletConfig;
        public final VaadinServlet servlet;
        public final DefaultDeploymentConfiguration deploymentConfiguration;
        public final VaadinServletService service;
        public final AlwaysLockedVaadinSession session;

        public CommunicationMock(final UI ui) throws Exception {
            servletConfig = new MockServletConfig();
            servlet = new VaadinServlet();
            servlet.init(servletConfig);

            deploymentConfiguration = new DefaultDeploymentConfiguration(
                    UI.class, new Properties());

            service = new VaadinServletService(servlet,
                    deploymentConfiguration);
            session = new AlwaysLockedVaadinSession(service);
            LegacyCommunicationManager communicationManager = new LegacyCommunicationManager(
                    session);
            session.setCommunicationManager(communicationManager);
            session.setConfiguration(deploymentConfiguration);
            session.addUIProvider(new UIProvider() {

                @Override
                public Class<? extends UI> getUIClass(
                        UIClassSelectionEvent event) {
                    return ui.getClass();
                }

                @Override
                public UI createInstance(UICreateEvent event) {
                    return ui;
                }
            });
            this.ui = ui;
        }

        public VaadinRequest createInitRequest() {
            return new VaadinServletRequest(
                    Mockito.mock(HttpServletRequest.class), service) {
                @Override
                public String getMethod() {
                    return "POST";
                }

                @Override
                public String getParameter(String name) {
                    if (UIInitHandler.BROWSER_DETAILS_PARAMETER.equals(name)) {
                        return "1";
                    }
                    return super.getParameter(name);
                }

            };
        }

    }

    @Test
    public void errorHandlerForInitException() throws Exception {
        final AtomicInteger pre = new AtomicInteger(0);
        final AtomicInteger errorHandlerCalls = new AtomicInteger(0);

        UI ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
                pre.incrementAndGet();
                throw new RuntimeException("Exception produced in init()");
            }
        };

        ui.setErrorHandler(new ErrorHandler() {
            @Override
            public void error(ErrorEvent event) {
                errorHandlerCalls.incrementAndGet();
            }
        });

        CommunicationMock mock = new CommunicationMock(ui);
        VaadinRequest initRequest = mock.createInitRequest();

        ServletUIInitHandler servletUIInitHandler = new ServletUIInitHandler();
        servletUIInitHandler.handleRequest(mock.session, initRequest,
                new VaadinServletResponse(
                        Mockito.mock(HttpServletResponse.class), mock.service) {
                    @Override
                    public ServletOutputStream getOutputStream()
                            throws IOException {
                        return new ServletOutputStream() {
                            @Override
                            public void write(int b) throws IOException {
                            }
                        };
                    }
                });

        Assert.assertEquals(1, pre.getAndIncrement());
        Assert.assertEquals(1, errorHandlerCalls.getAndIncrement());
        Assert.assertEquals(mock.session, ui.getSession());
    }

    @Test
    public void initExceptionNoErrorHandler() throws Exception {
        final AtomicInteger pre = new AtomicInteger(0);

        UI ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
                pre.incrementAndGet();
                throw new RuntimeException("Exception produced in init()");
            }
        };

        CommunicationMock mock = new CommunicationMock(ui);
        VaadinRequest initRequest = mock.createInitRequest();

        ServletUIInitHandler servletUIInitHandler = new ServletUIInitHandler();
        servletUIInitHandler.handleRequest(mock.session, initRequest,
                new VaadinServletResponse(
                        Mockito.mock(HttpServletResponse.class), mock.service) {
                    @Override
                    public ServletOutputStream getOutputStream()
                            throws IOException {
                        return new ServletOutputStream() {
                            @Override
                            public void write(int b) throws IOException {
                            }
                        };
                    }
                });

        Assert.assertEquals(1, pre.getAndIncrement());
        // Default error handler only logs the exception
        Assert.assertEquals(mock.session, ui.getSession());
    }

}
