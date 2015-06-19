/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for empty table as a DnD target: it should not throws client side
 * exception.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class DndEmptyTableTest extends MultiBrowserTest {

    @Test
    public void testDndEmptyTable() {
        setDebug(true);
        openTestURL();

        WebElement source = driver.findElement(By.className("v-ddwrapper"));
        WebElement target = driver.findElement(By.className("v-table-body"));
        Actions actions = new Actions(driver);
        actions.clickAndHold(source).moveToElement(target).release();

        assertNoErrorNotifications();
    }

}
