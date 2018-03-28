package com.vaadin.tests.components.treetable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that TreeTable with ContainerHierarchicalWrapper is updated correctly
 * when the setParent() is called for the item just added
 *
 * @author Vaadin Ltd
 */
public class TreeTableContainerHierarchicalWrapperTest
        extends MultiBrowserTest {

    @Test
    public void testStructure() throws InterruptedException {
        openTestURL();

        TreeTableElement treeTable = $(TreeTableElement.class).first();
        WebElement findElement = treeTable.getCell(0, 0)
                .findElement(By.className("v-treetable-treespacer"));
        findElement.click();

        TestBenchElement cell = treeTable.getCell(5, 0);
        WebElement findElement2 = cell
                .findElement(By.className("v-treetable-treespacer"));
        assertEquals("Item 0-5", cell.getText());
        findElement2.click();

        TestBenchElement cell2 = treeTable.getCell(10, 0);
        assertEquals("Item 0-5-5", cell2.getText());
    }

}
