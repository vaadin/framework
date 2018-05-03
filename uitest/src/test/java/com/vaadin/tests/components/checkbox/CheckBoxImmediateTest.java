package com.vaadin.tests.components.checkbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxImmediateTest extends MultiBrowserTest {

    @Test
    public void testNonImmediateCheckBox() {
        openTestURL();

        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).first();
        WebElement inputElem = checkBoxElement.findElement(By.tagName("input"));
        final WebElement countElem = $(LabelElement.class).id("count");

        inputElem.click();
        assertEquals("Events received: 0", countElem.getText());
    }

    @Test
    public void testImmediateCheckBox() {
        openTestURL();

        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).get(1);
        WebElement inputElem = checkBoxElement.findElement(By.tagName("input"));
        final WebElement countElem = $(LabelElement.class).id("count");

        inputElem.click();
        assertEquals("Events received: 1", countElem.getText());
    }
}
