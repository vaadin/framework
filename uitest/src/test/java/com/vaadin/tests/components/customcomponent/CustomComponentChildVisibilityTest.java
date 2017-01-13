package com.vaadin.tests.components.customcomponent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CustomComponentChildVisibilityTest extends MultiBrowserTest {

    @Test
    public void childVisibilityIsSet() {
        openTestURL();

        assertTrue(isChildElementVisible());

        $(ButtonElement.class).first().click();

        assertFalse(isChildElementVisible());
    }

    private boolean isChildElementVisible() {
        return $(LabelElement.class).all().size() > 1;
    }

}
