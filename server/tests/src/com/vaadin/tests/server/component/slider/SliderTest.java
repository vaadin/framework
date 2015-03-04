package com.vaadin.tests.server.component.slider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Slider;

public class SliderTest {

    @Test
    public void minCannotBeLargerThanMax() {
        Slider slider = new Slider();

        slider.setMax(100);
        slider.setMin(101);

        assertThat(slider.getMin(), is(101.0));
        assertThat(slider.getMax(), is(101.0));
    }

    @Test
    public void maxCannotBeSmallerThanMin() {
        Slider slider = new Slider();

        slider.setMin(50);
        slider.setMax(10);

        assertThat(slider.getMax(), is(10.0));
        assertThat(slider.getMin(), is(10.0));
    }

    @Test
    public void valueOutOfBoundsExceptionMessageContainsBounds() {
        Slider slider = new Slider();

        try {

            slider.setValue(-1.0);
        } catch (Slider.ValueOutOfBoundsException e) {
            assertThat(e.getMessage(),
                    containsString("Value -1.0 is out of bounds: [0.0, 100.0]"));
        }
    }

    @Test
    public void valueIsSet() {
        Slider slider = new Slider();

        slider.setValue(5.0);

        assertThat(slider.getValue(), is(5.0));
    }

    @Test
    public void valueCannotBeOutOfBounds() {
        Slider s = new Slider(0, 10);

        try {
            s.setValue(20.0);
            Assert.fail("Should throw out of bounds exception");
        } catch (Slider.ValueOutOfBoundsException e) {
            // TODO: handle exception
        }

    }
}
