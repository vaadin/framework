package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.PopupViewElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridPopupViewTest extends MultiBrowserTest {

    @Test
    public void gridSizeCorrect() {
        openTestURL();
        PopupViewElement pv = $(PopupViewElement.class).first();

        for (int i = 0; i < 3; i++) {
            pv.click();
            GridElement grid = $(GridElement.class).first();
            Dimension rect = grid.getCell(0, 0).getSize();
            assertEquals(500, rect.width);
            assertEquals(38, rect.height);
            findElement(By.className("v-ui")).click();
            waitForElementNotPresent(By.className("v-grid"));
        }

    }

}
