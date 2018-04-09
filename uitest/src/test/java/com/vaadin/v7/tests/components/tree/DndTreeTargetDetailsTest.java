package com.vaadin.v7.tests.components.tree;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.components.table.DndTableTargetDetailsTest;

/**
 * Test for mouse details in AbstractSelectTargetDetails class when DnD target
 * is a tree.
 *
 * @author Vaadin Ltd
 */
public class DndTreeTargetDetailsTest extends DndTableTargetDetailsTest {

    @Override
    protected WebElement getTarget() {
        return findElement(By.className("target"));
    }

}
