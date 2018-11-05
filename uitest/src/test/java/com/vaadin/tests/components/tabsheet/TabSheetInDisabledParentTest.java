package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to check that TabsheetBaseConnector reacts on disabling its parent.
 *
 * @author Vaadin Ltd
 */
public class TabSheetInDisabledParentTest extends MultiBrowserTest {

    @Test
    public void testTabsheetInDisabledParent() {
        openTestURL();

        WebElement button = getDriver().findElement(By.className("v-button"));
        // disable parent
        button.click();

        List<WebElement> tabHeaders = getDriver()
                .findElements(By.className("v-tabsheet-tabitemcell"));
        tabHeaders.get(1).findElement(By.className("v-captiontext")).click();

        assertFalse(
                "It's possible to activate TabSheet tab when its parent is disabled",
                tabHeaders.get(1).getAttribute("class")
                        .contains("v-tabsheet-tabitemcell-selected"));

        // enable parent back
        button.click();

        // selected tab is still the same
        tabHeaders = getDriver()
                .findElements(By.className("v-tabsheet-tabitemcell"));
        assertTrue("Tabsheet has wrong selected tab after enabling its parent",
                tabHeaders.get(0).getAttribute("class")
                        .contains("v-tabsheet-tabitemcell-selected"));

        // click to the second tab
        tabHeaders.get(1).findElement(By.className("v-captiontext")).click();
        // check the second tab is selected
        assertTrue(
                "Second tab is not activated in the Tabsheet after clicking on it",
                tabHeaders.get(1).getAttribute("class")
                        .contains("v-tabsheet-tabitemcell-selected"));
    }

}
