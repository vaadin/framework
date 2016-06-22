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
package com.vaadin.tests.components.tabsheet;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test if the page scroll when press space on a tabsheet's tab.
 * 
 * @author Vaadin Ltd
 */
public class TabSpaceNotScrollTest extends MultiBrowserTest {

    @Test
    public void testScroll() throws InterruptedException, IOException {
        openTestURL();

        TestBenchElement tab = (TestBenchElement) getDriver().findElement(
                By.className("v-tabsheet-tabitemcell"));
        tab.click(10, 10);

        Point oldLocation = tab.getLocation();

        tab.sendKeys(Keys.SPACE);

        Point newLocation = tab.getLocation();

        Assert.assertEquals(oldLocation, newLocation);
    }

}
