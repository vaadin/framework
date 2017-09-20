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

package com.vaadin.tests.tb3;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;

/**
 * Base class for tests which should be run on all supported browsers. The test
 * is automatically launched for multiple browsers in parallel by the test
 * runner.
 *
 * Sub classes can, but typically should not, restrict the browsers used by
 * implementing a
 *
 * <pre>
 * &#064;Parameters
 * public static Collection&lt;DesiredCapabilities&gt; getBrowsersForTest() {
 * }
 * </pre>
 *
 * @author Vaadin Ltd
 */
public abstract class MultiBrowserTest extends PrivateTB3Configuration {

    protected List<DesiredCapabilities> getBrowsersSupportingWebSocket() {
        // No WebSocket support in PhantomJS 1
        return getBrowserCapabilities(Browser.IE11, Browser.FIREFOX,
                Browser.CHROME);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingPhantomJS() {
        return getBrowserCapabilities(Browser.IE11, Browser.CHROME,
                Browser.FIREFOX);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingIE() {
        return getBrowserCapabilities(Browser.FIREFOX, Browser.CHROME,
                Browser.PHANTOMJS);
    }

    protected List<DesiredCapabilities> getBrowsersExcludingFirefox() {
        // this is sometimes needed as the Firefox driver causes extra mouseOut
        // events that make tooltips disappear etc.
        return getBrowserCapabilities(Browser.IE11, Browser.CHROME,
                Browser.PHANTOMJS);
    }

    protected List<DesiredCapabilities> getBrowsersSupportingShiftClick() {
        return getBrowserCapabilities(Browser.IE11, Browser.CHROME);
    }

    protected List<DesiredCapabilities> getIEBrowsersOnly() {
        return getBrowserCapabilities(Browser.IE11);
    }

    protected List<DesiredCapabilities> getBrowsersSupportingContextMenu() {
        // context menu doesn't work in phantom JS
        return getBrowserCapabilities(Browser.IE11, Browser.FIREFOX,
                Browser.CHROME);
    }

    protected List<DesiredCapabilities> getBrowsersSupportingTooltip() {
        // With IEDriver, the cursor seems to jump to default position after the
        // mouse move, so we are not able to test the tooltip behaviour properly
        // unless using requireWindowFocusForIE() { return true; } .
        // See #13854.
        // On Firefox, the driver causes additional mouseOut events causing the
        // tooltip to disappear immediately. Tooltips may work in some
        // particular cases, but not in general.
        return getBrowserCapabilities(Browser.CHROME, Browser.PHANTOMJS);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE11, Browser.FIREFOX,
                Browser.CHROME, Browser.PHANTOMJS);
    }

    protected List<DesiredCapabilities> getBrowserCapabilities(
            Browser... browsers) {
        List<DesiredCapabilities> capabilities = new ArrayList<>();
        for (Browser browser : browsers) {
            capabilities.add(browser.getDesiredCapabilities());
        }
        return capabilities;
    }
}
