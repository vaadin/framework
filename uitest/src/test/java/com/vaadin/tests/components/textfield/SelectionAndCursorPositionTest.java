package com.vaadin.tests.components.textfield;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class SelectionAndCursorPositionTest extends SingleBrowserTest {

    private static final int DEFAULT_TEXT_LENGTH = SelectionAndCursorPosition.DEFAULT_TEXT
            .length();
    private WebElement textField;

    @Test
    public void testSelection() {
        openTestURL();
        textField = findElement(By.id(SelectionAndCursorPosition.TEXTFIELD_ID));

        // Select all
        getSelectAll().click();
        assertSelection(0, DEFAULT_TEXT_LENGTH);

        // Select range
        setSelectionRange(10, 5);
        assertSelection(10, 5);

        // Test for index out of bounds
        setSelectionRange(0, DEFAULT_TEXT_LENGTH);
        assertSelection(0, DEFAULT_TEXT_LENGTH);
        setSelectionRange(0, DEFAULT_TEXT_LENGTH + 1);
        assertSelection(0, DEFAULT_TEXT_LENGTH);
        setSelectionRange(1, DEFAULT_TEXT_LENGTH);
        assertSelection(1, DEFAULT_TEXT_LENGTH - 1);
        setSelectionRange(DEFAULT_TEXT_LENGTH - 1, 2);
        assertSelection(DEFAULT_TEXT_LENGTH - 1, 1);

        // Cursor position
        setCursorPosition(0);
        assertCursorPosition(0);

    }

    private void assertCursorPosition(int i) {
        assertSelection(i, 0);
    }

    private void setCursorPosition(int i) {
        $(TextFieldElement.class).id(SelectionAndCursorPosition.CURSOR_POS_ID)
                .setValue(String.valueOf(i));
        $(ButtonElement.class).id(SelectionAndCursorPosition.CURSOR_POS_SET_ID)
                .click();

    }

    private void setSelectionRange(int start, int length) {
        $(TextFieldElement.class).id(SelectionAndCursorPosition.RANGE_START_ID)
                .setValue(String.valueOf(start));
        $(TextFieldElement.class).id(SelectionAndCursorPosition.RANGE_LENGTH_ID)
                .setValue(String.valueOf(length));
        $(ButtonElement.class)
                .id(SelectionAndCursorPosition.RANGE_SET_BUTTON_ID).click();
    }

    private void assertSelection(int start, int length) {
        Assert.assertEquals(new Selection(start, length),
                getSelection(textField));
    }

    private void clearSelection() {
        setSelectionRange(0, 0);

    }

    private WebElement getSelectAll() {
        return findElement(By.id(SelectionAndCursorPosition.SELECT_ALL_ID));
    }

    private Selection getSelection(WebElement textField) {
        @SuppressWarnings("unchecked")
        List<Long> range = (List<Long>) executeScript(
                "return [arguments[0].selectionStart,arguments[0].selectionEnd]",
                textField);
        return new Selection(range);
    }
}
