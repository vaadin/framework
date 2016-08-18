package com.vaadin.testbench.customelements;

import org.openqa.selenium.By;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.DateField")
public class DateFieldElement
        extends com.vaadin.testbench.elements.DateFieldElement {
    public void openPopup() {
        findElement(By.tagName("button")).click();
    }
}
