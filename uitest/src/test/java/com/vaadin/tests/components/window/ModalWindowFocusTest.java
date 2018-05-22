package com.vaadin.tests.components.window;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that a modal window is focused on creation and that on closing a window
 * focus is given to underlying modal window
 *
 * @author Vaadin Ltd
 */
public class ModalWindowFocusTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    /**
     * First scenario: press button -> two windows appear, press Esc two times
     * -> all windows should be closed
     */
    @Test
    public void testModalWindowFocusTwoWindows() throws IOException {

        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();

        waitForElementPresent(By.id("windowButton"));
        assertTrue("Second window should be opened",
                findElements(By.id("windowButton")).size() == 1);

        pressEscAndWait();
        pressEscAndWait();
        assertTrue("All windows should be closed",
                findElements(By.className("v-window")).size() == 0);

    }

    /**
     * Second scenario: press button -> two windows appear, press button in the
     * 2nd window -> 3rd window appears on top, press Esc three times -> all
     * windows should be closed
     */
    @Test
    public void testModalWindowFocusPressButtonInWindow() throws IOException {

        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();

        waitForElementPresent(By.id("windowButton"));
        WebElement buttonInWindow = findElement(By.id("windowButton"));
        buttonInWindow.click();

        waitForElementPresent(By.id("window3"));
        assertTrue("Third window should be opened",
                findElements(By.id("window3")).size() == 1);

        pressEscAndWait();
        pressEscAndWait();
        pressEscAndWait();
        assertTrue("All windows should be closed",
                findElements(By.className("v-window")).size() == 0);

    }

    private void pressEscAndWait() {
        new Actions(driver).sendKeys(Keys.ESCAPE).build().perform();
        try {
            sleep(100);
        } catch (InterruptedException e) {
        }
    }

}
