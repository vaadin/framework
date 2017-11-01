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

package com.vaadin.tests.server.navigator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.easymock.IMocksControl;
import org.junit.Test;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ui.PageState;
import com.vaadin.tests.server.navigator.ClassBasedViewProviderTest.TestView2;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class NavigatorTest {

    private final class TestNavigationStateManager
            implements NavigationStateManager {
        private String state;

        @Override
        public void setState(String state) {
            this.state = state;
        }

        @Override
        public void setNavigator(Navigator navigator) {
        }

        @Override
        public String getState() {
            return state;
        }
    }

    // TODO test internal parameters (and absence of them)
    // TODO test listeners blocking navigation, multiple listeners

    public static class NullDisplay implements ViewDisplay {
        @Override
        public void showView(View view) {
            // do nothing
        }
    }

    public static class NullFragmentManager implements NavigationStateManager {
        @Override
        public String getState() {
            return null;
        }

        @Override
        public void setState(String fragment) {
            // do nothing
        }

        @Override
        public void setNavigator(Navigator navigator) {
            // do nothing
        }
    }

    public static class TestDisplay implements ViewDisplay {
        private View currentView;

        @Override
        public void showView(View view) {
            currentView = view;
        }

        public View getCurrentView() {
            return currentView;
        }
    }

    public static class TestNavigator extends Navigator {
        public TestNavigator() {
            super(createMockUI(), new NullFragmentManager(), new TestDisplay());
        }

        public TestNavigator(UI ui) {
            super(ui, EasyMock.createMock(ViewDisplay.class));
        }

        public View getView(String viewAndParameters) {
            try {
                navigateTo(viewAndParameters);
            } catch (IllegalArgumentException e) {
                // ignore
            }
            return ((TestDisplay) getDisplay()).getCurrentView();
        }

        @Override
        protected NavigationStateManager getStateManager() {
            return super.getStateManager();
        }
    }

    public static class TestNavigatorWithFragments extends Navigator {
        public TestNavigatorWithFragments() {
            super(createMockUI(), new NullFragmentManager(), new TestDisplay());
        }

        public TestNavigatorWithFragments(UI ui) {
            super(ui, new UriFragmentManager(ui.getPage()),
                    EasyMock.createMock(ViewDisplay.class));
        }

        public View getView(String viewAndParameters) {
            try {
                navigateTo(viewAndParameters);
            } catch (IllegalArgumentException e) {
                // ignore
            }
            return ((TestDisplay) getDisplay()).getCurrentView();
        }

        @Override
        protected NavigationStateManager getStateManager() {
            return super.getStateManager();
        }
    }

    public static class ViewChangeTestListener implements ViewChangeListener {
        private final LinkedList<ViewChangeEvent> referenceEvents = new LinkedList<>();
        private final LinkedList<Boolean> referenceIsCheck = new LinkedList<>();
        private final LinkedList<Boolean> checkReturnValues = new LinkedList<>();

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
            if (!stringEquals(reference.getParameters(),
                    event.getParameters())) {
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

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            if (referenceEvents.isEmpty()) {
                fail("Unexpected call to beforeViewChange()");
            }
            ViewChangeEvent reference = referenceEvents.remove();
            Boolean isCheck = referenceIsCheck.remove();
            if (!isCheck) {
                fail("Expected afterViewChange(), received beforeViewChange()");
            }
            // here to make sure exactly the correct values are removed from
            // each queue
            Boolean returnValue = checkReturnValues.remove();
            if (!equalsReferenceEvent(event, reference)) {
                fail("View change event does not match reference event");
            }
            return returnValue;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            if (referenceEvents.isEmpty()) {
                fail("Unexpected call to afterViewChange()");
            }
            ViewChangeEvent reference = referenceEvents.remove();
            Boolean isCheck = referenceIsCheck.remove();
            if (isCheck) {
                fail("Expected beforeViewChange(), received afterViewChange()");
            }
            if (!equalsReferenceEvent(event, reference)) {
                fail("View change event does not match reference event");
            }
        }
    }

    private static class TestUI extends UI {

        TestUI(Page page) {
            this.page = page;
        }

        @Override
        protected void init(VaadinRequest request) {
        }

        @Override
        public Page getPage() {
            return page;
        }

        private final Page page;
    }

    @PushStateNavigation
    private static class TestPushStateUI extends TestUI {

        TestPushStateUI(Page page) {
            super(page);
        }
    }

    private static class TestPage extends Page {

        public TestPage() {
            super(null, null);
        }

        @Override
        public Registration addPopStateListener(PopStateListener listener) {
            addPopstateCalled = true;
            return () -> removePopstateCalled = true;
        }

        boolean addPopstateCalled() {
            return addPopstateCalled;
        }

        boolean removePopstateCalled() {
            return removePopstateCalled;
        }

        private boolean addPopstateCalled;

        private boolean removePopstateCalled;
    }

    private static class TestPageWithUriFragments extends Page {

        public TestPageWithUriFragments() {
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

    public static ViewChangeEvent eventParametersEqual(final String expected) {
        EasyMock.reportMatcher(new IArgumentMatcher() {
            @Override
            public void appendTo(StringBuffer buffer) {
                buffer.append("paramsIs(\"").append(expected).append("\")");
            }

            @Override
            public boolean matches(Object actual) {
                return actual instanceof ViewChangeEvent && expected
                        .equals(((ViewChangeEvent) actual).getParameters());
            }
        });
        return null;
    }

    private static UI createMockUI() {
        UI ui = EasyMock.createMock(UI.class);
        ui.setNavigator(EasyMock.anyObject(Navigator.class));
        EasyMock.replay(ui);
        return ui;
    }

    private static Navigator createNavigator(NavigationStateManager manager,
            ViewDisplay display) {
        return new Navigator(createMockUI(), manager, display);
    }

    @Test
    public void testDestroy_unsetNavigatorInUIAndUriFragmentManager() {
        TestPageWithUriFragments page = new TestPageWithUriFragments();
        UI ui = new TestUI(page);

        TestNavigatorWithFragments navigator = new TestNavigatorWithFragments(
                ui);
        assertTrue("Add URI fragment Page method has not been called",
                page.addUriFragmentCalled());
        assertFalse("Unexpected remove URI fragment Page method call",
                page.removeUriFragmentCalled());
        assertNotNull("Navigator is null in UI", ui.getNavigator());

        navigator.destroy();
        assertTrue(
                "Remove URI fragment Page method has not been called after destroy",
                page.removeUriFragmentCalled());
        assertNull("Navigator is not null in UI after destroy",
                ui.getNavigator());

        try {
            page.setUriFragment("foobar", true); // This should throw
            fail("Expected null pointer exception after call uriFragmentChanged "
                    + "for destroyed navigator");
        } catch (NullPointerException e) {
            // All ok.
        }
    }

    @Test
    public void testDestroy_unsetNavigatorInUIAndPopstateManager() {
        TestPage page = new TestPage();
        UI ui = new TestPushStateUI(page);

        TestNavigator navigator = new TestNavigator(ui);
        assertTrue("Add URI fragment Page method has not been called",
                page.addPopstateCalled());
        assertFalse("Unexpected remove URI fragment Page method call",
                page.removePopstateCalled());
        assertNotNull("Navigator is null in UI", ui.getNavigator());

        navigator.destroy();
        assertTrue(
                "Remove URI fragment Page method has not been called after destroy",
                page.removePopstateCalled());
        assertNull("Navigator is not null in UI after destroy",
                ui.getNavigator());

        try {
            page.updateLocation("http://server/path/info", true, true);

            fail("Expected null pointer exception after call uriFragmentChanged "
                    + "for destroyed navigator");
        } catch (NullPointerException e) {
            // All ok.
        }
    }

    @Test
    public void testBasicNavigation() {
        IMocksControl control = EasyMock.createControl();
        NavigationStateManager manager = control
                .createMock(NavigationStateManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        TestView view1 = new TestView();
        TestView view2 = new TestView();

        // prepare mocks: what to expect
        manager.setNavigator(EasyMock.anyObject(Navigator.class));

        EasyMock.expect(provider.getViewName("test1")).andReturn("test1")
                .times(2);
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getState()).andReturn("");
        display.showView(view1);
        manager.setState("test1");
        EasyMock.expect(manager.getState()).andReturn("test1");

        EasyMock.expect(provider.getViewName("test2/")).andReturn("test2")
                .times(2);
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        EasyMock.expect(manager.getState()).andReturn("test1");
        display.showView(view2);
        manager.setState("test2");
        EasyMock.expect(manager.getState()).andReturn("test2");

        EasyMock.expect(provider.getViewName("test1/params")).andReturn("test1")
                .times(2);
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getState()).andReturn("test2");
        display.showView(view1);
        manager.setState("test1/params");
        EasyMock.expect(manager.getState()).andReturn("test1/params");

        control.replay();

        // create and test navigator
        Navigator navigator = createNavigator(manager, display);
        navigator.addProvider(provider);

        navigator.navigateTo("test1");
        assertEquals("test1", navigator.getState());
        assertEquals("", view1.getParams());
        navigator.navigateTo("test2/");
        assertEquals("test2", navigator.getState());
        assertEquals("", view2.getParams());

        navigator.navigateTo("test1/params");
        assertEquals("test1/params", navigator.getState());
        assertEquals("params", view1.getParams());
    }

    public static class TestView implements View {
        private String params;

        @Override
        public void enter(ViewChangeEvent event) {
            params = event.getParameters();
        }

        public String getParams() {
            return params;
        }
    }

    @Test
    public void testMainView() {
        IMocksControl control = EasyMock.createControl();
        NavigationStateManager manager = control
                .createMock(NavigationStateManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        TestView view1 = new TestView();
        TestView view2 = new TestView();

        // prepare mocks: what to expect
        manager.setNavigator(EasyMock.anyObject(Navigator.class));

        EasyMock.expect(provider.getViewName("test2")).andReturn("test2")
                .times(2);
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        EasyMock.expect(manager.getState()).andReturn("view1");
        display.showView(view2);
        manager.setState("test2");

        EasyMock.expect(provider.getViewName("")).andReturn("test1").times(2);
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getState()).andReturn("");
        display.showView(view1);
        manager.setState("test1");

        EasyMock.expect(provider.getViewName("test1/params")).andReturn("test1")
                .times(2);
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getState()).andReturn("test2");
        display.showView(view1);
        manager.setState("test1/params");

        control.replay();

        // create and test navigator
        Navigator navigator = createNavigator(manager, display);
        navigator.addProvider(provider);

        navigator.navigateTo("test2");
        assertEquals("", view2.getParams());
        assertEquals(null, view1.getParams());
        navigator.navigateTo("");
        assertEquals("", view1.getParams());
        navigator.navigateTo("test1/params");
        assertEquals("params", view1.getParams());
    }

    @Test
    public void testListeners() {
        IMocksControl control = EasyMock.createControl();
        NavigationStateManager manager = control
                .createMock(NavigationStateManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        TestView view1 = new TestView();
        TestView view2 = new TestView();
        ViewChangeTestListener listener = new ViewChangeTestListener();

        // create navigator to test
        Navigator navigator = createNavigator(manager, display);

        // prepare mocks: what to expect
        EasyMock.expect(provider.getViewName("test1")).andReturn("test1")
                .times(2);
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        ViewChangeEvent event1 = new ViewChangeEvent(navigator, null, view1,
                "test1", "");
        listener.addExpectedIsViewChangeAllowed(event1, true);
        EasyMock.expect(manager.getState()).andReturn("");
        display.showView(view1);
        manager.setState("test1");
        listener.addExpectedNavigatorViewChange(event1);

        EasyMock.expect(provider.getViewName("test2")).andReturn("test2")
                .times(2);
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        ViewChangeEvent event2 = new ViewChangeEvent(navigator, view1, view2,
                "test2", "");
        listener.addExpectedIsViewChangeAllowed(event2, true);
        EasyMock.expect(manager.getState()).andReturn("test1");
        display.showView(view2);
        manager.setState("test2");
        listener.addExpectedNavigatorViewChange(event2);

        control.replay();

        // test navigator
        navigator.addProvider(provider);
        navigator.addViewChangeListener(listener);

        navigator.navigateTo("test1");
        navigator.navigateTo("test2");

        if (!listener.isReady()) {
            fail("Missing listener calls");
        }
    }

    @Test
    public void testComponentContainerViewDisplay() {
        class TestView extends VerticalLayout implements View {
            @Override
            public void enter(ViewChangeEvent event) {

            }
        }

        TestView tv1 = new TestView();
        TestView tv2 = new TestView();

        VerticalLayout container = new VerticalLayout();
        ViewDisplay display = new Navigator.ComponentContainerViewDisplay(
                container);
        Navigator navigator = createNavigator(new NullFragmentManager(),
                display);

        navigator.addView("tv1", tv1);
        navigator.addView("tv2", tv2);

        navigator.navigateTo("tv1");

        assertSame(tv1, container.getComponent(0));
        assertEquals(1, container.getComponentCount());

        navigator.navigateTo("tv2");

        assertSame(tv2, container.getComponent(0));
        assertEquals(1, container.getComponentCount());
    }

    @Test
    public void testBlockNavigation() {
        IMocksControl control = EasyMock.createControl();
        NavigationStateManager manager = control
                .createMock(NavigationStateManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);
        ViewProvider provider = control.createMock(ViewProvider.class);
        TestView view1 = new TestView();
        TestView view2 = new TestView();
        ViewChangeTestListener listener1 = new ViewChangeTestListener();
        ViewChangeTestListener listener2 = new ViewChangeTestListener();

        Navigator navigator = createNavigator(manager, display);

        // prepare mocks: what to expect
        // first listener blocks first view change
        EasyMock.expect(provider.getViewName("test1")).andReturn("test1")
                .times(2);
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getState()).andReturn("");
        ViewChangeEvent event1 = new ViewChangeEvent(navigator, null, view1,
                "test1", "");
        listener1.addExpectedIsViewChangeAllowed(event1, false);

        // second listener blocks second view change
        EasyMock.expect(provider.getViewName("test1/test")).andReturn("test1")
                .times(2);
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getState()).andReturn("");
        ViewChangeEvent event2 = new ViewChangeEvent(navigator, null, view1,
                "test1", "test");
        listener1.addExpectedIsViewChangeAllowed(event2, true);
        listener2.addExpectedIsViewChangeAllowed(event2, false);

        // both listeners allow view change
        EasyMock.expect(provider.getViewName("test1/bar")).andReturn("test1")
                .times(2);
        EasyMock.expect(provider.getView("test1")).andReturn(view1);
        EasyMock.expect(manager.getState()).andReturn("");
        ViewChangeEvent event3 = new ViewChangeEvent(navigator, null, view1,
                "test1", "bar");
        listener1.addExpectedIsViewChangeAllowed(event3, true);
        listener2.addExpectedIsViewChangeAllowed(event3, true);
        display.showView(view1);
        manager.setState("test1/bar");
        listener1.addExpectedNavigatorViewChange(event3);
        listener2.addExpectedNavigatorViewChange(event3);

        // both listeners allow view change from non-null view
        EasyMock.expect(provider.getViewName("test2")).andReturn("test2")
                .times(2);
        EasyMock.expect(provider.getView("test2")).andReturn(view2);
        EasyMock.expect(manager.getState()).andReturn("view1");
        ViewChangeEvent event4 = new ViewChangeEvent(navigator, view1, view2,
                "test2", "");
        listener1.addExpectedIsViewChangeAllowed(event4, true);
        listener2.addExpectedIsViewChangeAllowed(event4, true);
        display.showView(view2);
        manager.setState("test2");
        listener1.addExpectedNavigatorViewChange(event4);
        listener2.addExpectedNavigatorViewChange(event4);

        control.replay();

        // test navigator
        navigator.addProvider(provider);
        navigator.addViewChangeListener(listener1);
        navigator.addViewChangeListener(listener2);

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

    @Test
    public void testAddViewInstance() throws Exception {
        View view = new TestView();

        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", view);

        assertEquals("Registered view instance not returned by navigator", view,
                navigator.getView("test"));
    }

    @Test
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

    @Test
    public void testAddViewClass() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);

        View view = navigator.getView("test");
        assertNotNull("Received null view", view);
        assertEquals("Received incorrect type of view", TestView.class,
                view.getClass());
    }

    @Test
    public void testAddViewClassSameName() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);
        navigator.addView("test", TestView2.class);

        assertEquals(
                "Adding second view class with same name should override previous view",
                TestView2.class, navigator.getView("test").getClass());
    }

    @Test
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

    @Test
    public void testAddViewWithNullName() throws Exception {
        Navigator navigator = new TestNavigator();

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

    @Test(expected = IllegalArgumentException.class)
    public void testAddViewWithNullInstance() throws Exception {
        Navigator navigator = new TestNavigator();

        navigator.addView("test", (View) null);
        fail("addView() accepted null view instance");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddViewWithNullClass() throws Exception {
        Navigator navigator = new TestNavigator();

        navigator.addView("test", (Class<View>) null);
        fail("addView() accepted null view class");
    }

    @Test
    public void testRemoveViewInstance() throws Exception {
        View view = new TestView();

        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", view);
        navigator.removeView("test");

        assertNull("View not removed", navigator.getView("test"));
    }

    @Test
    public void testRemoveViewInstanceNothingElse() throws Exception {
        View view = new TestView();
        View view2 = new TestView2();

        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", view);
        navigator.addView("test2", view2);
        navigator.removeView("test");

        assertEquals("Removed extra views", view2, navigator.getView("test2"));
    }

    @Test
    public void testRemoveViewClass() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);
        navigator.removeView("test");

        assertNull("View not removed", navigator.getView("test"));
    }

    @Test
    public void testRemoveViewClassNothingElse() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test", TestView.class);
        navigator.addView("test2", TestView2.class);
        navigator.removeView("test");

        assertEquals("Removed extra views", TestView2.class,
                navigator.getView("test2").getClass());
    }

    @Test
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
                TestView2.class,
                navigator.getView("test/subview/parameters").getClass());
        assertEquals("Incorrect view name found for top level view string",
                TestView.class, navigator.getView("test").getClass());
        assertEquals(
                "Incorrect view name found for top level view string with empty parameters",
                TestView.class, navigator.getView("test/").getClass());
        assertEquals(
                "Incorrect view name found for top level view string with parameters starting like subview name",
                TestView.class,
                navigator.getView("test/subviewnothere").getClass());
    }

    @Test
    public void testGetViewLongestPrefixOrder() throws Exception {
        TestNavigator navigator = new TestNavigator();

        navigator.addView("test/subview", TestView2.class);
        navigator.addView("test", TestView.class);

        assertEquals("Incorrect view name found", TestView.class,
                navigator.getView("test").getClass());

        // other order

        TestNavigator navigator2 = new TestNavigator();

        navigator2.addView("test", TestView.class);
        navigator2.addView("test/subview", TestView2.class);

        assertEquals("Incorrect view name found", TestView.class,
                navigator2.getView("test").getClass());
    }

    @Test
    public void testNavigateToUnknownView() {
        TestNavigator navigator = new TestNavigator();

        TestView errorView = new TestView();

        try {
            navigator.navigateTo("doesnotexist");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
        }

        navigator.setErrorView(errorView);
        navigator.navigateTo("doesnotexist");

        TestView testView = new TestView();

        navigator.addView("doesnotexist", testView);
        navigator.navigateTo("doesnotexist");
        TestView errorView2 = new TestView();

        ViewProvider errorProvider = EasyMock.createMock(ViewProvider.class);
        EasyMock.expect(errorProvider.getView("doesnotexist2"))
                .andReturn(errorView2);
        EasyMock.expect(errorProvider.getViewName("doesnotexist2"))
                .andReturn("doesnotexist2");
        EasyMock.replay(errorProvider);

        navigator.setErrorProvider(errorProvider);
        navigator.navigateTo("doesnotexist2");
    }

    @Test
    public void testShowViewEnterOrder() {
        IMocksControl control = EasyMock.createStrictControl();

        View view = control.createMock(View.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);

        display.showView(view);
        view.enter(EasyMock.anyObject(ViewChangeEvent.class));

        control.replay();

        NavigationStateManager manager = EasyMock
                .createNiceMock(NavigationStateManager.class);
        EasyMock.replay(manager);

        Navigator navigator = new Navigator(createMockUI(), manager, display);
        navigator.addView("view", view);
        navigator.navigateTo("view");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullViewProvider() {
        IMocksControl control = EasyMock.createControl();
        NavigationStateManager manager = control
                .createMock(NavigationStateManager.class);
        ViewDisplay display = control.createMock(ViewDisplay.class);

        // create navigator to test
        Navigator navigator = createNavigator(manager, display);

        navigator.addProvider(null);
        fail("Should not be allowed to add a null view provider");

    }

    @Test
    public void testNavigateTo_navigateSameUriTwice_secondNavigationDoesNothing() {
        NavigationStateManager manager = new TestNavigationStateManager();

        final String viewName = "view";

        final View view = EasyMock.createMock(View.class);
        ViewProvider provider = new ViewProvider() {

            @Override
            public String getViewName(String viewAndParameters) {
                return viewName;
            }

            @Override
            public View getView(String viewName) {
                return view;
            }

        };

        final int[] count = { 0 };
        Navigator navigator = new Navigator(createMockUI(), manager,
                EasyMock.createMock(ViewDisplay.class)) {
            @Override
            protected void navigateTo(View view, String viewName,
                    String parameters) {
                count[0] += 1;
                super.navigateTo(view, viewName, parameters);
            }
        };
        navigator.addProvider(provider);

        // First time navigation
        navigator.navigateTo(viewName);
        assertEquals(1, count[0]);

        // Second time navigation to the same view
        navigator.navigateTo(viewName);
        assertEquals(1, count[0]);
    }

    public static class ViewIsNotAComponent implements View {

        private HorizontalLayout layout = new HorizontalLayout(
                new Label("Hello"));

        @Override
        public Component getViewComponent() {
            return layout;
        }

        @Override
        public void enter(ViewChangeEvent event) {

        }
    }

    @Test
    public void viewWhichIsNotAComponent() {
        UI ui = new UI() {

            private Page page;
            {
                page = new Page(this, new PageState()) {
                    private String fragment = "";

                    @Override
                    public String getUriFragment() {
                        return fragment;
                    };

                    @Override
                    public void setUriFragment(String newUriFragment,
                            boolean fireEvents) {
                        fragment = newUriFragment;
                    };
                };
            }

            @Override
            protected void init(VaadinRequest request) {
            }

            @Override
            public Page getPage() {
                return page;
            }
        };

        Navigator navigator = new Navigator(ui, ui);
        ui.setNavigator(navigator);
        navigator.addView("foo", ViewIsNotAComponent.class);
        navigator.navigateTo("foo");

        assertEquals(HorizontalLayout.class, ui.getContent().getClass());
        assertEquals("Hello",
                ((Label) ((HorizontalLayout) ui.getContent()).getComponent(0))
                        .getValue());
    }

    @Test
    public void parameterMap_noViewSeparator() {
        Navigator navigator = createNavigatorWithState("fooview");
        assertTrue(navigator.getStateParameterMap().isEmpty());
        assertTrue(navigator.getStateParameterMap("foo").isEmpty());
    }

    @Test
    public void parameterMap_noParameters() {
        Navigator navigator = createNavigatorWithState("fooview/");
        assertTrue(navigator.getStateParameterMap().isEmpty());
    }

    @Test
    public void parameterMap_oneParameterNoValue() {
        Navigator navigator = createNavigatorWithState("fooview/bar");
        assertMap(navigator.getStateParameterMap(), entry("bar", ""));
    }

    @Test
    public void parameterMap_oneParameterNoValueButEquals() {
        Navigator navigator = createNavigatorWithState("fooview/bar=");
        assertMap(navigator.getStateParameterMap(), entry("bar", ""));
    }

    @Test
    public void parameterMap_oneParameterWithValue() {
        Navigator navigator = createNavigatorWithState("fooview/bar=baz");
        assertMap(navigator.getStateParameterMap(), entry("bar", "baz"));
    }

    @Test
    public void parameterMap_twoParameters() {
        Navigator navigator = createNavigatorWithState("fooview/foo=bar&baz");
        assertMap(navigator.getStateParameterMap(), entry("foo", "bar"),
                entry("baz", ""));
    }

    @Test
    public void parameterMap_customSeparator() {
        Navigator navigator = createNavigatorWithState("fooview/foo=bar&baz");
        assertMap(navigator.getStateParameterMap("a"), entry("foo", "b"),
                entry("r&b", ""), entry("z", ""));
    }

    @SafeVarargs
    private final void assertMap(Map<String, String> map,
            Entry<String, String>... entries) {
        assertEquals(entries.length, map.size());
        for (Entry<String, String> entry : entries) {
            assertTrue(
                    "Map should contain a key called '" + entry.getKey() + "'",
                    map.containsKey(entry.getKey()));
            assertEquals(entry.getValue(), map.get(entry.getKey()));
        }

    }

    private Entry<String, String> entry(String key, String value) {
        return new Entry<String, String>() {

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public String setValue(String value) {
                throw new UnsupportedOperationException();
            }

        };
    }

    private Navigator createNavigatorWithState(String state) {
        TestNavigationStateManager manager = new TestNavigationStateManager();
        Navigator navigator = new Navigator(createMockUI(), manager,
                EasyMock.createMock(ViewDisplay.class));
        manager.setState(state);
        return navigator;
    }

    @Test
    public void parameterMapFromViewChangeEvent() {
        Navigator navigator = createNavigatorWithState("foo");
        TestView view1 = new TestView();
        TestView view2 = new TestView();
        navigator.addView("view1", view1);
        navigator.addView("view2", view2);

        AtomicReference<Map<String, String>> mapRef = new AtomicReference<>();
        AtomicReference<Map<String, String>> mapRefB = new AtomicReference<>();
        navigator.addViewChangeListener(event -> {
            mapRef.set(event.getParameterMap());
            mapRefB.set(event.getParameterMap("b"));
            return true;
        });

        navigator.navigateTo("view1");

        assertTrue(mapRef.get().isEmpty());
        assertTrue(mapRefB.get().isEmpty());
        navigator.navigateTo("view1/a&b=c&d");

        assertMap(mapRef.get(), entry("a", ""), entry("b", "c"),
                entry("d", ""));
        assertMap(mapRefB.get(), entry("a&", ""), entry("", "c&d"));
    }

    @Test
    public void view_beforeLeave_preventNavigation() {
        Navigator navigator = createNavigatorWithState("foo");
        View view1 = new View() {

            @Override
            public void enter(ViewChangeEvent event) {
            }

            @Override
            public void beforeLeave(ViewBeforeLeaveEvent event) {
                // Leaving this empty means the user can never leave
            }

        };
        View view2 = EasyMock.createMock(View.class);
        navigator.addView("view1", view1);
        navigator.addView("view2", view2);
        navigator.navigateTo("view1");
        navigator.navigateTo("view2");
        assertEquals("view1", navigator.getState());
    }

    @Test
    public void view_beforeLeave_allowNavigation() {
        Navigator navigator = createNavigatorWithState("foo");
        View view1 = new View() {

            @Override
            public void enter(ViewChangeEvent event) {
            }

            @Override
            public void beforeLeave(ViewBeforeLeaveEvent event) {
                event.navigate();
            }

        };
        View view2 = EasyMock.createMock(View.class);
        navigator.addView("view1", view1);
        navigator.addView("view2", view2);
        navigator.navigateTo("view1");
        navigator.navigateTo("view2");
        assertEquals("view2", navigator.getState());

    }

    @Test
    public void view_beforeLeave_delayNavigation() {
        Navigator navigator = createNavigatorWithState("foo");
        AtomicReference<ViewBeforeLeaveEvent> eventRef = new AtomicReference<ViewBeforeLeaveEvent>();
        View view1 = new View() {

            @Override
            public void enter(ViewChangeEvent event) {
            }

            @Override
            public void beforeLeave(ViewBeforeLeaveEvent event) {
                eventRef.set(event);
            }

        };
        View view2 = EasyMock.createMock(View.class);
        navigator.addView("view1", view1);
        navigator.addView("view2", view2);
        navigator.navigateTo("view1");
        navigator.navigateTo("view2");
        assertEquals("view1", navigator.getState());
        eventRef.get().navigate();
        assertEquals("view2", navigator.getState());

    }

    @Test
    public void navigator_invokeBeforeLeaveManually() {
        Navigator navigator = createNavigatorWithState("foo");
        AtomicReference<ViewBeforeLeaveEvent> eventRef = new AtomicReference<ViewBeforeLeaveEvent>();
        View view1 = new View() {

            @Override
            public void enter(ViewChangeEvent event) {
            }

            @Override
            public void beforeLeave(ViewBeforeLeaveEvent event) {
                eventRef.set(event);
            }

        };
        TestView view2 = new TestView();
        navigator.addView("view1", view1);
        navigator.addView("view2", view2);
        navigator.navigateTo("view1");

        AtomicInteger leaveCount = new AtomicInteger(0);
        navigator.runAfterLeaveConfirmation(() -> leaveCount.incrementAndGet());
        assertEquals(0, leaveCount.get());
        eventRef.get().navigate();
        assertEquals(1, leaveCount.get());
        assertEquals("view1", navigator.getState());
    }
}
