package com.itmill.toolkit.ui;

import java.util.Map;
import java.util.Set;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

public class Slider extends AbstractField {
	
	public static final int ORIENTATION_HORIZONTAL = 0;
	public static final int ORIENTATION_VERTICAL = 1;
	
	/** Minimum value of slider */
	private float min = 0;
	/** Maximum value of slider */
	private float max = 100;
	
	/**
	 * Resolution, how many digits are considered relevant after desimal point.
	 * Must be a non-negative value 
	 */
	private int resolution = 0;
	
	/** 
	 * Object values for slider in stead of numeric (usually strings).
	 * If this is set, min, max and resolution values are ignored.
	 */
	private Set values;
	
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
	private int handleSize = 20;
	
	
	public Slider() {
		super();
		super.setValue(new Float(min));
	}

	public float getMax() {
		return max;
	}

	/**
	 * Set the maximum value of the Slider. As a side-effect nullifies the "values" Set.
	 * @param max
	 */
	public void setMax(float max) {
		this.max = max;
		this.values = null;
		try {
			if((new Float(getValue().toString())).floatValue() > max)
				super.setValue(new Float(min));
		} catch(ClassCastException e) {
			super.setValue(new Float(max));
		}
		requestRepaint();
	}

	public float getMin() {
		return min;
	}
	
	/**
	 * Set the minimum value of the Slider. As a side-effect nullifies the "values" Set.
	 * @param max
	 */
	public void setMin(float min) {
		this.min = min;
		this.values = null;
		try {
			if((new Float(getValue().toString())).floatValue() < min)
				super.setValue(new Float(min));
		} catch(ClassCastException e) {
			super.setValue(new Float(min));
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

	public Set getValues() {
		return values;
	}

	public void setValues(Set values) {
		this.values = values;
		requestRepaint();
	}

	public void setValue(Float value, boolean repaintIsNotNeeded) throws ValueOutOfBoundsException {
		float v = new Float(value.toString()).floatValue();
		Object newValue;
		if(resolution>0) {
			// Round up to resolution
			newValue = new Float(v * (resolution*10) / (resolution*10));
			if(min > ((Float)newValue).floatValue() || max < ((Float)newValue).floatValue())
				throw new ValueOutOfBoundsException(value);
		} else {
			newValue = new Float((int) v);
			if(min > ((Float)newValue).intValue() || max < ((Float)newValue).intValue())
				throw new ValueOutOfBoundsException(value);
		}
		super.setValue(newValue, repaintIsNotNeeded);
	}
	
	public void setValue(Float value)throws ValueOutOfBoundsException {
		setValue(value, false);
	}
	
	public void setValue(String value, boolean repaintIsNotNeeded) throws ValueOutOfBoundsException {
		if(this.values != null) {
			String v = new String(value.toString());
			if(this.values.contains(v))
				super.setValue(v, repaintIsNotNeeded);
			else throw new ValueOutOfBoundsException(value);
		} else {
			// TODO
		}
	}
	
	public void setValue(String value) throws ValueOutOfBoundsException {
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

	public String getTag() {
		return "slider";
	}
	
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		if(values == null) {
			
			target.addAttribute("min", (long) min);
			target.addAttribute("max", (long) max);
			target.addAttribute("resolution", resolution);
			
			if(resolution > 0)
				target.addVariable(this, "value", ((Float)getValue()).floatValue());
			else
				target.addVariable(this, "value", ((Float)getValue()).intValue());
		
		} else {
			target.addVariable(this, "value", getValue().toString());
			target.addAttribute("values", values.toArray(new String[values.size()]));
		}
		
		if(orientation == ORIENTATION_VERTICAL)
			target.addAttribute("vertical", true);
		
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
			if(values == null) {
				if(resolution >0)
					newValue = new Long(newValue.toString());
				else 
					newValue = new Integer(newValue.toString());
				if(newValue != null && newValue != getValue() && !newValue.equals(getValue())) {
					setValue(newValue);
				}
			} else {
				// TODO
			}
		}
	}
	
	public class ValueOutOfBoundsException extends Exception {

		/**
		 * Serial generated by Eclipse.
		 */
		private static final long serialVersionUID = -6451298598644446340L;
		
		private Object value;
		
		/**
		 * Constructs an <code>ValueOutOfBoundsException</code> with the specified
		 * detail message.
		 * 
		 * @param valueOutOfBounds
		 */
		public ValueOutOfBoundsException(Object valueOutOfBounds) {
			this.value = valueOutOfBounds;
		}
		
		public Object getValue() {
			return this.value;
		}
		
	}

	public Class getType() {
		if(values == null)
			return Float.class;
		return String.class;
	}

}
