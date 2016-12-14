package com.vaadin.tests.focusable;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.components.listselect.ListSelectTestUI;

public class ListSelectFocusableTest extends AbstractFocusableComponentTest {

    @Override
    protected Class<?> getUIClass() {
        return ListSelectTestUI.class;
    }

    @Override
    protected WebElement getFocusElement() {
        return findElement(By.className("v-select-select"));
    }
}
