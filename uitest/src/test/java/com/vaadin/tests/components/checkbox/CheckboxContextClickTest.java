package com.vaadin.tests.components.checkbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckboxContextClickTest extends MultiBrowserTest {

    @Test
    public void contextClickCheckboxAndText() {
        openTestURL();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        assertEquals("checked", checkbox.getValue());
        WebElement input = checkbox.findElement(By.xpath("input"));
        WebElement label = checkbox.findElement(By.xpath("label"));

        contextClickElement(input);
        assertEquals("1. checkbox context clicked", getLogRow(0));
        assertEquals("checked", checkbox.getValue());

        contextClickElement(label);
        assertEquals("2. checkbox context clicked", getLogRow(0));
        assertEquals("checked", checkbox.getValue());
    }

}
