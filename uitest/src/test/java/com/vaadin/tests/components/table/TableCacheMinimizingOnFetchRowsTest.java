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
package com.vaadin.tests.components.table;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableCacheMinimizingOnFetchRowsTest extends MultiBrowserTest {

    @Test
    public void testCacheSize() throws InterruptedException {

        openTestURL();

        scrollToBottomOfTable();

        // the row request might vary slightly with different browsers
        String logtext1 = "requested 60 rows";
        String logtext2 = "requested 61 rows";

        assertThat("Requested cached rows did not match expected",
                logContainsText(logtext1) || logContainsText(logtext2));

    }

    private void scrollToBottomOfTable() {
        waitForElementPresent(By.className("v-button"));
        $(ButtonElement.class).first().click();
    }
}
