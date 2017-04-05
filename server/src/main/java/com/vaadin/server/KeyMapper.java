/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.server;

import java.io.Serializable;
import java.util.HashMap;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataKeyMapper;

/**
 * <code>KeyMapper</code> is the simple two-way map for generating textual keys
 * for objects and retrieving the objects later with the key.
 *
 * @author Vaadin Ltd.
 * @since 3.0
 */
public class KeyMapper<V> implements DataKeyMapper<V>, Serializable {

    private int lastKey = 0;

    private final HashMap<V, String> objectKeyMap = new HashMap<>();

    private final HashMap<String, V> keyObjectMap = new HashMap<>();

    /**
     * Gets key for an object.
     *
     * @param o
     *            the object.
     */
    @Override
    public String key(V o) {

        if (o == null) {
            return "null";
        }

        // If the object is already mapped, use existing key
        String key = objectKeyMap.get(o);
        if (key != null) {
            return key;
        }

        // If the object is not yet mapped, map it
        key = String.valueOf(++lastKey);
        objectKeyMap.put(o, key);
        keyObjectMap.put(key, o);

        return key;
    }

    @Override
    public boolean has(V o) {
        return objectKeyMap.containsKey(o);
    }

    /**
     * Retrieves object with the key.
     *
     * @param key
     *            the name with the desired value.
     * @return the object with the key.
     */
    @Override
    public V get(String key) {
        return keyObjectMap.get(key);
    }

    /**
     * Removes object from the mapper.
     *
     * @param removeobj
     *            the object to be removed.
     */
    @Override
    public void remove(V removeobj) {
        final String key = objectKeyMap.get(removeobj);

        if (key != null) {
            objectKeyMap.remove(removeobj);
            keyObjectMap.remove(key);
        }
    }

    /**
     * Removes all objects from the mapper.
     */
    @Override
    public void removeAll() {
        objectKeyMap.clear();
        keyObjectMap.clear();
    }

    /**
     * Checks if the given key is mapped to an object.
     *
     * @since 7.7
     *
     * @param key
     *            the key to check
     * @return <code>true</code> if the key is currently mapped,
     *         <code>false</code> otherwise
     */
    public boolean containsKey(String key) {
        return keyObjectMap.containsKey(key);
    }

    @Override
    public void refresh(V dataObject,
            ValueProvider<V, Object> identifierGetter) {
        Object id = identifierGetter.apply(dataObject);
        objectKeyMap.entrySet().stream()
                .filter(e -> identifierGetter.apply(e.getKey()).equals(id))
                .findAny().ifPresent(e -> {
                    String key = objectKeyMap.remove(e.getKey());
                    objectKeyMap.put(dataObject, key);
                    keyObjectMap.put(key, dataObject);
                });
    }
}
