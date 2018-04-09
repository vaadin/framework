package com.vaadin.tests.elements;

import com.vaadin.testbench.elements.VerticalLayoutElement;

public class ElementComponentGetCaptionVerticalLayoutTest
        extends ElementComponentGetCaptionBaseTest {
    @Override
    protected Class<?> getUIClass() {
        return ElementComponentGetCaptionVerticalLayout.class;
    }

    @Override
    protected void openTestURL() {
        openTestURL("theme=reindeer");
        mainLayout = $(VerticalLayoutElement.class).get(2);
    }
}
