/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Map holding weak references to its values. It is internally backed by a
 * normal HashMap and all values are stored as WeakReferences. Garbage collected
 * entries are removed when touched.
 * 
 * @author Vaadin Ltd
 * @since 7.1.4
 */
public class WeakValueMap<K, V> implements Map<K, V> {

    /**
     * This class holds a weak reference to the value and a strong reference to
     * the key for efficient removal of stale values.
     */
    private static class WeakValueReference<K, V> extends WeakReference<V> {
        private final K key;

        WeakValueReference(K key, V value, ReferenceQueue<V> refQueue) {
            super(value, refQueue);
            this.key = key;
        }

        K getKey() {
            return key;
        }
    }

    private final HashMap<K, WeakValueReference<K, V>> backingMap;
    private final ReferenceQueue<V> refQueue;

    /**
     * Constructs a new WeakValueMap, where all values are stored as weak
     * references.
     */
    public WeakValueMap() {
        backingMap = new HashMap<K, WeakValueReference<K, V>>();
        refQueue = new ReferenceQueue<V>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        removeStaleEntries();
        backingMap.put(key, new WeakValueReference<K, V>(key, value, refQueue));
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object o) {
        removeStaleEntries();
        WeakReference<V> value = backingMap.remove(o);
        return value == null ? null : value.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        if (map != null) {
            for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        backingMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet() {
        removeStaleEntries();
        return backingMap.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(Object o) {
        removeStaleEntries();
        WeakReference<V> weakValue = backingMap.get(o);
        if (weakValue != null) {
            return weakValue.get();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        removeStaleEntries();
        return backingMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        removeStaleEntries();
        return backingMap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object o) {
        removeStaleEntries();
        return backingMap.containsKey(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object o) {
        removeStaleEntries();
        for (V value : values()) {
            if (o.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values() {
        removeStaleEntries();
        Collection<V> values = new HashSet<V>();
        for (WeakReference<V> weakValue : backingMap.values()) {
            V value = weakValue.get();
            if (value != null) {
                // null values have been GC'd, which may happen long before
                // anything is enqueued in the ReferenceQueue.
                values.add(value);
            }
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        removeStaleEntries();
        Set<Entry<K, V>> entrySet = new HashSet<Entry<K, V>>();
        for (Entry<K, WeakValueReference<K, V>> entry : backingMap.entrySet()) {
            V value = entry.getValue().get();
            if (value != null) {
                // null values have been GC'd, which may happen long before
                // anything is enqueued in the ReferenceQueue.
                entrySet.add(new AbstractMap.SimpleEntry<K, V>(entry.getKey(),
                        value));
            }
        }
        return entrySet;
    }

    /**
     * Cleans up stale entries by polling the ReferenceQueue.
     * <p>
     * Depending on the GC implementation and strategy, the ReferenceQueue is
     * not necessarily notified immediately when a reference is garbage
     * collected, but it will eventually be.
     */
    private void removeStaleEntries() {
        Reference<? extends V> ref;
        while ((ref = refQueue.poll()) != null) {
            Object key = ((WeakValueReference<?, ?>) ref).getKey();
            backingMap.remove(key);
        }
    }
}
