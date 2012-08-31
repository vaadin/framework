package com.vaadin.shared.ui.slider;

import com.vaadin.shared.AbstractFieldState;

public class SliderState extends AbstractFieldState {

    public double value;

    public double maxValue;
    public double minValue;

    /**
     * The number of fractional digits that are considered significant. Must be
     * non-negative.
     */
    public int resolution;

    public SliderOrientation orientation;

}
