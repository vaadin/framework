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
package com.vaadin.tests.applicationcontext;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class CleanupBrokenUITest extends SingleBrowserTest {

    @Test
    public void ensureUIDetached() {
        openTestURL();
        // UI 1 has not yet been added in UI.init where logging takes place
        Assert.assertEquals("1. UIs in session: 0", getLogRow(0));

        String url = getTestURL(getUIClass())
                .replace("restartApplication", "1");
        driver.get(url);
        // UI 1 remains in session during UI2 init where logging takes place
        Assert.assertEquals("1. UIs in session: 1", getLogRow(0));

        // At this point UI1 should be removed from the session
        driver.get(url);

        // UI 2 remains in session during UI3 init where logging takes place
        // UI 1 should have been removed
        Assert.assertEquals("1. UIs in session: 1", getLogRow(0));
    }
}
