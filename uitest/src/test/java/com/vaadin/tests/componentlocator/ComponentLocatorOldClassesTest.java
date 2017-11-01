package com.vaadin.tests.componentlocator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComponentLocatorOldClassesTest extends SingleBrowserTest {

    @Test
    public void testTestBenchFindsBothTextFields() {
        openTestURL();
        assertEquals("ComponentLocator did not find elements as expected.", 2,
                $(TextFieldElement.class).all().size());
    }

}
