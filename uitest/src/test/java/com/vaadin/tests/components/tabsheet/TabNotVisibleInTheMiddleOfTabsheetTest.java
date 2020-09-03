package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabNotVisibleInTheMiddleOfTabsheetTest extends MultiBrowserTest {
    @Test
    public void testFirstTabIsVisibleAfterBeingInvisible() {
        openTestURL();

        TabSheetElement tabSheet = $(TabSheetElement.class).first();
        List<WebElement> captionElements = tabSheet
                .findElements(By.className("v-caption"));
        int secondPosition = captionElements.get(1).getLocation().getX();
        int thirdPosition = captionElements.get(2).getLocation().getX();

        assertGreater(
                "Third tab should be further than the second tab: "
                        + thirdPosition + " vs. " + secondPosition,
                thirdPosition, secondPosition);

        toggleSecondTabVisibility();

        assertFalse("TabSheet should not have second tab visible",
                captionElements.get(1).isDisplayed());

        thirdPosition = captionElements.get(2).getLocation().getX();

        assertEquals("Third tab should be where second tab was:",
                secondPosition, thirdPosition);
    }

    private void toggleSecondTabVisibility() {
        $(ButtonElement.class).caption("Toggle second tab").first().click();
    }
}
