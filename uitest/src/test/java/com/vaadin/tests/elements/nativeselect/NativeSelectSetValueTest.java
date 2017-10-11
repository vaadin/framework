package com.vaadin.tests.elements.nativeselect;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NativeSelectSetValueTest extends MultiBrowserTest {

    NativeSelectElement select;
    LabelElement counter;

    @Before
    public void init() {
        openTestURL();
        select = $(NativeSelectElement.class).get(0);
        counter = $(LabelElement.class).id("counter");
    }

    @Test
    public void testSetValue() throws InterruptedException {
        select.setValue("item 2");
        checkTestValue();
    }

    @Test
    public void testSelectByText() {
        select.selectByText("item 2");
        checkTestValue();
    }

    private void checkTestValue() {
        // checks value has changed
        String actual = select.getValue();
        assertEquals("item 2", actual);
        // checks change value event occures
        assertEquals("1", counter.getText());
    }
}
