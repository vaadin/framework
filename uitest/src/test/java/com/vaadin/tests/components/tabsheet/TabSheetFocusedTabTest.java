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

        assertTrue(isFocused(getTab(1)));

        new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT).perform();

        assertFalse(isFocused(getTab(1)));
        assertTrue(isFocused(getTab(3)));

        getTab(5).click();

        assertFalse(isFocused(getTab(3)));
        assertTrue(isFocused(getTab(5)));

        getTab(1).click();

        assertFalse(isFocused(getTab(5)));
        assertTrue(isFocused(getTab(1)));
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
