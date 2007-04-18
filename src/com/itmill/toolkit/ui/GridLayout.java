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

import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedList;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * <p>
 * A container that consists of components with certain coordinates on a grid.
 * It also maintains cursor for adding component in left to right, top to bottom
 * order.
 * </p>
 * 
 * <p>
 * Each component in a <code>GridLayout</code> uses a certain
 * {@link GridLayout.Area area} (x1,y1,x2,y2) from the grid. One should not add
 * components that would overlap with the existing components because in such
 * case an {@link OverlapsException} is thrown. Adding component with cursor
 * automatically extends the grid by increasing the grid height.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class GridLayout extends AbstractComponentContainer implements Layout {

	/**
	 * Initial grid x size.
	 */
	private int width = 0;

	/**
	 * Initial grid y size.
	 */
	private int height = 0;

	/**
	 * Cursor X position: this is where the next component with unspecified x,y
	 * is inserted
	 */
	private int cursorX = 0;

	/**
	 * Cursor Y position: this is where the next component with unspecified x,y
	 * is inserted
	 */
	private int cursorY = 0;

	/**
	 * Contains all items that are placed on the grid. These are components with
	 * grid area definition.
	 */
	private LinkedList areas = new LinkedList();

	/**
	 * Mapping from components to threir respective areas.
	 */
	private LinkedList components = new LinkedList();

	/**
	 * Constructor for grid of given size. Note that grid's final size depends
	 * on the items that are added into the grid. Grid grows if you add
	 * components outside the grid's area.
	 * 
	 * @param width
	 *            the Width of the grid.
	 * @param height
	 *            the Height of the grid.
	 */
	public GridLayout(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	/**
	 * Constructs an empty grid layout that is extended as needed.
	 */
	public GridLayout() {
		this(1, 1);
	}

	/**
	 * <p>
	 * Adds a component with a specified area to the grid. The area the new
	 * component should take is defined by specifying the upper left corner (x1,
	 * y1) and the lower right corner (x2, y2) of the area.
	 * </p>
	 * 
	 * <p>
	 * If the new component overlaps with any of the existing components already
	 * present in the grid the operation will fail and an
	 * {@link OverlapsException} is thrown.
	 * </p>
	 * 
	 * @param c
	 *            the component to be added.
	 * @param x1
	 *            the X-coordinate of the upper left corner of the area
	 *            <code>c</code> is supposed to occupy.
	 * @param y1
	 *            the Y-coordinate of the upper left corner of the area
	 *            <code>c</code> is supposed to occupy.
	 * @param x2
	 *            the X-coordinate of the lower right corner of the area
	 *            <code>c</code> is supposed to occupy.
	 * @param y2
	 *            the Y-coordinate of the lower right corner of the area
	 *            <code>c</code> is supposed to occupy.
	 * @throws OverlapsException
	 *             if the new component overlaps with any of the components
	 *             already in the grid.
	 * @throws OutOfBoundsException
	 *             if the coordinates are outside of the grid area.
	 */
	public void addComponent(Component component, int x1, int y1, int x2, int y2)
			throws OverlapsException, OutOfBoundsException {

		if (component == null)
			throw new NullPointerException("Component must not be null");

		// Checks that the component does not already exist in the container
		if (components.contains(component))
			throw new IllegalArgumentException(
					"Component is already in the container");

		// Creates the area
		Area area = new Area(component, x1, y1, x2, y2);

		// Checks the validity of the coordinates
		if (x2 < x1 || y2 < y2)
			throw new IllegalArgumentException(
					"Illegal coordinates for the component");
		if (x1 < 0 || y1 < 0 || x2 >= width || y2 >= height)
			throw new OutOfBoundsException(area);

		// Checks that newItem does not overlap with existing items
		checkExistingOverlaps(area);

		// Inserts the component to right place at the list
		// Respect top-down, left-right ordering
		component.setParent(this);
		Iterator i = areas.iterator();
		int index = 0;
		boolean done = false;
		while (!done && i.hasNext()) {
			Area existingArea = (Area) i.next();
			if ((existingArea.y1 >= y1 && existingArea.x1 > x1)
					|| existingArea.y1 > y1) {
				areas.add(index, area);
				components.add(index, component);
				done = true;
			}
			index++;
		}
		if (!done) {
			areas.addLast(area);
			components.addLast(component);
		}

		super.addComponent(component);
		requestRepaint();
	}

	/**
	 * Tests if the given area overlaps with any of the items already on the
	 * grid.
	 * 
	 * @param area
	 *            the Area to be checked for overlapping.
	 * @throws OverlapsException
	 *             if <code>area</code> overlaps with any existing area.
	 */
	private void checkExistingOverlaps(Area area) throws OverlapsException {
		for (Iterator i = areas.iterator(); i.hasNext();) {
			Area existingArea = (Area) i.next();
			if (existingArea.overlaps(area))

				// Component not added, overlaps with existing component
				throw new OverlapsException(existingArea);
		}
	}

	/**
	 * Adds the component into this container to coordinates x1,y1 (NortWest
	 * corner of the area.) End coordinates (SouthEast corner of the area) are
	 * the same as x1,y1. Component width and height is 1.
	 * 
	 * @param c
	 *            the component to be added.
	 * @param x
	 *            the X-coordinate.
	 * @param y
	 *            the Y-coordinate.
	 */
	public void addComponent(Component c, int x, int y) {
		this.addComponent(c, x, y, x, y);
	}

	/**
	 * Force the next component to be added to the beginning of the next line.
	 * By calling this function user can ensure that no more components are
	 * added to the right of the previous component.
	 * 
	 * @see #space()
	 */
	public void newLine() {
		cursorX = 0;
		cursorY++;
	}

	/**
	 * Moves the cursor forwards by one. If the cursor goes out of the right
	 * grid border, move it to next line.
	 * 
	 * @see #newLine()
	 */
	public void space() {
		cursorX++;
		if (cursorX >= width) {
			cursorX = 0;
			cursorY++;
		}
	}

	/**
	 * Adds the component into this container to the cursor position. If the
	 * cursor position is already occupied, the cursor is moved forwards to find
	 * free position. If the cursor goes out from the bottom of the grid, the
	 * grid is automaticly extended.
	 * 
	 * @param c
	 *            the component to be added.
	 */
	public void addComponent(Component component) {

		// Finds first available place from the grid
		Area area;
		boolean done = false;
		while (!done)
			try {
				area = new Area(component, cursorX, cursorY, cursorX, cursorY);
				checkExistingOverlaps(area);
				done = true;
			} catch (OverlapsException ignored) {
				space();
			}

		// Extends the grid if needed
		width = cursorX >= width ? cursorX + 1 : width;
		height = cursorY >= height ? cursorY + 1 : height;

		addComponent(component, cursorX, cursorY);
	}

	/**
	 * Removes the given component from this container.
	 * 
	 * @param c
	 *            the component to be removed.
	 */
	public void removeComponent(Component component) {

		// Check that the component is contained in the container
		if (component == null || !components.contains(component))
			return;

		super.removeComponent(component);

		Area area = null;
		for (Iterator i = areas.iterator(); area == null && i.hasNext();) {
			Area a = (Area) i.next();
			if (a.getComponent() == component)
				area = a;
		}

		components.remove(component);
		if (area != null)
			areas.remove(area);

		requestRepaint();
	}

	/**
	 * Removes the component specified with it's top-left corner coordinates
	 * from this grid.
	 * 
	 * @param x
	 *            the Component's top-left corner's X-coordinate.
	 * @param y
	 *            the Component's top-left corner's Y-coordinate.
	 */
	public void removeComponent(int x, int y) {

		// Finds the area
		for (Iterator i = areas.iterator(); i.hasNext();) {
			Area area = (Area) i.next();
			if (area.getX1() == x && area.getY1() == y) {
				removeComponent(area.getComponent());
				return;
			}
		}
	}

	/**
	 * Gets an Iterator to the component container contents. Using the Iterator
	 * it's possible to step through the contents of the container.
	 * 
	 * @return the Iterator of the components inside the container.
	 */
	public Iterator getComponentIterator() {
		return Collections.unmodifiableCollection(components).iterator();
	}

	/**
	 * Paints the contents of this component.
	 * 
	 * @param target
	 *            the Paint Event.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		target.addAttribute("h", height);
		target.addAttribute("w", width);

		// Area iterator
		Iterator areaiterator = areas.iterator();

		// Current item to be processed (fetch first item)
		Area area = areaiterator.hasNext() ? (Area) areaiterator.next() : null;

		// Collects rowspan related information here
		HashMap cellUsed = new HashMap();

		// Empty cell collector
		int emptyCells = 0;

		// Iterates every applicable row
		for (int cury = 0; cury < height; cury++) {
			target.startTag("gr");

			// Iterates every applicable column
			for (int curx = 0; curx < width; curx++) {

				// Checks if current item is located at curx,cury
				if (area != null && (area.y1 == cury) && (area.x1 == curx)) {

					// First check if empty cell needs to be rendered
					if (emptyCells > 0) {
						target.startTag("gc");
						target.addAttribute("x", curx - emptyCells);
						target.addAttribute("y", cury);
						if (emptyCells > 1) {
							target.addAttribute("w", emptyCells);
						}
						target.endTag("gc");
						emptyCells = 0;
					}

					// Now proceed rendering current item
					int cols = (area.x2 - area.x1) + 1;
					int rows = (area.y2 - area.y1) + 1;
					target.startTag("gc");

					target.addAttribute("x", curx);
					target.addAttribute("y", cury);

					if (cols > 1) {
						target.addAttribute("w", cols);
					}
					if (rows > 1) {
						target.addAttribute("h", rows);
					}
					area.getComponent().paint(target);

					target.endTag("gc");

					// Fetch next item
					if (areaiterator.hasNext()) {
						area = (Area) areaiterator.next();
					} else {
						area = null;
					}

					// Updates the cellUsed if rowspan needed
					if (rows > 1) {
						int spannedx = curx;
						for (int j = 1; j <= cols; j++) {
							cellUsed.put(new Integer(spannedx), new Integer(
									cury + rows - 1));
							spannedx++;
						}
					}

					// Skips the current item's spanned columns
					if (cols > 1) {
						curx += cols - 1;
					}

				} else {

					// Checks against cellUsed, render space or ignore cell
					if (cellUsed.containsKey(new Integer(curx))) {

						// Current column contains already an item,
						// check if rowspan affects at current x,y position
						int rowspanDepth = ((Integer) cellUsed.get(new Integer(
								curx))).intValue();

						if (rowspanDepth >= cury) {

							// ignore cell
							// Check if empty cell needs to be rendered
							if (emptyCells > 0) {
								target.startTag("gc");
								target.addAttribute("x", curx - emptyCells);
								target.addAttribute("y", cury);
								if (emptyCells > 1) {
									target.addAttribute("w", emptyCells);
								}
								target.endTag("gc");

								emptyCells = 0;
							}
						} else {

							// empty cell is needed
							emptyCells++;

							// Removes the cellUsed key as it has become
							// obsolete
							cellUsed.remove(new Integer(curx));
						}
					} else {

						// empty cell is needed
						emptyCells++;
					}
				}

			} // iterates every column

			// Last column handled of current row

			// Checks if empty cell needs to be rendered
			if (emptyCells > 0) {
				target.startTag("gc");
				target.addAttribute("x", width - emptyCells);
				target.addAttribute("y", cury);
				if (emptyCells > 1) {
					target.addAttribute("w", emptyCells);
				}
				target.endTag("gc");

				emptyCells = 0;
			}

			target.endTag("gr");
		} // iterates every row

		// Last row handled
	}

	/**
	 * Gets the components UIDL tag.
	 * 
	 * @return the Component UIDL tag as string.
	 * @see com.itmill.toolkit.ui.AbstractComponent#getTag()
	 */
	public String getTag() {
		return "gridlayout";
	}

	/**
	 * This class defines an area on a grid. An Area is defined by the
	 * coordinates of its upper left corner (x1,y1) and lower right corner
	 * (x2,y2).
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class Area {

		/**
		 * X-coordinate of the upper left corner of the area.
		 */
		private int x1;

		/**
		 * Y-coordinate of the upper left corner of the area.
		 */
		private int y1;

		/**
		 * X-coordinate of the lower right corner of the area.
		 */
		private int x2;

		/**
		 * Y-coordinate of the lower right corner of the area.
		 */
		private int y2;

		/**
		 * Component painted on the area.
		 */
		private Component component;

		/**
		 * <p>
		 * Construct a new area on a grid.
		 * </p>
		 * 
		 * @param component
		 *            the component connected to the area.
		 * @param x1
		 *            the X-coordinate of the upper left corner of the area
		 *            <code>c</code> is supposed to occupy.
		 * @param y1
		 *            the Y-coordinate of the upper left corner of the area
		 *            <code>c</code> is supposed to occupy.
		 * @param x2
		 *            the X-coordinate of the lower right corner of the area
		 *            <code>c</code> is supposed to occupy.
		 * @param y2
		 *            the Y-coordinate of the lower right corner of the area
		 *            <code>c</code> is supposed to occupy.
		 * @throws OverlapsException
		 *             if the new component overlaps with any of the components
		 *             already in the grid
		 */
		public Area(Component component, int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.component = component;
		}

		/**
		 * Tests if the given Area overlaps with an another Area.
		 * 
		 * @param other
		 *            the Another Area that's to be tested for overlap with this
		 *            area.
		 * @return <code>true</code> if <code>other</code> overlaps with
		 *         this area, <code>false</code> if it doesn't.
		 */
		public boolean overlaps(Area other) {
			return x1 <= other.getX2() && y1 <= other.getY2()
					&& x2 >= other.getX1() && y2 >= other.getY1();

		}

		/**
		 * Gets the component connected to the area.
		 * 
		 * @return the Component.
		 */
		public Component getComponent() {
			return component;
		}

		/**
		 * Sets the component connected to the area.
		 * 
		 * <p>
		 * This function only sets the value in the datastructure and does not
		 * send any events or set parents.
		 * </p>
		 * 
		 * @param newComponent
		 *            the new connected overriding the existing one.
		 */
		protected void setComponent(Component newComponent) {
			component = newComponent;
		}

		/**
		 * Gets the top-left corner x-coordinate.
		 * 
		 * @return the top-left corner of x-coordinate.
		 */
		public int getX1() {
			return x1;
		}

		/**
		 * Gets the bottom-right corner x-coordinate.
		 * 
		 * @return the x-coordinate.
		 */
		public int getX2() {
			return x2;
		}

		/**
		 * Gets the top-left corner y-coordinate.
		 * 
		 * @return the y-coordinate.
		 */
		public int getY1() {
			return y1;
		}

		/**
		 * Returns the bottom-right corner y-coordinate.
		 * 
		 * @return the y-coordinate.
		 */
		public int getY2() {
			return y2;
		}

	}

	/**
	 * An <code>Exception</code> object which is thrown when two Items occupy
	 * the same space on a grid.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class OverlapsException extends java.lang.RuntimeException {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3978144339870101561L;

		private Area existingArea;

		/**
		 * Constructs an <code>OverlapsException</code>.
		 * 
		 * @param existingArea
		 */
		public OverlapsException(Area existingArea) {
			this.existingArea = existingArea;
		}

		/**
		 * Gets the area .
		 * 
		 * @return the existing area.
		 */
		public Area getArea() {
			return existingArea;
		}
	}

	/**
	 * An <code>Exception</code> object which is thrown when an area exceeds
	 * the bounds of the grid.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class OutOfBoundsException extends java.lang.RuntimeException {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3618985589664592694L;

		private Area areaOutOfBounds;

		/**
		 * Constructs an <code>OoutOfBoundsException</code> with the specified
		 * detail message.
		 * 
		 * @param areaOutOfBounds
		 */
		public OutOfBoundsException(Area areaOutOfBounds) {
			this.areaOutOfBounds = areaOutOfBounds;
		}

		/**
		 * Gets the area that is out of bounds.
		 * 
		 * @return the area out of Bound.
		 */
		public Area getArea() {
			return areaOutOfBounds;
		}
	}

	/**
	 * Sets the width of the grid. The width can not be reduced if there are any
	 * areas that would be outside of the shrunk grid.
	 * 
	 * @param width
	 *            the New width of the grid.
	 */
	public void setWidth(int width) {

		// The the param
		if (width < 1)
			throw new IllegalArgumentException(
					"The grid width and height must be at least 1");

		// In case of no change
		if (this.width == width)
			return;

		// Checks for overlaps
		if (this.width > width)
			for (Iterator i = areas.iterator(); i.hasNext();) {
				Area area = (Area) i.next();
				if (area.x2 >= width)
					throw new OutOfBoundsException(area);
			}

		this.width = width;

		requestRepaint();
	}

	/**
	 * Get the width of the grids.
	 * 
	 * @return the width of the grid.
	 */
	public final int getWidth() {
		return this.width;
	}

	/**
	 * Sets the height of the grid. The width can not be reduced if there are
	 * any areas that would be outside of the shrunk grid.
	 * 
	 * @param height
	 *            the height of the grid.
	 */
	public void setHeight(int height) {

		// The the param
		if (height < 1)
			throw new IllegalArgumentException(
					"The grid width and height must be at least 1");

		// In case of no change
		if (this.height == height)
			return;

		// Checks for overlaps
		if (this.height > height)
			for (Iterator i = areas.iterator(); i.hasNext();) {
				Area area = (Area) i.next();
				if (area.y2 >= height)
					throw new OutOfBoundsException(area);
			}

		this.height = height;

		requestRepaint();
	}

	/**
	 * Gets the height of the grid.
	 * 
	 * @return int - how many cells high the grid is.
	 */
	public final int getHeight() {
		return this.height;
	}

	/**
	 * Gets the current cursor x-position. The cursor position points the
	 * position for the next component that is added without specifying its
	 * coordinates. When the cursor position is occupied, the next component
	 * will be added to first free position after the cursor.
	 * 
	 * @return the Cursor x-coordinate.
	 */
	public int getCursorX() {
		return cursorX;
	}

	/**
	 * Gets the current cursor y-position. The cursor position points the
	 * position for the next component that is added without specifying its
	 * coordinates. When the cursor position is occupied, the next component
	 * will be added to first free position after the cursor.
	 * 
	 * @return the Cursor y-coordinate.
	 */
	public int getCursorY() {
		return cursorY;
	}

	/* Documented in superclass */
	public void replaceComponent(Component oldComponent, Component newComponent) {

		// Gets the locations
		Area oldLocation = null;
		Area newLocation = null;
		for (Iterator i = areas.iterator(); i.hasNext();) {
			Area location = (Area) i.next();
			Component component = (Component) location.getComponent();
			if (component == oldComponent)
				oldLocation = location;
			if (component == newComponent)
				newLocation = location;
		}

		if (oldLocation == null)
			addComponent(newComponent);
		else if (newLocation == null) {
			removeComponent(oldComponent);
			addComponent(newComponent, oldLocation.getX1(),
					oldLocation.getY1(), oldLocation.getX2(), oldLocation
							.getY2());
		} else {
			oldLocation.setComponent(newComponent);
			newLocation.setComponent(oldComponent);
			requestRepaint();
		}
	}

	/*
	 * Removes all components from this container.
	 * 
	 * @see com.itmill.toolkit.ui.ComponentContainer#removeAllComponents()
	 */
	public void removeAllComponents() {
		super.removeAllComponents();
		this.cursorX = 0;
		this.cursorY = 0;
	}

}
