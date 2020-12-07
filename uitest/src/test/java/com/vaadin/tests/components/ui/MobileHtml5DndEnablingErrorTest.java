package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class MobileHtml5DndEnablingErrorTest extends SingleBrowserTest {

    @Test
    public void testErrorMessage() {
        openTestURL();
        LabelElement label = $(LabelElement.class).id("error");
        assertTrue("Unexpected Label content: " + label.getText(),
                label.getText().startsWith("Error message: HTML5 DnD cannot"));
    }
}
