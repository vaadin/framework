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

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class VerifyBrowserVersionTest extends MultiBrowserTest {

    @Test
    public void verifyUserAgent() {
        openTestURL();

        DesiredCapabilities desiredCapabilities = getDesiredCapabilities();

        String userAgent = vaadinElementById("userAgent").getText();
        String browserIdentifier;

        if (BrowserUtil.isChrome(getDesiredCapabilities())) {
            // Chrome version does not necessarily match the desired version
            // because of auto updates...
            browserIdentifier = getExpectedUserAgentString(getDesiredCapabilities())
                    + "44";
        } else {
            browserIdentifier = getExpectedUserAgentString(desiredCapabilities)
                    + desiredCapabilities.getVersion();
        }

        assertThat(userAgent, containsString(browserIdentifier));

        assertThat(vaadinElementById("touchDevice").getText(),
                is("Touch device? No"));
    }

    private String getExpectedUserAgentString(DesiredCapabilities dCap) {
        if (BrowserUtil.isIE(dCap)) {
            if (!BrowserUtil.isIE(dCap, 11)) {
                // IE8-10
                return "MSIE ";
            } else {
                // IE11
                return "Trident/7.0; rv:";
            }
        } else if (BrowserUtil.isFirefox(dCap)) {
            return "Firefox/";
        } else if (BrowserUtil.isChrome(dCap)) {
            return "Chrome/";
        } else if (BrowserUtil.isPhantomJS(dCap)) {
            return "PhantomJS/";
        }
        throw new UnsupportedOperationException(
                "Test is being run on unknown browser.");
    }

}
