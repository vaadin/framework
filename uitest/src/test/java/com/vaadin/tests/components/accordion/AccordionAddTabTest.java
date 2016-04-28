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
package com.vaadin.tests.components.accordion;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for Accordion : replace widget in tab should remove old widget.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class AccordionAddTabTest extends MultiBrowserTest {

    @Test
    public void testRemoveAndAdd() {
        openTestURL();

        WebElement button = driver.findElement(By.className("v-button"));
        button.click();

        List<WebElement> panels = driver.findElements(By.className("v-panel"));

        Assert.assertEquals("Found two widgets inside one tab after "
                + "subsequent tab removal and addition", 1, panels.size());
    }

}
