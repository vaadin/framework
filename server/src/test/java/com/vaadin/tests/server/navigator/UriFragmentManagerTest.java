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

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
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
        Assert.assertEquals("Incorrect fragment value", "", manager.getState());
        manager.setState("test");
        Assert.assertEquals("Incorrect fragment value", "test",
                manager.getState());
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
