package com.vaadin.tests.server.component.slider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vaadin.ui.Slider;

public class SliderTest {

    @Test
    public void minCannotBeLargerThanMax() {
        Slider slider = new Slider();

        slider.setMax(100);
        slider.setMin(101);

        assertEquals(101.0, slider.getMin(), 0);
        assertEquals(101.0, slider.getMax(), 0);
    }

    @Test
    public void maxCannotBeSmallerThanMin() {
        Slider slider = new Slider();

        slider.setMin(50);
        slider.setMax(10);

        assertEquals(10.0, slider.getMax(), 0);
        assertEquals(10.0, slider.getMin(), 0);
    }

    @Test
    public void valueOutOfBoundsExceptionMessageContainsBounds() {
        Slider slider = new Slider();

        try {

            slider.setValue(-1.0);
        } catch (Slider.ValueOutOfBoundsException e) {
            assertTrue(e.getMessage()
                    .contains("Value -1.0 is out of bounds: [0.0, 100.0]"));
        }
    }

    @Test
    public void valueIsSet() {
        Slider slider = new Slider();

        slider.setValue(5.0);

        assertEquals(5.0, slider.getValue(), 0);
    }

    @Test
    public void valueCannotBeOutOfBounds() {
        Slider s = new Slider(0, 10);

        try {
            s.setValue(20.0);
            fail("Should throw out of bounds exception");
        } catch (Slider.ValueOutOfBoundsException e) {
            // TODO: handle exception
        }
    }

    @Test
    public void valueCanHaveLargePrecision() {
        Slider slider = new Slider();
        slider.setResolution(20);

        slider.setValue(99.01234567891234567890123456789);

        assertEquals(99.01234567891234567890123456789, slider.getValue(), 0);
    }

    @Test
    public void doublesCanBeUsedAsLimits() {
        Slider slider = new Slider(1.5, 2.5, 1);

        assertEquals(1.5, slider.getMin(), 0);
        assertEquals(1.5, slider.getValue().doubleValue(), 0);
        assertEquals(2.5, slider.getMax(), 0);
    }

    @Test
    public void valuesGreaterThanIntMaxValueCanBeUsed() {
        double minValue = (double) Integer.MAX_VALUE + 1;

        Slider s = new Slider(minValue, minValue + 1, 0);

        assertEquals(minValue, s.getValue(), 0);
    }

    @Test
    public void negativeValuesCanBeUsed() {
        Slider slider = new Slider(-0.7, 1.0, 0);

        slider.setValue(-0.4);

        assertEquals(-0.0, slider.getValue(), 0);
    }

    @Test
    public void boundariesAreRounded() {
        Slider slider = new Slider(1.5, 2.5, 0);

        slider.setValue(1.0);

        assertEquals(1.0, slider.getValue(), 0);
        assertEquals(1.0, slider.getMin(), 0);
        assertEquals(2.0, slider.getMax(), 0);
    }

    @Test
    public void valueWithSmallerPrecisionCanBeUsed() {
        Slider slider = new Slider(0, 100, 10);

        slider.setValue(1.2);

        assertEquals(1.2, slider.getValue(), 0);
    }

    @Test
    public void valueWithLargerPrecisionCanBeUsed() {
        Slider slider = new Slider(0, 100, 2);

        slider.setValue(1.2345);

        assertEquals(1.23, slider.getValue(), 0);
    }
}
