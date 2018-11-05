package com.vaadin.tests.extensions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.extensions.UnknownExtensionHandling.MyExtension;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class UnknownExtensionHandlingTest extends SingleBrowserTest {

    @Test
    public void testUnknownExtensionHandling() {
        setDebug(true);
        openTestURL();

        openDebugLogTab();

        assertTrue(hasMessageContaining(MyExtension.class.getCanonicalName()));

        assertFalse(hasMessageContaining("Hierachy claims"));
    }

    private boolean hasMessageContaining(String needle) {
        List<WebElement> elements = findElements(
                By.className("v-debugwindow-message"));
        for (WebElement messageElement : elements) {
            // Can't use getText() since element isn't scrolled into view
            String text = (String) executeScript(
                    "return arguments[0].textContent", messageElement);
            if (text.contains(needle)) {
                return true;
            }
        }

        return false;
    }

}
