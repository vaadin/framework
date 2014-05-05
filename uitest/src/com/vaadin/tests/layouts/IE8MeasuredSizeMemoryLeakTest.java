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
package com.vaadin.tests.layouts;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class IE8MeasuredSizeMemoryLeakTest extends MultiBrowserTest {

    @Test
    public void testMeasuredSizesMapCleaned() {
        openTestURL();
        Assert.assertEquals("No extra measured sizes in the beginning", 3,
                getMeasuredSizesMapSize());
        vaadinElementById("toggle").click();
        Assert.assertEquals("Measured sizes after single toggle", 204,
                getMeasuredSizesMapSize());
        vaadinElementById("toggle").click();
        Assert.assertEquals("Measured sizes cleaned on toggle", 204,
                getMeasuredSizesMapSize());
    }

    private int getMeasuredSizesMapSize() {
        JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();
        Number result = (Number) jsExec
                .executeScript("return window.vaadin.getMeasuredSizesCount();");
        return result.intValue();
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Collections.singletonList(Browser.IE8.getDesiredCapabilities());
    }
}
