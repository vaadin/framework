package com.vaadin.test.osgi;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.TextFieldElement;

public class KarafIntegrationIT extends TestBenchTestCase {

    private static final String URL_PREFIX = "http://localhost:8181/";
    private static final String APP1_URL = URL_PREFIX + "myapp1";
    private static final String APP2_URL = URL_PREFIX + "myapp2";

    @Test
    public void testApp1() {
        runBasicTest(APP1_URL, "bar");
        // App theme should make a button pink
        WebElement element = getDriver().findElement(By.className("v-button"));
        String buttonColor = element.getCssValue("color");
        assertEquals("rgba(255, 128, 128, 1)", buttonColor);
    }

    @Test
    public void testApp2() {
        runBasicTest(APP2_URL, "foo");
    }

    private void runBasicTest(String app1Url, String text) {
        getDriver().navigate().to(app1Url);
        new WebDriverWait(getDriver(), 5000)
                .until(driver -> isElementPresent(TextFieldElement.class));
        getDriver().findElement(By.className("v-textfield")).sendKeys(text);
        getDriver().findElement(By.className("v-button")).click();
        String foundText = getDriver().findElement(By.className("v-label"))
                .getText();
        assertEquals("Thanks " + text + ", it works!", foundText);
    }

    @Before
    public void setup() {
        setDriver(new PhantomJSDriver());
    }

    @After
    public void teardown() {
        getDriver().quit();
    }
}
