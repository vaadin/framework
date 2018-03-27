package com.vaadin.tests.components.grid;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridEditorUITest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        setDebug(true);
        openTestURL();
    }

    private void openEditor(int rowIndex) {
        GridElement grid = $(GridElement.class).first();

        GridCellElement cell = grid.getCell(rowIndex, 1);

        new Actions(driver).moveToElement(cell).doubleClick().build().perform();
    }

    private void saveEditor() {
        findElement(By.cssSelector(".v-grid-editor-save")).click();
    }

    private GridCellElement getHeaderCell(int rowIndex, int colIndex) {
        GridElement grid = $(GridElement.class).first();

        GridCellElement headerCell = grid.getHeaderCell(rowIndex, colIndex);

        return headerCell;
    }

    @Test
    public void testEditor() {
        assertFalse("Sanity check",
                isElementPresent(PasswordFieldElement.class));

        openEditor(5);
        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();

        openEditor(10);

        assertTrue("Editor should be opened with a password field",
                isElementPresent(PasswordFieldElement.class));

        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));
    }

    @Test
    public void savingResetsSortingIndicator() {
        GridCellElement headerCell = getHeaderCell(0, 0);
        headerCell.click();

        openEditor(1);

        saveEditor();

        assertThat(headerCell.getAttribute("class"),
                not(containsString("sort-")));
    }
}
