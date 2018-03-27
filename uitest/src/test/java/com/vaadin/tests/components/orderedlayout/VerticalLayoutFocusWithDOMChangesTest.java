package com.vaadin.tests.components.orderedlayout;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.BrowserUtil;
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
        Assert.assertEquals("input", activeElement.getTagName());
        Assert.assertEquals("", activeElement.getAttribute("value"));

        tf1.sendKeys(incrementalText);
        new Actions(getDriver())
                .moveToElement(
                        getDriver().findElement(By.className("v-button")))
                .click().build().perform();
        activeElement = getFocusedElement();
        Assert.assertEquals("Just a button", activeElement.getText());

        DesiredCapabilities capabilities = getDesiredCapabilities();
        if (BrowserUtil.isIE8(capabilities)
                || BrowserUtil.isIE(capabilities, 9)) {
            // IE8 and IE9 insert cursor in the start of input instead of end.
            Assert.assertEquals(incrementalText + initialText,
                    tf1.getAttribute("value"));
        } else {
            Assert.assertEquals(initialText + incrementalText,
                    tf1.getAttribute("value"));
        }
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
        Assert.assertEquals("input", activeElement.getTagName());
        Assert.assertEquals(firstText, activeElement.getAttribute("value"));

        new Actions(getDriver()).sendKeys(secondText).build().perform();
        DesiredCapabilities capabilities = getDesiredCapabilities();
        if (BrowserUtil.isIE8(capabilities)
                || BrowserUtil.isIE(capabilities, 9)) {
            // IE8 and IE9 insert cursor in the start of input instead of end.
            Assert.assertEquals(secondText + firstText,
                    tf2.getAttribute("value"));
        } else {
            Assert.assertEquals(firstText + secondText,
                    tf2.getAttribute("value"));
        }
    }
}
