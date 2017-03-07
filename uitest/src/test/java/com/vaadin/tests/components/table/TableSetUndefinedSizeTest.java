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
package com.vaadin.tests.components.table;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableSetUndefinedSizeTest extends MultiBrowserTest {
    @Test
    public void testTableShouldChangeSizeIfWidthSetToUndefined() {
        openTestURL();

        $(ButtonElement.class).caption("width 500px").first().click();

        final TableElement table = $(TableElement.class).first();
        final int previousWidth = table.getSize().getWidth();

        $(ButtonElement.class).caption("undefined width").first().click();

        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return previousWidth != table.getSize().getWidth();
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "table to change size (was: " + previousWidth + ")";
            }
        });
    }
}
