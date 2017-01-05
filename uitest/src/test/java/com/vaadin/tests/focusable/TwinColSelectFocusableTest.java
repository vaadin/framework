package com.vaadin.tests.focusable;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.components.twincolselect.TwinColSelectTestUI;

public class TwinColSelectFocusableTest extends AbstractFocusableComponentTest {

    @Override
    protected Class<?> getUIClass() {
        return TwinColSelectTestUI.class;
    }

    @Override
    protected WebElement getFocusElement() {
        return findElement(By.className("v-select-twincol-options"));
    }
}
