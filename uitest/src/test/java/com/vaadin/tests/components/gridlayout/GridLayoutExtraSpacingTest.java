package com.vaadin.tests.components.gridlayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutExtraSpacingTest extends MultiBrowserTest {

    @Test
    public void componentRowFour() throws IOException, Exception {
        openTestURL();
        CssLayoutElement component = $(CssLayoutElement.class).first();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();

        // Spacing on, not hiding empty rows/columns
        // There should be 3 * 6px spacing (red) above the csslayout
        verifySpacingAbove(3 * 6, gridLayout, component);

        CheckBoxElement spacingCheckbox = $(CheckBoxElement.class)
                .caption("spacing").first();
        check(spacingCheckbox);

        // Spacing off, not hiding empty rows/columns
        // There should not be any spacing (red) above the csslayout
        verifySpacingAbove(0, gridLayout, component);
        verifySpacingBelow(0, gridLayout, component);

        CheckBoxElement hideRowsColumnsCheckbox = $(CheckBoxElement.class)
                .caption("hide empty rows/columns").first();
        check(hideRowsColumnsCheckbox);

        // Spacing off, hiding empty rows/columns
        // There should not be any spacing (red) above the csslayout
        verifySpacingAbove(0, gridLayout, component);
        verifySpacingBelow(0, gridLayout, component);

        check(spacingCheckbox);
        // Spacing on, hiding empty rows/columns
        // There should not be any spacing (red) above or below the csslayout

        verifySpacingAbove(0, gridLayout, component);
        verifySpacingBelow(0, gridLayout, component);

    }

    /**
     * workaround for http://dev.vaadin.com/ticket/13763
     */
    private void check(CheckBoxElement checkbox) {
        WebElement cb = checkbox.findElement(By.xpath("input"));
        if (BrowserUtil.isChrome(getDesiredCapabilities())) {
            testBenchElement(cb).click(0, 0);
        } else {
            cb.click();
        }
    }

    private void verifySpacingAbove(int spacing, GridLayoutElement gridLayout,
            CssLayoutElement component) {
        assertHeight(component, 500 - spacing, 1);
        int offset = component.getLocation().getY()
                - gridLayout.getLocation().getY();
        assertEquals(spacing, offset);

    }

    private void verifySpacingBelow(int spacing, GridLayoutElement gridLayout,
            CssLayoutElement component) {
        assertHeight(component, 500 - spacing, 1);

        int offset = component.getLocation().getY()
                - gridLayout.getLocation().getY();
        assertEquals(0, offset);

    }

    private void assertHeight(WebElement component, int height, int tolerance) {
        assertTrue(Math
                .abs(height - component.getSize().getHeight()) <= tolerance);
    }
}
