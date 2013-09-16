/*
 * Copyright 2000-2013 Vaadin Ltd.
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

/**
 * 
 */
package com.vaadin.tests.tb3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * A {@link MultiBrowserTest} which restricts the tests to the browsers which
 * support websocket
 * 
 * @author Vaadin Ltd
 */
public abstract class WebsocketTest extends PrivateTB3Configuration {
    private static List<DesiredCapabilities> websocketBrowsers = new ArrayList<DesiredCapabilities>();
    static {
        websocketBrowsers.addAll(MultiBrowserTest.getAllBrowsers());
        websocketBrowsers.remove(BrowserUtil.ie(8));
        websocketBrowsers.remove(BrowserUtil.ie(9));
    }

    /**
     * @return All supported browsers which are actively tested and support
     *         websockets
     */
    public static Collection<DesiredCapabilities> getWebsocketBrowsers() {
        return Collections.unmodifiableCollection(websocketBrowsers);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.tb3.AbstractTB3Test#getBrowserToRunOn()
     */
    @Override
    public Collection<DesiredCapabilities> getBrowsersToTest() {
        return getWebsocketBrowsers();
    }
}
