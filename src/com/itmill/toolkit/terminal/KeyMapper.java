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

import java.util.Hashtable;

/**
 * <code>KeyMapper</code> is the simple two-way map for generating textual
 * keys for objects and retrieving the objects later with the key.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class KeyMapper {

	private int lastKey = 0;

	private Hashtable objectKeyMap = new Hashtable();

	private Hashtable keyObjectMap = new Hashtable();

	/**
	 * Gets key for an object.
	 * 
	 * @param o
	 *            the object.
	 */
	public String key(Object o) {

		if (o == null)
			return "null";

		// If the object is already mapped, use existing key
		String key = (String) objectKeyMap.get(o);
		if (key != null)
			return key;

		// If the object is not yet mapped, map it
		key = String.valueOf(++lastKey);
		objectKeyMap.put(o, key);
		keyObjectMap.put(key, o);

		return key;
	}

	/**
	 * Checks if the key belongs to a new id.
	 * <p>
	 * Usage of new id:s are specific to components, but for example Select
	 * component uses newItemId:s for selection of newly added items in
	 * <code>allowNewItems</code>-mode.
	 * 
	 * @param key
	 * @return <code>true</code> if the key belongs to the new id,otherwise
	 *         <code>false</code>.
	 */
	public boolean isNewIdKey(String key) {
		return "NEW".equals(key);
	}

	/**
	 * Retrieves object with the key.
	 * 
	 * @param key
	 *            the name with the desired value.
	 * @return the object with the key.
	 */
	public Object get(String key) {

		return keyObjectMap.get(key);
	}

	/**
	 * Removes object from the mapper.
	 * 
	 * @param removeobj
	 *            the object to be removed.
	 */
	public void remove(Object removeobj) {
		String key = (String) objectKeyMap.get(removeobj);

		if (key != null) {
			objectKeyMap.remove(key);
			keyObjectMap.remove(removeobj);
		}
	}

	/**
	 * Removes all objects from the mapper.
	 */
	public void removeAll() {
		objectKeyMap.clear();
		keyObjectMap.clear();
	}
}
