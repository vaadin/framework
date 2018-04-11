package com.vaadin.tests.components.panel;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PanelSetScrollTopWithLargeNumberTest extends MultiBrowserTest {
    private PanelElement panel;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-panel"));
        panel = $(PanelElement.class).first();
    }

    @Test
    public void testSetScrollTopWithLargeNumber() {
        WebElement contentNode = panel
                .findElement(By.className("v-panel-content"));
        int panelContentScrollTop = ((Number) executeScript(
                "return arguments[0].scrollTop", contentNode)).intValue();
        assertGreater(
                "Panel should scroll when scrollTop is set to a number larger than panel height",
                panelContentScrollTop, 0);
    }

}
