package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.CssLayoutElement;

public class ElementComponentGetCaptionCssLayoutTest
        extends ElementComponentGetCaptionBaseTest {
    @Override
    protected Class<?> getUIClass() {
        return ElementComponentGetCaptionCssLayout.class;
    }

    @Override
    protected void openTestURL() {
        openTestURL("theme=reindeer");
        mainLayout = $(CssLayoutElement.class).get(0);
    }
}
