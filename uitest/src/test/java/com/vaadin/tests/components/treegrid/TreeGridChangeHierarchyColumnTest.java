package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridChangeHierarchyColumnTest extends MultiBrowserTest {

    @Test
    public void renderingFrozenColumnsShouldFactorInHiddenColumns() {
        openTestURL();
        waitForElementPresent(By.id("TreeGrid"));
        waitForElementPresent(By.id("hideHierColButton"));
        waitForElementPresent(By.id("setHierColButton"));

        TreeGridElement treeGrid = $(TreeGridElement.class).id("TreeGrid");
        ButtonElement hideHierCol = $(ButtonElement.class)
                .id("hideHierColButton");
        ButtonElement setHierCol = $(ButtonElement.class)
                .id("setHierColButton");

        hideHierCol.click();
        setHierCol.click();

        // Wait for the new hierarchy column to be rendered
        waitForElementPresent(By.className("v-treegrid-expander"));

        List<WebElement> frozenCells = treeGrid
                .findElements(By.className("frozen"));

        assertEquals("Only the MultiSelect column should have frozen cells.", 2,
                frozenCells.size());
    }
}
