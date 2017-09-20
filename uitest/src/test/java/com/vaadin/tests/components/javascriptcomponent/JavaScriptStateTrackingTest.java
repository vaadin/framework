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
package com.vaadin.tests.components.javascriptcomponent;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class JavaScriptStateTrackingTest extends SingleBrowserTest {
    @Test
    public void testStateTracking() {
        openTestURL();

        // field2 should really be null instead of undefined, but that's a
        // separate issue
        assertValues(0, "initial value", "undefined");

        $(ButtonElement.class).id("setField2").click();

        assertValues(1, "initial value", "updated value 1");

        $(ButtonElement.class).id("clearField1").click();

        assertValues(2, "null", "updated value 1");

        $(ButtonElement.class).id("setField2").click();

        assertValues(3, "null", "updated value 3");
    }

    private void assertValues(int expectedCounter, String expectedField1,
            String expectedField2) {
        Assert.assertEquals(String.valueOf(expectedCounter),
                findElement(By.id("counter")).getText());
        Assert.assertEquals(String.valueOf(expectedField1),
                findElement(By.id("field1")).getText());
        Assert.assertEquals(String.valueOf(expectedField2),
                findElement(By.id("field2")).getText());
    }
}
