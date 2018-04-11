/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.v7.ui;

import java.util.Collection;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.v7.shared.ui.slider.SliderOrientation;
import com.vaadin.v7.shared.ui.slider.SliderServerRpc;
import com.vaadin.v7.shared.ui.slider.SliderState;;

/**
 * A component for selecting a numerical value within a range.
 *
 * @author Vaadin Ltd.
 *
 * @deprecated As of 8.0 replaced by {@link com.vaadin.ui.Slider} based on the
 *             new data binding API
 */
@Deprecated
public class Slider extends AbstractField<Double> {

    private SliderServerRpc rpc = new SliderServerRpc() {

        @Override
        public void valueChanged(double value) {

            /*
             * Client side updates the state before sending the event so we need
             * to make sure the cached state is updated to match the client. If
             * we do not do this, a reverting setValue() call in a listener will
             * not cause the new state to be sent to the client.
             *
             * See #12133.
             */
            getUI().getConnectorTracker().getDiffState(Slider.this).put("value",
                    value);

            try {
                setValue(value, true);
            } catch (final ValueOutOfBoundsException e) {
                // Convert to nearest bound
                double out = e.getValue().doubleValue();
                if (out < getState().minValue) {
                    out = getState().minValue;
                }
                if (out > getState().maxValue) {
                    out = getState().maxValue;
                }
                Slider.super.setValue(new Double(out), false);
            }
        }

    };

    /**
     * Default slider constructor. Sets all values to defaults and the slide
     * handle at minimum value.
     *
     */
    public Slider() {
        super();
        registerRpc(rpc);
        super.setValue(new Double(getState().minValue));
    }

