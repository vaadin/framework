package com.vaadin.tests.components.grid;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridDetailsDetachTest extends MultiBrowserTest {

    @Test
    public void testDetachGridWithDetailsOpen() {
        setDebug(true);
        openTestURL();

        $(GridElement.class).first().getCell(3, 0).click();
        $(GridElement.class).first().getCell(5, 0).click();

        assertNoErrorNotifications();

        $(ButtonElement.class).first().click();

        assertNoErrorNotifications();
    }

    @Test
    public void testDetachAndReattachGridWithDetailsOpen() {
        setDebug(true);
        openTestURL();

        $(GridElement.class).first().getCell(3, 0).click();
        $(GridElement.class).first().getCell(5, 0).click();

        assertNoErrorNotifications();

        $(ButtonElement.class).first().click();

        assertNoErrorNotifications();

        $(ButtonElement.class).get(1).click();

        assertNoErrorNotifications();

        List<WebElement> spacers = findElements(By.className("v-grid-spacer"));
        Assert.assertEquals("Not enough spacers in DOM", 2, spacers.size());
        Assert.assertEquals("Spacer content not visible",
                "Extra data for Bean 3", spacers.get(0).getText());
        Assert.assertEquals("Spacer content not visible",
                "Extra data for Bean 5", spacers.get(1).getText());
    }

    @Test
    public void testDetachAndImmediateReattach() {
        setDebug(true);
        openTestURL();

        $(GridElement.class).first().getCell(3, 0).click();
        $(GridElement.class).first().getCell(5, 0).click();

        assertNoErrorNotifications();

        // Detach and Re-attach Grid
        $(ButtonElement.class).get(1).click();

        assertNoErrorNotifications();

        List<WebElement> spacers = findElements(By.className("v-grid-spacer"));
        Assert.assertEquals("Not enough spacers in DOM", 2, spacers.size());
        Assert.assertEquals("Spacer content not visible",
                "Extra data for Bean 3", spacers.get(0).getText());
        Assert.assertEquals("Spacer content not visible",
                "Extra data for Bean 5", spacers.get(1).getText());
    }

}
