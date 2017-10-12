package com.vaadin.tests.themes.valo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowTitleOverflowTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    private void openWindow(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

    private String getWindowHeaderMarginRight() {
        return getWindowHeader().getCssValue("margin-right");
    }

    private WebElement getWindowHeader() {
        return findElement(By.className("v-window-header"));
    }

    @Test
    public void headerMarginIsCorrectForResizable() {
        openWindow("Open Resizable");

        assertEquals("74px", getWindowHeaderMarginRight());
    }

    @Test
    public void headerMarginIsCorrectForClosable() {
        openWindow("Open Closable");

        assertEquals("37px", getWindowHeaderMarginRight());
    }

    @Test
    public void headerMarginIsCorrectForResizableAndClosable() {
        openWindow("Open Resizable and Closable");

        assertEquals("74px", getWindowHeaderMarginRight());
    }

    @Test
    public void headerMarginIsCorrectForNonResizableAndNonClosable() {
        openWindow("Open Non-Resizable and Non-Closable");

        assertEquals("12px", getWindowHeaderMarginRight());
    }
}
