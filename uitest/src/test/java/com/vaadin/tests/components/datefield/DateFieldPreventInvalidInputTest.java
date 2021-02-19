package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateFieldPreventInvalidInputTest extends SingleBrowserTest {

    @Test
    public void modifyValueAndPressTab() {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();

        WebElement dateTextbox = dateField
                .findElement(By.className("v-textfield"));
        ButtonElement button = $(ButtonElement.class).first();
        LabelElement label = $(LabelElement.class).id("value");

        // DateField is set not accept invalid input, this date is not in range
        dateTextbox.click();
        dateTextbox.sendKeys("01/01/21", Keys.TAB);
        assertEquals("no-value", label.getText());

        // Set DateField accept invalid input
        button.click();

        dateTextbox.click();
        dateTextbox.sendKeys("01/01/21", Keys.TAB);
        Assert.assertNotEquals("no-value", label.getText());
    }
}
