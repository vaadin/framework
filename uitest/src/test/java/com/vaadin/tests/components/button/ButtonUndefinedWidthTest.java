package com.vaadin.tests.components.button;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Validates button Widths for Buttons or Native Buttons, inside or outside
 * tables.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class ButtonUndefinedWidthTest extends MultiBrowserTest {

    @Test
    public void undefinedButtonWidthTest() throws Exception {
        openTestURL();

        // make sure all the elements are rendered before commencing tests
        waitForElementVisible(By.className("v-table-row-odd"));

        // click all of the buttons
        for (NativeButtonElement button : $(NativeButtonElement.class).all()) {
            button.click();
        }
        for (ButtonElement button : $(ButtonElement.class).all()) {
            button.click();
        }

        // remove focus
        getDriver().findElement(By.className("v-app")).click();

        // check button widths in VerticalLayout
        VerticalLayoutElement vLayout = $(VerticalLayoutElement.class)
                .$(VerticalLayoutElement.class).first();
        int containerWidth = vLayout.getSize().getWidth();

        NativeButtonElement nativeButton = vLayout.$(NativeButtonElement.class)
                .first();
        int buttonWidth = nativeButton.getSize().getWidth();

        assertButtonWidth(buttonWidth, containerWidth);

        ButtonElement button = vLayout.$(ButtonElement.class).first();
        buttonWidth = button.getSize().getWidth();
        assertButtonWidth(buttonWidth, containerWidth);

        // check button widths in table, also make sure that there is some
        // spacing between the table edges and buttons
        List<WebElement> rows = findElements(
                By.className("v-table-cell-content"));
        int rowWidth = rows.get(0).getSize().getWidth();

        List<WebElement> rowWrappers = findElements(
                By.className("v-table-cell-wrapper"));
        WebElement row = rowWrappers.get(0);

        containerWidth = row.getSize().getWidth();
        assertRowWrapperWidth(containerWidth, rowWidth);

        buttonWidth = row.findElement(By.className("v-button")).getSize()
                .getWidth();
        assertButtonWidth(buttonWidth, containerWidth);

        row = rowWrappers.get(1);
        containerWidth = row.getSize().getWidth();
        assertRowWrapperWidth(containerWidth, rowWidth);

        buttonWidth = row.findElement(By.className("v-nativebutton")).getSize()
                .getWidth();
        assertButtonWidth(buttonWidth, containerWidth);

    }

    private void assertRowWrapperWidth(int wrapperWidth, int rowWidth) {
        assertTrue("Wrapper should be narrower than its parent: " + wrapperWidth
                + " < " + rowWidth, wrapperWidth < rowWidth);
    }

    private void assertButtonWidth(int buttonWidth, int containerWidth) {
        assertTrue("Button should be narrower than its parent: " + buttonWidth
                + " < " + containerWidth, buttonWidth < containerWidth);
    }
}
