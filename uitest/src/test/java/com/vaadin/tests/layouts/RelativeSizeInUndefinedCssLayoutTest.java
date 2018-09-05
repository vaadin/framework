package com.vaadin.tests.layouts;

import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.TextFieldElement;

public class RelativeSizeInUndefinedCssLayoutTest
        extends SingleBrowserTest {

    @Test
    public void relativeSizeInUndefinedCssLayout() {
        openTestURL();
        int w = $(FormLayoutElement.class).first().getSize().getWidth();
        Assert.assertEquals(w, 520);

        int w2 = $(TextFieldElement.class).first().getSize().getWidth();
        Assert.assertTrue(w2 > 400);
    }
}
