package com.vaadin.tests.components.nativeselect;

import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.NativeSelectElement;

public class NativeSelectNullTest extends SingleBrowserTest {
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
