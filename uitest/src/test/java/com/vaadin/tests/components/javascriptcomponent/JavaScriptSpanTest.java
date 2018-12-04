package com.vaadin.tests.components.javascriptcomponent;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class JavaScriptSpanTest extends SingleBrowserTest {
    @Test
    public void componentShownAsSpan() {
        openTestURL();

        assertElementPresent(By.xpath("//span[text()='Hello World']"));

    }
}
