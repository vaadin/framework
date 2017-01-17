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
