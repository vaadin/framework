package com.itmill.toolkit.ui;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * An abstract class that defines default implementation for the {@link Layout}
 * interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
public abstract class AbstractLayout extends AbstractComponentContainer
		implements Layout {

	/**
	 * Layout edge margins, clockwise from top: top, right, bottom, left. Each
	 * is set to true, if the client-side implementation should leave extra
	 * space at that edge.
	 */
	protected boolean[] margins;
	
	/**
	 * Height of the layout. Set to -1 for undefined height.
	 */
	private int height = -1;

	/**
	 * Height unit.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable.UNIT_SYMBOLS;
	 */
	private int heightUnit = UNITS_PIXELS;

	/**
	 * Width of the layout. Set to -1 for undefined width.
	 */
	private int width = -1;

	/**
	 * Width unit.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable.UNIT_SYMBOLS;
	 */
	private int widthUnit = UNITS_PIXELS;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.ui.AbstractComponent#getTag()
	 */
	public abstract String getTag();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.ui.Layout#setMargin(boolean)
	 */
	public void setMargin(boolean enabled) {
		margins = new boolean[] { enabled, enabled, enabled, enabled };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.ui.Layout#setMargin(boolean, boolean, boolean,
	 *      boolean)
	 */
	public void setMargin(boolean topEnabled, boolean rightEnabled,
			boolean bottomEnabled, boolean leftEnabled) {
		margins = new boolean[] { topEnabled, rightEnabled, bottomEnabled,
				leftEnabled };
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getHeightUnits()
	 */
	public int getHeightUnits() {
		return heightUnit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getWidthUnits()
	 */
	public int getWidthUnits() {
		return widthUnit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setHeight(int)
	 */
	public void setHeight(int height) {
		this.height = height;
		requestRepaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setHeightUnits(int)
	 */
	public void setHeightUnits(int units) {
		this.heightUnit = units;
		requestRepaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setSizeFull()
	 */
	public void setSizeFull() {
		height = 100;
		width = 100;
		heightUnit = UNITS_PERCENTAGE;
		widthUnit = UNITS_PERCENTAGE;
		requestRepaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setSizeUndefined()
	 */
	public void setSizeUndefined() {
		height = -1;
		width = -1;
		heightUnit = UNITS_PIXELS;
		widthUnit = UNITS_PIXELS;
		requestRepaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setWidth(int)
	 */
	public void setWidth(int width) {
		this.width = width;
		requestRepaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#setWidthUnits(int)
	 */
	public void setWidthUnits(int units) {
		this.widthUnit = units;
		requestRepaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.ui.AbstractComponent#paintContent(com.itmill.toolkit.terminal.PaintTarget)
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// Add margin info. Defaults to false.
		if (margins == null)
			setMargin(false);
		if (margins[0])
			target.addAttribute("marginTop", margins[0]);
		if (margins[1])
			target.addAttribute("marginRight", margins[1]);
		if (margins[2])
			target.addAttribute("marginBottom", margins[2]);
		if (margins[3])
			target.addAttribute("marginLeft", margins[3]);

		// Add size info
		if (getHeight() > -1)
			target.addAttribute("height", getHeight()
					+ UNIT_SYMBOLS[getHeightUnits()]);
		if (getWidth() > -1)
			target.addAttribute("width", getWidth()
					+ UNIT_SYMBOLS[getWidthUnits()]);
	}

}
