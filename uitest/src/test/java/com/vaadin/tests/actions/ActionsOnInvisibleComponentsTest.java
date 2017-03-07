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
package com.vaadin.tests.actions;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ActionsOnInvisibleComponentsTest extends MultiBrowserTest {
    private static final String LAST_INIT_LOG = "3. 'C' triggers a click on a visible and enabled button";

    // This method should be removed once #12785 is fixed
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE11, Browser.PHANTOMJS);
    }

    @Test
    public void testShortcutsOnInvisibleDisabledButtons() {
        openTestURL();
        Assert.assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("A");
        Assert.assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("B");
        Assert.assertEquals(LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("C");
        Assert.assertEquals("4. Click event for enabled button", getLogRow(0));
    }

    private void invokeShortcut(CharSequence key) {
        new Actions(getDriver()).sendKeys(key).perform();
    }
}
