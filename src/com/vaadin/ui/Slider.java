/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VSliderPaintable;

/**
 * A component for selecting a numerical value within a range.
 * 
 * Example code: <code>
 * 	class MyPlayer extends CustomComponent implements ValueChangeListener {
 * 		
 * 		Label volumeIndicator = new Label();
 * 		Slider slider;
 * 		
 * 		public MyPlayer() {
 * 			VerticalLayout vl = new VerticalLayout();
 * 			setCompositionRoot(vl);
 * 			slider = new Slider("Volume", 0, 100);
 * 			slider.setImmediate(true);
 *                      slider.setValue(new Double(50));
 * 			vl.addComponent(slider);
 * 			vl.addComponent(volumeIndicator);
 * 			volumeIndicator.setValue("Current volume:" + 50.0);
 * 			slider.addListener(this);
 * 			
 * 		}
 * 		
 * 		public void setVolume(double d) {
 * 			volumeIndicator.setValue("Current volume: " + d);
 * 		}
 * 
 * 		public void valueChange(ValueChangeEvent event) {
 * 			Double d = (Double) event.getProperty().getValue();
 * 			setVolume(d.doubleValue());
 * 		}
 * 	}
 * 
 * </code>
 * 
 * @author Vaadin Ltd.
 */
@ClientWidget(VSliderPaintable.class)
public class Slider extends AbstractField<Double> {

    public static final int ORIENTATION_HORIZONTAL = 0;

    public static final int ORIENTATION_VERTICAL = 1;

    /** Minimum value of slider */
    private double min = 0;

    /** Maximum value of slider */
    private double max = 100;

    /**
     * Resolution, how many digits are considered relevant after the decimal
     * point. Must be a non-negative value
     */
    private int resolution = 0;

    /**
     * Slider orientation (horizontal/vertical), defaults .
     */
    private int orientation = ORIENTATION_HORIZONTAL;

    /**
     * Default slider constructor. Sets all values to defaults and the slide
     * handle at minimum value.
     * 
     */
    public Slider() {
        super();
        super.setValue(new Double(min));
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
        setMin(min);
        setMax(max);
        setResolution(resolution);
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

    /**
     * Gets the maximum slider value
     * 
     * @return the largest value the slider can have
     */
    public double getMax() {
        return max;
    }

    /**
     * Set the maximum slider value. If the current value of the slider is
     * larger than this, the value is set to the new maximum.
     * 
     * @param max
     *            The new maximum slider value
     */
    public void setMax(double max) {
        this.max = max;
        if (getValue() > max) {
            setValue(max);
        }
        requestRepaint();
    }

    /**
     * Gets the minimum slider value
     * 
     * @return the smallest value the slider can have
     */
    public double getMin() {
        return min;
    }

    /**
     * Set the minimum slider value. If the current value of the slider is
     * smaller than this, the value is set to the new minimum.
     * 
     * @param max
     *            The new minimum slider value
     */
    public void setMin(double min) {
        this.min = min;
        if (getValue() < min) {
            setValue(min);
        }
        requestRepaint();
    }

    /**
     * Get the current orientation of the slider (horizontal or vertical).
     * 
     * @return {@link #ORIENTATION_HORIZONTAL} or
     *         {@link #ORIENTATION_HORIZONTAL}
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation of the slider.
     * 
     * @param The
     *            new orientation, either {@link #ORIENTATION_HORIZONTAL} or
     *            {@link #ORIENTATION_VERTICAL}
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        requestRepaint();
    }

    /**
     * Get the current resolution of the slider. The resolution is the number of
     * digits after the decimal point.
     * 
     * @return resolution
     */
    public int getResolution() {
        return resolution;
    }

    /**
     * Set a new resolution for the slider. The resolution is the number of
     * digits after the decimal point.
     * 
     * @param resolution
     */
    public void setResolution(int resolution) {
        if (resolution < 0) {
            return;
        }
        this.resolution = resolution;
        requestRepaint();
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
        final double v = value.doubleValue();
        double newValue;
        if (resolution > 0) {
            // Round up to resolution
            newValue = (int) (v * Math.pow(10, resolution));
            newValue = newValue / Math.pow(10, resolution);
            if (min > newValue || max < newValue) {
                throw new ValueOutOfBoundsException(value);
            }
        } else {
            newValue = (int) v;
            if (min > newValue || max < newValue) {
                throw new ValueOutOfBoundsException(value);
            }
        }
        super.setValue(newValue, repaintIsNotNeeded);
    }

    @Override
    public void setValue(Object newFieldValue)
            throws com.vaadin.data.Property.ReadOnlyException {
        if (newFieldValue != null && newFieldValue instanceof Number
                && !(newFieldValue instanceof Double)) {
            // Support setting all types of Numbers
            newFieldValue = ((Number) newFieldValue).doubleValue();
        }

        super.setValue(newFieldValue);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        target.addAttribute("min", min);
        if (max > min) {
            target.addAttribute("max", max);
        } else {
            target.addAttribute("max", min);
        }
        target.addAttribute("resolution", resolution);

        if (resolution > 0) {
            target.addVariable(this, "value", getValue().doubleValue());
        } else {
            target.addVariable(this, "value", getValue().intValue());
        }

        if (orientation == ORIENTATION_VERTICAL) {
            target.addAttribute("vertical", true);
        }

    }

    /**
     * Invoked when the value of a variable has changed. Slider listeners are
     * notified if the slider value has changed.
     * 
     * @param source
     * @param variables
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        if (variables.containsKey("value")) {
            final Object value = variables.get("value");
            final Double newValue = new Double(value.toString());
            if (newValue != null && newValue != getValue()
                    && !newValue.equals(getValue())) {
                try {
                    setValue(newValue, true);
                } catch (final ValueOutOfBoundsException e) {
                    // Convert to nearest bound
                    double out = e.getValue().doubleValue();
                    if (out < min) {
                        out = min;
                    }
                    if (out > max) {
                        out = max;
                    }
                    super.setValue(new Double(out), false);
                }
            }
        }
    }

    /**
     * Thrown when the value of the slider is about to be set to a value that is
     * outside the valid range of the slider.
     * 
     * @author Vaadin Ltd.
     * 
     */
    public class ValueOutOfBoundsException extends RuntimeException {

        private final Double value;

        /**
         * Constructs an <code>ValueOutOfBoundsException</code> with the
         * specified detail message.
         * 
         * @param valueOutOfBounds
         */
        public ValueOutOfBoundsException(Double valueOutOfBounds) {
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

}
