package com.vaadin.tests.components.checkbox;

import static org.junit.Assert.assertEquals;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CheckboxLabelInputElementTest extends MultiBrowserTest {

    @Test
    public void contextClickCheckboxAndText() {
        openTestURL();
        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).first();
        WebElement labelElem = checkBoxElement.findElement(By.tagName("label"));
        WebElement inputElem = checkBoxElement.findElement(By.tagName("input"));

        assertEquals("my-label-class", labelElem.getAttribute("class"));
        assertEquals("my-input-class", inputElem.getAttribute("class"));

        $(ButtonElement.class).caption("add-style").first().click();

        assertEquals("my-label-class later-applied-label-class", labelElem.getAttribute("class"));
        assertEquals("my-input-class later-applied-input-class", inputElem.getAttribute("class"));

        $(ButtonElement.class).caption("remove-style").first().click();

        assertEquals("later-applied-label-class", labelElem.getAttribute("class"));
        assertEquals("later-applied-input-class", inputElem.getAttribute("class"));
    }
}
