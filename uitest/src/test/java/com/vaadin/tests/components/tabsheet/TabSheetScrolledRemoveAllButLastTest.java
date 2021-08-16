package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetScrolledRemoveAllButLastTest
        extends MultiBrowserTest {

    @Test
    public void closeTabs() {
        openTestURL("debug");
        WebElement firstTab = findElement(
                By.className("v-tabsheet-tabitemcell-first"));
        assertNotEquals(
                "Tab bar should be scrolled, unexpected first visible tab",
                "Tab #1", firstTab.getText());

        $(ButtonElement.class).first().click();
        assertEquals("Unexpected error notification(s),", 0,
                findElements(By.className("v-Notification-error")).size());
    }
}
