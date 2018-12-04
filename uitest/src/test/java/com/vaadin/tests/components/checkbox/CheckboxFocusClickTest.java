package com.vaadin.tests.components.checkbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckboxFocusClickTest extends MultiBrowserTest {

    @Test
    public void contextClickCheckboxAndText() {
        openTestURL();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        assertEquals("checked", checkbox.getValue());
        WebElement label = checkbox.findElement(By.xpath("label"));

        label.click();
        assertEquals("1. checkbox focused", getLogRow(0));
    }
}
