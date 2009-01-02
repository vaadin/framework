/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * TODO comment
 * 
 * Example code: <code>
 * 	class MyPlayer extends CustomComponent implements ValueChangeListener {
 * 		
 * 		Label volumeIndicator = new Label();
 * 		Slider slider;
 * 		
 * 		public MyPlayer() {
 * 			OrderedLayout ol = new OrderedLayout();
 * 			setCompositionRoot(ol);
 * 			slider = new Slider("Volume", 0, 100);
 * 			slider.setImmediate(true);
 * 			ol.addComponent(slider);
 * 			ol.addComponent(volumeIndicator);
 * 			volumeIndicator.setValue(new Double(50));
 * 			slider.addListener(this);
 * 			
 * 		}
 * 		
 * 		public void setVolume(double d) {
 * 			volumeIndicator.setValue("Current volume : " + d);
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
 */
public class Slider extends AbstractField {

    public static final int ORIENTATION_HORIZONTAL = 0;

    public static final int ORIENTATION_VERTICAL = 1;

    /**
     * Style constant representing a scrollbar styled slider. Use this with
     * {@link #addStyleName(String)}. Default styling usually represents a
     * common slider found e.g. in Adobe Photoshop. The client side
     * implementation dictates how different styles will look.
     */
    public static final String STYLE_SCROLLBAR = "scrollbar";

    /** Minimum value of slider */
    private double min = 0;

    /** Maximum value of slider */
    private double max = 100;

    /**
     * Resolution, how many digits are considered relevant after desimal point.
     * Must be a non-negative value
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
     */
    private int handleSize = -1;

    /**
     * Show arrows that can be pressed to slide the handle in some increments
     * (client-side implementation decides the increment, usually somewhere
     * between 5-10% of slide range).
     */
    private final boolean arrows = false;

    /**
     * Default Slider constructor. Sets all values to defaults and the slide
     * handle at minimum value.
     * 
     */
    public Slider() {
        super();
        super.setValue(new Double(min));
    }

    /**
     * Create a new slider with the caption given as parameter. All slider
     * values set to defaults.
     * 
     * @param caption
     *            The caption for this Slider (e.g. "Volume").
     */
    public Slider(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Create a new slider with given range and resolution
     * 
     * @param min
     * @param max
     * @param resolution
     */
    public Slider(double min, double max, int resolution) {
        this();
        setMin(min);
        setMax(max);
        setResolution(resolution);
    }

    /**
     * Create a new slider with given range
     * 
     * @param min
     * @param max
     */
    public Slider(int min, int max) {
        this();
        setMin(min);
        setMax(max);
        setResolution(0);
    }

    /**
     * Create a new slider with given caption and range
     * 
     * @param caption
     * @param min
     * @param max
     */
    public Slider(String caption, int min, int max) {
        this(min, max);
        setCaption(caption);
    }

    /**
     * Gets the biggest possible value in Sliders range.
     * 
     * @return the biggest value slider can have
     */
    public double getMax() {
        return max;
    }

    /**
     * Set the maximum value of the Slider. If the current value of the Slider
     * is out of new bounds, the value is set to new minimum.
     * 
     * @param max
     *            New maximum value of the Slider.
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
     * Gets the minimum value in Sliders range.
     * 
     * @return the smalles value slider can have
     */
    public double getMin() {
        return min;
    }

    /**
     * Set the minimum value of the Slider. If the current value of the Slider
     * is out of new bounds, the value is set to new minimum.
     * 
     * @param min
     *            New minimum value of the Slider.
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
     * Get the current orientation of the Slider (horizontal or vertical).
     * 
     * @return orientation
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation of the Slider.
     * 
     * @param int new orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        requestRepaint();
    }

    /**
     * Get the current resolution of the Slider.
     * 
     * @return resolution
     */
    public int getResolution() {
        return resolution;
    }

    /**
     * Set a new resolution for the Slider.
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
     * Set the value of this Slider.
     * 
     * @param value
     *            New value of Slider. Must be within Sliders range (min - max),
     *            otherwise throws an exception.
     * @param repaintIsNotNeeded
     *            If true, client-side is not requested to repaint itself.
     * @throws ValueOutOfBoundsException
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
     * Set the value of this Slider.
     * 
     * @param value
     *            New value of Slider. Must be within Sliders range (min - max),
     *            otherwise throws an exception.
     * @throws ValueOutOfBoundsException
     */
    public void setValue(Double value) throws ValueOutOfBoundsException {
        setValue(value, false);
    }

    /**
     * Set the value of this Slider.
     * 
     * @param value
     *            New value of Slider. Must be within Sliders range (min - max),
     *            otherwise throws an exception.
     * @throws ValueOutOfBoundsException
     */
    public void setValue(double value) throws ValueOutOfBoundsException {
        setValue(new Double(value), false);
    }

    /**
     * Get the current Slider size.
     * 
     * @return size in pixels or -1 for auto sizing.
     * @deprecated use standard getWidth/getHeight instead
     */
    @Deprecated
    public int getSize() {
        return size;
    }

    /**
     * Set the size for this Slider.
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
     * Get the handle size of this Slider.
     * 
     * @return handle size in percentages.
     */
    public int getHandleSize() {
        return handleSize;
    }

    /**
     * Set the handle size of this Slider.
     * 
     * @param handleSize
     *            in percentages relative to slider base size.
     */
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

    /*
     * Show or hide slider arrows.
     * 
     * @param visible
     *//*
        * public void setArrows(boolean visible) { arrows = visible;
        * requestRepaint(); }
        */

    /*
     * Does the slider have arrows?
     * 
     * @return arrows visible
     *//*
        * public boolean isArrowsVisible() { return arrows; }
        */

    @Override
    public String getTag() {
        return "slider";
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
            target.addVariable(this, "value", ((Double) getValue())
                    .doubleValue());
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
    public void changeVariables(Object source, Map variables) {
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
     * ValueOutOfBoundsException
     * 
     * @author IT Mill Ltd.
     * 
     */
    public class ValueOutOfBoundsException extends Exception {

        /**
         * Serial generated by Eclipse.
         */
        private static final long serialVersionUID = -6451298598644446340L;

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

        public Double getValue() {
            return value;
        }

    }

    @Override
    public Class getType() {
        return Double.class;
    }

}
