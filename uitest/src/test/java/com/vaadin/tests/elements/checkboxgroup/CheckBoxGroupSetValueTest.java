package com.vaadin.tests.elements.checkboxgroup;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxGroupSetValueTest extends MultiBrowserTest {

    private static final String NEW_VALUE = "item2";

    private CheckBoxGroupElement group;

    @Before
    public void init() {
        openTestURL();
        group = $(CheckBoxGroupElement.class).first();

    }

    @Test
    public void testSetValue() {
        group.setValue(NEW_VALUE);
        Assert.assertEquals(Collections.singletonList(NEW_VALUE),
                group.getSelection());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(NEW_VALUE);
        Assert.assertEquals(Collections.singletonList(NEW_VALUE),
                group.getSelection());
    }

}
