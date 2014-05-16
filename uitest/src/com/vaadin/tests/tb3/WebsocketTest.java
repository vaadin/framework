/*
 * Copyright 2000-2014 Vaadin Ltd.
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
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest.Browser;

/**
 * A {@link MultiBrowserTest} which restricts the tests to the browsers which
 * support websocket
 * 
 * @author Vaadin Ltd
 */
@TestCategory("push")
public abstract class WebsocketTest extends PrivateTB3Configuration {
    private static List<DesiredCapabilities> websocketBrowsers = new ArrayList<DesiredCapabilities>();
    static {
        websocketBrowsers.addAll(MultiBrowserTest.getAllBrowsers());
        websocketBrowsers.remove(Browser.IE8.getDesiredCapabilities());
        websocketBrowsers.remove(Browser.IE9.getDesiredCapabilities());
        websocketBrowsers.remove(Browser.PHANTOMJS.getDesiredCapabilities());
    }

    /**
     * @return All supported browsers which are actively tested and support
     *         websockets
     */
    public static List<DesiredCapabilities> getWebsocketBrowsers() {
        return Collections.unmodifiableList(websocketBrowsers);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.tb3.AbstractTB3Test#getBrowserToRunOn()
     */
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return new ArrayList<DesiredCapabilities>(getWebsocketBrowsers());
    }
}
