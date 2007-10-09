/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.ui;

import java.util.Iterator;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Sizeable;

/**
 * SplitPanel.
 * 
 * <code>SplitPanel</code> is a component container, that can contain two
 * components (possibly containers) which are split by divider element.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
public class SplitPanel extends AbstractComponentContainer implements Layout,
		Sizeable {

	/* Predefined orientations ***************************************** */

	/**
	 * Components are to be layed out vertically.
	 */
	public static int ORIENTATION_VERTICAL = 0;

	/**
	 * Components are to be layed out horizontally.
	 */
	public static int ORIENTATION_HORIZONTAL = 1;

	private Component firstComponent;

	private Component secondComponent;

	/**
	 * Orientation of the layout.
	 */
	private int orientation;

	private int height;

	private int heightUnit;

	private int width;

	private int widthUnit;

	private int pos = 50;

	private int posUnit = UNITS_PERCENTAGE;

	/**
	 * Creates a new split panel. The orientation of the panels is
	 * <code>ORIENTATION_VERTICAL</code>.
	 */
	public SplitPanel() {
		orientation = ORIENTATION_VERTICAL;
	}

	/**
	 * Create a new split panels. The orientation of the panel is given as
	 * parameters.
	 * 
	 * @param orientation
	 *            the Orientation of the layout.
	 */
	public SplitPanel(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * Gets the component UIDL tag.
	 * 
	 * @return the Component UIDL tag as string.
	 */
	public String getTag() {
		if (orientation == ORIENTATION_HORIZONTAL) {
			return "hsplitpanel";
		} else {
			return "vsplitpanel";
		}
	}

	/**
	 * Add a component into this container. The component is added to the right
	 * or under the previous component.
	 * 
	 * @param c
	 *            the component to be added.
	 */
	public void addComponent(Component c) {
		if (firstComponent == null) {
			firstComponent = c;
		} else if (secondComponent == null) {
			secondComponent = c;
		} else {
			throw new UnsupportedOperationException(
					"Split panel can contain only two components");
		}
		super.addComponent(c);
		requestRepaint();
	}

	public void setFirstComponent(Component c) {
		if (firstComponent != null) {
			// detach old
			removeComponent(firstComponent);
		}
		firstComponent = c;
		super.addComponent(c);
	}

	public void setSecondComponent(Component c) {
		if (secondComponent != null) {
			// detach old
			removeComponent(c);
		}
		secondComponent = c;
		super.addComponent(c);
	}

	/**
	 * Removes the component from this container.
	 * 
	 * @param c
	 *            the component to be removed.
	 */
	public void removeComponent(Component c) {
		super.removeComponent(c);
		if (c == firstComponent)
			firstComponent = null;
		else
			secondComponent = null;
		requestRepaint();
	}

	/**
	 * Gets the component container iterator for going trough all the components
	 * in the container.
	 * 
	 * @return the Iterator of the components inside the container.
	 */
	public Iterator getComponentIterator() {
		return new Iterator() {
			int i = 0;

			public boolean hasNext() {
				if (i < 2)
					return true;
				return false;
			}

			public Object next() {
				i++;
				if (i == 1)
					return firstComponent;
				else if (i == 2)
					return secondComponent;
				return null;
			}

			public void remove() {
				if (i == 1)
					setFirstComponent(null);
				else if (i == 2)
					setSecondComponent(null);
			}
		};
	}

	/**
	 * Paints the content of this component.
	 * 
	 * @param target
	 *            the Paint Event.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// TODO refine size attributes
		if (width > 0) {
			target.addAttribute("width", width + UNIT_SYMBOLS[widthUnit]);
		} else {
			target.addAttribute("width", "100%");
		}
		if (height > 0) {
			target.addAttribute("height", height + UNIT_SYMBOLS[heightUnit]);
		} else {
			target.addAttribute("height", "100%");
		}

		String position = pos + UNIT_SYMBOLS[posUnit];

		target.addAttribute("position", position);

		if (firstComponent != null)
			firstComponent.paint(target);
		else
			(new OrderedLayout()).paint(target);
		if (secondComponent != null)
			secondComponent.paint(target);
		else
			(new OrderedLayout()).paint(target);
	}

	/**
	 * Gets the orientation of the container.
	 * 
	 * @return the Value of property orientation.
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * Set the orientation of the container.
	 * 
	 * @param orientation
	 *            the New value of property orientation.
	 */
	public void setOrientation(int orientation) {

		// Checks the validity of the argument
		if (orientation < ORIENTATION_VERTICAL
				|| orientation > ORIENTATION_HORIZONTAL)
			throw new IllegalArgumentException();

		this.orientation = orientation;
		requestRepaint();
	}

	/* Documented in superclass */
	public void replaceComponent(Component oldComponent, Component newComponent) {
		if (oldComponent == firstComponent) {
			setFirstComponent(newComponent);
		} else if (oldComponent == secondComponent) {
			setSecondComponent(secondComponent);
		}
		requestRepaint();
	}

	/**
	 * Moves the position of the splitter.
	 * 
	 * @param pos
	 *            the new size of the first region in persentage
	 */
	public void setSplitPosition(int pos) {
		setSplitPosition(pos, UNITS_PERCENTAGE);
	}

	/**
	 * Moves the position of the splitter with given position and unit.
	 * 
	 * Supported Units are {@link Sizeable}.UNITS_PERSENTAGE and
	 * Sizeable.UNITS_PIXELS
	 * 
	 * @param pos
	 *            size of the first region
	 * @oaran unit the unit (from {@link Sizeable}) in which the size is given.
	 */
	public void setSplitPosition(int pos, int unit) {
		this.pos = pos;
		this.posUnit = unit;
	}

	public int getHeight() {
		return height;
	}

	public int getHeightUnits() {
		return heightUnit;
	}

	public int getWidth() {
		return width;
	}

	public int getWidthUnits() {
		return widthUnit;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setHeightUnits(int units) {
		this.heightUnit = units;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setWidthUnits(int units) {
		this.widthUnit = units;
	}
}
