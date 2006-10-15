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

import java.util.Hashtable;

/** Simple two-way map for generating textual keys for objects and
 * retrieving the objects later with the key.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class KeyMapper {
    
    private int lastKey = 0;
    private Hashtable objectKeyMap = new Hashtable();
    private Hashtable keyObjectMap = new Hashtable();
    
    /** Get key for an object */
    public String key(Object o) {
        
        if (o == null) return "null";
        
        // If the object is already mapped, use existing key
        String key = (String) objectKeyMap.get(o);
        if (key != null) return key;
        
        // If the object is not yet mapped, map it
        key = String.valueOf(++lastKey);
        objectKeyMap.put(o,key);
        keyObjectMap.put(key,o);

        return key;
    }

	/** Check if the key belongs to a new id. 
	 * <p>Usage of new id:s are specific to components, but for example Select
	 * component uses newItemId:s for selection of newly added items in 
	 * <code>allowNewItems</code>-mode
	 */ 
	public boolean isNewIdKey(String key) {
		return "NEW".equals(key);
	}
    
    /** Retrieve object with the key*/
    public Object get(String key) {

        return keyObjectMap.get(key);
    }
    
    /** Remove object from the mapper. */
    public void remove(Object o) {
        String key = (String) objectKeyMap.get(o);
        
        if (key != null) {
            objectKeyMap.remove(key);
            keyObjectMap.remove(o);
        }
    }
    
    /** Remove all objects from the mapper. */
    public void removeAll() {
    	objectKeyMap.clear();
    	keyObjectMap.clear();
    }    
}
