package com.vaadin.tests.focusable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.components.checkbox.CheckBoxGroupTestUI;

public class CheckBoxGroupFocusableTest extends AbstractFocusableComponentTest {

    @Override
    protected Class<?> getUIClass() {
        return CheckBoxGroupTestUI.class;
    }

    @Override
    protected WebElement getFocusElement() {
        return findElement(By.xpath("//input[@type='checkbox']"));
    }
}
