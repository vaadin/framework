/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.tokka.ui.components.fields;

import java.util.Collection;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.shared.tokka.ui.components.fields.SliderState;
import com.vaadin.shared.ui.Orientation;
import com.vaadin.shared.ui.slider.SliderServerRpc;
import com.vaadin.tokka.event.EventListener;
import com.vaadin.tokka.event.Registration;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * A component for selecting a numerical value within a range.
 * 
 * @author Vaadin Ltd.
 */
public class Slider extends AbstractField<Double> {

    public class SliderChange extends ValueChange<Double> {
        public SliderChange(boolean userOriginated) {
            super(Slider.this, userOriginated);
        }
    }

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

            setValue(Math.max(Math.min(value, getMax()), getMin()), true);
        }

    };

    /**
     * Default slider constructor.
     * <p>
     * The range of the slider is set to 0-100 and only integer values are
     * allowed.
     */
    public Slider() {
        super();
        registerRpc(rpc);
    }

    /**
     * Create a new slider with the caption given as parameter.
     * <p>
     * The range of the slider is set to 0-100 and only integer values are
     * allowed.
     * 
     * @param caption
     *            the caption for this slider (e.g. "Volume")
     */
    public Slider(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new slider with the given range and resolution.
     * 
     * @param min
     *            the minimum value of the slider
     * @param max
     *            the maximum value of the slider
     * @param resolution
     *            the number of digits after the decimal point.
     */
    public Slider(double min, double max, int resolution) {
        this();
        setMax(max);
        setMin(min);
        setResolution(resolution);
    }

    /**
     * Create a new slider with the given range of integers.
     * 
     * @param min
     *            the minimum value of the slider
     * @param max
     *            the maximum value of the slider
     */
    public Slider(int min, int max) {
        this(min, max, 0);
    }

    /**
     * Creates a new slider with the given caption and integer range.
     * 
     * @param caption
     *            the caption for the slider
     * @param min
     *            the minimum value of the slider
     * @param max
     *            the maximum value of the slider
     */
    public Slider(String caption, int min, int max) {
        this(min, max);
        setCaption(caption);
    }

    @Override
    public Double getValue() {
        return getState(false).value;
    }

    @Override
    public Registration addValueChangeListener(
            EventListener<ValueChange<Double>> listener) {
        return addListener(ValueChange.class, listener);
    }

    /**
     * Gets the maximum slider value. The default value is 100.0.
     * 
     * @return the largest value the slider can have
     */
    public double getMax() {
        return getState(false).maxValue;
    }

    /**
     * Sets the maximum slider value. If the current value of the slider is
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
     * Gets the minimum slider value. The default value is 0.0.
     * 
     * @return the smallest value the slider can have
     */
    public double getMin() {
        return getState(false).minValue;
    }

    /**
     * Sets the minimum slider value. If the current value of the slider is
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
     * Gets the current orientation of the slider (horizontal or vertical).
     * 
     * @return {@link Orientation#HORIZONTAL} or {@link Orientation#VERTICAL}
     */
    public Orientation getOrientation() {
        return getState(false).orientation;
    }

    /**
     * Sets the orientation of the slider.
     * 
     * @param orientation
     *            the new orientation, either {@link Orientation#HORIZONTAL} or
     *            {@link Orientation#VERTICAL}
     */
    public void setOrientation(Orientation orientation) {
        getState().orientation = orientation;
    }

    /**
     * Gets the resolution of the slider. The resolution is the number of digits
     * after the decimal point. The default resolution is 0 (only integers
     * allowed).
     * 
     * @return resolution
     */
    public int getResolution() {
        return getState(false).resolution;
    }

    /**
     * Sets a new resolution for the slider. The resolution is the number of
     * digits after the decimal point.
     * 
     * @throws IllegalArgumentException
     *             if resolution is negative.
     * 
     * @param resolution
     *            the resolution to set
     */
    public void setResolution(int resolution) {
        if (resolution < 0) {
            throw new IllegalArgumentException(
                    "Cannot set a negative resolution to Slider");
        }
        getState().resolution = resolution;
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

    /**
     * Sets the value of the slider.
     * 
     * @param value
     *            The new value of the slider.
     * @throws IllegalArgumentException
     *             If the given value is not inside the range of the slider.
     * @throws NullPointerException
     *             If the given value is null.
     * 
     * @see #setMin(double)
     * @see #setMax(double)
     */
    @Override
    public void setValue(Double value) {
        super.setValue(value);
    }

    @Override
    public void readDesign(Element design, DesignContext context) {
        super.readDesign(design, context);
        Attributes attr = design.attributes();
        if (attr.hasKey("vertical")) {
            setOrientation(Orientation.VERTICAL);
        }
        if (attr.hasKey("value")) {
            Double value = DesignAttributeHandler.readAttribute("value", attr,
                    Double.class);
            setValue(value);
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext context) {
        super.writeDesign(design, context);
        if (getOrientation() == Orientation.VERTICAL) {
            design.attr("vertical", true);
        }
        Slider defaultSlider = context.getDefaultInstance(this);
        DesignAttributeHandler.writeAttribute(this, "value",
                design.attributes(), defaultSlider);
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> result = super.getCustomAttributes();
        result.add("orientation");
        result.add("vertical");
        return result;
    }

    @Override
    protected void doSetValue(Double value) {
        double newValue = getRoundedValue(value);

        if (value.isNaN()) {
            throw new IllegalArgumentException("Value given is not a number");
        }

        if (getMin() > newValue || getMax() < newValue) {
            throw new IllegalArgumentException(
                    String.format("Value %s is out of bounds: [%s, %s]", value,
                            getMin(), getMax()));
        }

        getState().value = value;
    }

    @Override
    protected ValueChange<Double> createValueChange(boolean userOriginated) {
        return new SliderChange(userOriginated);
    }

    @Override
    protected SliderState getState() {
        return (SliderState) super.getState();
    }

    @Override
    protected SliderState getState(boolean markAsDirty) {
        return (SliderState) super.getState(markAsDirty);
    }
}
