package com.vaadin.tests.components.abstractcomponent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ChangeHierarchyBeforeResponseTest extends SingleBrowserTest {
    @Test
    public void testHierarchyChangeBeforeResponse() {
        openTestURL();

        ButtonElement button = $(ButtonElement.class).first();

        assertEquals(
                "Button caption should change by its own beforeClientResponse",
                "Add label to layout", button.getText());

        button.click();

        LabelElement label = $(LabelElement.class).all().get(1);

        assertEquals("Label should have been considered initial twice",
                "Initial count: 2", label.getText());
    }
}
