package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.HorizontalLayoutElement;

public class ElementComponentGetCaptionHorizontalTest
        extends ElementComponentGetCaptionBaseTest {

    @Override
    protected Class<?> getUIClass() {
        return ElementComponentGetCaptionHorizontal.class;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();
        mainLayout = $(HorizontalLayoutElement.class).get(0);
    }
}
