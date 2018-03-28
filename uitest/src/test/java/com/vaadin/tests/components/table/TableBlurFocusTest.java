package com.vaadin.tests.components.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableBlurFocusTest extends MultiBrowserTest {

    @Test
    public void testBlurAndFocus() throws InterruptedException {
        openTestURL();
        waitForElementPresent(By.className("v-button"));

        assertAnyLogText("1. variable change");
        assertEquals("Unexpected column header,", "COLUMN2",
                $(TableElement.class).first().getHeaderCell(1).getCaption());
        assertEquals("Unexpected button caption,", "click to focus",
                $(ButtonElement.class).first().getCaption());

        $(ButtonElement.class).first().click();
        assertAnyLogText("2. focus", "3. focus");

        $(TableElement.class).first().getHeaderCell(1).click();
        assertAnyLogText("3. blur", "4. blur");
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
