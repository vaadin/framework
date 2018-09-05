package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComboBoxItemStyleGeneratorTest extends SingleBrowserTest {
    @Test
    public void testItemStyleGenerator() {
        openTestURL();

        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        selectMenuPath("Component", "Features", "Item style generator",
                "Bold fives");

        comboBox.openPopup();

        List<WebElement> boldItems = findElements(
                By.className("v-filterselect-item-bold"));

        assertEquals(1, boldItems.size());
        assertEquals("Item 5", boldItems.get(0).getText());

        selectMenuPath("Component", "Features", "Item style generator", "-");

        boldItems = findElements(By.className("v-filterselect-item-bold"));
        assertEquals(0, boldItems.size());
    }

    @Override
    protected Class<?> getUIClass() {
        return ComboBoxes2.class;
    }

}
