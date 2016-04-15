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
package com.vaadin.tests.components;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class OutOfSyncTest extends MultiBrowserTest {

    @Test
    public void testClientResync() throws InterruptedException {
        openTestURL();

        // Wait for server to get rid of the Button
        sleep(1000);

        // On the first round-trip after the component has been removed, the
        // server assumes the client will remove the button. How ever (to force
        // it to be out of sync) the test UI calls markClean() on the Button to
        // make it not update with the response.
        $(ButtonElement.class).first().click();
        Assert.assertTrue(
                "Button should not have disappeared on the first click.",
                $(ButtonElement.class).exists());

        // Truly out of sync, full resync is forced.
        $(ButtonElement.class).first().click();
        Assert.assertFalse("Button should disappear with the second click.",
                $(ButtonElement.class).exists());
    }

}
