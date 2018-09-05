package com.vaadin.tests.themes.valo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

/**
 * Test for default contrast color variable in valo-font-color function.
 *
 * @author Vaadin Ltd
 */
public class ContrastFontColorTest extends SingleBrowserTest {

    @Test
    public void testTextColor() {
        openTestURL();

        String color = $(TextFieldElement.class).first().getCssValue("color");
        assertEquals(
                "Unexpected text color value using 0.1 as defualt contrast value :"
                        + color,
                "rgba(230, 230, 230, 1)", color);
    }
}
