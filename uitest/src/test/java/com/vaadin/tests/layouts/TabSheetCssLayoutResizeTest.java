package com.vaadin.tests.layouts;

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabSheetCssLayoutResizeTest extends MultiBrowserTest {

    @Test
    public void testContentsResize() {
        openTestURL();
        testBench().resizeViewPortTo(400, 600);
        $(ButtonElement.class).caption("Add TabSheet").first().click();

        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        WebElement panel = tabsheet
                .findElement(By.className("v-tabsheet-tabsheetpanel"));
        assertThat("Incorrect initial width.",
                (double) tabsheet.getSize().getWidth(),
                closeTo(panel.getSize().getWidth(), 5d));

        testBench().resizeViewPortTo(800, 600);
        assertThat("Incorrect resized width.",
                (double) tabsheet.getSize().getWidth(),
                closeTo(panel.getSize().getWidth(), 5d));
    }
}
