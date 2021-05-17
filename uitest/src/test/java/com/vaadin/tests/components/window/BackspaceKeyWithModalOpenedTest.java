package com.vaadin.tests.components.window;

import static com.vaadin.tests.components.window.BackspaceKeyWithModalOpened.BTN_NEXT_ID;
import static com.vaadin.tests.components.window.BackspaceKeyWithModalOpened.BTN_OPEN_MODAL_ID;
import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.Keys.BACK_SPACE;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

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
