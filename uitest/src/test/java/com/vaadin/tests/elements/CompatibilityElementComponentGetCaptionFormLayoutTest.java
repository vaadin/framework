package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.FormLayoutElement;

public class CompatibilityElementComponentGetCaptionFormLayoutTest
        extends CompatibilityElementComponentGetCaptionBaseTest {
    @Override
    protected Class<?> getUIClass() {
        return CompatibilityElementComponentGetCaptionFormLayout.class;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();
        mainLayout = $(FormLayoutElement.class).get(0);
    }
}
