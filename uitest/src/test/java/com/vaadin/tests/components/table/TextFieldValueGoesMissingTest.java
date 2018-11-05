package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that a text field's value isn't cleared after a label in the same
 * layout is changed.
 *
 * @since 7.3
 * @author Vaadin Ltd
 */
public class TextFieldValueGoesMissingTest extends MultiBrowserTest {

    /* This test was rewritten from a TB2 test. */
    @Test
    public void valueMissingTest() throws Exception {
        openTestURL();

        waitForElementVisible(By.className("v-textfield"));

        TextFieldElement textfield = $(TextFieldElement.class).first();
        textfield.focus();
        textfield.sendKeys("test");

        $(ButtonElement.class).first().click();

        new Actions(getDriver()).contextClick(textfield).perform();

        assertEquals("test", textfield.getValue());
    }
}
