/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.server.navigator;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.vaadin.navigator.FragmentManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;

public class NavigatorTest extends TestCase {

    // TODO test internal parameters (and absence of them)
    // TODO test listeners blocking navigation, multiple listeners

    public void testBasicNavigation() {
        IMocksControl control = EasyMock.createControl();
        FragmentManager manager = control.createMock(FragmentManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        View view1 = control.createMock(View.class);
        View view2 = control.createMock(View.class);

        // prepare mocks: what to expect
        EasyMock.expect(provider.getViewName("test1")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("");
        view1.navigateTo(null);
        display.showView(view1);
        manager.setFragment("test1");

        EasyMock.expect(provider.getViewName("test2/")).andReturn("test2");
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        view2.init();
        EasyMock.expect(manager.getFragment()).andReturn("view1");
        view2.navigateTo(null);
        display.showView(view2);
        manager.setFragment("test2");

        EasyMock.expect(provider.getViewName("test1/params"))
                .andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("view2");
        view1.navigateTo("params");
        display.showView(view1);
        manager.setFragment("test1/params");

        control.replay();

        // create and test navigator
        Navigator navigator = new Navigator(manager, display);
        navigator.registerProvider(provider);

        navigator.navigateTo("test1");
        navigator.navigateTo("test2/");
        navigator.navigateTo("test1/params");
    }

    public void testMainView() {
        IMocksControl control = EasyMock.createControl();
        FragmentManager manager = control.createMock(FragmentManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        View view1 = control.createMock(View.class);
        View view2 = control.createMock(View.class);

        // prepare mocks: what to expect
        EasyMock.expect(provider.getViewName("test1")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("");
        view1.navigateTo(null);
        display.showView(view1);
        manager.setFragment("test1");

        EasyMock.expect(provider.getViewName("test2")).andReturn("test2");
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        view2.init();
        EasyMock.expect(manager.getFragment()).andReturn("view1");
        view2.navigateTo(null);
        display.showView(view2);
        manager.setFragment("test2");

        EasyMock.expect(provider.getViewName("test1")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("");
        view1.navigateTo(null);
        display.showView(view1);
        manager.setFragment("test1");

        EasyMock.expect(provider.getViewName("test1/params"))
                .andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("view2");
        view1.navigateTo("params");
        display.showView(view1);
        manager.setFragment("test1/params");

        control.replay();

        // create and test navigator
        Navigator navigator = new Navigator(manager, display);
        navigator.registerProvider(provider);
        // this also triggers navigation
        navigator.setMainView("test1");

        navigator.navigateTo("test2");
        navigator.navigateTo("");
        navigator.navigateTo("test1/params");
    }

    public void testListeners() {
        IMocksControl control = EasyMock.createControl();
        FragmentManager manager = control.createMock(FragmentManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        View view1 = control.createMock(View.class);
        View view2 = control.createMock(View.class);
        ViewChangeListener listener = control
                .createMock(ViewChangeListener.class);

        // prepare mocks: what to expect
        EasyMock.expect(provider.getViewName("test1")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("");
        EasyMock.expect(
                listener.isViewChangeAllowed(null, view1, "test1", null,
                        new Object[0])).andReturn(true);
        view1.navigateTo(null);
        display.showView(view1);
        manager.setFragment("test1");
        listener.navigatorViewChanged(null, view1);

        EasyMock.expect(provider.getViewName("test2")).andReturn("test2");
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        view2.init();
        EasyMock.expect(manager.getFragment()).andReturn("view1");
        EasyMock.expect(
                listener.isViewChangeAllowed(view1, view2, "test2", null,
                        new Object[0])).andReturn(true);
        view2.navigateTo(null);
        display.showView(view2);
        manager.setFragment("test2");
        listener.navigatorViewChanged(view1, view2);

        control.replay();

        // create and test navigator
        Navigator navigator = new Navigator(manager, display);
        navigator.registerProvider(provider);
        navigator.addListener(listener);

        navigator.navigateTo("test1");
        navigator.navigateTo("test2");
    }

    public void testBlockNavigation() {
        IMocksControl control = EasyMock.createControl();
        FragmentManager manager = control.createMock(FragmentManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        View view1 = control.createMock(View.class);
        View view2 = control.createMock(View.class);
        ViewChangeListener listener1 = control
                .createMock(ViewChangeListener.class);
        ViewChangeListener listener2 = control
                .createMock(ViewChangeListener.class);

        // prepare mocks: what to expect
        // first listener blocks first view change
        EasyMock.expect(provider.getViewName("test1")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("");
        EasyMock.expect(
                listener1.isViewChangeAllowed(null, view1, "test1", null,
                        new Object[0])).andReturn(false);

        // second listener blocks second view change
        EasyMock.expect(provider.getViewName("test1/test")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("");
        EasyMock.expect(
                listener1.isViewChangeAllowed(null, view1, "test1", "test",
                        new Object[0])).andReturn(true);
        EasyMock.expect(
                listener2.isViewChangeAllowed(null, view1, "test1", "test",
                        new Object[0])).andReturn(false);

        // both listeners allow view change
        EasyMock.expect(provider.getViewName("test1/bar")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        view1.init();
        EasyMock.expect(manager.getFragment()).andReturn("");
        EasyMock.expect(
                listener1.isViewChangeAllowed(null, view1, "test1", "bar",
                        new Object[0])).andReturn(true);
        EasyMock.expect(
                listener2.isViewChangeAllowed(null, view1, "test1", "bar",
                        new Object[0])).andReturn(true);
        view1.navigateTo("bar");
        display.showView(view1);
        manager.setFragment("test1/bar");
        listener1.navigatorViewChanged(null, view1);
        listener2.navigatorViewChanged(null, view1);

        // both listeners allow view change from non-null view
        EasyMock.expect(provider.getViewName("test2")).andReturn("test2");
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        view2.init();
        EasyMock.expect(manager.getFragment()).andReturn("view1");
        EasyMock.expect(
                listener1.isViewChangeAllowed(view1, view2, "test2", null,
                        new Object[0])).andReturn(true);
        EasyMock.expect(
                listener2.isViewChangeAllowed(view1, view2, "test2", null,
                        new Object[0])).andReturn(true);
        view2.navigateTo(null);
        display.showView(view2);
        manager.setFragment("test2");
        listener1.navigatorViewChanged(view1, view2);
        listener2.navigatorViewChanged(view1, view2);

        control.replay();

        // create and test navigator
        Navigator navigator = new Navigator(manager, display);
        navigator.registerProvider(provider);
        navigator.addListener(listener1);
        navigator.addListener(listener2);

        navigator.navigateTo("test1");
        navigator.navigateTo("test1/test");
        navigator.navigateTo("test1/bar");
        navigator.navigateTo("test2");
    }

}
