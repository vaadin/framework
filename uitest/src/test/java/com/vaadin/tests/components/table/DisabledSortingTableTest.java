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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledSortingTableTest extends MultiBrowserTest {

    Class<?> uiClass;

    @Override
    protected java.lang.Class<?> getUIClass() {
        return uiClass;
    };

    @Test
    public void sortingByEmptyArrayShouldClearSortingIndicator() {
        uiClass = DisabledSortingTable.class;
        openTestURL();

        assertThatFirstCellHasText("0");

        sortFirstColumnAscending();
        assertThatFirstCellHasText("4");

        disableSorting();

        sortByEmptyArray();
        assertThatFirstCellHasText("4");
    }

    private void sortFirstColumnAscending() {
        getFirstColumnHeader().click();
        waitUntilHeaderHasExpectedClass("v-table-header-cell-asc");
    }

    private TestBenchElement getFirstColumnHeader() {
        return getTable().getHeaderCell(1);
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void assertThatFirstCellHasText(String text) {
        assertThat(getTable().getCell(0, 0).getText(), is(text));
    }

    private void sortByEmptyArray() {
        $(ButtonElement.class).caption("Sort by empty array").first().click();

        waitUntilHeaderHasExpectedClass("v-table-header-cell");
    }

    private void disableSorting() {
        $(ButtonElement.class).caption("Disable sorting").first().click();
    }

    protected void waitUntilHeaderHasExpectedClass(final String className) {
        final TestBenchElement header = getFirstColumnHeader();
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return header.getAttribute("class").contains(className);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String.format("header to get class name '%s'",
                        className);
            }
        });
    }
}
