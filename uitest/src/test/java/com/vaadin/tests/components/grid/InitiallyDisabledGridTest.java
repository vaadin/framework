package com.vaadin.tests.components.grid;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class InitiallyDisabledGridTest extends SingleBrowserTest {

    @Test
    public void columnsExpanded() {
        openTestURL();

        List<WebElement> cells = findElements(By.className("v-grid-cell"));
        WebElement col0 = cells.get(0);
        WebElement col1 = cells.get(1);
        Assert.assertTrue(col0.getSize().getWidth() > 250);
        Assert.assertTrue(col1.getSize().getWidth() > 250);
    }

    @Test
    public void worksWhenEnabled() {
        openTestURL();
        $(ButtonElement.class).first().click();

        GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(80);
        GridCellElement col0 = grid.getCell(80, 0);
        Assert.assertEquals("First 80", col0.getText());
    }
}
