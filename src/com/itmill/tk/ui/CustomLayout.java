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
import java.util.HashMap;

/** <p>A container component with freely designed layout and style. The
 * container consists of items with textually represented locations. Each
 * item contains one sub-component. The adapter and theme are resposible for
 * rendering the layout with given style by placing the items on the screen
 * in defined locations.</p>
 *
 * <p>The definition of locations is not fixed - the each style can define
 * its locations in a way that is suitable for it. One typical example would
 * be to create visual design for a website as a custom layout: the visual
 * design could define locations for "menu", "body" and "title" for example.
 * The layout would then be implemented as XLS-template with for given
 * style.</p>
 *
 * <p>The default theme handles the styles that are not defined by just
 * drawing the subcomponents with flowlayout.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class CustomLayout extends AbstractComponentContainer implements Layout {

	/** Custom layout slots containing the components */
	private HashMap slots = new HashMap();

	/** Constructor for custom layout with given style */
	public CustomLayout(String style) {
		setStyle(style);
	}

	/** Get component UIDL tag.
	 * @return Component UIDL tag as string.
	 */
	public String getTag() {
		return "customlayout";
	}

	/** Add a component into this container to given location.
	 * @param c The component to be added.
	 * @param location The location of the component
	 */
	public void addComponent(Component c, String location) {
		Component old = (Component)slots.get(location);
		if (old != null) {
			removeComponent(old);
		}
		slots.put(location, c);
		c.setParent(this);
		fireComponentAttachEvent(c);
		requestRepaint();
	}

	/** Add a component into this container. The component is added without
	 * specifying the location (empty string is then used as location). Only 
	 * one component can be added to the default "" location and adding
	 * more components into that location overwrites the old components.
	 * @param c The component to be added.
	 */
	public void addComponent(Component c) {
		this.addComponent(c, "");
	}

	/** Remove a component from this container.
	 * @param c The component to be removed.
	 */
	public void removeComponent(Component c) {
		if (c == null) return;
		slots.values().remove(c);
		c.setParent(null);
		fireComponentDetachEvent(c);
		requestRepaint();
	}

	/** Remove a component from this container from given location.
	 * @param location Location identifier of the component
	 */
	public void removeComponent(String location) {
		this.removeComponent((Component) slots.get(location));
	}

	/** Get component container iterator for going trough all the components in 
	 * the container.
	 * @return Iterator of the components inside the container.
	 */
	public Iterator getComponentIterator() {
		return slots.values().iterator();
	}

	/** Get child-component by its location.
	 * 
	 * @param location The name of the location where the requested 
	 *         component resides
	 * @return Component in the given location or null if not found.
	 */
	public Component getComponent(String location) {
		return (Component) slots.get(location);
	}

	/** Paint the content of this component.
	 * @param event PaintEvent.
	 * @throws PaintException The paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {

		// Add all items in all the locations
		for (Iterator i = slots.keySet().iterator(); i.hasNext();) {

			// Get the (location,component)
			String location = (String) i.next();
			Component c = (Component) slots.get(location);

			// Write the item
			target.startTag("location");
			target.addAttribute("name", location);
			c.paint(target);
			target.endTag("location");
		}
	}
	
	/* Documented in superclass */
	public void replaceComponent(
		Component oldComponent,
		Component newComponent) {

		// Get the locations			
		String oldLocation = null;
		String newLocation  = null;
		for (Iterator i=slots.keySet().iterator(); i.hasNext();) {
			String location = (String) i.next();
			Component component = (Component) slots.get(location);
			if (component == oldComponent) oldLocation = location;
			if (component == newComponent) newLocation = location;
		}	

		if (oldLocation == null)
			addComponent(newComponent);
		else if (newLocation == null) {
			removeComponent(oldLocation);
			addComponent(newComponent,oldLocation);
		} else {
			slots.put(newLocation,oldComponent);
			slots.put(oldLocation,newComponent);	
			requestRepaint();
		}
	}

}
