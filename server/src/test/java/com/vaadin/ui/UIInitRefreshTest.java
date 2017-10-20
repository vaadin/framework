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
package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;

public class UIInitRefreshTest {

    private boolean initCalled;
    private boolean refreshCalled;
    private boolean fragmentChangeCalled;
    private boolean browserWindowResizeCalled;

    private class TestUI extends UI
            implements UriFragmentChangedListener, BrowserWindowResizeListener {
        @Override
        protected void init(VaadinRequest request) {
            getPage().addBrowserWindowResizeListener(this);
            getPage().addUriFragmentChangedListener(this);

            initCalled = true;

            assertEquals("foo", getPage().getUriFragment());
            assertEquals(100, getPage().getBrowserWindowWidth());
            assertEquals(100, getPage().getBrowserWindowHeight());

            assertFalse(fragmentChangeCalled);
            assertFalse(browserWindowResizeCalled);
        }

        @Override
        protected void refresh(VaadinRequest request) {
            refreshCalled = true;

            assertEquals("bar", getPage().getUriFragment());
            assertEquals(200, getPage().getBrowserWindowWidth());
            assertEquals(200, getPage().getBrowserWindowHeight());

            assertFalse(fragmentChangeCalled);
            assertFalse(browserWindowResizeCalled);
        }

        @Override
        public void browserWindowResized(BrowserWindowResizeEvent event) {
            assertEquals(200, event.getWidth());
            assertEquals(200, event.getHeight());
            browserWindowResizeCalled = true;
        }

        @Override
        public void uriFragmentChanged(UriFragmentChangedEvent event) {
            assertEquals("bar", event.getUriFragment());
            fragmentChangeCalled = true;
        }
    }

    @Before
    public void setUp() {
        initCalled = refreshCalled = fragmentChangeCalled = browserWindowResizeCalled = false;
    }

    @Test
    public void testListenersCalled() {
        IMocksControl control = EasyMock.createNiceControl();

        VaadinRequest initRequest = control.createMock(VaadinRequest.class);

        EasyMock.expect(initRequest.getParameter("v-loc"))
                .andReturn("http://example.com/#foo");
        EasyMock.expect(initRequest.getParameter("v-cw")).andReturn("100");
        EasyMock.expect(initRequest.getParameter("v-ch")).andReturn("100");

        VaadinRequest reinitRequest = control.createMock(VaadinRequest.class);
        EasyMock.expect(reinitRequest.getParameter("v-loc"))
                .andReturn("http://example.com/#bar");
        EasyMock.expect(reinitRequest.getParameter("v-cw")).andReturn("200");
        EasyMock.expect(reinitRequest.getParameter("v-ch")).andReturn("200");

        VaadinSession session = control.createMock(VaadinSession.class);
        DeploymentConfiguration dc = control
                .createMock(DeploymentConfiguration.class);

        EasyMock.expect(session.hasLock()).andStubReturn(true);
        EasyMock.expect(session.getConfiguration()).andStubReturn(dc);
        EasyMock.expect(session.getLocale()).andStubReturn(Locale.getDefault());

        control.replay();

        UI ui = new TestUI();
        ui.setSession(session);
        ui.doInit(initRequest, 0, "");

        assertTrue(initCalled);
        assertFalse(fragmentChangeCalled);
        assertFalse(browserWindowResizeCalled);

        ui.doRefresh(reinitRequest);

        assertTrue(refreshCalled);
        assertTrue(fragmentChangeCalled);
        assertTrue(browserWindowResizeCalled);
    }
}
