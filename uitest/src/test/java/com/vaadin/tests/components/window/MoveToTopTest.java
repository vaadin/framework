package com.vaadin.tests.components.window;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @author Vaadin Ltd
 */
public class MoveToTopTest extends MultiBrowserTest {

    @Test
    public void testBringToFrontViaHeader() throws IOException {
        openTestURL();

        WebElement firstWindow = driver
                .findElement(By.className("first-window"));

        WebElement secondWindow = driver
                .findElement(By.className("second-window"));

        secondWindow.click();

        compareScreen("second-window-over-first");

        WebElement headerFirst = firstWindow
                .findElement(By.className("v-window-outerheader"));
        headerFirst.click();

        compareScreen("first-window-over-second");
    }

}
