package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.VerticalLayoutElement;

public class CompatibilityElementComponentGetCaptionVerticalLayoutTest
        extends CompatibilityElementComponentGetCaptionBaseTest {
    @Override
    protected Class<?> getUIClass() {
        return CompatibilityElementComponentGetCaptionVerticalLayout.class;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();
        mainLayout = $(VerticalLayoutElement.class).get(2);
    }
}
