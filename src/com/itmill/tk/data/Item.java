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


package com.itmill.tk.data;

import java.util.Collection;

/** <p>Provides a mechanism for handling a set of Properties, each associated
 * to a locally unique identifier. The interface is split into subinterfaces
 * to enable a class to implement only the functionalities it needs.</p>
 * 
 * @author  IT Mill Ltd
 * @version @VERSION@
 * @since 3.0
 */
public interface Item {

	/** Gets the Property corresponding to the given Property ID stored in
	 * the Item. If the Item does not contain the Property, 
	 * <code>null</code> is returned.
	 * 
	 * @param id identifier of the Property to get
	 * @return the Property with the given ID or <code>null</code>
	 */
	public Property getItemProperty(Object id);

	/** Gets the collection of IDs of all Properties stored in the Item.
	 * 
	 * @return unmodifiable collection containing IDs of the Properties
	 * stored the Item
	 */
	public Collection getItemPropertyIds();

	/** Tries to add a new Property into the Item.
	 * 
	 * <p>This functionality is optional.</p>
	 * 
	 * @param id ID of the new Property
	 * @param property the Property to be added and associated with
	 * <code>id</code>
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean addItemProperty(Object id, Property property)
	throws UnsupportedOperationException;

	/** Removes the Property identified by ID from the Item. 

	 * <p>This functionality is optional.</p>
	 *
	 * @param id ID of the Property to be removed
	 * @throws UnsupportedOperationException if the operation is not supported.
	 * @return <code>true</code> if the operation succeeded
	 * <code>false</code> if not
	 */
	public boolean removeItemProperty(Object id)
	throws UnsupportedOperationException;

	/** Interface implemented by viewer classes capable of using an Item as
	 * a data source.
	 */
	public interface Viewer {

		/** Sets the Item that serves as the data source of the viewer.
		 * 
		 * @param newDataSource The new data source Item
		 */
		public void setItemDataSource(Item newDataSource);

		/** Gets the Item serving as the data source of the viewer.
		 * 
		 * @return data source Item
		 */
		public Item getItemDataSource();
	}

	/** Interface implemented by the editor classes capable of editing the
	 * Item. Implementing this interface means that the Item serving as the
	 * data source of the editor can be modified through it. Note that
	 * not implementing the <code>Item.Editor</code> interface does not
	 * restrict the class from editing the contents of an internally.
	 */
	public interface Editor extends Item.Viewer {

	}

	/* Property set change event ******************************************** */

	/** An <code>Event</code> object specifying the Item whose contents
	 * has been changed through the Property.Managed interface. Note that
	 * the values stored in the Properties may change without triggering
	 * this event.
	 */
	public interface PropertySetChangeEvent {

		/** Retrieves the Item whose contents has been modified.
		 * 
		 * @return source Item of the event
		 */
		public Item getItem();
	}

	/** The listener interface for receiving
	 * <code>PropertySetChangeEvent</code> objects.
	 */
	public interface PropertySetChangeListener {

		/** Notifies this listener that the Item's property set has changed.
		 * 
		 * @param event Property set change event object
		 */
		public void itemPropertySetChange(Item.PropertySetChangeEvent event);
	}

	/** The interface for adding and removing
	 * <code>PropertySetChangeEvent</code> listeners. By implementing this
	 * interface a class explicitly announces that it will generate a
	 * <code>PropertySetChangeEvent</code> when its Property set is
	 * modified.
	 * 
	 * Note that the general Java convention is not to explicitly declare
	 * that a class generates events, but to directly define the
	 * <code>addListener</code> and <code>removeListener</code> methods.
	 * That way the caller of these methods has no real way of finding out
	 * if the class really will send the events, or if it just defines the
	 * methods to be able to implement an interface.
	 */
	public interface PropertySetChangeNotifier {

		/** Registers a new property set change listener for this Item.
		 * 
		 * @param listener The new Listener to be registered.
		 */
		public void addListener(Item.PropertySetChangeListener listener);

		/** Removes a previously registered property set change listener.
		 * 
		 * @param listener Listener to be removed.
		 */
		public void removeListener(Item.PropertySetChangeListener listener);
	}
}
