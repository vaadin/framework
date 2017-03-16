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
package com.vaadin.tests.components.grid;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that Grid gets correct height based on height mode, and resizes
 * properly with details row if height is undefined.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridManyColumnsV7Test extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-grid"));
    }

    @Test
    public void testGridPerformance() throws InterruptedException {
        long renderingTime = testBench().totalTimeSpentRendering();
        long requestTime = testBench().totalTimeSpentServicingRequests();
        System.out.println("Grid V7 with many columns spent " + renderingTime
                + "ms rendering and " + requestTime + "ms servicing requests ("
                + getDesiredCapabilities().getBrowserName() + ")");
    }

}
