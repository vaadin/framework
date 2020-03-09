package com.vaadin.tests.components.grid;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridWithRendererWorkaroundTest extends MultiBrowserTest {

    private GridElement grid;
    private String pseudoClassPropertyScript = "return window.getComputedStyle(arguments[0], ':%s').getPropertyValue('%s');";

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        grid = $(GridElement.class).first();
    }

    @Test
    public void testRadioButtonGroupAfterGridResize() {
        String afterScript = String.format(pseudoClassPropertyScript, "after",
                "background-color");
        WebElement radioButton = grid.getCell(0, 0)
                .findElement(By.className("v-select-option-selected"));
        WebElement label = radioButton.findElement(By.tagName("label"));
        String bgColor = ((JavascriptExecutor) driver)
                .executeScript(afterScript, label).toString();
        assertFalse("Unexpected selection color before resize: " + bgColor,
                bgColor.contains("0"));

        getDriver().manage().window().setSize(new Dimension(600, 800));

        assertEquals("Unexpected selection color after resize,", bgColor,
                ((JavascriptExecutor) driver).executeScript(afterScript, label)
                        .toString());
    }

    @Test
    public void testSliderAfterGridScrolling() {
        // scroll in increments to slow down the process
        for (int i = 10; i <= 60; i = i + 10) {
            grid.scrollToRow(i);
        }
        // test that all visible sliders have a non-zero position
        List<WebElement> sliders = grid
                .findElements(By.className("v-slider-handle"));
        for (WebElement slider : sliders) {
            String marginString = slider.getCssValue("margin-left");
            Double margin = Double.valueOf(
                    marginString.substring(0, marginString.indexOf("px")));
            assertThat("Unexpected margin width.", margin, greaterThan(0d));
        }
    }
}
