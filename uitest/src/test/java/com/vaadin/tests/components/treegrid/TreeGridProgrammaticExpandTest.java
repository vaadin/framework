package com.vaadin.tests.components.treegrid;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridProgrammaticExpandTest extends MultiBrowserTest {

    @Test
    public void expandAndClick() {
        openTestURL("debug");
        TreeGridElement treeGrid = $(TreeGridElement.class).first();
        $(ButtonElement.class).first().click();
        waitUntilLoadingIndicatorNotVisible();
        treeGrid.getCell(5, 0).click();
        waitUntilLoadingIndicatorNotVisible();
        assertElementNotPresent(By.className("v-Notification-error"));
    }
}
