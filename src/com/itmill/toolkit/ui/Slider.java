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
	 * Must be a value between 1-100.
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
			if((new Float(getValue().toString())).floatValue() > max)
				super.setValue(new Float(min));
		} catch(ClassCastException e) {
			super.setValue(new Float(max));
		}
		if(handleSize == -1)
			handleSize = (int) ((max-min)/max*10 + (max-min)/10);
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
		if(handleSize == -1)
			handleSize = (int) ((max-min)/max*10 + (max-min)/10);
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
		if(handleSize > 100 || handleSize < 1)
			return;
		this.handleSize = handleSize;
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
		target.addAttribute("max", max);
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
		
		target.addAttribute("hsize", handleSize);
		
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
			Object newValue = variables.get("value");
			if(resolution > 0)
				newValue = new Double(newValue.toString());
			else 
				newValue = new Integer(newValue.toString());
			if(newValue != null && newValue != getValue() && !newValue.equals(getValue())) {
				setValue(newValue, true);
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
