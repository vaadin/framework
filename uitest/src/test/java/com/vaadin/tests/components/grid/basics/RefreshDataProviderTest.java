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
package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class RefreshDataProviderTest extends MultiBrowserTest {

    @Test
    public void updateFirstRow() {
        openTestURL();

        findElement(By.id("update")).click();
        WebElement first = findElement(By.tagName("td"));
        assertEquals("UI component is not refreshed after update in data",
                "Updated coordinates", first.getText());
    }

    @Test
    public void addFirstRow() {
        openTestURL();

        findElement(By.id("add")).click();
        WebElement first = findElement(By.tagName("td"));

        assertEquals("UI component is not refreshed after add new data",
                "Added", first.getText());
    }

    @Test
    public void removeFirstRow() {
        openTestURL();

        WebElement first = findElement(By.tagName("td"));
        String old = first.getText();
        first = findElement(By.id("remove"));
        assertNotEquals("UI component is not refreshed after removal", old,
                first.getText());
    }

}
