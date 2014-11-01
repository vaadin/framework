package com.vaadin.tests.themes.valo;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.*;
import org.junit.*;
import org.openqa.selenium.*;

import java.io.*;

public class TableSortIndicatorTest extends MultiBrowserTest {

    private void clickOnCellHeader() {
        clickElementByClass("v-table-header-cell");
    }

    @Test
    public void ascendingIndicatorIsShown() throws IOException {
        openTestURL();

        clickOnCellHeader();

        compareScreen("ascending");
    }

    @Test
    public void descendingIndicatorIsShown() throws IOException {
        openTestURL();

        clickOnCellHeader();
        clickOnSortIndicator();

        compareScreen("descending");
    }

    private void clickOnSortIndicator() {
        clickElementByClass("v-table-sort-indicator");
    }

    private void clickElementByClass(String className) {
        findElementByClass(className).click();
    }

    private WebElement findElementByClass(String className) {
        return driver.findElement(By.className(className));
    }
}