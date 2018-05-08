package com.vaadin.tests.components.abstractcomponent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * @author Vaadin Ltd
 *
 */
public class TooltipModesTest extends TooltipTest {

    @Test
    public void checkTooltipModes() throws Exception {
        openTestURL();

        $(ButtonElement.class).first().showTooltip();

        // preformatted is default
        checkTooltip(
                "<pre class=\"v-tooltip-pre\">Several\n lines\n tooltip</pre>");

        // Use html inside tooltip
        $(ButtonElement.class).get(1).click();

        $(ButtonElement.class).first().showTooltip();

        checkTooltip("<div>Html <b><span>tooltip</span></b></div>");

        // Use text inside tooltip
        $(ButtonElement.class).get(2).click();

        $(ButtonElement.class).first().showTooltip();
        checkTooltip("&lt;b&gt;tooltip&lt;/b&gt;");
    }

    @Override
    protected void checkTooltip(String tooltipText)
            throws InterruptedException {
        WebElement tooltip = getTooltip();
        WebElement tooltipContent = tooltip
                .findElement(By.className("v-tooltip-text"));
        assertEquals(tooltipText, tooltipContent.getAttribute("innerHTML"));
    }
}
