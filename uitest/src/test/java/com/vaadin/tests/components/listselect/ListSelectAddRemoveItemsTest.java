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
package com.vaadin.tests.components.listselect;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ListSelectAddRemoveItemsTest extends SingleBrowserTest {
    @Test
    public void testAddAndRemove() {
        openTestURL();
        assertOptions("a", "b", "c");

        click("Add first");
        assertOptions("first", "a", "b", "c");

        click("Swap");
        assertOptions("c", "a", "b", "first");

        click("Remove first");
        assertOptions("a", "b", "first");

        click("Add middle");
        assertOptions("a", "middle", "b", "first");

        click("Add last");
        assertOptions("a", "middle", "b", "first", "last");

        click("Remove middle");
        assertOptions("a", "middle", "first", "last");

        click("Reset");
        assertOptions("a", "b", "c");
    }

    private void assertOptions(String... options) {
        Assert.assertEquals(Arrays.asList(options),
                $(ListSelectElement.class).first().getOptions());
    }

    private void click(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }
}
