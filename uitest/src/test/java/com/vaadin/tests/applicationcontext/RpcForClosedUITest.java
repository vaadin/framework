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
package com.vaadin.tests.applicationcontext;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RpcForClosedUITest extends MultiBrowserTest {
    @Override
    protected Class<?> getUIClass() {
        return CloseUI.class;
    }

    @Test
    public void testRpcForUIClosedInBackground() throws Exception {
        openTestURL();
        /* Close the UI in a background thread */
        clickButton("Close UI (background)");
        /* Try to log 'hello' */
        clickButton("Log 'hello'");
        /* Ensure 'hello' was not logged */
        checkLogMatches("2. Current WrappedSession id: .*");
        Assert.assertFalse("Page contains word 'Hello'",
                driver.getPageSource().contains("Hello"));
    }

    private void clickButton(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

    private void checkLogMatches(String expected) {
        String actual = getLogRow(0);
        Assert.assertTrue(String.format(
                "Unexpected log row.\n expected format: '%s'\n was: '%s'",
                expected, actual), actual.matches(expected));
    }
}
