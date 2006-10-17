/* *************************************************************************
 
                               Enably Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.terminal;

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
