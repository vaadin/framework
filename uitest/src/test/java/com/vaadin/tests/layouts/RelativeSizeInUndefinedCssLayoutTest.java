package com.vaadin.tests.layouts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class RelativeSizeInUndefinedCssLayoutTest
        extends SingleBrowserTest {

    @Test
    public void relativeSizeInUndefinedCssLayout() {
        openTestURL();
        int w = $(FormLayoutElement.class).first().getSize().getWidth();
        assertEquals(w, 520);

        int w2 = $(TextFieldElement.class).first().getSize().getWidth();
        assertTrue(w2 > 400);
    }
}
