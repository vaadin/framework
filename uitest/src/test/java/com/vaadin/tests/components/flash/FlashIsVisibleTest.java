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
package com.vaadin.tests.components.flash;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class FlashIsVisibleTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // FF and PhantomJS fail at Flash and ShiftClick
        List<DesiredCapabilities> capabilities = getBrowsersSupportingShiftClick();
        // Flash support in Chrome is disabled
        capabilities.removeAll(getBrowserCapabilities(Browser.CHROME));
        return capabilities;
    }

    @Test
    public void testFlashIsCorrectlyDisplayed() throws Exception {
        openTestURL();
        /* Allow the flash plugin to load before taking the screenshot */
        sleep(5000);
        compareScreen("blue-circle");
    }
}
