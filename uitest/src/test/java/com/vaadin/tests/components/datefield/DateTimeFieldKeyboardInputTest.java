package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldKeyboardInputTest extends MultiBrowserTest {

    @Test
    public void testValueChangeEvent() {
        openTestURL();
        WebElement dateFieldText = $(DateTimeFieldElement.class).first()
                .findElement(By.tagName("input"));
        dateFieldText.clear();
        int numLabelsBeforeUpdate = $(LabelElement.class).all().size();
        dateFieldText.sendKeys("20.10.2013 7:2", Keys.RETURN);
        int numLabelsAfterUpdate = $(LabelElement.class).all().size();
        assertTrue("Changing the date failed.",
                numLabelsAfterUpdate == numLabelsBeforeUpdate + 1);
    }
}
