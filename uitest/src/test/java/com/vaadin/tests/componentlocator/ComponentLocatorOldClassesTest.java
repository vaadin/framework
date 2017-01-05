package com.vaadin.tests.componentlocator;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComponentLocatorOldClassesTest extends SingleBrowserTest {

    @Test
    public void testTestBenchFindsBothTextFields() {
        openTestURL();
        Assert.assertEquals(
                "ComponentLocator did not find elements as expected.", 2,
                $(TextFieldElement.class).all().size());
    }

}
