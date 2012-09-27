package com.vaadin.shared.ui.slider;

import com.vaadin.client.ui.slider.VSlider;
import com.vaadin.shared.AbstractFieldState;

public class SliderState extends AbstractFieldState {
    {
        primaryStyleName = VSlider.CLASSNAME;
    }

    public double value;

    public double maxValue = 100;
    public double minValue = 0;

    /**
     * The number of fractional digits that are considered significant. Must be
     * non-negative.
     */
    public int resolution = 0;

    public SliderOrientation orientation = SliderOrientation.HORIZONTAL;

}
