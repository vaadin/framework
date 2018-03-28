package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.GridLayoutElement;

public class ElementComponentGetCaptionGridLayoutTest
        extends ElementComponentGetCaptionBaseTest {
    @Override
    protected Class<?> getUIClass() {
        return ElementComponentGetCaptionGridLayout.class;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();
        mainLayout = $(GridLayoutElement.class).get(0);
    }
}
