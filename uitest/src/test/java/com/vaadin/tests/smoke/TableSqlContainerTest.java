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
package com.vaadin.tests.smoke;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class TableSqlContainerTest extends MultiBrowserTest {

    @Test
    public void sqlContainerSmokeTest() {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        char ch = 'A';
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(String.valueOf(i + 1),
                    table.getCell(i, 0).getText());
            Assert.assertEquals(String.valueOf(ch) + i % 2,
                    table.getCell(i, 2).getText());
            if (i == 1) {
                ch++;
            }
        }

        table.getCell(1, 0).click();

        Assert.assertEquals("Selected: 2",
                findElement(By.id("selection")).getText());
    }

}
