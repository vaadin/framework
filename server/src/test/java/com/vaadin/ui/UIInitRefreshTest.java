package com.vaadin.ui;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;

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

            Assert.assertEquals("foo", getPage().getUriFragment());
            Assert.assertEquals(100, getPage().getBrowserWindowWidth());
            Assert.assertEquals(100, getPage().getBrowserWindowHeight());

            Assert.assertFalse(fragmentChangeCalled);
            Assert.assertFalse(browserWindowResizeCalled);
        }

        @Override
        protected void refresh(VaadinRequest request) {
            refreshCalled = true;

            Assert.assertEquals("bar", getPage().getUriFragment());
            Assert.assertEquals(200, getPage().getBrowserWindowWidth());
            Assert.assertEquals(200, getPage().getBrowserWindowHeight());

            Assert.assertFalse(fragmentChangeCalled);
            Assert.assertFalse(browserWindowResizeCalled);
        }

        @Override
        public void browserWindowResized(BrowserWindowResizeEvent event) {
            Assert.assertEquals(200, event.getWidth());
            Assert.assertEquals(200, event.getHeight());
            browserWindowResizeCalled = true;
        }

        @Override
        public void uriFragmentChanged(UriFragmentChangedEvent event) {
            Assert.assertEquals("bar", event.getUriFragment());
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

        control.replay();

        UI ui = new TestUI();
        ui.doInit(initRequest, 0, "");

        Assert.assertTrue(initCalled);
        Assert.assertFalse(fragmentChangeCalled);
        Assert.assertFalse(browserWindowResizeCalled);

        ui.doRefresh(reinitRequest);

        Assert.assertTrue(refreshCalled);
        Assert.assertTrue(fragmentChangeCalled);
        Assert.assertTrue(browserWindowResizeCalled);
    }
}
