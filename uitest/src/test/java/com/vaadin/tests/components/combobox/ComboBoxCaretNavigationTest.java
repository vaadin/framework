package com.vaadin.tests.components.combobox;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class ComboBoxCaretNavigationTest extends SingleBrowserTest {

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testHomeAndEndKeys() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String text = comboBox.getPopupSuggestions().get(1);
        comboBox.selectByText(text);
        comboBox.sendKeys(Keys.HOME);
        assertCaretPosition("Home key didn't work well.", 0, comboBox);
        comboBox.sendKeys(Keys.END);
        assertCaretPosition("End key didn't work well.", text.length(),
                comboBox);
    }

    @Test
    public void testLeftAndRightKeys() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String text = comboBox.getPopupSuggestions().get(1);
        comboBox.selectByText(text);
        comboBox.sendKeys(Keys.ARROW_LEFT);
        assertCaretPosition("Left Arrow key didn't work well.",
                text.length() - 1, comboBox);
        comboBox.sendKeys(Keys.ARROW_RIGHT);
        assertCaretPosition("Right Arrow key didn't work well.", text.length(),
                comboBox);
    }

    @Test
    public void testHomeAndRightKeys() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String text = comboBox.getPopupSuggestions().get(1);
        comboBox.selectByText(text);
        comboBox.sendKeys(Keys.HOME);
        assertCaretPosition("Home key didn't work well.", 0, comboBox);
        comboBox.sendKeys(Keys.ARROW_RIGHT);
        assertCaretPosition("Right Arrow key didn't work well.", 1, comboBox);
    }

    @Test
    public void testLeftAndEndKeys() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String text = comboBox.getPopupSuggestions().get(1);
        comboBox.selectByText(text);
        comboBox.sendKeys(Keys.ARROW_LEFT);
        assertCaretPosition("Left Arrow key didn't work well.",
                text.length() - 1, comboBox);
        comboBox.sendKeys(Keys.END);
        assertCaretPosition("End key didn't work well.", text.length(),
                comboBox);
    }

    private void assertCaretPosition(String message, int position,
            ComboBoxElement comboBox) {
        assertArrayEquals(message, new int[] { position, position },
                getSelection(comboBox.getInputField()));
    }

    private int[] getSelection(WebElement element) {
        @SuppressWarnings("unchecked")
        List<Long> range = (List<Long>) executeScript(
                "return [arguments[0].selectionStart,arguments[0].selectionEnd]",
                element);
        return new int[] { range.get(0).intValue(), range.get(1).intValue() };
    }

}
