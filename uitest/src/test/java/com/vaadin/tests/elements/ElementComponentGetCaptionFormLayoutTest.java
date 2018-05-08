package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.FormLayoutElement;

public class ElementComponentGetCaptionFormLayoutTest
        extends ElementComponentGetCaptionBaseTest {
    @Override
    protected Class<?> getUIClass() {
        return ElementComponentGetCaptionFormLayout.class;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();
        mainLayout = $(FormLayoutElement.class).get(0);
    }
}
