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
package com.vaadin.tests.serialization;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NoLayoutTest extends MultiBrowserTest {
    @Test
    public void testNoLayout() {
        openTestURL();
        assertCounts(1, 0);

        $(CheckBoxElement.class).caption("UI polling enabled").first()
                .findElement(By.tagName("input")).click();

        // Toggling check box requires layout
        assertCounts(2, 0);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Count should not change even with polling enabled
        assertCounts(2, 0);

        // Disable polling
        $(CheckBoxElement.class).caption("UI polling enabled").first()
                .findElement(By.tagName("input")).click();
        // Toggling checkbox layotus again
        assertCounts(3, 0);

        $(ButtonElement.class).caption("Change regular state").first().click();
        // Updating normal state layouts
        assertCounts(4, 0);

        $(ButtonElement.class).caption("Change @NoLayout state").first();
        // Updating @NoLayout state does not layout
        assertCounts(4, 0);

        $(ButtonElement.class).caption("Do regular RPC").first().click();
        // Doing normal RPC layouts
        assertCounts(5, 0);

        $(ButtonElement.class).caption("Do @NoLayout RPC").first().click();
        // Doing @NoLayout RPC does not layout, but updates the RPC count
        assertCounts(5, 1);

        $(ButtonElement.class).caption("Update LegacyComponent").first()
                .click();
        // Painting LegacyComponent layouts
        assertCounts(6, 1);
    }

    private void assertCounts(int layoutCount, int rpcCount) {
        Assert.assertEquals("Unexpected layout count", layoutCount,
                getCount("layoutCount"));
        Assert.assertEquals("Unexpected RPC count", rpcCount,
                getCount("rpcCount"));
    }

    private int getCount(String id) {
        return Integer.parseInt(getDriver().findElement(By.id(id)).getText());
    }
}
