package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldKeyboardInputTest extends MultiBrowserTest {

    @Test
    public void testValueChangeEvent() {
        openTestURL();
        WebElement dateFieldText = $(DateFieldElement.class).first()
                .findElement(By.tagName("input"));
        dateFieldText.clear();
        int numLabelsBeforeUpdate = $(LabelElement.class).all().size();
        dateFieldText.sendKeys("20.10.2013", Keys.RETURN);
        int numLabelsAfterUpdate = $(LabelElement.class).all().size();
        assertTrue("Changing the date failed.",
                numLabelsAfterUpdate == numLabelsBeforeUpdate + 1);
    }
}