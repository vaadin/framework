package com.vaadin.tests.components.textfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class EnumTextFieldTest extends SingleBrowserTest {
    @Test
    public void validValues() {
        openTestURL();
        $(TextFieldElement.class).first().clear();
        $(TextFieldElement.class).first().sendKeys("Value", Keys.TAB);
        assertEquals("3. Value (valid)", getLogRow(0));

        $(TextFieldElement.class).first().clear();
        $(TextFieldElement.class).first().sendKeys("VaLuE");
        $(TextFieldElement.class).first().sendKeys(Keys.TAB);
        assertEquals("5. Value (valid)", getLogRow(0));

        $(TextFieldElement.class).first().clear();
        $(TextFieldElement.class).first().sendKeys("The last value");
        $(TextFieldElement.class).first().sendKeys(Keys.TAB);
        assertEquals("7. The last value (valid)", getLogRow(0));

        $(TextFieldElement.class).first().clear();
        assertEquals("8. null (valid)", getLogRow(0));

    }

    @Test
    public void invalidValue() {
        openTestURL();
        $(TextFieldElement.class).first().clear();

        $(TextFieldElement.class).first().sendKeys("bar");
        $(TextFieldElement.class).first().sendKeys(Keys.TAB);
        assertEquals("3. bar (INVALID)", getLogRow(0));

    }
}
