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
package com.vaadin.tests.components.formlayout;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableInFormLayoutCausesScrollingTest extends MultiBrowserTest {

    @Test
    @Ignore
    // This test is actually testing that #7309 is NOT fixed.
    // Ignoring the test because it is not stable and it's
    // occasionally failing on browsers even when it shouldn't.
    // There's no point fixing this test before #7309 is actually fixed.
    public void pageIsNotScrolled() throws IOException {
        openTestURL();

        new Actions(driver).sendKeys(Keys.PAGE_DOWN).perform();

        $(TableElement.class).first().getCell(2, 0).click();

        compareScreen("scrolledDown");
    }
}
