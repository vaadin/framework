/* 
@ITMillApache2LicenseForJavaFiles@
 */

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

    private final Hashtable objectKeyMap = new Hashtable();

    private final Hashtable keyObjectMap = new Hashtable();

    /**
     * Gets key for an object.
     * 
     * @param o
     *                the object.
     */
    public String key(Object o) {

        if (o == null) {
            return "null";
        }

        // If the object is already mapped, use existing key
        String key = (String) objectKeyMap.get(o);
        if (key != null) {
            return key;
        }

        // If the object is not yet mapped, map it
        key = String.valueOf(++lastKey);
        objectKeyMap.put(o, key);
        keyObjectMap.put(key, o);

        return key;
    }

    /**
     * Retrieves object with the key.
     * 
     * @param key
     *                the name with the desired value.
     * @return the object with the key.
     */
    public Object get(String key) {

        return keyObjectMap.get(key);
    }

    /**
     * Removes object from the mapper.
     * 
     * @param removeobj
     *                the object to be removed.
     */
    public void remove(Object removeobj) {
        final String key = (String) objectKeyMap.get(removeobj);

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
