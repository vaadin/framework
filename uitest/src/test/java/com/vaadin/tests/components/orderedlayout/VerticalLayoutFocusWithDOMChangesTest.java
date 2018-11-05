package com.vaadin.tests.components.orderedlayout;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class VerticalLayoutFocusWithDOMChangesTest extends MultiBrowserTest {

    private String initialText = "Some";
    private String incrementalText = " text";

    @Test
    public void inputTextAndChangeFocus() throws InterruptedException {
        openTestURL();
        List<WebElement> textFields = getDriver()
                .findElements(By.tagName("input"));
        WebElement tf1 = textFields.get(0);
        WebElement tf2 = textFields.get(1);
        tf1.sendKeys(initialText);
        new Actions(getDriver()).moveToElement(tf2).click().build().perform();

        WebElement activeElement = getFocusedElement();
        assertEquals("input", activeElement.getTagName());
        assertEquals("", activeElement.getAttribute("value"));

        tf1.sendKeys(incrementalText);
        new Actions(getDriver())
                .moveToElement(
                        getDriver().findElement(By.className("v-button")))
                .click().build().perform();
        activeElement = getFocusedElement();
        assertEquals("Just a button", activeElement.getText());

        DesiredCapabilities capabilities = getDesiredCapabilities();
        assertEquals(initialText + incrementalText, tf1.getAttribute("value"));
    }

    @Test
    public void moveFocusAndChangeFieldWithValue() {
        openTestURL();
        List<WebElement> textFields = getDriver()
                .findElements(By.tagName("input"));
        WebElement tf1 = textFields.get(0);
        WebElement tf2 = textFields.get(1);

        String firstText = "This is";
        String secondText = " default value";

        tf2.sendKeys(firstText);
        tf1.sendKeys(initialText);
        new Actions(getDriver()).moveToElement(tf2).click().build().perform();

        WebElement activeElement = getFocusedElement();
        assertEquals("input", activeElement.getTagName());
        assertEquals(firstText, activeElement.getAttribute("value"));

        new Actions(getDriver()).sendKeys(secondText).build().perform();
        DesiredCapabilities capabilities = getDesiredCapabilities();
        assertEquals(firstText + secondText, tf2.getAttribute("value"));
    }
}
