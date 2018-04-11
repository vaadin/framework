package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class WindowCaptionTest extends SingleBrowserTest {

    private WindowElement htmlWindow;
    private WindowElement textWindow;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-window"));
        htmlWindow = $(WindowElement.class).id("htmlWindow");
        textWindow = $(WindowElement.class).id("textWindow");
    }

    @Test
    public void htmlCaption() {
        assertEquals("HtmlWindow's caption didn't match,",
                "This may or may not be red", htmlWindow.getCaption());
        assertEquals("TextWindow's caption didn't match,",
                "<font style='color: red;'>This may or may not be red</font>",
                textWindow.getCaption());
    }

    @Test
    public void textCaption() {
        clickButton("Plain text");
        ensureCaptionsEqual("This is just text");
    }

    @Test
    public void nullCaption() {
        clickButton("Null");
        ensureCaptionsEqual("");
    }

    @Test
    public void emptyCaption() {
        clickButton("Empty");
        ensureCaptionsEqual("");
    }

    private void clickButton(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

    private void ensureCaptionsEqual(final String expectedCaption) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return expectedCaption.equals(htmlWindow.getCaption());
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "htmlWindow's caption to be '" + expectedCaption
                        + "' (was: '" + htmlWindow.getCaption() + "')";
            }

        });

        assertEquals("TextWindow's caption didn't match,", expectedCaption,
                textWindow.getCaption());
    }
}
