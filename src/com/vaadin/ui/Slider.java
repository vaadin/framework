/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VSlider;

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
@ClientWidget(VSlider.class)
public class Slider extends AbstractField {

    public static final int ORIENTATION_HORIZONTAL = 0;

    public static final int ORIENTATION_VERTICAL = 1;

    /**
     * Style constant representing a scrollbar styled slider. Use this with
     * {@link #addStyleName(String)}. Default styling usually represents a
     * common slider found e.g. in Adobe Photoshop. The client side
     * implementation dictates how different styles will look.
     */
    @Deprecated
    public static final String STYLE_SCROLLBAR = "scrollbar";

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
     * Slider size in pixels. In horizontal mode, if set to -1, allow 100% width
     * of container. In vertical mode, if set to -1, default height is
     * determined by the client-side implementation.
     * 
     * @deprecated
     */
    @Deprecated
    private int size = -1;

    /**
     * Handle (draggable control element) size in percents relative to base
     * size. Must be a value between 1-99. Other values are converted to nearest
     * bound. A negative value sets the width to auto (client-side
     * implementation calculates).
     * 
     * @deprecated The size is dictated by the current theme.
     */
    @Deprecated
    private int handleSize = -1;

    /**
     * Show arrows that can be pressed to slide the handle in some increments
     * (client-side implementation decides the increment, usually somewhere
     * between 5-10% of slide range).
     */
    @Deprecated
    private final boolean arrows = false;

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
        try {
            if ((new Double(getValue().toString())).doubleValue() > max) {
                super.setValue(new Double(max));
            }
        } catch (final ClassCastException e) {
            // FIXME: Handle exception
            /*
             * Where does ClassCastException come from? Can't see any casts
             * above
             */
            super.setValue(new Double(max));
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
        try {
            if ((new Double(getValue().toString())).doubleValue() < min) {
                super.setValue(new Double(min));
            }
        } catch (final ClassCastException e) {
            // FIXME: Handle exception
            /*
             * Where does ClassCastException come from? Can't see any casts
             * above
             */
            super.setValue(new Double(min));
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
    public void setValue(Double value, boolean repaintIsNotNeeded)
            throws ValueOutOfBoundsException {
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
        super.setValue(new Double(newValue), repaintIsNotNeeded);
    }

    /**
     * Sets the value of the slider.
     * 
     * @param value
     *            The new value of the slider.
     * @throws ValueOutOfBoundsException
     *             If the given value is not inside the range of the slider.
     * @see #setMin(double) {@link #setMax(double)}
     */
    public void setValue(Double value) throws ValueOutOfBoundsException {
        setValue(value, false);
    }

    /**
     * Sets the value of the slider.
     * 
     * @param value
     *            The new value of the slider.
     * @throws ValueOutOfBoundsException
     *             If the given value is not inside the range of the slider.
     * @see #setMin(double) {@link #setMax(double)}
     */
    public void setValue(double value) throws ValueOutOfBoundsException {
        setValue(new Double(value), false);
    }

    /**
     * Get the current slider size.
     * 
     * @return size in pixels or -1 for auto sizing.
     * @deprecated use standard getWidth/getHeight instead
     */
    @Deprecated
    public int getSize() {
        return size;
    }

    /**
     * Set the size for this slider.
     * 
     * @param size
     *            in pixels, or -1 auto sizing.
     * @deprecated use standard setWidth/setHeight instead
     */
    @Deprecated
    public void setSize(int size) {
        this.size = size;
        switch (orientation) {
        case ORIENTATION_HORIZONTAL:
            setWidth(size, UNITS_PIXELS);
            break;
        default:
            setHeight(size, UNITS_PIXELS);
            break;
        }
        requestRepaint();
    }

    /**
     * Get the handle size of this slider.
     * 
     * @return handle size in percentages.
     * @deprecated The size is dictated by the current theme.
     */
    @Deprecated
    public int getHandleSize() {
        return handleSize;
    }

    /**
     * Set the handle size of this slider.
     * 
     * @param handleSize
     *            in percentages relative to slider base size.
     * @deprecated The size is dictated by the current theme.
     */
    @Deprecated
    public void setHandleSize(int handleSize) {
        if (handleSize < 0) {
            this.handleSize = -1;
        } else if (handleSize > 99) {
            this.handleSize = 99;
        } else if (handleSize < 1) {
            this.handleSize = 1;
        } else {
            this.handleSize = handleSize;
        }
        requestRepaint();
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
            target.addVariable(this, "value",
                    ((Double) getValue()).doubleValue());
        } else {
            target.addVariable(this, "value", ((Double) getValue()).intValue());
        }

        if (orientation == ORIENTATION_VERTICAL) {
            target.addAttribute("vertical", true);
        }

        if (arrows) {
            target.addAttribute("arrows", true);
        }

        if (size > -1) {
            target.addAttribute("size", size);
        }

        if (min != max && min < max) {
            target.addAttribute("hsize", handleSize);
        } else {
            target.addAttribute("hsize", 100);
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
    public class ValueOutOfBoundsException extends Exception {

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
    public Class getType() {
        return Double.class;
    }

}
