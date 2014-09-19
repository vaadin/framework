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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
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
import com.vaadin.server.communication.UIInitHandler;
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
    private Lock httpSessionLock;

    @Before
    public void setup() throws Exception {
        httpSessionLock = new ReentrantLock();
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
                Object res;
                try {
                    Thread.sleep(100); // for deadlock testing
                    org.junit.Assert.assertTrue("Deadlock detected",
                            httpSessionLock.tryLock(5, TimeUnit.SECONDS)); // simulates
                                                                           // servlet
                                                                           // container's
                                                                           // session
                                                                           // locking
                    String lockAttribute = mockService.getServiceName()
                            + ".lock";
                    if (lockAttribute.equals(name)) {
                        res = lock;
                    } else if ("com.vaadin.server.VaadinSession.Mock Servlet"
                            .equals(name)) {
                        res = session;
                    } else {
                        res = super.getAttribute(name);
                    }
                    httpSessionLock.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return res;
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
                if ("theme".equals(name) || "restartApplication".equals(name)
                        || "ignoreRestart".equals(name)
                        || "closeApplication".equals(name)) {
                    return null;
                } else if (UIInitHandler.BROWSER_DETAILS_PARAMETER.equals(name)) {
                    return "1";
                }
                return super.getParameter(name);
            }

            @Override
            public String getMethod() {
                return "POST";
            }

            @Override
            public WrappedSession getWrappedSession(boolean allowSessionCreation) {
                return mockWrappedSession;
            }

        };

        ui.doInit(vaadinRequest, session.getNextUIid(), null);

        ui.setSession(session);
        session.addUI(ui);

    }

    /**
     * This reproduces #14452 situation with deadlock - see diagram
     */
    @Test
    public void testInvalidationDeadlock() {

        // this simulates servlet container's session invalidation from another
        // thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150); // delay selected so that VaadinSession
                                       // will be already locked by the main
                                       // thread
                                       // when we get here
                    httpSessionLock.lock();// simulating servlet container's
                                           // session lock
                    mockService.fireSessionDestroy(session);
                    httpSessionLock.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        try {
            mockService.findVaadinSession(vaadinRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void threadLocalsAfterUnderlyingSessionTimeout()
            throws InterruptedException {

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
        mockService.runPendingAccessTasks(session); // as soon as we changed
                                                    // session.accessSynchronously
                                                    // to session.access in
                                                    // VaadinService.fireSessionDestroy,
                                                    // we need to run the
                                                    // pending task ourselves
        Assert.assertTrue(detachCalled.get());
    }

    @Test
    public void threadLocalsAfterSessionDestroy() throws InterruptedException {
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
        mockService.runPendingAccessTasks(session); // as soon as we changed
                                                    // session.accessSynchronously
                                                    // to session.access in
                                                    // VaadinService.fireSessionDestroy,
                                                    // we need to run the
                                                    // pending task ourselves
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
