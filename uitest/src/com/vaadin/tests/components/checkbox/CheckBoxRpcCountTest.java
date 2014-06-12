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
package com.vaadin.tests.components.checkbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxRpcCountTest extends MultiBrowserTest {

    @Test
    public void testNumberOfRpcCalls() {
        openTestURL();

        WebElement labelElem = driver.findElement(By
                .cssSelector(".v-checkbox label"));
        WebElement inputElem = driver.findElement(By
                .cssSelector(".v-checkbox input"));
        WebElement countElem = driver.findElement(By.id("count-label"));

        // Click on the actual checkbox.
        inputElem.click();
        assertEquals("1 RPC call(s) made.", countElem.getText());

        // Click on the checkbox label.
        labelElem.click();
        assertEquals("2 RPC call(s) made.", countElem.getText());

        // Again on the label.
        labelElem.click();
        assertEquals("3 RPC call(s) made.", countElem.getText());
    }
}
