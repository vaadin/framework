package com.vaadin.v7.tests.components.grid.basicfeatures.client;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

@SuppressWarnings("all")
public class GridClientStructureTest extends GridBasicClientFeaturesTest {
    @Test
    public void haederDecoSizeShouldBeRecalculated() {
        // it's easier to notice with valo
        openTestURL("theme=valo");

        WebElement topDeco = getGridElement()
                .findElement(By.className("v-grid-header-deco"));
        assertGreater(
                "The header deco in Valo hasn't been recalculated after initial rendering",
                topDeco.getSize().getHeight(), 20);
    }
}
