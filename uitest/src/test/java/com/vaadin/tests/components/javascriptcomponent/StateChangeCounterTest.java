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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class StateChangeCounterTest extends SingleBrowserTest {
    @Test
    public void testStateChanges() {
        openTestURL();

        // Expecting message from initial state change
        assertMessages("State change, counter = 0");

        $(ButtonElement.class).caption("Mark as dirty").first().click();
        // Shouldn't change anything
        assertMessages("State change, counter = 0");

        $(ButtonElement.class).caption("Send RPC").first().click();

        // Should only add an RPC message, no state change message
        assertMessages("State change, counter = 0", "RPC");

        $(ButtonElement.class).caption("Change state").first().click();

        // Should add one message, about a new state change
        assertMessages("State change, counter = 0", "RPC",
                "State change, counter = 1");
    }

    private void assertMessages(String... expectedMessages) {
        List<String> actualMessages = findElements(By.className("logRow"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList(expectedMessages), actualMessages);
    }
}
