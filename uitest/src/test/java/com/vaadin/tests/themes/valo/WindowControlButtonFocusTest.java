package com.vaadin.tests.themes.valo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowControlButtonFocusTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Arrays.asList(Browser.CHROME.getDesiredCapabilities());
    }

    @Test
    public void focusMaximize() throws IOException, InterruptedException {
        openTestURL();

        WebElement window = $(WindowElement.class).first();
        WebElement maximize = window
                .findElement(By.className("v-window-maximizebox"));

        executeScript("arguments[0].focus()", maximize);
        compareScreen(window, "maximize-focused");
    }

    @Test
    public void focusClose() throws IOException {
        openTestURL();

        WebElement window = $(WindowElement.class).first();
        WebElement close = window
                .findElement(By.className("v-window-closebox"));

        executeScript("arguments[0].focus()", close);
        compareScreen(window, "close-focused");
    }

}
