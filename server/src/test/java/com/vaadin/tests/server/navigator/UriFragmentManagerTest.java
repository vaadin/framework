package com.vaadin.tests.server.navigator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.UriFragmentManager;
import com.vaadin.server.Page;
import com.vaadin.shared.Registration;

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
        assertEquals("Incorrect fragment value", "", manager.getState());
        manager.setState("test");
        assertEquals("Incorrect fragment value", "test", manager.getState());
    }

    @Test
    public void testListener() {
        // create mocks
        IMocksControl control = EasyMock.createNiceControl();
        Navigator navigator = control.createMock(Navigator.class);
        Page page = control.createMock(Page.class);

        UriFragmentManager manager = new UriFragmentManager(page);
        manager.setNavigator(navigator);
        control.resetToNice();

        EasyMock.expect(page.getUriFragment()).andReturn("!test");
        navigator.navigateTo("test");
        control.replay();

        page.setUriFragment("oldtest", true);
    }

    @Test
    public void setNavigator_someNavigatorInstance_uriFragmentChangedListenerIsRemoved() {
        TestPage page = new TestPage();

        UriFragmentManager manager = new UriFragmentManager(page);
        manager.setNavigator(EasyMock.createMock(Navigator.class));

        assertTrue(
                "addUriFragmentChangedListener() method is not called for the Page",
                page.addUriFragmentCalled());
        assertFalse(
                "removeUriFragmentChangedListener() method is called for the Page",
                page.removeUriFragmentCalled());
    }

    @Test
    public void setNavigator_nullNavigatorInstance_uriFragmentChangedListenerIsRemoved() {
        TestPage page = new TestPage();

        UriFragmentManager manager = new UriFragmentManager(page);
        manager.setNavigator(EasyMock.createMock(Navigator.class));

        manager.setNavigator(null);
        assertTrue(
                "removeUriFragmentChangedListener() method is not called for the Page",
                page.removeUriFragmentCalled());
    }

    private static class TestPage extends Page {

        public TestPage() {
            super(null, null);
        }

        @Override
        public Registration addUriFragmentChangedListener(
                UriFragmentChangedListener listener) {
            addUriFragmentCalled = true;
            return () -> removeUriFragmentCalled = true;
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
