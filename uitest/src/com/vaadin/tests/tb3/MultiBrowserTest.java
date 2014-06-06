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

package com.vaadin.tests.tb3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

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

    protected List<DesiredCapabilities> getBrowsersExcludingIE() {
        List<DesiredCapabilities> browsers = new ArrayList<DesiredCapabilities>(getAllBrowsers());
        browsers.remove(Browser.IE8.getDesiredCapabilities());
        browsers.remove(Browser.IE9.getDesiredCapabilities());
        browsers.remove(Browser.IE10.getDesiredCapabilities());
        browsers.remove(Browser.IE11.getDesiredCapabilities());

        return browsers;
    }

    public enum Browser {
        FIREFOX(BrowserUtil.firefox(24)), CHROME(BrowserUtil.chrome(33)), SAFARI(
                BrowserUtil.safari(7)), IE8(BrowserUtil.ie(8)), IE9(BrowserUtil
                .ie(9)), IE10(BrowserUtil.ie(10)), IE11(BrowserUtil.ie(11)), OPERA(
                BrowserUtil.opera(17)), PHANTOMJS(BrowserUtil.phantomJS(1));
        private DesiredCapabilities desiredCapabilities;

        private Browser(DesiredCapabilities desiredCapabilities) {
            this.desiredCapabilities = desiredCapabilities;
        }

        public DesiredCapabilities getDesiredCapabilities() {
            return desiredCapabilities;
        }
    }

    static List<DesiredCapabilities> allBrowsers = new ArrayList<DesiredCapabilities>();
    static {
        allBrowsers.add(Browser.IE8.getDesiredCapabilities());
        allBrowsers.add(Browser.IE9.getDesiredCapabilities());
        allBrowsers.add(Browser.IE10.getDesiredCapabilities());
        allBrowsers.add(Browser.IE11.getDesiredCapabilities());
        allBrowsers.add(Browser.FIREFOX.getDesiredCapabilities());
        // Uncomment once we have the capability to run on Safari 6
        // allBrowsers.add(SAFARI);
        allBrowsers.add(Browser.CHROME.getDesiredCapabilities());
        allBrowsers.add(Browser.PHANTOMJS.getDesiredCapabilities());
        // Re-enable this when it is possible to run on a modern Opera version
        // allBrowsers.add(Browser.OPERA.getDesiredCapabilities());
    }

    /**
     * @return all supported browsers which are actively tested
     */
    public static List<DesiredCapabilities> getAllBrowsers() {
        return Collections.unmodifiableList(allBrowsers);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Return a copy so sub classes can do
        // super.getBrowseresToTest().remove(something)
        return new ArrayList<DesiredCapabilities>(getAllBrowsers());
    }

}
