package com.vaadin.tests.fonticon;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutOnFontLoadTest extends MultiBrowserTest {

    @Test
    public void testComponentsDontOverlap() throws Exception {
        openTestURL();

        // Make sure fonts are loaded.
        sleep(1000);

        ButtonElement button = $(ButtonElement.class).first();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        TextAreaElement textarea = $(TextAreaElement.class).first();
        GridElement grid = $(GridElement.class).first();

        assertTrue(
                "Button overlaps with checkbox (layout done before fonts loaded)",
                button.getLocation().getX() + button.getSize().width <= checkbox
                        .getLocation().getX());
        assertTrue(
                "TextArea overlaps with grid caption (layout done before fonts loaded)",
                textarea.getLocation().getY() + textarea.getSize().height
                        + 10 < grid.getLocation().getY());
    }

}
