package com.vaadin.tests.components.grid;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import static org.junit.Assert.assertTrue;

@TestCategory("grid")
public class GridEditorEnableDisableTest extends SingleBrowserTest {
    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    @Test
    public void testEnabledEditor() {
        GridElement grid = $(GridElement.class).first();
        grid.getCell(0, 0).doubleClick();

        assertTrue("Editor must work when it is enabled!",
                isElementPresent(TextFieldElement.class));
    }

    @Test
    public void testDisabledEditor() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement disableButton = $(ButtonElement.class).caption("Disable")
                .first();

        disableButton.click();
        grid.getCell(0, 0).doubleClick();

        assertTrue("Editor must not work when it is disabled!",
                !isElementPresent(TextFieldElement.class));
    }

    @Test
    public void testCancelAndDisableEditorWhenEditing() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement cancelAndDisableButton = $(ButtonElement.class)
                .caption("Cancel & Disable").first();

        grid.getCell(0, 0).doubleClick();
        cancelAndDisableButton.click();

        assertTrue("Editing must be canceled after calling cancel method!",
                !isElementPresent(TextFieldElement.class));

        grid.getCell(0, 0).doubleClick();
        assertTrue("Editor must not work when it is disabled!",
                !isElementPresent(TextFieldElement.class));
    }

    @Test
    public void testDisableEditorAfterCancelEditing() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement disableButton = $(ButtonElement.class).caption("Disable")
                .first();

        grid.getCell(0, 0).doubleClick();
        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();

        assertTrue("Editing must be canceled after pressing Escape key!",
                !isElementPresent(TextFieldElement.class));

        disableButton.click();
        grid.getCell(0, 0).doubleClick();

        assertTrue("Editor must not work when it is disabled!",
                !isElementPresent(TextFieldElement.class));
    }

    @Test
    public void testReenableEditorAfterCancelEditing() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement cancelAndDisableButton = $(ButtonElement.class)
                .caption("Cancel & Disable").first();
        ButtonElement enableButton = $(ButtonElement.class).caption("Enable")
                .first();

        grid.getCell(0, 0).doubleClick();
        cancelAndDisableButton.click();
        enableButton.click();
        grid.getCell(0, 0).doubleClick();

        assertTrue("Editor must work after re-enabling!",
                isElementPresent(TextFieldElement.class));
    }

    @Test
    public void testEnableAndEditRow() {
        ButtonElement disableButton = $(ButtonElement.class).caption("Disable")
                .first();
        ButtonElement enableAndEditRowButton = $(ButtonElement.class)
                .caption("Enable & Edit Row").first();

        disableButton.click();
        enableAndEditRowButton.click();

        assertTrue("Editor must be open after calling editRow method!",
                isElementPresent(TextFieldElement.class));
    }
}
