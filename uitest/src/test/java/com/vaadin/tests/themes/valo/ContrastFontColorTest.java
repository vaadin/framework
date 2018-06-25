package com.vaadin.tests.themes.valo;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for default contrast color variable in valo-font-color function.
 *
 * @author Vaadin Ltd
 */
public class ContrastFontColorTest extends MultiBrowserTest {

    @Test
    public void testTextColor() {
        openTestURL();

        String color = $(TextFieldElement.class).first().getCssValue("color");
        Assert.assertEquals(
                "Unexpected text color value using 0.1 as defualt contrast value :"
                        + color,
                "rgba(230, 230, 230, 1)", color);
    }
}
