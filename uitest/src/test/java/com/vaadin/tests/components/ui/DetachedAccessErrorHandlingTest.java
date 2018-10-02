package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DetachedAccessErrorHandlingTest extends SingleBrowserTest {
    @Test
    public void testDetachedErrorHandling_pageOpen_noErrors() {
        openTestURL();

        $(ButtonElement.class).id("simple").click();
        assertNoErrors();

        // The thing to really test here is that nothing is logged to stderr,
        // but that's not practical to detect
        $(ButtonElement.class).id("handling").click();
        assertNoErrors();
    }

    private void assertNoErrors() {
        // Reload page to trigger detach event
        openTestURL();

        $(ButtonElement.class).id("show").click();
        Assert.assertEquals(0, findElements(By.className("errorLabel")).size());
    }
}
