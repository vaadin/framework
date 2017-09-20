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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.vaadin.testbench.By;
import com.vaadin.testcategory.MeasurementTest;
import com.vaadin.tests.tb3.SingleBrowserTest;

@Category(MeasurementTest.class)
public class MemoryIT extends SingleBrowserTest {

    private static final int MAX_ITERATIONS = 20;

    @Test
    public void measureMemory() {
        performTest(GridMemory.PATH + "?items=1", "grid-v8-one-item-");
        performTest(CompatibilityGridMemory.PATH + "?items=1",
                "grid-v7-one-item-");

        performTest(GridMemory.PATH + "?items=1", "grid-v8-100thousand-items-");
        performTest(CompatibilityGridMemory.PATH + "?items=100000",
                "grid-v7-100thousand-items-");

        performTest(TreeGridMemory.PATH + "?items=1", "tree-grid-one-item-");
        performTest(TreeTableMemory.PATH + "?items=1", "tree-table-one-item-");


        performTest(TreeGridMemory.PATH + "?items=100&initiallyExpanded",
                "tree-grid-100-items-initially-expanded-");
        performTest(TreeTableMemory.PATH + "?items=100&initiallyExpanded",
                "tree-table-100-items-initially-expanded-");

        performTest(TreeGridMemory.PATH + "?items=100000",
                "tree-grid-100thousand-items-");
        performTest(TreeTableMemory.PATH + "?items=100000",
                "tree-table-100thousand-items-");
    }

    @Override
    protected void closeApplication() {
    }

    private void performTest(String path, String teamcityStatPrefix) {
        double lastResult = 0;
        int stableNumber = 0;
        List<Long> renderingTimes = new ArrayList<>();
        List<Long> requestTimes = new ArrayList<>();
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            openUI(path);
            renderingTimes.add(testBench().totalTimeSpentRendering());
            requestTimes.add(testBench().totalTimeSpentServicingRequests());
            long currentResult = Long
                    .parseLong(findElement(By.id("memory")).getText());
            close();

            if (approx(lastResult, currentResult, 0.001)) {
                stableNumber++;
            }
            lastResult = currentResult;
            if (stableNumber == 5) {
                System.out.println(
                        "Memory usage stabilized after " + i + " iterations");
                printTeamcityStats(teamcityStatPrefix + "size", currentResult);
                printTeamcityStats(teamcityStatPrefix + "rendering-time",
                        median(renderingTimes));
                printTeamcityStats(teamcityStatPrefix + "request-time",
                        median(requestTimes));
                return;
            }
            if (i == MAX_ITERATIONS) {
                Assert.fail("Memory size does not stabilize");
            }
        }
    }

    private long median(List<Long> values) {
        values.sort(Long::compareTo);
        int middle = values.size() / 2;
        if (values.size() % 2 == 1) {
            return values.get(middle);
        } else {
            return (values.get(middle - 1) + values.get(middle)) / 2;
        }
    }

    private boolean approx(double num1, double num2, double epsilon) {
        double delta = Math.abs(num1 - num2);
        double deltaLimit = num2 * epsilon;
        return delta < deltaLimit;
    }

    private void openUI(String path) {
        getDriver().get(StringUtils.strip(getBaseURL(), "/") + path);
        Assert.assertTrue(isElementPresent(By.className("v-grid"))
                || isElementPresent(By.className("v-treegrid"))
                || isElementPresent(By.className("v-table")));
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
