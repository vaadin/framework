package com.vaadin.tests.server.component.slider;

import org.junit.Test;

import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Slider;

/**
 * Tests declarative support for implementations of {@link Slider}.
 *
 * @author Vaadin Ltd
 */
public class SliderDeclarativeTest extends DeclarativeTestBase<Slider> {

    @Test
    public void testDefault() {
        String design = "<vaadin-slider>";

        Slider expected = new Slider();

        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testHorizontal() {
        String design = "<vaadin-slider min=10 max=20 resolution=1 value=12.3>";

        Slider expected = new Slider();
        expected.setMin(10.0);
        expected.setMax(20.0);
        expected.setResolution(1);
        expected.setValue(12.3);

        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testVertical() {
        String design = "<vaadin-slider vertical>";

        Slider expected = new Slider();
        expected.setOrientation(SliderOrientation.VERTICAL);

        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin-slider readonly min=10 max=20 resolution=1 value=12.3>";

        Slider expected = new Slider();
        expected.setMin(10.0);
        expected.setMax(20.0);
        expected.setResolution(1);
        expected.setValue(12.3);
        expected.setReadOnly(true);

        testRead(design, expected);
        testWrite(design, expected);
    }
}
