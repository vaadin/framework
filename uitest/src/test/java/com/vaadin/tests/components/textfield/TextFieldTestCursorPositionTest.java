package com.vaadin.tests.components.textfield;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import static org.junit.Assert.assertEquals;

import java.util.List;

public class TextFieldTestCursorPositionTest extends MultiBrowserTest {

    private WebElement textFieldCheckCursor;
    private WebElement textFieldCheckRang ;

    @Test
    public void testSelection() {
        openTestURL();
        textFieldCheckCursor =findElement(org.openqa.selenium.By.id(TextFieldTestCursorPosition.CURSOR_POS_TF));
        textFieldCheckRang=findElement(org.openqa.selenium.By.id(TextFieldTestCursorPosition.RANGE_LENGTH_TF));

        // Range selected correctly
        setSelectionRange();
        assertSelection((TextFieldTestCursorPosition.valueLength)/2, TextFieldTestCursorPosition.valueLength,textFieldCheckRang);

        // Cursor position
        setCursorPosition();
        assertCursorPosition(TextFieldTestCursorPosition.valueLength);

    }

    private void assertCursorPosition(int i) {
        assertSelection(i, i,textFieldCheckCursor);
    }

    private void setCursorPosition() {
        $(ButtonElement.class).id(TextFieldTestCursorPosition.BUTTON_SETPOSITION)
                .click();

    }

    private void setSelectionRange() {
        $(ButtonElement.class)
                .id(TextFieldTestCursorPosition.BUTTON_SETRANGE).click();
    }

    //expected and actual
    private void assertSelection(int start, int length,WebElement textField) {
        assertEquals(new Selection(start, length), getSelection(textField));
    }

    private Selection getSelection(WebElement textField) {
        @SuppressWarnings("unchecked")
        List<Long> range = (List<Long>) executeScript(
                "return [arguments[0].selectionStart,arguments[0].selectionEnd]",
                textField);
        return new Selection(Math.toIntExact(range.get(0)),Math.toIntExact(range.get(1)));
    }
}
