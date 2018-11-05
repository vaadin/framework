package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PreventTabChangeTest extends MultiBrowserTest {
    @Test
    public void preventTabChange() throws Exception {
        openTestURL();

        clickTab(1);
        clickTab(2);
        Thread.sleep(2000);
        assertTabSelected(2);
        assertEquals("Tab 3 contents", getSelectedTabContent().getText());
        clickTab(0);
        clickTab(2);
        assertTabSelected(0);
        assertEquals("Tab 1 contents", getSelectedTabContent().getText());
    }

    private void assertTabSelected(int i) throws NoSuchElementException {
        WebElement tabItem = findTab(i).findElement(By.xpath(".."));
        assertTrue("Tab " + i + " should be selected but isn't", tabItem
                .getAttribute("class").contains("v-tabsheet-tabitem-selected"));
    }

    private void clickTab(int i) {
        findTab(i).click();
    }

    private WebElement findTab(int i) {
        return driver.findElement(
                com.vaadin.testbench.By.vaadin("//TabSheet#tab[" + i + "]"));
    }

    private WebElement getSelectedTabContent() {
        return driver.findElement(
                com.vaadin.testbench.By.vaadin("//TabSheet#tabpanel"));
    }

}
