package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridEditorNonBufferedTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        setDebug(true);
        openTestURL();
    }

    private void openEditor(int rowIndex, int cellIndex) {
        GridElement grid = $(GridElement.class).first();
        GridCellElement cell = grid.getCell(rowIndex, cellIndex);
        new Actions(driver).moveToElement(cell).doubleClick().build().perform();
    }

    @Test
    public void testEditor() {
        openEditor(5, 1);

        assertTrue("Editor should be opened with a TextField",
                isElementPresent(TextFieldElement.class));

        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));
    }

    @Test
    public void testEscClosesEditor() {
        openEditor(5, 1);
        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();

        assertFalse("Editor should be closed",
                isElementPresent(TextFieldElement.class));
    }

    @Test
    public void preventNavigationToNextRowIfEditorValueIsInvalid() {
        // Test with navigation to next row
        openEditor(5, 2);
        WebElement field = findElement(By.className("v-textfield-focus"));
        String selectAll = Keys.chord(Keys.CONTROL, "a");
        field.sendKeys(selectAll);
        field.sendKeys(Keys.DELETE);
        field.sendKeys(Keys.TAB);

        String editorMessage = getEditorMessage();

        assertEquals(
                "Last Name: " + GridEditorNonBuffered.VALIDATION_ERROR_MESSAGE,
                editorMessage);
    }

    @Test
    public void preventNavigationToPrevRowIfEditorValueIsInvalid() {
        // Test with navigation to previous row
        openEditor(5, 1);
        WebElement field = findElement(By.className("v-textfield-focus"));
        String selectAll = Keys.chord(Keys.CONTROL, "a");
        field.sendKeys(selectAll);
        field.sendKeys(Keys.DELETE);
        field.sendKeys(Keys.chord(Keys.SHIFT, Keys.TAB));
        String editorMessage = getEditorMessage();

        assertEquals(
                "First Name: " + GridEditorNonBuffered.VALIDATION_ERROR_MESSAGE,
                editorMessage);
    }

    private String getEditorMessage() {
        return findElement(
                By.xpath("//div[@class = 'v-grid-editor-message']/div"))
                        .getText();
    }
}
