package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.GridLayoutElement;

public class CompatibilityElementComponentGetCaptionGridLayoutTest
        extends CompatibilityElementComponentGetCaptionBaseTest {
    @Override
    protected Class<?> getUIClass() {
        return CompatibilityElementComponentGetCaptionGridLayout.class;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();
        mainLayout = $(GridLayoutElement.class).get(0);
    }
}
