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
package com.vaadin.tests.components.grid;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class BasicEscalatorTest extends MultiBrowserTest {

    @Test
    public void testNormalHeight() throws Exception {
        openTestURL();
        compareScreen("normalHeight");
    }

    @Test
    public void testModifiedHeight() throws Exception {
        openTestURLWithTheme("reindeer-tests");
        compareScreen("modifiedHeight");
    }

    private WebElement getFirstBodyRowCell() {
        return getDriver().findElement(
                By.xpath("//tbody/tr[@class='v-escalator-row'][1]/td[1]"));
    }

    private void openTestURLWithTheme(String themeName) {
        String testUrl = getTestUrl();
        testUrl += (testUrl.contains("?")) ? "&" : "?";
        testUrl += "theme=" + themeName;
        getDriver().get(testUrl);
    }
}
