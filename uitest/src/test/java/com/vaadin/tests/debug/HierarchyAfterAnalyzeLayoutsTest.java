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
package com.vaadin.tests.debug;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Check that analyze layouts does not find problems for a trivial application.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class HierarchyAfterAnalyzeLayoutsTest extends MultiBrowserTest {

    @Test
    public void checkNoLayoutProblemsFound() throws IOException {
        setDebug(true);
        openTestURL();

        // select tab
        pressDebugWindowButton(findByXpath("//button[@title = 'Examine component hierarchy']"));

        // click "analyze layouts"
        pressDebugWindowButton(findByXpath("//button[@title = 'Check layouts for potential problems']"));

        // check that no problems found
        findByXpath("//div[text() = 'Layouts analyzed, no top level problems']");

        // check that original label still there
        findByXpath("//div[text() = 'This is a label']");
    }

    private void pressDebugWindowButton(WebElement element) {
        element.click();
        // This is for IE8, which otherwise just focuses the button.
        // This may result in duplicate events on other browsers, but they
        // should not break the test.
        element.sendKeys(" ");
    }

    private WebElement findByXpath(String path) {
        return getDriver().findElement(By.xpath(path));
    }
}
