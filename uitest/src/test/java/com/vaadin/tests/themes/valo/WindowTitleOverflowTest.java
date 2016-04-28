package com.vaadin.tests.themes.valo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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

        assertThat(getWindowHeaderMarginRight(), is("74px"));
    }

    @Test
    public void headerMarginIsCorrectForClosable() {
        openWindow("Open Closable");

        assertThat(getWindowHeaderMarginRight(), is("37px"));
    }

    @Test
    public void headerMarginIsCorrectForResizableAndClosable() {
        openWindow("Open Resizable and Closable");

        assertThat(getWindowHeaderMarginRight(), is("74px"));
    }

    @Test
    public void headerMarginIsCorrectForNonResizableAndNonClosable() {
        openWindow("Open Non-Resizable and Non-Closable");

        assertThat(getWindowHeaderMarginRight(), is("12px"));
    }
}