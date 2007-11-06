package com.itmill.toolkit.ui;

import java.util.Iterator;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Sizeable;

/**
 * TODO finish documentation
 * 
 * our layouts (except custom layout of course) don't currently work at all with
 * relative widths. This layout tries to cope with this issue.
 * 
 * basically this is ordered layout which has Sizeable interface 100 % height &
 * width by default
 * 
 * all contained components may also have Sizeable interfaces sizes
 * 
 * can be used to build flexible layout where some component gets all the space
 * other components don't use. Or just provide expanded container.
 * 
 */
public class ExpandLayout extends OrderedLayout implements Sizeable {

	private Component expanded;

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

	public ExpandLayout() {
		setSizeFull();
	}

	public ExpandLayout(int orientation) {
		this();
		setOrientation(orientation);
	}

	/**
	 * @param c
	 *            Component which container will be maximized
	 */
	public void expand(Component c) {
		this.expanded = c;
		requestRepaint();
	}

	public String getTag() {
		return "expandlayout";
	}

	public void paintContent(PaintTarget target) throws PaintException {

		//TODO should we add margins?
		
		// Size
		if (getHeight() >= 0)
			target.addAttribute("height", "" + getHeight()
					+ Sizeable.UNIT_SYMBOLS[getHeightUnits()]);
		if (getWidth() >= 0)
			target.addAttribute("width", "" + getWidth()
					+ Sizeable.UNIT_SYMBOLS[getWidthUnits()]);

		// Adds the attributes: orientation
		// note that the default values (b/vertival) are omitted
		if (getOrientation() == ORIENTATION_HORIZONTAL)
			target.addAttribute("orientation", "horizontal");

		// Adds all items in all the locations
		for (Iterator i = getComponentIterator(); i.hasNext();) {
			Component c = (Component) i.next();
			if (c != null) {
				target.startTag("cc");
				if (c == expanded)
					target.addAttribute("expanded", true);
				c.paint(target);
				target.endTag("cc");
			}
		}
	}

	public void addComponent(Component c, int index) {
		if (expanded == null) {
			expanded = c;
		}
		super.addComponent(c, index);
	}

	public void addComponent(Component c) {
		if (expanded == null) {
			expanded = c;
		}
		super.addComponent(c);
	}

	public void addComponentAsFirst(Component c) {
		if (expanded == null) {
			expanded = c;
		}
		super.addComponentAsFirst(c);
	}

	public void removeComponent(Component c) {
		super.removeComponent(c);
		if (c == expanded && this.getComponentIterator().hasNext())
			expanded = (Component) this.getComponentIterator().next();
		else
			expanded = null;
	}

	public void replaceComponent(Component oldComponent, Component newComponent) {
		super.replaceComponent(oldComponent, newComponent);
		if (oldComponent == expanded)
			expanded = newComponent;
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

}
