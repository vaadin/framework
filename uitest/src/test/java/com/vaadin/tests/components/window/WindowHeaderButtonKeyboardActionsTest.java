package com.vaadin.tests.components.window;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowHeaderButtonKeyboardActionsTest extends MultiBrowserTest {

    private static final String HEADER_CLASS = "v-window-header";
    private static final String RESTORE_BOX_CLASS = "v-window-restorebox";
    private static final String MAXIMIZE_BOX_CLASS = "v-window-maximizebox";
    private static final String CLOSE_BOX_CLASS = "v-window-closebox";

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();

        // open window before each test case
        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();

        waitForElementPresent(By.id("testWindow"));
    }

    /**
     * Scenario: focus the close button of the opened window -> press ENTER key
     * -> window should be closed
     */
    @Test
    public void testCloseWindowWithEnter() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(CLOSE_BOX_CLASS));
        setFocusToElementAndWait(closeButton);

        assertTrue("Window's close button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.ENTER);
        assertTrue("Window is not closed",
                findElements(By.className("v-window")).size() == 0);
    }

    /**
     * Scenario: focus the close button of the opened window -> press SPACE key
     * -> window should be closed
     */
    @Test
    public void testCloseWindowWithSpace() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(CLOSE_BOX_CLASS));
        setFocusToElementAndWait(closeButton);

        assertTrue("Window's close button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));
        pressKeyAndWait(Keys.SPACE);

        assertTrue("Window is not closed",
                findElements(By.className("v-window")).size() == 0);
    }

    /**
     * Scenario: focus close button of opened window -> press keys DELETE,
     * ARROW_LEFT, and END -> window should remain open after all actions
     */
    @Test
    public void testIncorrectKeyInputDoesntFireClose() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(CLOSE_BOX_CLASS));
        setFocusToElementAndWait(closeButton);

        assertTrue("Window's close button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.DELETE);
        assertTrue(
                "Window is closed by DELETE when close button is the focused element",
                findElements(By.className("v-window")).size() > 0);

        pressKeyAndWait(Keys.ARROW_LEFT);
        assertTrue(
                "Window is closed by ARROW_LEFT when close button is the focused element",
                findElements(By.className("v-window")).size() > 0);

        pressKeyAndWait(Keys.END);
        assertTrue(
                "Window is closed by END when close button is the focused element",
                findElements(By.className("v-window")).size() > 0);
    }

    /**
     * Scenario: close button of opened window is not focused -> press keys
     * ENTER and SPACE -> window should remain open after all actions
     */
    @Test
    public void testNonfocusedKeyDoesntCloseWindow() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(CLOSE_BOX_CLASS));
        assertTrue("Window's close button is the focused element",
                !closeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.ENTER);
        assertTrue(
                "Window is closed by ENTER when close button is not the focused element",
                findElements(By.className("v-window")).size() > 0);

        pressKeyAndWait(Keys.SPACE);
        assertTrue(
                "Window is closed by SPACE when close button is not the focused element",
                findElements(By.className("v-window")).size() > 0);
    }

    /**
     * Scenario: focus close button of opened window -> press keys TAB, and
     * TAB+SHIFT in succession, shifting focus from and back to the button ->
     * press ENTER key -> window should be closed
     */
    @Test
    public void testShiftFocusAndCloseWindow() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(CLOSE_BOX_CLASS));
        setFocusToElementAndWait(closeButton);
        assertTrue("Window's close button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.TAB);
        assertTrue("Window's close button is the focused element",
                !closeButton.equals(driver.switchTo().activeElement()));
        pressKeyAndWait(Keys.SHIFT, Keys.TAB);
        assertTrue("Window's close button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.ENTER);
        assertTrue(
                "Window is not closed when focus is shifted back-and-forth",
                findElements(By.className("v-window")).size() == 0);
    }

    /**
     * Scenario: focus close button of opened window -> click close button with
     * the mouse cursor -> window should be closed
     */
    @Test
    public void testMouseClickClosesWindowOnFocus() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(CLOSE_BOX_CLASS));
        setFocusToElementAndWait(closeButton);
        assertTrue("Window's close button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));

        // click button with mouse and wait
        closeButton.click();
        sleep(200);

        assertTrue("Window is not closed when focused element is clicked",
                findElements(By.className("v-window")).size() == 0);
    }

    // Tests for maximize-restore button

    /**
     * Scenario: focus the maximize button of the opened window -> press ENTER
     * key -> window should be maximized
     */
    @Test
    public void testMaximizeWindowWithEnter() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(MAXIMIZE_BOX_CLASS));
        setFocusToElementAndWait(closeButton);

        assertTrue("Window's maximize button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));
        pressKeyAndWait(Keys.ENTER);

        assertTrue("Window is not maximized", windowElement.isMaximized());
    }

    /**
     * Scenario: focus the maximize button of the opened window -> press SPACE
     * key -> window should be maximized
     */
    @Test
    public void testMaximizeWindowWithSpace() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(MAXIMIZE_BOX_CLASS));
        setFocusToElementAndWait(closeButton);

        assertTrue("Window's maximize button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));
        pressKeyAndWait(Keys.SPACE);

        assertTrue("Window is not maximized", windowElement.isMaximized());
    }

    /**
     * Scenario: focus maximize button of opened window -> press keys DELETE,
     * ARROW_UP, and ADD -> window should remain open after all actions
     */
    @Test
    public void testIncorrectKeyInputDoesntFireMaximize() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement maximizeButton = windowElement
                .findElement(By.className(MAXIMIZE_BOX_CLASS));
        setFocusToElementAndWait(maximizeButton);

        assertTrue("Window's maximize button is not the focused element",
                maximizeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.DELETE);
        assertTrue(
                "Window is maximized by DELETE when maximize button is the focused element",
                !windowElement.isMaximized());

        pressKeyAndWait(Keys.ARROW_UP);
        assertTrue(
                "Window is cmaximized by ARROW_UP when maximize button is the focused element",
                !windowElement.isMaximized());

        pressKeyAndWait(Keys.ADD);
        assertTrue(
                "Window is maximized by ADD when maximize button is the focused element",
                !windowElement.isMaximized());
    }

    /**
     * Scenario: close button of opened window is not focused -> press keys
     * ENTER and SPACE -> window should remain non-maximized after all actions
     */
    @Test
    public void testNonfocusedKeyDoesntMaximizeWindow() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement maximizeButton = windowElement
                .findElement(By.className(MAXIMIZE_BOX_CLASS));
        assertTrue("Window's close button is the focused element",
                !maximizeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.ENTER);
        assertTrue(
                "Window is maximized by ENTER when maximize button is not the focused element",
                !windowElement.isMaximized());

        pressKeyAndWait(Keys.SPACE);
        assertTrue(
                "Window is maximized by SPACE when maximize button is not the focused element",
                !windowElement.isMaximized());
    }

    /**
     * Scenario: focus maximize button of opened window -> press keys TAB, and
     * TAB+SHIFT in succession, shifting focus from and back to the button ->
     * press ENTER key -> window should be maximized
     */
    @Test
    public void testShiftFocusAndMaximizeWindow() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement maximizeButton = windowElement
                .findElement(By.className(MAXIMIZE_BOX_CLASS));
        setFocusToElementAndWait(maximizeButton);
        assertTrue("Window's maximize button is not the focused element",
                maximizeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.TAB);
        assertTrue("Window's maximize button is the focused element",
                !maximizeButton.equals(driver.switchTo().activeElement()));
        pressKeyAndWait(Keys.SHIFT, Keys.TAB);
        assertTrue("Window's maximize button is not the focused element",
                maximizeButton.equals(driver.switchTo().activeElement()));

        pressKeyAndWait(Keys.ENTER);
        assertTrue(
                "Window is not maximized when focus is shifted back-and-forth",
                windowElement.isMaximized());
    }

    /**
     * Scenario: focus maximize button of opened window -> click maximize button
     * with mouse cursor -> window should be maximized
     */
    @Test
    public void testMouseClickMaximizesWindowOnFocus() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement maximizeButton = windowElement
                .findElement(By.className(MAXIMIZE_BOX_CLASS));
        setFocusToElementAndWait(maximizeButton);
        assertTrue("Window's maximize button is not the focused element",
                maximizeButton.equals(driver.switchTo().activeElement()));

        // click button with mouse and wait
        maximizeButton.click();
        sleep(100);

        assertTrue("Window is not maximized when focused element is clicked",
                windowElement.isMaximized());
    }

    /**
     * Scenario: focus the maximize button of the opened window -> press ENTER
     * key -> window should be maximized -> press ENTER key again -> window
     * should be restored
     */
    @Test
    public void testMaximizeAndRestoreWindowWithEnter() throws IOException {

        assertTrue("Window is not open",
                findElements(By.id("testWindow")).size() == 1);

        WindowElement windowElement = $(WindowElement.class).first();
        WebElement closeButton = windowElement
                .findElement(By.className(MAXIMIZE_BOX_CLASS));
        setFocusToElementAndWait(closeButton);

        assertTrue("Window's maximize button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));
        pressKeyAndWait(Keys.ENTER);

        assertTrue("Window is not maximized", windowElement.isMaximized());

        assertTrue("Window's maximize button is not the focused element",
                closeButton.equals(driver.switchTo().activeElement()));
        pressKeyAndWait(Keys.ENTER);

        assertTrue("Window remains maximized", !windowElement.isMaximized());
    }

    protected void setFocusToElementAndWait(WebElement element) {
        String elementId = element.getAttribute("id");

        ((JavascriptExecutor) getDriver()).executeScript(
                "document.getElementById('" + elementId + "').focus();",
                element);
        sleep(100);
    }

    protected void pressKeyAndWait(Keys... key) {
        new Actions(driver).sendKeys(key).build().perform();
        sleep(1000);
    }
}
