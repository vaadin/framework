package com.vaadin.shared.ui.slider;

import com.vaadin.shared.AbstractFieldState;

public class SliderState extends AbstractFieldState {

    protected double value;

    protected double maxValue;
    protected double minValue;

    /**
     * The number of fractional digits that are considered significant. Must be
     * non-negative.
     */
    protected int resolution;

    protected SliderOrientation orientation;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public SliderOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(SliderOrientation orientation) {
        this.orientation = orientation;
    }

}
