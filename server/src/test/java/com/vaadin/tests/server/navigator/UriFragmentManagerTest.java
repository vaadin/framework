package com.vaadin.tests.server.navigator;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.UriFragmentManager;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;

public class UriFragmentManagerTest {

    @Test
    public void testGetSetUriFragment() {
        Page page = EasyMock.createMock(Page.class);
        UriFragmentManager manager = new UriFragmentManager(page);

        // prepare mock
        EasyMock.expect(page.getUriFragment()).andReturn("");
        page.setUriFragment("!test", false);
        EasyMock.expect(page.getUriFragment()).andReturn("!test");
        EasyMock.replay(page);

        // test manager using the mock
        Assert.assertEquals("Incorrect fragment value", "", manager.getState());
        manager.setState("test");
        Assert.assertEquals("Incorrect fragment value", "test",
                manager.getState());
    }

    @Test
    public void testListener() {
        // create mocks
        IMocksControl control = EasyMock.createControl();
        Navigator navigator = control.createMock(Navigator.class);
        Page page = control.createMock(Page.class);

        UriFragmentManager manager = new UriFragmentManager(page);
        manager.setNavigator(navigator);

        EasyMock.expect(page.getUriFragment()).andReturn("!test");
        navigator.navigateTo("test");
        control.replay();

        UriFragmentChangedEvent event = new UriFragmentChangedEvent(page,
                "oldtest");
        manager.uriFragmentChanged(event);
    }

    @Test
    public void setNavigator_someNavigatorInstance_uriFragmentChangedListenerIsRemoved() {
        TestPage page = new TestPage();

        UriFragmentManager manager = new UriFragmentManager(page);
        manager.setNavigator(EasyMock.createMock(Navigator.class));

        Assert.assertTrue(
                "addUriFragmentChangedListener() method is not called for the Page",
                page.addUriFragmentCalled());
        Assert.assertFalse(
                "removeUriFragmentChangedListener() method is called for the Page",
                page.removeUriFragmentCalled());
    }

    @Test
    public void setNavigator_nullNavigatorInstance_uriFragmentChangedListenerIsRemoved() {
        TestPage page = new TestPage();

        UriFragmentManager manager = new UriFragmentManager(page);
        manager.setNavigator(EasyMock.createMock(Navigator.class));

        manager.setNavigator(null);
        Assert.assertTrue(
                "removeUriFragmentChangedListener() method is not called for the Page",
                page.removeUriFragmentCalled());
    }

    private static class TestPage extends Page {

        public TestPage() {
            super(null, null);
        }

        @Override
        public void addUriFragmentChangedListener(
                UriFragmentChangedListener listener) {
            addUriFragmentCalled = true;
        }

        @Override
        public void removeUriFragmentChangedListener(
                UriFragmentChangedListener listener) {
            removeUriFragmentCalled = true;
        }

        boolean addUriFragmentCalled() {
            return addUriFragmentCalled;
        }

        boolean removeUriFragmentCalled() {
            return removeUriFragmentCalled;
        }

        private boolean addUriFragmentCalled;

        private boolean removeUriFragmentCalled;
    }
}
