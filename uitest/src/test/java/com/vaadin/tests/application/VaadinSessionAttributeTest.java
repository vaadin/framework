package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class VaadinSessionAttributeTest extends MultiBrowserTest {

    @Test
    public void testSessionAttribute() {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertEquals("notification does not contain suitable text", "42 & 84",
                $(NotificationElement.class).first().getCaption());
    }

}
