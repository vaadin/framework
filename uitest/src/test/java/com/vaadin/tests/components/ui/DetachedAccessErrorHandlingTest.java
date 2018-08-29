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
package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DetachedAccessErrorHandlingTest extends SingleBrowserTest {
    @Test
    public void testDetachedErrorHandling() {
        openTestURL();

        $(ButtonElement.class).id("simple").click();
        assertNoErrors();

        // The thing to really test here is that nothing is logged to stderr,
        // but that's not practical to detect
        $(ButtonElement.class).id("handling").click();
        assertNoErrors();
    }

    private void assertNoErrors() {
        // Reload page to trigger detach event
        openTestURL();

        $(ButtonElement.class).id("show").click();
        Assert.assertEquals(0, findElements(By.className("errorLabel")).size());
    }
}
