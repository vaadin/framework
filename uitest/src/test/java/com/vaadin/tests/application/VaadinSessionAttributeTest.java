package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.FixedNotificationElement;

public class VaadinSessionAttributeTest extends MultiBrowserTest {

    @Test
    public void testSessionAttribute() {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertEquals("notification does not contain suitable text", "42 & 84",
                $(FixedNotificationElement.class).first().getCaption());
    }

}
