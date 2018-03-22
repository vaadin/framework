package com.vaadin.tests.components.popupview;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopUpViewInTabsheetTest extends MultiBrowserTest {

    @Before
    public void testPopupView() {
        openTestURL();
    }

    @Test
    public void testPopUpNotVisisble() {
        WebElement view = findElement(By.className("v-popupview"));
        view.click();
        assertTrue(
                findElement(By.className("v-popupview-popup")).isDisplayed());
        findElement(By.id("tab1")).click();
        findElement(By.id("tab0")).click();
        assertTrue(findElements(By.className("v-popupview-popup")).isEmpty());
    }
}
