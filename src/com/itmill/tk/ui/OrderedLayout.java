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

package com.itmill.tk.ui;

import com.itmill.tk.terminal.PaintException;
import com.itmill.tk.terminal.PaintTarget;

import java.util.Iterator;
import java.util.LinkedList;

/** Ordered layout.
 *
 * Ordered layout is a component container, which shows the subcomponents in the
 * order of their addition in specified orientation.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class OrderedLayout
	extends AbstractComponentContainer
	implements Layout {

	/* Predefined orientations ***************************************** */

	/** Components are to be layed out vertically. */
	public static int ORIENTATION_VERTICAL = 0;
	/** Components are to be layed out horizontally. */
	public static int ORIENTATION_HORIZONTAL = 1;

	/** Custom layout slots containing the components */
	private LinkedList components = new LinkedList();

	/** Orientation of the layout. */
	private int orientation;

	/** Create a new ordered layout.
	 * The order of the layout is ORIENTATION_VERTICAL.
	 */
	public OrderedLayout() {
		orientation = ORIENTATION_VERTICAL;
	}

	/** Create a new ordered layout.
	 * The orientation of the layout is given as parameters.
	 *
	 * @param orientation Orientation of the layout.
	 */
	public OrderedLayout(int orientation) {
		this.orientation = orientation;
	}

	/** Get component UIDL tag.
	 * @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "orderedlayout";
	}

	/** Add a component into this container. The component is added to the
	 * right or under the previous component.
	 * @param c The component to be added.
	 */
	public void addComponent(Component c) {
		components.add(c);
		super.addComponent(c);
		requestRepaint();
	}

	/** Add a component into this container. The component is added to the
	 * left or on top of the other components.
	 * @param c The component to be added.
	 */
	public void addComponentAsFirst(Component c) {
		components.addFirst(c);
		super.addComponent(c);
		requestRepaint();
	}

	/** Add a component into indexed position in this container. 
	 * @param c The component to be added.
	 * @param index Index of the component position. 
	 * The components currently in and after the position are shifted forwards.
	 */
	public void addComponent(Component c, int index) {
		components.add(index, c);
		super.addComponent(c);
		requestRepaint();
	}

	/** Remove a component from this container.
	 * @param c The component to be removed.
	 */
	public void removeComponent(Component c) {
		super.removeComponent(c);
		components.remove(c);
		requestRepaint();
	}

	/** Get component container iterator for going trough all the components in
	 * the container.
	 * @return Iterator of the components inside the container.
	 */
	public Iterator getComponentIterator() {
		return components.iterator();
	}

	/** Paint the content of this component.
	 * @param event PaintEvent.
	 * @throws PaintException The paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// Add the attributes: orientation 
		// note that the default values (b/vertival) are omitted
		if (orientation == ORIENTATION_HORIZONTAL)
			target.addAttribute("orientation", "horizontal");

		// Add all items in all the locations
		for (Iterator i = components.iterator(); i.hasNext();) {
			Component c = (Component) i.next();
			if (c != null) {
				c.paint(target);
			}
		}
	}

	/** Get the orientation of the container.
	 * @return Value of property orientation.
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/** Set the orientation of the container.
	 * @param orientation New value of property orientation.
	 */
	public void setOrientation(int orientation) {

		// Check the validity of the argument
		if (orientation < ORIENTATION_VERTICAL
			|| orientation > ORIENTATION_HORIZONTAL)
			throw new IllegalArgumentException();

		this.orientation = orientation;
	}

	/* Documented in superclass */
	public void replaceComponent(
		Component oldComponent,
		Component newComponent) {

		// Get the locations			
		int oldLocation = -1;
		int newLocation = -1;
		int location = 0;
		for (Iterator i = components.iterator(); i.hasNext();) {
			Component component = (Component) i.next();

			if (component == oldComponent)
				oldLocation = location;
			if (component == newComponent)
				newLocation = location;

			location++;
		}

		if (oldLocation == -1)
			addComponent(newComponent);
		else if (newLocation == -1) {
			removeComponent(oldComponent);
			addComponent(newComponent, oldLocation);
		} else {
			if (oldLocation > newLocation) {
				components.remove(oldComponent);
				components.add(newLocation, oldComponent);
				components.remove(newComponent);
				components.add(oldLocation, newComponent);
			} else {
				components.remove(newComponent);
				components.add(oldLocation, newComponent);
				components.remove(oldComponent);
				components.add(newLocation, oldComponent);
			}

			requestRepaint();
		}
	}
}
