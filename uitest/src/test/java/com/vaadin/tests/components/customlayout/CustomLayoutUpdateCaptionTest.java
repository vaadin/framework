package com.vaadin.tests.components.customlayout;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CustomLayoutUpdateCaptionTest extends SingleBrowserTest {

    @Test
    public void captionUpdated() {
        openTestURL();
        List<TextFieldElement> tfs = $(TextFieldElement.class).all();
        TextFieldElement tf1 = tfs.get(0);
        TextFieldElement tf2 = tfs.get(1);

        assertEquals("initial", tf1.getCaption());
        assertEquals("initial", tf2.getCaption());

        $(ButtonElement.class).first().click();

        assertEquals("updated", tf1.getCaption());
        assertEquals("updated", tf2.getCaption());

    }
}
