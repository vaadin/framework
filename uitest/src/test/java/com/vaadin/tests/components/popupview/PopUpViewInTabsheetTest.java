package com.vaadin.tests.components.popupview;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopUpViewInTabsheetTest extends MultiBrowserTest {

    private static final String FIRST_TAB = "//*[@id = 'tab0']";
    private static final String SECOND_TAB = "//*[@id = 'tab1']";

    WebElement view;

    @Before
    public void testPopupView() {
        openTestURL();
        view = findElement(By.className("v-popupview"));
    }

    @Test
    public void testPopUpNotVisisble() {
        view.click();
        assertTrue(
                findElement(By.className("v-popupview-popup")).isDisplayed());
        findElement(By.xpath(SECOND_TAB)).click();
        findElement(By.xpath(FIRST_TAB)).click();
        assertTrue(findElements(By.className("v-popupview-popup")).isEmpty());
    }
}
