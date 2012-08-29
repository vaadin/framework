/*
 * Copyright 2011 Vaadin Ltd.
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

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.UriFragmentManager;
import com.vaadin.server.Page;
import com.vaadin.server.Page.FragmentChangedEvent;

public class UriFragmentManagerTest extends TestCase {

    public void testGetSetFragment() {
        Page page = EasyMock.createMock(Page.class);
        UriFragmentManager manager = new UriFragmentManager(page, null);

        // prepare mock
        EasyMock.expect(page.getFragment()).andReturn("");
        page.setFragment("test", false);
        EasyMock.expect(page.getFragment()).andReturn("test");
        EasyMock.replay(page);

        // test manager using the mock
        assertEquals("Incorrect fragment value", "", manager.getFragment());
        manager.setFragment("test");
        assertEquals("Incorrect fragment value", "test", manager.getFragment());
    }

    public void testListener() {
        // create mocks
        IMocksControl control = EasyMock.createControl();
        Navigator navigator = control.createMock(Navigator.class);
        Page page = control.createMock(Page.class);

        UriFragmentManager manager = new UriFragmentManager(page, navigator);

        EasyMock.expect(page.getFragment()).andReturn("test");
        navigator.navigateTo("test");
        control.replay();

        FragmentChangedEvent event = page.new FragmentChangedEvent(page,
                "oldtest");
        manager.fragmentChanged(event);
    }
}
