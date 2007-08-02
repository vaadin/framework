package com.itmill.toolkit.ui;

import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

public class Slider extends AbstractField {
	
	public static final int ORIENTATION_HORIZONTAL = 0;
	public static final int ORIENTATION_VERTICAL = 1;
	
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
	 * Slider orientation (horizontal==default/vertical).
	 */
	private int orientation = ORIENTATION_HORIZONTAL;
	
	/**
	 * Slider size in pixels.
	 * In horizontal mode if set to -1, allow 100% with container.
	 * In vertical mode if set to -1, default height 120 pixels.
	 */
	private int size = -1;
	
	/**
	 * Handle size in percents related to base size.
	 * Must be a value between 1-99. Other values are converted to nearest bound.
	 * A negative value sets the width to auto (client calculates).
	 */
	private int handleSize = -1;
	
	/**
	 * Show arrows that can be pressed to slide the 
	 * handle in some increments (client-side 
	 * implementation decides the increment).
	 */
	private boolean arrows = true;
	
	public Slider() {
		super();
		super.setValue(new Double(min));
	}
	
	public Slider(String caption) {
		this();
		setCaption(caption);
	}
	
	public Slider(double min, double max, int resolution) {
		this();
		setMin(min);
		setMax(max);
		setResolution(resolution);
	}
	
	public Slider(int min, int max) {
		this();
		setMin(min);
		setMax(max);
		setResolution(0);
	}
	
	public Slider(String caption, int min, int max) {
		this(min, max);
		setCaption(caption);
	}

	public double getMax() {
		return max;
	}

	/**
	 * Set the maximum value of the Slider. As a side-effect nullifies the "values" Set.
	 * @param max
	 */
	public void setMax(double max) {
		this.max = max;
		try {
			if((new Double(getValue().toString())).doubleValue() > max)
				super.setValue(new Double(min));
		} catch(ClassCastException e) {
			super.setValue(new Double(max));
		}
		requestRepaint();
	}

	public double getMin() {
		return min;
	}
	
	/**
	 * Set the minimum value of the Slider. As a side-effect nullifies the "values" Set.
	 * @param max
	 */
	public void setMin(double min) {
		this.min = min;
		try {
			if((new Double(getValue().toString())).doubleValue() < min)
				super.setValue(new Double(min));
		} catch(ClassCastException e) {
			super.setValue(new Double(min));
		}
		requestRepaint();
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
		requestRepaint();
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		if(resolution < 0)
			return;
		this.resolution = resolution;
		requestRepaint();
	}

	public void setValue(Double value, boolean repaintIsNotNeeded) throws ValueOutOfBoundsException {
		double v = new Double(value.toString()).doubleValue();
		double newValue;
		if(resolution>0) {
			// Round up to resolution
			newValue = (int) (v * (double) Math.pow(10, resolution));
			newValue = newValue / (double) Math.pow(10, resolution);
			if(min > newValue || max < newValue)
				throw new ValueOutOfBoundsException(value);
		} else {
			newValue = (int) v;
			if(min > newValue || max < newValue)
				throw new ValueOutOfBoundsException(value);
		}
		super.setValue(new Double(newValue), repaintIsNotNeeded);
	}
	
	public void setValue(Double value) throws ValueOutOfBoundsException {
		setValue(value, false);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		requestRepaint();
	}

	public int getHandleSize() {
		return handleSize;
	}

	public void setHandleSize(int handleSize) {
		if(handleSize < 0)
			this.handleSize = -1;
		else if(handleSize > 99)
			this.handleSize = 99;
		else if(handleSize < 1)
			this.handleSize = 1;
		else this.handleSize = handleSize;
		requestRepaint();
	}
	
	public void setArrows(boolean visible) {
		arrows = visible;
		requestRepaint();
	}
	
	public boolean arrowsVisible() {
		return arrows;
	}

	public String getTag() {
		return "slider";
	}
	
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		
		target.addAttribute("min", min);
		if(max > min)
			target.addAttribute("max", max);
		else
			target.addAttribute("max", min);
		target.addAttribute("resolution", resolution);
		
		if(resolution > 0)
			target.addVariable(this, "value", ((Double)getValue()).doubleValue());
		else
			target.addVariable(this, "value", ((Double)getValue()).intValue());
	
		if(orientation == ORIENTATION_VERTICAL)
			target.addAttribute("vertical", true);
		
		if(arrows)
			target.addAttribute("arrows", true);
		
		if(size > -1)
			target.addAttribute("size", size);
		
		if(min != max && min < max)
			target.addAttribute("hsize", handleSize);
		else
			target.addAttribute("hsize", 100);
		
	}

	/**
	 * Invoked when the value of a variable has changed. Slider listeners are
	 * notified if the slider value has changed.
	 * 
	 * @param source
	 * @param variables
	 */
	public void changeVariables(Object source, Map variables) {
		if (variables.containsKey("value")) {
			Object value = variables.get("value");
			Double newValue = new Double(value.toString());
			if(newValue != null && newValue != getValue() && !newValue.equals(getValue())) {
				try {
					setValue(newValue, true);
				} catch(ValueOutOfBoundsException e) {
					// Convert to nearest bound
					double out = e.getValue().doubleValue();
					if(out < min)
						out = min;
					if(out > max)
						out = max;
					super.setValue(new Double(out), false);
				}
			}
		}
	}
	
	public class ValueOutOfBoundsException extends Exception {

		/**
		 * Serial generated by Eclipse.
		 */
		private static final long serialVersionUID = -6451298598644446340L;
		
		private Double value;
		
		/**
		 * Constructs an <code>ValueOutOfBoundsException</code> with the specified
		 * detail message.
		 * 
		 * @param valueOutOfBounds
		 */
		public ValueOutOfBoundsException(Double valueOutOfBounds) {
			this.value = valueOutOfBounds;
		}
		
		public Double getValue() {
			return this.value;
		}
		
	}

	public Class getType() {
		return Double.class;
	}

}
