package com.vaadin.v7.tests.components.nativeselect;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class NativeSelectNullTest extends SingleBrowserTestPhantomJS2 {
    @Test
    public void selectNull() {
        openTestURL();
        NativeSelectElement select = $(NativeSelectElement.class).first();
        select.selectByText("Item");
        Assert.assertEquals("1. Value: Item", getLogRow(0));
        select.selectByText("");
        Assert.assertEquals("2. Value: null", getLogRow(0));
    }
}
