package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that components within table cells get resized when their column gets
 * resized.
 *
 * @author Vaadin Ltd
 */
public class TableColumnResizeContentsWidthTest extends MultiBrowserTest {

    @Test
    public void testResizing() throws InterruptedException {
        openTestURL();

        List<ButtonElement> buttons = $(ButtonElement.class).all();

        WebElement resizer = getTable()
                .findElement(By.className("v-table-resizer"));

        assertEquals(100, getTextFieldWidth());

        moveResizer(resizer, -20);
        assertEquals(80, getTextFieldWidth());

        moveResizer(resizer, 40);
        assertEquals(120, getTextFieldWidth());

        // click the button for decreasing size
        buttons.get(1).click();
        waitUntilTextFieldWidthIs(80);

        // click the button for increasing size
        buttons.get(0).click();
        waitUntilTextFieldWidthIs(100);
    }

    private void waitUntilTextFieldWidthIs(final int width) {
        waitUntil(input -> getTextFieldWidth() == width);
    }

    private int getTextFieldWidth() {
        TableElement table = getTable();
        final WebElement textField = table
                .findElement(By.className("v-textfield"));

        return textField.getSize().width;
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void moveResizer(WebElement resizer, int offset) {
        new Actions(driver).clickAndHold(resizer).moveByOffset(offset, 0)
                .release().perform();
    }
}
