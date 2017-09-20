package com.vaadin.tests.components.javascriptcomponent;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class JSComponentLoadingIndicatorTest extends SingleBrowserTest {

    @Test
    public void ensureLoadingIndicatorShown() {
        openTestURL();
        WebElement js = findElement(By.id("js"));
        testBench().disableWaitForVaadin();

        js.click();
        waitUntilLoadingIndicatorVisible();
        waitUntilLoadingIndicatorNotVisible();
        Assert.assertEquals(1, findElements(By.className("pong")).size());

        js.click();
        waitUntilLoadingIndicatorVisible();
        waitUntilLoadingIndicatorNotVisible();
        Assert.assertEquals(2, findElements(By.className("pong")).size());
    }

}
