package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridThemeChangeTest extends MultiBrowserTest {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Seems like stylesheet onload is not fired on PhantomJS
        // https://github.com/ariya/phantomjs/issues/12332
        return super.getBrowsersExcludingPhantomJS();
    }

    @Test
    public void testThemeChange() {
        openTestURL("debug");

        GridElement grid = $(GridElement.class).first();

        int reindeerHeight = grid.getRow(0).getSize().getHeight();

        grid.getCell(0, 0).click();

        grid = $(GridElement.class).first();
        int valoHeight = grid.getRow(0).getSize().getHeight();

        assertTrue(
                "Row height should increase when changing from Reindeer to Valo",
                valoHeight > reindeerHeight);
    }
}
