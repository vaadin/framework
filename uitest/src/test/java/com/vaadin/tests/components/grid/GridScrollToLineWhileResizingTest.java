package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridScrollToLineWhileResizingTest extends MultiBrowserTest {

    @Test
    public void testScrollToLineWorksWhileMovingSplitProgrammatically() {
        openTestURL();

        $(GridElement.class).first().getCell(21, 0).click();

        List<WebElement> cells = findElements(By.className("v-grid-cell"));
        boolean foundCell21 = false;
        for (WebElement cell : cells) {
            if ("cell21".equals(cell.getText())) {
                foundCell21 = true;
            }
        }

        assertTrue(foundCell21);
    }
}
