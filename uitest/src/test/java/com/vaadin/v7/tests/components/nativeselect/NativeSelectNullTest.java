package com.vaadin.v7.tests.components.nativeselect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class NativeSelectNullTest extends SingleBrowserTestPhantomJS2 {
    @Test
    public void selectNull() {
        openTestURL();
        NativeSelectElement select = $(NativeSelectElement.class).first();
        select.selectByText("Item");
        assertEquals("1. Value: Item", getLogRow(0));
        select.selectByText("");
        assertEquals("2. Value: null", getLogRow(0));
    }
}
