package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetFocusedTabTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return TabsheetScrolling.class;
    }

    @Test
    public void clickingChangesFocusedTab() throws Exception {
        openTestURL();

        getTab(1).click();

        assertTrue("Tab 1 should have been focused but wasn't.",
                isFocused(getTab(1)));

        new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT).perform();

        assertFalse("Tab 1 was focused but shouldn't have been.",
                isFocused(getTab(1)));
        assertTrue("Tab 3 should have been focused but wasn't.",
                isFocused(getTab(3)));

        getTab(5).click();

        assertFalse("Tab 3 was focused but shouldn't have been.",
                isFocused(getTab(3)));
        assertTrue("Tab 5 should have been focused but wasn't.",
                isFocused(getTab(5)));

        getTab(1).click();

        assertFalse("Tab 5 was focused but shouldn't have been.",
                isFocused(getTab(5)));
        assertTrue("Tab 1 should have been focused but wasn't.",
                isFocused(getTab(1)));
    }

    @Test
    public void scrollingChangesFocusedTab() {
        openTestURL();

        getTab(7).click();

        assertTrue("Tab 7 should have been focused but wasn't.",
                isFocused(getTab(7)));

        findElement(By.className("v-tabsheet-scrollerNext")).click();

        assertFalse("Tab 7 was focused but shouldn't have been.",
                isFocused(getTab(7)));
        assertTrue("Tab 3 should have been focused but wasn't.",
                isFocused(getTab(3)));

        new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT).perform();

        assertFalse("Tab 3 was focused but shouldn't have been.",
                isFocused(getTab(3)));
        assertTrue("Tab 5 should have been focused but wasn't.",
                isFocused(getTab(5)));
    }

    private WebElement getTab(int index) {
        return getDriver().findElement(By.xpath(
                "(//table[contains(@class, 'v-tabsheet-tabs')])[1]/tbody/tr/td["
                        + (index + 1) + "]/div"));
    }

    private boolean isFocused(WebElement tab) {
        return tab.getAttribute("class").contains("v-tabsheet-tabitem-focus");
    }

}
