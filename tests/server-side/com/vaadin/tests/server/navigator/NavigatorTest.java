/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.server.navigator;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.vaadin.navigator.FragmentManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.SimpleViewDisplay;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.tests.server.navigator.ClassBasedViewProviderTest.TestView;
import com.vaadin.tests.server.navigator.ClassBasedViewProviderTest.TestView2;
import com.vaadin.ui.Root;

public class NavigatorTest extends TestCase {

    // TODO test internal parameters (and absence of them)
    // TODO test listeners blocking navigation, multiple listeners

    public static class NullDisplay implements ViewDisplay {
        public void showView(View view) {
            // do nothing
        }
    }

    public static class NullFragmentManager implements FragmentManager {
        public String getFragment() {
            return null;
        }

        public void setFragment(String fragment) {
            // do nothing
        }
    }

    public static class TestDisplay implements ViewDisplay {
        private View currentView;

        public void showView(View view) {
            currentView = view;
        }

        public View getCurrentView() {
            return currentView;
        }
    }

    public static class TestNavigator extends Navigator {
        public TestNavigator() {
            super(new NullFragmentManager(), new TestDisplay());
        }

        public View getView(String viewAndParameters) {
            navigateTo(viewAndParameters);
            return ((TestDisplay) getDisplay()).getCurrentView();
        }
    }

    public static class ViewChangeTestListener implements ViewChangeListener {
        private final LinkedList<ViewChangeEvent> referenceEvents = new LinkedList<ViewChangeListener.ViewChangeEvent>();
        private final LinkedList<Boolean> referenceIsCheck = new LinkedList<Boolean>();
        private final LinkedList<Boolean> checkReturnValues = new LinkedList<Boolean>();

        public void addExpectedIsViewChangeAllowed(ViewChangeEvent event,
                boolean returnValue) {
            referenceIsCheck.add(true);
            referenceEvents.add(event);
            checkReturnValues.add(returnValue);
        }

        public void addExpectedNavigatorViewChange(ViewChangeEvent event) {
            referenceIsCheck.add(false);
            referenceEvents.add(event);
        }

        public boolean isReady() {
            return referenceEvents.isEmpty();
        }

        public boolean equalsReferenceEvent(ViewChangeEvent event,
                ViewChangeEvent reference) {
            if (event == null) {
                return false;
            }
            if (reference.getNavigator() != event.getNavigator()) {
                return false;
            }
            if (reference.getOldView() != event.getOldView()) {
                return false;
            }
            if (reference.getNewView() != event.getNewView()) {
                return false;
            }
            if (!stringEquals(reference.getViewName(), event.getViewName())) {
                return false;
            }
            if (!stringEquals(reference.getFragmentParameters(),
                    event.getFragmentParameters())) {
                return false;
            }
            return true;
        }

        private static boolean stringEquals(String string1, String string2) {
            if (string1 == null) {
                return string2 == null;
            } else {
                return string1.equals(string2);
            }
        }

        public boolean isViewChangeAllowed(ViewChangeEvent event) {
            if (referenceEvents.isEmpty()) {
                fail("Unexpected call to isViewChangeAllowed()");
            }
            ViewChangeEvent reference = referenceEvents.remove();
            Boolean isCheck = referenceIsCheck.remove();
            if (!isCheck) {
                fail("Expected navigatorViewChanged(), received isViewChangeAllowed()");
            }
            // here to make sure exactly the correct values are removed from
            // each queue
            Boolean returnValue = checkReturnValues.remove();
            if (!equalsReferenceEvent(event, reference)) {
                fail("View change event does not match reference event");
            }
            return returnValue;
        }

        public void navigatorViewChanged(ViewChangeEvent event) {
            if (referenceEvents.isEmpty()) {
                fail("Unexpected call to navigatorViewChanged()");
            }
            ViewChangeEvent reference = referenceEvents.remove();
            Boolean isCheck = referenceIsCheck.remove();
            if (isCheck) {
                fail("Expected isViewChangeAllowed(), received navigatorViewChanged()");
            }
            if (!equalsReferenceEvent(event, reference)) {
                fail("View change event does not match reference event");
            }
        }
    }

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
        EasyMock.expect(manager.getFragment()).andReturn("");
        view1.navigateTo(null);
        display.showView(view1);
        manager.setFragment("test1");

        EasyMock.expect(provider.getViewName("test2/")).andReturn("test2");
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        EasyMock.expect(manager.getFragment()).andReturn("view1");
        view2.navigateTo(null);
        display.showView(view2);
        manager.setFragment("test2");

        EasyMock.expect(provider.getViewName("test1/params"))
                .andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
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
        EasyMock.expect(provider.getViewName("test2")).andReturn("test2");
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        EasyMock.expect(manager.getFragment()).andReturn("view1");
        view2.navigateTo(null);
        display.showView(view2);
        manager.setFragment("test2");

