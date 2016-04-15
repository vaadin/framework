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
package com.vaadin.tests.components.window;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @author Vaadin Ltd
 */
public class MoveToTopTest extends MultiBrowserTest {

    @Test
    public void testBringToFrontViaHeader() throws IOException {
        openTestURL();

        WebElement firstWindow = driver.findElement(By
                .className("first-window"));

        WebElement secondWindow = driver.findElement(By
                .className("second-window"));

        secondWindow.click();

        compareScreen("second-window-over-first");

        WebElement headerFirst = firstWindow.findElement(By
                .className("v-window-outerheader"));
        headerFirst.click();

        compareScreen("first-window-over-second");
    }

}
