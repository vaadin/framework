package com.vaadin.tests.elements.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxUITest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testMultipleSelectByTextOperationsAllowingNullSelection() {
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        testMultipleSelectByTextOperationsIn(cb);
    }

    @Test
    public void testMultipleSelectByTextOperationsForbiddingNullSelection() {
        ComboBoxElement cb = $(ComboBoxElement.class).get(1);
        testMultipleSelectByTextOperationsIn(cb);
    }

    @Test
    public void testSelectByTextNotFound() {
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.selectByText("foobar");
    }

    @Test
    public void testSelectByTextWithIcons() {
        ComboBoxElement cb = $(ComboBoxElement.class).id("with-icons");
        cb.selectByText("GBP");
        assertEquals("GBP", cb.getValue());
        cb.selectByText("EUR");
        assertEquals("EUR", cb.getValue());
    }

    private void testMultipleSelectByTextOperationsIn(
            ComboBoxElement comboBox) {
        // Select all items from the menu
        for (String currency : ComboBoxUI.currencies) {
            comboBox.selectByText(currency);

            // Check that the value was selected as the input value of the CB
            WebElement input = comboBox.getInputField();
            assertEquals(currency, input.getAttribute("value"));
        }
    }
}
