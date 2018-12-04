package com.vaadin.tests.design;

import org.junit.Test;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

public class AbstractComponentSetResponsiveTest
        extends DeclarativeTestBase<Label> {

    @Test
    public void testResponsiveFlag() {
        Label label = new Label();
        label.setContentMode(ContentMode.HTML);
        label.setResponsive(true);
        String design = "<vaadin-label responsive />";
        testWrite(design, label);
        testRead(design, label);
    }

}
