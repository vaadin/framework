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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class VerifyBrowserVersionTest extends MultiBrowserTest {

    private Map<DesiredCapabilities, String> expectedUserAgent = new HashMap<DesiredCapabilities, String>();

    {
        expectedUserAgent
                .put(Browser.FIREFOX.getDesiredCapabilities(),
                        "Mozilla/5.0 (Windows NT 6.1; rv:24.0) Gecko/20100101 Firefox/24.0");
        expectedUserAgent
                .put(Browser.IE8.getDesiredCapabilities(),
                        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        expectedUserAgent
                .put(Browser.IE9.getDesiredCapabilities(),
                        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
        expectedUserAgent
                .put(Browser.IE10.getDesiredCapabilities(),
                        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
        expectedUserAgent
                .put(Browser.IE11.getDesiredCapabilities(),
                        "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko");
        expectedUserAgent
                .put(Browser.CHROME.getDesiredCapabilities(),
                        "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
        expectedUserAgent
                .put(Browser.PHANTOMJS.getDesiredCapabilities(),
                        "Mozilla/5.0 (Unknown; Linux x86_64) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.7 Safari/534.34");

    }

    @Test
    public void verifyUserAgent() {
        openTestURL();
        Assert.assertEquals(expectedUserAgent.get(getDesiredCapabilities()),
                vaadinElementById("userAgent").getText());
        Assert.assertEquals("Touch device? No",
                vaadinElementById("touchDevice").getText());
    }
}
