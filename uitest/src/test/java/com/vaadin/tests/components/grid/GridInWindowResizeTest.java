package com.vaadin.tests.components.grid;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridInWindowResizeTest extends MultiBrowserTest {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Must test on a browser with animations
        return Collections
                .singletonList(Browser.CHROME.getDesiredCapabilities());
    }

    @Test
    public void resizeWindow() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        int col1WidthBefore = grid.getCell(0, 0).getSize().getWidth();
        $(ButtonElement.class).caption("resize").first().click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){

        }
        
        int col1WidthAfter = grid.getCell(0, 0).getSize().getWidth();

        Assert.assertTrue(col1WidthAfter < col1WidthBefore);
    }
}
