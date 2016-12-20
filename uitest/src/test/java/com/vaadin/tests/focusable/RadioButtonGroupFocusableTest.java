package com.vaadin.tests.focusable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.components.radiobutton.RadioButtonGroupTestUI;

public class RadioButtonGroupFocusableTest
        extends AbstractFocusableComponentTest {

    @Override
    protected Class<?> getUIClass() {
        return RadioButtonGroupTestUI.class;
    }

    @Override
    protected WebElement getFocusElement() {
        return findElement(By.xpath("//input[@type='radio']"));
    }
}
