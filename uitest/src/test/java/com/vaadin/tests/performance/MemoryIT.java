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
package com.vaadin.tests.performance;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.vaadin.testbench.By;
import com.vaadin.testcategory.MeasurementTest;
import com.vaadin.tests.tb3.SingleBrowserTest;

@Category(MeasurementTest.class)
public class MemoryIT extends SingleBrowserTest {

    @Test
    public void measureMemory() {
        printTeamcityStats("grid-v8-one-item-size",
                getGridSize(GridMemory.PATH, 1));
        printTeamcityStats("grid-v7-one-item-size",
                getGridSize(CompatibilityGridMemory.PATH, 1));

        printTeamcityStats("grid-v8-100thousand-items-size",
                getGridSize(GridMemory.PATH, 100000));
        printTeamcityStats("grid-v7-100thousand-items-size",
                getGridSize(CompatibilityGridMemory.PATH, 100000));
    }

    @Override
    protected void closeApplication() {
    }

    private long getGridSize(String path, int itemsCount) {
        // Repeat until we get consecutive results within 0.1% of each other
        double lastResult = 0;
        int stableNumber = 0;
        for (int i = 0; i < 500; i++) {
            openUI(path, itemsCount);
            long currentResult = Long
                    .parseLong(findElement(By.id("memory")).getText());
            close();

            if (approx(lastResult, currentResult, 0.001)) {
                stableNumber++;
            }
            lastResult = currentResult;
            if (stableNumber == 5) {
                return currentResult;
            }
        }

        Assert.fail("Memory size does not stabilize");
        return -1;
    }

    private boolean approx(double num1, double num2, double epsilon) {
        double delta = Math.abs(num1 - num2);
        double deltaLimit = num2 * epsilon;
        return delta < deltaLimit;
    }

    private void openUI(String path, int itemsNumber) {
        getDriver().get(StringUtils.strip(getBaseURL(), "/") + path + "?items="
                + itemsNumber);
        Assert.assertTrue(isElementPresent(By.className("v-grid")));
    }

    private void close() {
        findElement(By.id("close")).click();
    }

    private void printTeamcityStats(String key, long value) {
        // ##teamcity[buildStatisticValue key=&#39;&lt;valueTypeKey&gt;&#39;
        // value=&#39;&lt;value&gt;&#39;]
        System.out.println("##teamcity[buildStatisticValue key='" + key
                + "' value='" + value + "']");

    }
}
