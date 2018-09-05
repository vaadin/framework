package com.vaadin.tests.components.window;

import static com.vaadin.tests.components.window.BackspaceKeyWithModalOpened.BTN_NEXT_ID;
import static com.vaadin.tests.components.window.BackspaceKeyWithModalOpened.BTN_OPEN_MODAL_ID;
import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.Keys.BACK_SPACE;
import static org.openqa.selenium.Keys.TAB;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class BackspaceKeyWithModalOpenedTest extends MultiBrowserTest {

    /**
     * Tests that backspace in textfield does work
     */
    @Test
    public void testWithFocusOnInput() throws Exception {

        TextFieldElement textField = getTextField();

        // Try to delete characters in a text field.
        textField.sendKeys("textt");
        textField.sendKeys(BACK_SPACE);
        assertEquals("text", textField.getValue());
        checkButtonsCount();
    }

    /**
     * Tests that backspace action outside textfield is prevented
     */
    @Test
    public void testWithFocusOnModal() throws Exception {
        // Try to send back actions to the browser.
        new Actions(getDriver()).sendKeys(BACK_SPACE).perform();

        checkButtonsCount();
    }

    /**
     * Tests that backspace action in the bottom component is prevented.
     *
     * Ignored because the fix to #8855 stops the top and bottom components from
     * functioning as focus traps. Meanwhile, navigation with Backspace is not
     * anymore supported by reasonable browsers.
     */
    @Test
    @Ignore
    public void testWithFocusOnBottom() throws Exception {
        TextFieldElement textField = getTextField();

        // tab in last field set focus on bottom component
        textField.sendKeys(TAB);

        // Try to send back actions to the browser.
        new Actions(getDriver()).sendKeys(BACK_SPACE).perform();

        checkButtonsCount();
    }

    private TextFieldElement getTextField() {
        return $(TextFieldElement.class).first();
    }

    @Before
    public void testSetup() {
        openTestURL();
        WebElement nextButton = driver.findElement(By.id(BTN_NEXT_ID));
        nextButton.click();

        WebElement openModalButton = driver
                .findElement(By.id(BTN_OPEN_MODAL_ID));
        openModalButton.click();
    }

    /**
     * If there was a back navigation due to the backspace the next button
     * would've been added again
     */
    private void checkButtonsCount() {
        assertEquals(2, $(ButtonElement.class).all().size());
    }
}
