package com.vaadin.tests.components.table;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests Table Footer ClickListener
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("deprecation")
public class HeaderFooterClickLeftRightMiddleTest extends MultiBrowserTest {

    @Test
    public void testSingleClick() throws IOException {
        openTestURL();

        waitForElementPresent(By.className("v-table"));

        TableElement table = $(TableElement.class).first();

        table.getHeaderCell(0).click();
        assertAnyLogText("1. Click on header col1 using left");

        table.getFooterCell(1).click();
        assertAnyLogText("2. Click on footer col2 using left");
    }

    @Test
    public void testContextClick() {
        openTestURL();

        waitForElementPresent(By.className("v-table"));

        TableElement table = $(TableElement.class).first();

        table.getHeaderCell(0).contextClick();
        assertAnyLogText("1. Click on header col1 using right");

        table.getFooterCell(1).contextClick();
        assertAnyLogText("2. Click on footer col2 using right");
    }

    @Test
    public void testDoubleClick() {
        openTestURL();

        waitForElementPresent(By.className("v-table"));

        TableElement table = $(TableElement.class).first();

        table.getHeaderCell(0).doubleClick();
        assertAnyLogText("2. Double click on header col1 using left",
                "3. Double click on header col1 using left");

        table.getFooterCell(1).doubleClick();
        assertAnyLogText("4. Double click on footer col2 using left",
                "5. Double click on footer col2 using left",
                "6. Double click on footer col2 using left");
    }

    private void assertAnyLogText(String... texts) {
        assertThat(String.format(
                "Correct log text was not found, expected any of %s",
                Arrays.asList(texts)), logContainsAnyText(texts));
    }

    private boolean logContainsAnyText(String... texts) {
        for (String text : texts) {
            if (logContainsText(text)) {
                return true;
            }
        }
        return false;
    }
}