    /**
     * Create a new slider with the caption given as parameter.
     *
     * The range of the slider is set to 0-100 and only integer values are
     * allowed.
     *
     * @param caption
     *            The caption for this slider (e.g. "Volume").
     */
    public Slider(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Create a new slider with the given range and resolution.
     *
     * @param min
     *            The minimum value of the slider
     * @param max
     *            The maximum value of the slider
     * @param resolution
     *            The number of digits after the decimal point.
     */
    public Slider(double min, double max, int resolution) {
        this();
        setResolution(resolution);
        setMax(max);
        setMin(min);
    }

    /**
     * Create a new slider with the given range that only allows integer values.
     *
     * @param min
     *            The minimum value of the slider
     * @param max
     *            The maximum value of the slider
     */
    public Slider(int min, int max) {
        this();
        setMin(min);
        setMax(max);
        setResolution(0);
    }

    /**
     * Create a new slider with the given caption and range that only allows
     * integer values.
     *
     * @param caption
     *            The caption for the slider
     * @param min
     *            The minimum value of the slider
     * @param max
     *            The maximum value of the slider
     */
    public Slider(String caption, int min, int max) {
        this(min, max);
        setCaption(caption);
    }

    @Override
    public SliderState getState() {
        return (SliderState) super.getState();
    }

    @Override
    public SliderState getState(boolean markAsDirty) {
        return (SliderState) super.getState(markAsDirty);
    }

    /**
     * Gets the maximum slider value.
     *
     * @return the largest value the slider can have
     */
    public double getMax() {
        return getState(false).maxValue;
    }

    /**
     * Set the maximum slider value. If the current value of the slider is
     * larger than this, the value is set to the new maximum.
     *
     * @param max
     *            The new maximum slider value
     */
    public void setMax(double max) {
        double roundedMax = getRoundedValue(max);
        getState().maxValue = roundedMax;

        if (getMin() > roundedMax) {
            getState().minValue = roundedMax;
        }

        if (getValue() > roundedMax) {
            setValue(roundedMax);
        }
    }

    /**
     * Gets the minimum slider value.
     *
     * @return the smallest value the slider can have
     */
    public double getMin() {
        return getState(false).minValue;
    }

    /**
     * Set the minimum slider value. If the current value of the slider is
     * smaller than this, the value is set to the new minimum.
     *
     * @param min
     *            The new minimum slider value
     */
    public void setMin(double min) {
        double roundedMin = getRoundedValue(min);
        getState().minValue = roundedMin;

        if (getMax() < roundedMin) {
            getState().maxValue = roundedMin;
        }

        if (getValue() < roundedMin) {
            setValue(roundedMin);
        }
    }

    /**
     * Get the current orientation of the slider (horizontal or vertical).
     *
     * @return {@link SliderOrientation#HORIZONTAL} or
     *         {@link SliderOrientation#VERTICAL}
     */
    public SliderOrientation getOrientation() {
        return getState(false).orientation;
    }

    /**
     * Set the orientation of the slider.
     *
     * @param orientation
     *            The new orientation, either
     *            {@link SliderOrientation#HORIZONTAL} or
     *            {@link SliderOrientation#VERTICAL}
     */
    public void setOrientation(SliderOrientation orientation) {
        getState().orientation = orientation;
    }

    /**
     * Get the current resolution of the slider. The resolution is the number of
     * digits after the decimal point.
     *
     * @return resolution
     */
    public int getResolution() {
        return getState(false).resolution;
    }

    /**
     * Set a new resolution for the slider. The resolution is the number of
     * digits after the decimal point.
     *
     * @throws IllegalArgumentException
     *             if resolution is negative.
     *
     * @param resolution
     */
    public void setResolution(int resolution) {
        if (resolution < 0) {
            throw new IllegalArgumentException(
                    "Cannot set a negative resolution to Slider");
        }
        getState().resolution = resolution;
    }

    /**
     * Sets the value of the slider.
     *
     * @param value
     *            The new value of the slider.
     * @param repaintIsNotNeeded
     *            If true, client-side is not requested to repaint itself.
     * @throws ValueOutOfBoundsException
     *             If the given value is not inside the range of the slider.
     * @see #setMin(double) {@link #setMax(double)}
     */
    @Override
    protected void setValue(Double value, boolean repaintIsNotNeeded) {
        double newValue = getRoundedValue(value);

        if (getMin() > newValue || getMax() < newValue) {
            throw new ValueOutOfBoundsException(newValue);
        }

        getState().value = newValue;
        super.setValue(newValue, repaintIsNotNeeded);
    }

    private double getRoundedValue(Double value) {
        final double v = value.doubleValue();
        final int resolution = getResolution();

        double ratio = Math.pow(10, resolution);
        if (v >= 0) {
            return Math.floor(v * ratio) / ratio;
        } else {
            return Math.ceil(v * ratio) / ratio;
        }
    }

    @Override
    public void setValue(Double newFieldValue) {
        super.setValue(newFieldValue);
        getState().value = newFieldValue;
    }

    /*
     * Overridden to keep the shared state in sync with the AbstractField
     * internal value. Should be removed once AbstractField is refactored to use
     * shared state.
     *
     * See tickets #10921 and #11064.
     */
    @Override
    protected void setInternalValue(Double newValue) {
        super.setInternalValue(newValue);
        if (newValue == null) {
            newValue = 0.0;
        }
        getState().value = newValue;
    }

    /**
     * Thrown when the value of the slider is about to be set to a value that is
     * outside the valid range of the slider.
     *
     * @author Vaadin Ltd.
     *
     */
    @Deprecated
    public class ValueOutOfBoundsException extends RuntimeException {

        private final Double value;

        /**
         * Constructs an <code>ValueOutOfBoundsException</code> with the
         * specified detail message.
         *
         * @param valueOutOfBounds
         */
        public ValueOutOfBoundsException(Double valueOutOfBounds) {
            super(String.format("Value %s is out of bounds: [%s, %s]",
                    valueOutOfBounds, getMin(), getMax()));
            value = valueOutOfBounds;
        }

        /**
         * Gets the value that is outside the valid range of the slider.
         *
         * @return the value that is out of bounds
         */
        public Double getValue() {
            return value;
        }
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public void clear() {
        super.setValue(Double.valueOf(getState().minValue));
    }

    @Override
    public boolean isEmpty() {
        // Slider is never really "empty"
        return false;
    }

    @Override
    public void readDesign(Element design, DesignContext context) {
        super.readDesign(design, context);
        Attributes attr = design.attributes();
        if (attr.hasKey("vertical")) {
            setOrientation(SliderOrientation.VERTICAL);
        }
        if (attr.hasKey("value")) {
            Double newFieldValue = DesignAttributeHandler.readAttribute("value",
                    attr, Double.class);
            setValue(newFieldValue, false, true);
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext context) {
        super.writeDesign(design, context);
        if (getOrientation() == SliderOrientation.VERTICAL) {
            design.attr("vertical", true);
        }
        Slider defaultSlider = context.getDefaultInstance(this);
        DesignAttributeHandler.writeAttribute(this, "value",
                design.attributes(), defaultSlider, context);
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        result.add("orientation");
        result.add("vertical");
        return result;
    }
}
