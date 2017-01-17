package com.vaadin.tests.elements.radiobuttongroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RadioButtonGroupSetValueTest extends MultiBrowserTest {

    private static final String NEW_VALUE = "item2";

    private RadioButtonGroupElement group;

    @Before
    public void init() {
        openTestURL();
        group = $(RadioButtonGroupElement.class).first();
    }

    @Test
    public void testSetValue() {
        group.setValue(NEW_VALUE);
        Assert.assertEquals(NEW_VALUE, group.getValue());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(NEW_VALUE);
        Assert.assertEquals(NEW_VALUE, group.getValue());
    }

}
