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
package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class EmptyTabSheetTest extends MultiBrowserTest {
    @Test
    public void emptyTabSheet() throws Exception {
        openTestURL();

        compareScreen("empty");
    }

    @Test
    public void emptyTabSheetValo() {
        openTestURL("theme=valo");

        WebElement deco = getDriver()
                .findElement(By.className("v-tabsheet-deco"));

        assertEquals("none", deco.getCssValue("display"));
    }

}
