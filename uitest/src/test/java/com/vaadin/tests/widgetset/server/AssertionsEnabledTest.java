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
package com.vaadin.tests.widgetset.server;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class AssertionsEnabledTest extends SingleBrowserTest {

    private static final String FAILING_CLASSNAME = "non-existent-widget";

    @Test
    public void testAssertionsAreEnabled() {
        setDebug(true);
        openTestURL();

        // If assertions are disabled, the AssertionFailureWidget will add a
        // label to the UI.
        Assert.assertFalse(
                "Label with classname " + FAILING_CLASSNAME
                        + " should not exist",
                isElementPresent(By.className(FAILING_CLASSNAME)));

        Assert.assertTrue("Assertion error Notification is not present",
                isElementPresent(NotificationElement.class));
    }

}
