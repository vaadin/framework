package com.vaadin.tests.server.component.slider;

import junit.framework.TestCase;

import org.junit.Assert;

import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;

public class SliderTest extends TestCase {

    public void testOutOfBounds() {
        Slider s = new Slider(0, 10);
        s.setValue(0.0);
        Assert.assertEquals(0.0, s.getValue().doubleValue(), 0.001);
        s.setValue(10.0);
        Assert.assertEquals(10.0, s.getValue().doubleValue(), 0.001);
        try {
            s.setValue(20.0);
            fail("Should throw out of bounds exception");
        } catch (ValueOutOfBoundsException e) {
            // TODO: handle exception
        }

    }
}
