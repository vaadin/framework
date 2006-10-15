/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.itmill.tk.terminal;

/** Scrollable interface. 
 * 
 * <p>This interface is implemented by all visual objects that can be scrolled.
 * The unit of scrolling is pixel.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface Scrollable {


	/** Get scroll X offset. 
	 * 
	 * <p>Scrolling offset is the number of pixels this scrollable has
	 * been scrolled to left.</p>
	 * 
	 * @return Horizontal scrolling position in pixels.
	 */
	public int getScrollOffsetX();

	/** Set scroll X offset.
	 * 
	 * <p>Scrolling offset is the number of pixels this scrollable has
	 * been scrolled to left.</p>
	 * 
	 * @param xOffset.
	 */
	public void setScrollOffsetX(int pixelsScrolledLeft);

	/** Get scroll Y offset. 
	 * 
	 * <p>Scrolling offset is the number of pixels this scrollable has
	 * been scrolled to down.</p>
	 * 
	 * @return Vertical scrolling position in pixels.
	 */
	public int getScrollOffsetY();

	/** Set scroll Y offset.
	 * 
	 * <p>Scrolling offset is the number of pixels this scrollable has
	 * been scrolled to down.</p>
	 * 
	 * @param yOffset.
	 */
	public void setScrollOffsetY(int pixelsScrolledDown);

	/** Is the scrolling enabled.
	 * 
	 * <p>Enabling scrolling allows the user to scroll the scrollable view
	 * interactively</p>
	 * 
	 * @return True iff the scrolling is allowed.
	 */
	public boolean isScrollable();

	/** Enable or disable scrolling..
	 * 
	 * <p>Enabling scrolling allows the user to scroll the scrollable view
	 * interactively</p>
	 *
	 * @param isScrollingEnabled True iff the scrolling is allowed.
	 */
	public void setScrollable(boolean isScrollingEnabled);

}
