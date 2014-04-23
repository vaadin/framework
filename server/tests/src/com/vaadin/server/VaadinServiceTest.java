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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionBindingEvent;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Vaadin Ltd
 */
public class VaadinServiceTest {

    private class TestSessionDestroyListener implements SessionDestroyListener {

        int callCount = 0;

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            callCount++;
        }
    }

    @Test
    public void testFireSessionDestroy() throws ServletException {
        ServletConfig servletConfig = new MockServletConfig();
        VaadinServlet servlet = new VaadinServlet();
        servlet.init(servletConfig);
        VaadinService service = servlet.getService();

        TestSessionDestroyListener listener = new TestSessionDestroyListener();

        service.addSessionDestroyListener(listener);

        MockVaadinSession vaadinSession = new MockVaadinSession(service);
        service.fireSessionDestroy(vaadinSession);
        Assert.assertEquals(
                "'fireSessionDestroy' method doesn't call 'close' for the session",
                1, vaadinSession.getCloseCount());

        vaadinSession.valueUnbound(EasyMock
                .createMock(HttpSessionBindingEvent.class));

        Assert.assertEquals("'fireSessionDestroy' method may not call 'close' "
                + "method for closing session", 1,
                vaadinSession.getCloseCount());

        Assert.assertEquals("SessionDestroyListeners not called exactly once",
                1, listener.callCount);
    }
}
