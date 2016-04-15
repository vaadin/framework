package com.vaadin.tests.components.table;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
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
