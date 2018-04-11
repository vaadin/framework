package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.CssLayoutElement;

public class CompatibilityElementComponentGetCaptionCssLayoutTest
        extends CompatibilityElementComponentGetCaptionBaseTest {
    @Override
    protected Class<?> getUIClass() {
        return CompatibilityElementComponentGetCaptionCssLayout.class;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();
        mainLayout = $(CssLayoutElement.class).get(0);
    }
}
