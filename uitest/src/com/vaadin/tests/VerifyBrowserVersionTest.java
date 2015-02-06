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
package com.vaadin.tests;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class VerifyBrowserVersionTest extends MultiBrowserTest {

    private Map<DesiredCapabilities, String> expectedUserAgent = new HashMap<DesiredCapabilities, String>();

    {
        expectedUserAgent.put(Browser.FIREFOX.getDesiredCapabilities(), "Firefox/");
        expectedUserAgent.put(Browser.IE8.getDesiredCapabilities(), "MSIE ");
        expectedUserAgent.put(Browser.IE9.getDesiredCapabilities(), "MSIE ");
        expectedUserAgent.put(Browser.IE10.getDesiredCapabilities(), "MSIE ");
        expectedUserAgent.put(Browser.IE11.getDesiredCapabilities(), "Trident/7.0; rv:");
        expectedUserAgent.put(Browser.CHROME.getDesiredCapabilities(), "Chrome/");
        expectedUserAgent.put(Browser.PHANTOMJS.getDesiredCapabilities(), "PhantomJS/");
    }

    @Test
    public void verifyUserAgent() {
        openTestURL();

        DesiredCapabilities desiredCapabilities = getDesiredCapabilities();

        assertThat(vaadinElementById("userAgent").getText(),
            containsString(expectedUserAgent.get(desiredCapabilities)
                                         + desiredCapabilities.getVersion()));

        assertThat(vaadinElementById("touchDevice").getText(),
                                                        is("Touch device? No"));
    }

}
