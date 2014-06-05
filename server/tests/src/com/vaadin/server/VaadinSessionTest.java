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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

public class VaadinSessionTest {

    private VaadinSession session;
    private VaadinServlet mockServlet;
    private VaadinServletService mockService;
    private ServletConfig mockServletConfig;
    private HttpSession mockHttpSession;
    private WrappedSession mockWrappedSession;
    private VaadinServletRequest vaadinRequest;
    private UI ui;

    @Before
    public void setup() throws Exception {
        mockServletConfig = new MockServletConfig();
        mockServlet = new VaadinServlet();
        mockServlet.init(mockServletConfig);
        mockService = mockServlet.getService();

        mockHttpSession = EasyMock.createMock(HttpSession.class);
        mockWrappedSession = new WrappedHttpSession(mockHttpSession) {
            final ReentrantLock lock = new ReentrantLock();
            {
                lock.lock();
            }

            @Override
            public Object getAttribute(String name) {
                String lockAttribute = mockService.getServiceName() + ".lock";
                if (lockAttribute.equals(name)) {
                    return lock;
                }
                return super.getAttribute(name);
            }
        };

        session = new VaadinSession(mockService);
        session.storeInSession(mockService, mockWrappedSession);

        ui = new UI() {
            Page page = new Page(this, getState(false).pageState) {
                @Override
                public void init(VaadinRequest request) {
                }
            };

            @Override
            protected void init(VaadinRequest request) {
            }

            @Override
            public Page getPage() {
                return page;
            }
        };
        vaadinRequest = new VaadinServletRequest(
                EasyMock.createMock(HttpServletRequest.class), mockService) {
            @Override
            public String getParameter(String name) {
                if ("theme".equals(name)) {
                    return null;
                }

                return super.getParameter(name);
            }
        };

        ui.doInit(vaadinRequest, session.getNextUIid(), null);

        ui.setSession(session);
        session.addUI(ui);

    }

    @Test
    public void threadLocalsAfterUnderlyingSessionTimeout() {

        final AtomicBoolean detachCalled = new AtomicBoolean(false);
        ui.addDetachListener(new DetachListener() {
            @Override
            public void detach(DetachEvent event) {
                detachCalled.set(true);
                Assert.assertEquals(ui, UI.getCurrent());
                Assert.assertEquals(ui.getPage(), Page.getCurrent());
                Assert.assertEquals(session, VaadinSession.getCurrent());
                Assert.assertEquals(mockService, VaadinService.getCurrent());
                Assert.assertEquals(mockServlet, VaadinServlet.getCurrent());
            }
        });

        session.valueUnbound(EasyMock.createMock(HttpSessionBindingEvent.class));
        Assert.assertTrue(detachCalled.get());
    }

    @Test
    public void threadLocalsAfterSessionDestroy() {
        final AtomicBoolean detachCalled = new AtomicBoolean(false);
        ui.addDetachListener(new DetachListener() {
            @Override
            public void detach(DetachEvent event) {
                detachCalled.set(true);
                Assert.assertEquals(ui, UI.getCurrent());
                Assert.assertEquals(ui.getPage(), Page.getCurrent());
                Assert.assertEquals(session, VaadinSession.getCurrent());
                Assert.assertEquals(mockService, VaadinService.getCurrent());
                Assert.assertEquals(mockServlet, VaadinServlet.getCurrent());
            }
        });
        CurrentInstance.clearAll();
        session.close();
        mockService.cleanupSession(session);
        Assert.assertTrue(detachCalled.get());
    }

    @Test
    public void testValueUnbound() {
        MockVaadinSession vaadinSession = new MockVaadinSession(mockService);

        vaadinSession.valueUnbound(EasyMock
                .createMock(HttpSessionBindingEvent.class));
        org.junit.Assert.assertEquals(
                "'valueUnbound' method doesn't call 'close' for the session",
                1, vaadinSession.getCloseCount());

        vaadinSession.valueUnbound(EasyMock
                .createMock(HttpSessionBindingEvent.class));

        org.junit.Assert.assertEquals(
                "'valueUnbound' method may not call 'close' "
                        + "method for closing session", 1,
                vaadinSession.getCloseCount());
    }
}
