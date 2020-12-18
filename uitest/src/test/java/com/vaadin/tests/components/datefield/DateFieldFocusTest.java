package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldFocusTest extends MultiBrowserTest {

    @Test
    public void focus() {
        openTestURL();

        assertEquals(" ", getLogRow(0));
        DateFieldElement dateField = $(DateFieldElement.class).first();
        TextFieldElement textField = $(TextFieldElement.class).caption("second")
                .first();

        // open DateField popup
        dateField.findElement(By.className("v-datefield-button")).click();
        waitUntilLoadingIndicatorNotVisible();
        // close DateField popup
        dateField.findElement(By.className("v-datefield-button")).click();
        waitUntilLoadingIndicatorNotVisible();

        assertEquals("1. focused", getLogRow(0));
        textField.focus();
        waitUntil(input -> "2. blurred".equals(getLogRow(0)));
    }
}
