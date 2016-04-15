package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;

public class DateFieldReadOnlyTest extends MultiBrowserTest {

    @Test
    public void readOnlyDateFieldPopupShouldNotOpen() throws IOException,
            InterruptedException {
        openTestURL();

        compareScreen("initial");
        toggleReadOnly();

        openPopup();
        compareScreen("readwrite-popup");

        closePopup();
        toggleReadOnly();
        compareScreen("readonly");
    }

    private void closePopup() {
        findElement(By.className("v-datefield-calendarpanel")).sendKeys(
                Keys.RETURN);
    }

    private void openPopup() {
        // waiting for openPopup() in TB4 beta1:
        // http://dev.vaadin.com/ticket/13766
        $(DateFieldElement.class).first().findElement(By.tagName("button"))
                .click();
    }

    private void toggleReadOnly() {
        $(ButtonElement.class).caption("Switch read-only").first().click();
    }
}
