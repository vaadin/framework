package com.vaadin.tests.components.panel;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


public class PanelHTMLCaptionTest extends MultiBrowserTest {
    @Test
    public void testCaptionDisplayedAsText() {
        openTestURL();
        assertElementNotPresent(By.id("divId"));
        findElement(By.id("buttonId")).click();
        assertElementPresent(By.id("divId"));
    }
}
