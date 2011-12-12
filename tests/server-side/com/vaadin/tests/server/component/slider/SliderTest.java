package com.vaadin.tests.server.component.slider;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;

public class SliderTest extends TestCase {

    public void testOutOfBounds() {
        Slider s = new Slider(0, 10);
        s.setValue(0);
        Assert.assertEquals(0.0, s.getValue());
        s.setValue(10);
        Assert.assertEquals(10.0, s.getValue());
        try {
            s.setValue(20);
            fail("Should throw out of bounds exception");
        } catch (ValueOutOfBoundsException e) {
            // TODO: handle exception
        }

    }
}
