package com.vaadin.tests.layouts;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class RelativeSizeInUndefinedCssLayoutTest
        extends SingleBrowserTestPhantomJS2 {

    @Test
    public void relativeSizeInUndefinedCssLayout() {
        openTestURL();
        int w = $(FormLayoutElement.class).first().getSize().getWidth();
        Assert.assertEquals(w, 520);

        int w2 = $(TextFieldElement.class).first().getSize().getWidth();
        Assert.assertTrue(w2 > 400);
    }
}
