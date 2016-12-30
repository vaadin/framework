package com.vaadin.tests.elements.slider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.tests.elements.ComponentElementGetValue;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SliderGetValueTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ComponentElementGetValue.class;
    }

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void checkSlider() {
        SliderElement pb = $(SliderElement.class).get(0);
        String expected = "" + ComponentElementGetValue.TEST_SLIDER_VALUE;
        String actual = pb.getValue();
        Assert.assertEquals(expected, actual);
    }
}
