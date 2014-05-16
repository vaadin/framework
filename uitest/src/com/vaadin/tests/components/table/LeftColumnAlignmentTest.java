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
package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test class for issue #13399 : Left alignment should not be set explicitly
 * instead of relying on default behavior
 * 
 * @author Vaadin Ltd
 */
public class LeftColumnAlignmentTest extends MultiBrowserTest {

    @Test
    public void testLeftColumnAlignment() throws Exception {
        openTestURL();

        // Do align columns to the left
        WebElement webElement = driver.findElement(By.className("v-button"));
        webElement.click();

        Assert.assertTrue("Table caption is not aligned to the left",
                isElementPresent(By
                        .className("v-table-caption-container-align-left")));

        WebElement footer = driver.findElement(By
                .className("v-table-footer-container"));

        Assert.assertEquals("Table footer is not aligned to the left", "left",
                footer.getCssValue("text-align"));

        WebElement cell = driver.findElement(By
                .className("v-table-cell-wrapper"));

        Assert.assertEquals("Table cell is not aligned to the left", "left",
                cell.getCssValue("text-align"));
    }

}
