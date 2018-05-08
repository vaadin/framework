package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.HorizontalLayoutElement;

public class CompatibilityElementComponentGetCaptionHorizontalTest
        extends CompatibilityElementComponentGetCaptionBaseTest {

    @Override
    protected Class<?> getUIClass() {
        return CompatibilityElementComponentGetCaptionHorizontal.class;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();
        mainLayout = $(HorizontalLayoutElement.class).get(0);
    }
}
