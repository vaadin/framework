package com.vaadin.tests.components.richtextarea;

import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.vaadin.testbench.elements.RichTextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RichTextAreaCreateLinkTest extends MultiBrowserTest {

    private static final Duration PROMPT_POLLING_INTERVAL = Duration
            .ofSeconds(5);
    private static final Duration PROMPT_TIMEOUT_INTERVAL = Duration
            .ofSeconds(30);
    private static final String TESTING_URI = "https://vaadin.com/";
    private static final String TESTING_TEXT = "Vaadin company name";

    RichTextAreaElement rta;

    @Before
    public void init() {
        openTestURL();
        rta = $(RichTextAreaElement.class).first();
    }

    @Test
    public void createLinkButtonShouldInsertUriAsTextAndHrefIfNoTextIsHighlighted() {
        createLinkViaButton(rta, TESTING_URI);
        String expected = "<a href=\"" + TESTING_URI + "\">" + TESTING_URI
                + "</a>";
        assertTrue(String.format(
                "RichTextArea's expected value is: %s. However, the following value was received: %s.",
                expected, rta.getValue()), rta.getValue().equals(expected));
    }

    @Test
    public void createLinkButtonShouldAddUriAsHrefIfTextIsHighlighted() {
        rta.setValue(TESTING_TEXT);
        WebElement textArea = rta.findElement(By.className("gwt-RichTextArea"));
        textArea.sendKeys(Keys.CONTROL, "a");
        createLinkViaButton(rta, TESTING_URI);
        String expected = "<a href=\"" + TESTING_URI + "\">" + TESTING_TEXT
                + "</a>";
        assertTrue(String.format(
                "RichTextArea's expected value is: %s. However, the following value was received: %s.",
                expected, rta.getValue()), rta.getValue().equals(expected));
    }

    private void createLinkViaButton(RichTextAreaElement rta, String Uri) {
        rta.findElement(By.cssSelector("div[title='Create Link']")).click();

        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(PROMPT_TIMEOUT_INTERVAL)
                .pollingEvery(PROMPT_POLLING_INTERVAL)
                .ignoring(NoSuchElementException.class);

        // Wait for the alert to be displayed and store it in a variable
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        // Type the URI
        alert.sendKeys(Uri);
        // Press the OK button
        alert.accept();
    }
}
