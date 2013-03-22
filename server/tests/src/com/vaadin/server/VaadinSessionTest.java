/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.ui.UI;

public class VaadinSessionTest {

    @Test
    public void threadLocalsAfterUnderlyingSessionTimeout() {

        final VaadinServlet mockServlet = new VaadinServlet() {
            @Override
            public String getServletName() {
                return "mockServlet";
            };
        };

        final VaadinServletService mockService = new VaadinServletService(
                mockServlet, EasyMock.createMock(DeploymentConfiguration.class));

        HttpSession mockHttpSession = EasyMock.createMock(HttpSession.class);
        WrappedSession mockWrappedSession = new WrappedHttpSession(
                mockHttpSession) {
            final ReentrantLock lock = new ReentrantLock();

            @Override
            public Object getAttribute(String name) {
                if ("mockServlet.lock".equals(name)) {
                    return lock;
                }
                return super.getAttribute(name);
            }
        };

        final VaadinSession session = new VaadinSession(mockService);
        session.storeInSession(mockService, mockWrappedSession);

        final UI ui = new UI() {
            Page page = new Page(this) {
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
        VaadinServletRequest vaadinRequest = new VaadinServletRequest(
                EasyMock.createMock(HttpServletRequest.class), mockService) {
            @Override
            public String getParameter(String name) {
                if ("theme".equals(name)) {
                    return null;
                }

                return super.getParameter(name);
            }
        };

        ui.doInit(vaadinRequest, session.getNextUIid());

        ui.setSession(session);
        session.addUI(ui);

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
}
