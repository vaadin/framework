package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class UiDependenciesInHtmlTest extends SingleBrowserTest {

    @Test
    public void testUiDependencisInHtml() {
        openTestURL();

        String statusText = findElement(By.id("statusBox")).getText();

        assertEquals(
                "Script loaded before vaadinBootstrap.js: true\nStyle tag before vaadin theme: true\nStyle tags in correct order: true",
                statusText);
    }

}
