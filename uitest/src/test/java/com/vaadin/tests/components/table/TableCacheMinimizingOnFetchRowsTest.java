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
