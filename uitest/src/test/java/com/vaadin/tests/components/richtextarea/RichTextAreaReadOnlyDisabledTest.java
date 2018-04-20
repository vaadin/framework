package com.vaadin.tests.components.richtextarea;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class RichTextAreaReadOnlyDisabledTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void shouldDelegateToShortcutActionHandler() {
        openTestURL();
        // gwt-RichTextArea is classname used by iframe
        WebElement readPrB = findElement(By.id("readPr"));
        WebElement enablePrB = findElement(By.id("enablePr"));

        assertElementNotPresent(By.className("gwt-RichTextArea"));
        readPrB.click();

        assertElementNotPresent(By.className("gwt-RichTextArea"));
        enablePrB.click();

        assertElementPresent(By.className("gwt-RichTextArea"));
        readPrB.click();//Set to read-only
        assertElementNotPresent(By.className("gwt-RichTextArea"));
        enablePrB.click();//Set disabled
        assertElementNotPresent(By.className("gwt-RichTextArea"));
    }
}
