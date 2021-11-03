package com.vaadin.v7.tests.components.grid;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserThemeTest;

@TestCategory("grid")
public class GridThemeUITest extends MultiBrowserThemeTest {

    private GridElement grid;

    @Test
    public void grid() throws Exception {
        openTestURL();
        selectPage("Editor");
        compareScreen("basic");
    }

    @Test
    public void headerAndFooter() throws Exception {
        openTestURL();
        selectPage("HeaderFooter");
        compareScreen("basic");
        grid.getHeaderCell(0, 6).$(ButtonElement.class).first().click();
        compareScreen("additional-header");
        grid.getHeaderCell(2, 1).click();
        compareScreen("sorted-last-name");
        grid.getHeaderCell(2, 4).click();
        compareScreen("sorted-age");
    }

    @Test
    public void editor() throws Exception {
        openTestURL();
        selectPage("Editor");
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        // Open editor row
        openEditor(ritaBirthdate);

        compareScreen("initial");

        GridEditorElement editor = grid.getEditor();

        DateFieldElement dateField = editor.$(DateFieldElement.class).first();
        WebElement input = dateField.findElement(By.xpath("input"));
        input.sendKeys("Invalid", Keys.TAB);
        editor.save();
        compareScreen("one-invalid");

        TextFieldElement age = editor.$(TextFieldElement.class).caption("Age")
                .first();
        age.sendKeys("abc", Keys.TAB);
        editor.save();

        compareScreen("two-invalid");
    }

    private void openEditor(GridCellElement targetCell) {
        new Actions(getDriver()).doubleClick(targetCell).perform();
        try {
            waitForElementPresent(By.className("v-grid-editor"));
        } catch (Exception e) {
            // Double-click is flaky, try again...
            new Actions(getDriver()).doubleClick(targetCell).perform();
            waitForElementPresent(By.className("v-grid-editor"));
        }
        WebElement editor = findElement(By.className("v-grid-editor"));
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver arg0) {
                int current = editor.getSize().getHeight();
                // it's actually expected to be the height of two rows plus one
                // pixel, but giving it 2 pixels of leeway
                int expected = targetCell.getSize().getHeight() * 2 - 1;
                return current >= expected;
            }

            @Override
            public String toString() {
                // Expected condition failed: waiting for ...
                return "editor to become visible, current height: "
                        + editor.getSize().getHeight() + ", row height: "
                        + targetCell.getSize().getHeight();
            }
        });
    }

    private void selectPage(String string) {
        $(NativeSelectElement.class).id("page").selectByText(string);
        grid = $(GridElement.class).first();
        waitUntilLoadingIndicatorNotVisible();
    }

}
