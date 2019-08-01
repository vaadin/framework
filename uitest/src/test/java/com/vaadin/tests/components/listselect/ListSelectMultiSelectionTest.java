package com.vaadin.tests.components.listselect;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ListSelectMultiSelectionTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ListSelectTestUI.class;
    }

    @Test
    public void testShiftSelect() {
        openTestURL();
        selectMenuPath("Component", "Listeners", "Selection listener");

        ListSelectElement listSelect = $(ListSelectElement.class).first();
        Select select = new Select(listSelect.getSelectElement());
        List<WebElement> options = listSelect
                .findElements(By.tagName("option"));
        options.get(0).click();

        // ensure the selection event got through
        assertEquals("1. Selected: [Item 0]", getLogs().get(0));

        // ensure the state corresponds with the event
        List<WebElement> selected = select.getAllSelectedOptions();
        assertEquals(1, selected.size());
        assertEquals("Item 0", selected.get(0).getText());

        new Actions(getDriver()).keyDown(Keys.SHIFT).perform();
        options.get(1).click();
        new Actions(getDriver()).keyUp(Keys.SHIFT).perform();

        assertEquals("2. Selected: [Item 0, Item 1]", getLogs().get(0));

        selected = select.getAllSelectedOptions();
        assertEquals(2, selected.size());
        assertEquals("Item 1", selected.get(1).getText());

        new Actions(getDriver()).keyDown(Keys.SHIFT).perform();
        options.get(2).click();
        new Actions(getDriver()).keyUp(Keys.SHIFT).perform();

        // ensure second shift selection added instead of moved
        assertEquals("3. Selected: [Item 0, Item 1, Item 2]", getLogs().get(0));

        selected = select.getAllSelectedOptions();
        assertEquals(3, selected.size());
        assertEquals("Item 2", selected.get(2).getText());
        assertEquals("Item 0", selected.get(0).getText());
    }

    @Test
    public void testShiftSelectWithKeys() {
        openTestURL();
        selectMenuPath("Component", "Listeners", "Selection listener");

        ListSelectElement listSelect = $(ListSelectElement.class).first();
        Select select = new Select(listSelect.getSelectElement());
        List<WebElement> options = listSelect
                .findElements(By.tagName("option"));
        options.get(0).click();

        // ensure the selection event got through
        assertEquals("1. Selected: [Item 0]", getLogs().get(0));

        // ensure the state corresponds with the event
        List<WebElement> selected = select.getAllSelectedOptions();
        assertEquals(1, selected.size());
        assertEquals("Item 0", selected.get(0).getText());

        new Actions(getDriver()).keyDown(Keys.SHIFT).perform();
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        new Actions(getDriver()).keyUp(Keys.SHIFT).perform();

        assertEquals("2. Selected: [Item 0, Item 1]", getLogs().get(0));

        selected = select.getAllSelectedOptions();
        assertEquals(2, selected.size());
        assertEquals("Item 1", selected.get(1).getText());

        new Actions(getDriver()).keyDown(Keys.SHIFT).perform();
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        new Actions(getDriver()).keyUp(Keys.SHIFT).perform();

        // ensure second shift selection added instead of moved
        assertEquals("3. Selected: [Item 0, Item 1, Item 2]", getLogs().get(0));

        selected = select.getAllSelectedOptions();
        assertEquals(3, selected.size());
        assertEquals("Item 2", selected.get(2).getText());
        assertEquals("Item 0", selected.get(0).getText());
    }
}
