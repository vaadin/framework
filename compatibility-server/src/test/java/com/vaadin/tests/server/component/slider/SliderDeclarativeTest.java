package com.vaadin.tests.server.component.slider;

import org.junit.Test;

import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.Slider;

/**
 * Tests declarative support for implementations of {@link Slider}.
 *
 * @since
 * @author Vaadin Ltd
 */
public class SliderDeclarativeTest
        extends AbstractFieldDeclarativeTest<Slider, Double> {

    @Override
    @Test
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        Double value = 12.3;
        int resolution = 1;
        String design = String.format("<%s resolution=%d value='%s'/>",
                getComponentTag(), resolution, value);

        Slider component = new Slider();
        component.setResolution(resolution);
        component.setValue(value);

        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testVertical() {
        String design = "<vaadin-slider vertical>";

        Slider expected = new Slider();
        expected.setOrientation(SliderOrientation.VERTICAL);

        testRead(design, expected);
        testWrite(design, expected);
    }

    @Override
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        Double value = 12.3;
        int resolution = 1;
        String design = String.format("<%s resolution=%d readonly value='%s'/>",
                getComponentTag(), resolution, value);

        Slider component = new Slider();
        component.setResolution(resolution);
        component.setValue(value);

        component.setReadOnly(true);
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void remainingAttributeDeserialization() {
        int min = 3;
        int max = 47;
        String design = String.format("<%s min=%d value=%d max='%d'/>",
                getComponentTag(), min, min, max);

        Slider component = new Slider();
        component.setMin(min);
        component.setMax(max);
        component.setOrientation(SliderOrientation.HORIZONTAL);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-slider";
    }

    @Override
    protected Class<Slider> getComponentClass() {
        return Slider.class;
    }

}
