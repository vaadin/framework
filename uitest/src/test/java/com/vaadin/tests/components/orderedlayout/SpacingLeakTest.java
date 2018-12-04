package com.vaadin.tests.components.orderedlayout;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class SpacingLeakTest extends MultiBrowserTest {

    @Test
    public void testSpacingLeak() throws Exception {
        setDebug(true);
        openTestURL();

        // Make sure debug window is visible
        showDebugWindow();

        openDebugLogTab();
        getDriver().findElement(By.id("addbutton")).click();
        getDriver().findElement(By.xpath("//button[@title = 'Clear log']"))
                .click();
        getDriver().findElement(By.id("removebutton")).click();

        // this should be present
        getDriver().findElement(By
                .xpath("//span[text() = 'Measured 0 non connector elements']"));
    }
}
