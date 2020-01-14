package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldReadOnlyTest extends MultiBrowserTest {

    @Test
    public void readOnlyDateFieldPopupShouldNotOpen()
            throws IOException, InterruptedException {
        openTestURL();

        DateTimeFieldElement df = $(DateTimeFieldElement.class).first();
        WebElement dfButton = df
                .findElement(By.className("v-datefield-button"));

        // ensure initial read-only state works and pop-up cannot be opened
        assertTrue(df.hasClassName("v-readonly"));
        assertEquals("none", dfButton.getCssValue("display"));
        assertTrue(findElements(By.className("v-datefield-calendarpanel"))
                .isEmpty());

        assertFalse(openPopup(df));
        assertTrue(findElements(By.className("v-datefield-calendarpanel"))
                .isEmpty());

        // ensure read-only state can be removed and the component is still
        // functional
        toggleReadOnly();
        assertFalse(df.hasClassName("v-readonly"));
        assertEquals("inline-block", dfButton.getCssValue("display"));

        assertTrue(openPopup(df));
        assertEquals(1,
                findElements(By.className("v-datefield-calendarpanel")).size());

        // ensure read-only state can be re-applied, pop-up is closed and cannot
        // be re-opened
        toggleReadOnly();
        assertTrue(df.hasClassName("v-readonly"));
        assertEquals("none", dfButton.getCssValue("display"));
        assertTrue(findElements(By.className("v-datefield-calendarpanel"))
                .isEmpty());

        assertFalse(openPopup(df));
        assertTrue(findElements(By.className("v-datefield-calendarpanel"))
                .isEmpty());
    }

    private boolean openPopup(DateTimeFieldElement df) {
        // ensure the hidden button cannot be interacted with
        try {
            df.openPopup();
            return true;
        } catch (ElementNotInteractableException e) {
            return false;
        }
    }

    private void toggleReadOnly() {
        $(ButtonElement.class).caption("Switch read-only").first().click();
    }
}