        EasyMock.expect(provider.getViewName("")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getFragment()).andReturn("");
        view1.navigateTo(null);
        display.showView(view1);
        manager.setFragment("test1");

        EasyMock.expect(provider.getViewName("test1/params"))
                .andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getFragment()).andReturn("view2");
        view1.navigateTo("params");
        display.showView(view1);
        manager.setFragment("test1/params");

        control.replay();

        // create and test navigator
        Navigator navigator = new Navigator(manager, display);
        navigator.registerProvider(provider);

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
        ViewChangeTestListener listener = new ViewChangeTestListener();

        // create navigator to test
        Navigator navigator = new Navigator(manager, display);

        // prepare mocks: what to expect
        EasyMock.expect(provider.getViewName("test1")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        ViewChangeEvent event1 = new ViewChangeEvent(navigator, null, view1,
                "test1", null);
        listener.addExpectedIsViewChangeAllowed(event1, true);
        EasyMock.expect(manager.getFragment()).andReturn("");
        view1.navigateTo(null);
        display.showView(view1);
        manager.setFragment("test1");
        listener.addExpectedNavigatorViewChange(event1);

        EasyMock.expect(provider.getViewName("test2")).andReturn("test2");
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        ViewChangeEvent event2 = new ViewChangeEvent(navigator, view1, view2,
                "test2", null);
        listener.addExpectedIsViewChangeAllowed(event2, true);
        EasyMock.expect(manager.getFragment()).andReturn("view1");
        view2.navigateTo(null);
        display.showView(view2);
        manager.setFragment("test2");
        listener.addExpectedNavigatorViewChange(event2);

        control.replay();

        // test navigator
        navigator.registerProvider(provider);
        navigator.addListener(listener);

        navigator.navigateTo("test1");
        navigator.navigateTo("test2");

        if (!listener.isReady()) {
            fail("Missing listener calls");
        }
    }

    public void testBlockNavigation() {
        IMocksControl control = EasyMock.createControl();
        FragmentManager manager = control.createMock(FragmentManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        View view1 = control.createMock(View.class);
        View view2 = control.createMock(View.class);
        ViewChangeTestListener listener1 = new ViewChangeTestListener();
        ViewChangeTestListener listener2 = new ViewChangeTestListener();

        Navigator navigator = new Navigator(manager, display);

        // prepare mocks: what to expect
        // first listener blocks first view change
        EasyMock.expect(provider.getViewName("test1")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getFragment()).andReturn("");
        ViewChangeEvent event1 = new ViewChangeEvent(navigator, null, view1,
                "test1", null);
        listener1.addExpectedIsViewChangeAllowed(event1, false);

        // second listener blocks second view change
        EasyMock.expect(provider.getViewName("test1/test")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getFragment()).andReturn("");
        ViewChangeEvent event2 = new ViewChangeEvent(navigator, null, view1,
                "test1", "test");
        listener1.addExpectedIsViewChangeAllowed(event2, true);
        listener2.addExpectedIsViewChangeAllowed(event2, false);

        // both listeners allow view change
        EasyMock.expect(provider.getViewName("test1/bar")).andReturn("test1");
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getFragment()).andReturn("");
        ViewChangeEvent event3 = new ViewChangeEvent(navigator, null, view1,
                "test1", "bar");
        listener1.addExpectedIsViewChangeAllowed(event3, true);
        listener2.addExpectedIsViewChangeAllowed(event3, true);
        view1.navigateTo("bar");
        display.showView(view1);
        manager.setFragment("test1/bar");
        listener1.addExpectedNavigatorViewChange(event3);
        listener2.addExpectedNavigatorViewChange(event3);

        // both listeners allow view change from non-null view
        EasyMock.expect(provider.getViewName("test2")).andReturn("test2");
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        EasyMock.expect(manager.getFragment()).andReturn("view1");
        ViewChangeEvent event4 = new ViewChangeEvent(navigator, view1, view2,
                "test2", null);
        listener1.addExpectedIsViewChangeAllowed(event4, true);
        listener2.addExpectedIsViewChangeAllowed(event4, true);
        view2.navigateTo(null);
        display.showView(view2);
        manager.setFragment("test2");
        listener1.addExpectedNavigatorViewChange(event4);
        listener2.addExpectedNavigatorViewChange(event4);

        control.replay();

        // test navigator
        navigator.registerProvider(provider);
        navigator.addListener(listener1);
        navigator.addListener(listener2);

        navigator.navigateTo("test1");
        navigator.navigateTo("test1/test");
        navigator.navigateTo("test1/bar");
        navigator.navigateTo("test2");

        if (!listener1.isReady()) {
            fail("Missing listener calls for listener1");
        }
        if (!listener2.isReady()) {
            fail("Missing listener calls for listener2");
        }
    }

    public void testDefaultDisplayType() {
        IMocksControl control = EasyMock.createControl();
        Root root = control.createMock(Root.class);

        Navigator navigator = new Navigator(root);

        assertEquals("Default display should be a SimpleViewDisplay",
                SimpleViewDisplay.class, navigator.getDisplay().getClass());
    }

    public void testAddViewInstance() throws Exception {
        View view = new TestView();

        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", view);

        assertEquals("Registered view instance not returned by navigator",
                view, navigator.getView("test"));
    }

    public void testAddViewInstanceSameName() throws Exception {
        View view1 = new TestView();
        View view2 = new TestView2();

        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", view1);
        navigator.addView("test", view2);

        assertEquals(
                "Adding second view with same name should override previous view",
                view2, navigator.getView("test"));
    }

    public void testAddViewClass() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);

        View view = navigator.getView("test");
        assertNotNull("Received null view", view);
        assertEquals("Received incorrect type of view", TestView.class,
                view.getClass());
    }

    public void testAddViewClassSameName() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);
        navigator.addView("test", TestView2.class);

        assertEquals(
                "Adding second view class with same name should override previous view",
                TestView2.class, navigator.getView("test").getClass());
    }

    public void testAddViewInstanceAndClassSameName() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);
        TestView2 view2 = new TestView2();
        navigator.addView("test", view2);

        assertEquals(
                "Adding second view class with same name should override previous view",
                view2, navigator.getView("test"));

        navigator.addView("test", TestView.class);

        assertEquals(
                "Adding second view class with same name should override previous view",
                TestView.class, navigator.getView("test").getClass());
    }

    public void testAddViewWithNullName() throws Exception {
        Navigator navigator = new Navigator(new NullFragmentManager(),
                new NullDisplay());

        try {
            navigator.addView(null, new TestView());
            fail("addView() accepted null view name");
        } catch (IllegalArgumentException e) {
        }
        try {
            navigator.addView(null, TestView.class);
            fail("addView() accepted null view name");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddViewWithNullInstance() throws Exception {
        Navigator navigator = new Navigator(new NullFragmentManager(),
                new NullDisplay());

        try {
            navigator.addView("test", (View) null);
            fail("addView() accepted null view instance");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAddViewWithNullClass() throws Exception {
        Navigator navigator = new Navigator(new NullFragmentManager(),
                new NullDisplay());

        try {
            navigator.addView("test", (Class<View>) null);
            fail("addView() accepted null view class");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testRemoveViewInstance() throws Exception {
        View view = new TestView();

        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", view);
        navigator.removeView("test");

        assertNull("View not removed", navigator.getView("test"));
    }

    public void testRemoveViewInstanceNothingElse() throws Exception {
        View view = new TestView();
        View view2 = new TestView2();

        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", view);
        navigator.addView("test2", view2);
        navigator.removeView("test");

        assertEquals("Removed extra views", view2, navigator.getView("test2"));
    }

    public void testRemoveViewClass() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);
        navigator.removeView("test");

        assertNull("View not removed", navigator.getView("test"));
    }

    public void testRemoveViewClassNothingElse() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);
        navigator.addView("test2", TestView2.class);
        navigator.removeView("test");

        assertEquals("Removed extra views", TestView2.class,
                navigator.getView("test2").getClass());
    }

    public void testGetViewNestedNames() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test/subview", TestView2.class);
        navigator.addView("test", TestView.class);

        assertEquals("Incorrect view name found for subview string",
                TestView2.class, navigator.getView("test/subview").getClass());
        assertEquals(
                "Incorrect view name found for subview string with empty parameters",
                TestView2.class, navigator.getView("test/subview/").getClass());
        assertEquals(
                "Incorrect view name found for subview string with parameters",
                TestView2.class, navigator.getView("test/subview/parameters")
                        .getClass());
        assertEquals("Incorrect view name found for top level view string",
                TestView.class, navigator.getView("test").getClass());
        assertEquals(
                "Incorrect view name found for top level view string with empty parameters",
                TestView.class, navigator.getView("test/").getClass());
        assertEquals(
                "Incorrect view name found for top level view string with parameters starting like subview name",
                TestView.class, navigator.getView("test/subviewnothere")
                        .getClass());
    }

    public void testGetViewLongestPrefixOrder() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test/subview", TestView2.class);
        navigator.addView("test", TestView.class);

        assertEquals("Incorrect view name found", TestView.class, navigator
                .getView("test").getClass());

        // other order

        TestNavigator navigator2 = new TestNavigator();

        navigator2.addView("test", TestView.class);
        navigator2.addView("test/subview", TestView2.class);

        assertEquals("Incorrect view name found", TestView.class, navigator2
                .getView("test").getClass());
    }
}
