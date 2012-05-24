/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.server.navigator;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.UriFragmentManager;
import com.vaadin.ui.Root;
import com.vaadin.ui.Root.FragmentChangedEvent;

public class UriFragmentManagerTest extends TestCase {

    public void testGetSetFragment() {
        Root root = EasyMock.createMock(Root.class);
        UriFragmentManager manager = new UriFragmentManager(root, null);

        // prepare mock
        EasyMock.expect(root.getFragment()).andReturn("");
        root.setFragment("test");
        EasyMock.expect(root.getFragment()).andReturn("test");
        EasyMock.replay(root);

        // test manager using the mock
        assertEquals("Incorrect fragment value", "", manager.getFragment());
        manager.setFragment("test");
        assertEquals("Incorrect fragment value", "test", manager.getFragment());
    }

    public void testListener() {
        // create mocks
        IMocksControl control = EasyMock.createControl();
        Navigator navigator = control.createMock(Navigator.class);
        Root root = control.createMock(Root.class);

        UriFragmentManager manager = new UriFragmentManager(root, navigator);

        EasyMock.expect(root.getFragment()).andReturn("test");
        navigator.navigateTo("test");
        control.replay();

        FragmentChangedEvent event = root.new FragmentChangedEvent(root,
                "oldtest");
        manager.fragmentChanged(event);
    }
}
