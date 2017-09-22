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
package com.vaadin.tests.components.composite;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CompositeChainUITest extends SingleBrowserTest {

    @Test
    public void compositeRenderedAndUpdatedCorrectly() {
        openTestURL();
        LabelElement label = $(LabelElement.class).id("innermost");
        WebElement labelGrandParent = label.findElement(By.xpath("../.."));

        Assert.assertEquals("v-slot", labelGrandParent.getAttribute("class"));
        Assert.assertEquals("Label caption", label.getCaption());

        $(ButtonElement.class).caption("Update caption").first().click();
        Assert.assertEquals("Label caption - updated", label.getCaption());

    }

    @Test
    public void compositeRemovedCorrectly() {
        openTestURL("debug");
        LabelElement label = $(LabelElement.class).id("innermost");
        $(ButtonElement.class).caption("Update caption").first().click();
        Assert.assertEquals("Label caption - updated", label.getCaption());
        $(ButtonElement.class).caption("Replace with another Composite").first()
                .click();
        label = $(LabelElement.class).id("innermost");
        Assert.assertEquals("Label caption", label.getCaption());
        assertNoErrorNotifications();
    }
}
