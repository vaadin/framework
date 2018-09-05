package com.vaadin.tests.components;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SaneErrorsTest extends MultiBrowserTest {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.tb3.MultiBrowserTest#getBrowsersToTest()
     */
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.FIREFOX);
    }

    @Test
    public void test() {
        openTestURL();
        List<WebElement> elements = getDriver()
                .findElements(By.xpath("//*[text() = 'Show me my NPE!']"));
        for (WebElement webElement : elements) {
            webElement.click();
        }

        getDriver().findElement(By.xpath("//*[text() = 'Collect exceptions']"))
                .click();

        List<WebElement> errorMessages = getDriver()
                .findElements(By.className("v-label"));
        for (WebElement webElement : errorMessages) {
            String text = webElement.getText();
            assertEquals("java.lang.NullPointerException", text);
        }
    }

}
