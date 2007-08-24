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

package com.itmill.toolkit.terminal;

/**
 * Interface to be implemented by components wishing to display some object that
 * may be dynamically resized during runtime.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Sizeable {

	/**
	 * Unit code representing pixels.
	 */
	public static final int UNITS_PIXELS = 0;

	/**
	 * Unit code representing points (1/72nd of an inch).
	 */
	public static final int UNITS_POINTS = 1;

	/**
	 * Unit code representing picas (12 points).
	 */
	public static final int UNITS_PICAS = 2;

	/**
	 * Unit code representing the font-size of the relevant font.
	 */
	public static final int UNITS_EM = 3;

	/**
	 * Unit code representing the x-height of the relevant font.
	 */
	public static final int UNITS_EX = 4;

	/**
	 * Unit code representing millimetres.
	 */
	public static final int UNITS_MM = 5;

	/**
	 * Unit code representing centimetres.
	 */
	public static final int UNITS_CM = 6;

	/**
	 * Unit code representing inches.
	 */
	public static final int UNITS_INCH = 7;

	/**
	 * Unit code representing in percentage of the containing element defined by
	 * terminal.
	 */
	public static final int UNITS_PERCENTAGE = 8;

	/**
	 * Unit code representing in rows of text. This unit is only applicaple to
	 * some components can it's meaning is specified by component
	 * implementation.
	 */
	public static final int UNITS_ROWS = 9;

	/**
	 * Textual representations of units symbols. Supported units and their
	 * symbols are:
	 * <ul>
	 * <li><code>UNITS_PIXELS</code>: "px"</li>
	 * <li><code>UNITS_POINTS</code>: "pt"</li>
	 * <li><code>UNITS_PICAS</code>: "pc"</li>
	 * <li><code>UNITS_EM</code>: "em"</li>
	 * <li><code>UNITS_EX</code>: "ex"</li>
	 * <li><code>UNITS_MM</code>: "mm"</li>
	 * <li><code>UNITS_CM</code>. "cm"</li>
	 * <li><code>UNITS_INCH</code>: "in"</li>
	 * <li><code>UNITS_PERCENTAGE</code>: "%"</li>
	 * <li><code>UNITS_ROWS</code>: "rows"</li>
	 * </ul>
	 * These can be used like <code>Sizeable.UNIT_SYMBOLS[UNITS_PIXELS]</code>.
	 */
	public static final String[] UNIT_SYMBOLS = { "px", "pt", "pc", "em", "ex",
			"mm", "cm", "in", "%", "rows" };

	/**
	 * Gets the width of the object. Negative number implies unspecified size
	 * (terminal is free to set the size).
	 * 
	 * @return width of the object in units specified by widthUnits property.
	 */
	public int getWidth();

	/**
	 * Sets the width of the object. Negative number implies unspecified size
	 * (terminal is free to set the size).
	 * 
	 * @param width
	 *            the width of the object in units specified by widthUnits
	 *            property.
	 */
	public void setWidth(int width);

	/**
	 * Gets the height of the object. Negative number implies unspecified size
	 * (terminal is free to set the size).
	 * 
	 * @return height of the object in units specified by heightUnits property.
	 */
	public int getHeight();

	/**
	 * Sets the height of the object. Negative number implies unspecified size
	 * (terminal is free to set the size).
	 * 
	 * @param height
	 *            the height of the object in units specified by heightUnits
	 *            property.
	 */
	public void setHeight(int height);

	/**
	 * Gets the width property units.
	 * 
	 * @return units used in width property.
	 */
	public int getWidthUnits();

	/**
	 * Sets the width property units.
	 * 
	 * @param units
	 *            the units used in width property.
	 */
	public void setWidthUnits(int units);

	/**
	 * Gets the height property units.
	 * 
	 * @return units used in height property.
	 */
	public int getHeightUnits();

	/**
	 * Sets the height property units.
	 * 
	 * @param units
	 *            the units used in height property.
	 */
	public void setHeightUnits(int units);

}
