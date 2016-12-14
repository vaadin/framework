package com.vaadin.tests.focusable;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.components.nativeselect.NativeSelects;

public class NativeSelectFocusableTest extends AbstractFocusableComponentTest {

    @Override
    protected Class<?> getUIClass() {
        return NativeSelects.class;
    }

    @Override
    protected WebElement getFocusElement() {
        return findElement(By.className("v-select-select"));
    }
}
