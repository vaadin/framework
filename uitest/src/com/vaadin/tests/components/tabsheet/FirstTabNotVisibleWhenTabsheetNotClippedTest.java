package com.vaadin.tests.components.tabsheet;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

public class FirstTabNotVisibleWhenTabsheetNotClippedTest extends
        MultiBrowserTest {
    @Test
    public void testNotClippedTabIsVisible() throws InterruptedException {
        openTestURL();

        ButtonElement toggleNotClipped = $(ButtonElement.class).caption(
                "Toggle first not clipped tab").first();

        toggleNotClipped.click();
        TabSheetElement notClippedTabSheet = $(TabSheetElement.class).get(0);
        WebElement firstTab = notClippedTabSheet.findElement(By
                .className("v-tabsheet-tabitemcell-first"));
        String caption = firstTab.findElement(By.className("v-captiontext"))
                .getText();
        Assert.assertEquals("Tab with -first style should be Tab 1", "Tab 1",
                caption);

        toggleNotClipped.click();
        firstTab = notClippedTabSheet.findElement(By
                .className("v-tabsheet-tabitemcell-first"));
        caption = firstTab.findElement(By.className("v-captiontext")).getText();
        Assert.assertEquals("Tab with -first style should be Tab 0", "Tab 0",
                caption);
    }

    @Test
    public void testShowPreviouslyHiddenTab() {
        openTestURL();

        $(ButtonElement.class).caption("show tab D").get(0).click();
        $(ButtonElement.class).caption("show tab C").get(0).click();

        WebElement firstTab = $(TabSheetElement.class).get(2).findElement(
                By.className("v-tabsheet-tabitemcell-first"));
        String firstCaption = firstTab.findElement(
                By.className("v-captiontext")).getText();

        org.junit.Assert.assertEquals("tab C", firstCaption);

        $(ButtonElement.class).caption("show tab D").get(1).click();
        $(ButtonElement.class).caption("show tab C").get(1).click();

        WebElement secondTab = $(TabSheetElement.class).get(3).findElement(
                By.className("v-tabsheet-tabitemcell-first"));
        String secondCaption = secondTab.findElement(
                By.className("v-captiontext")).getText();

        org.junit.Assert.assertEquals("tab C", secondCaption);
    }
}
