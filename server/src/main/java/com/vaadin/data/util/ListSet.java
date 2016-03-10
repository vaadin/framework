/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * ListSet is an internal Vaadin class which implements a combination of a List
 * and a Set. The main purpose of this class is to provide a list with a fast
 * {@link #contains(Object)} method. Each inserted object must by unique (as
 * specified by {@link #equals(Object)}). The {@link #set(int, Object)} method
 * allows duplicates because of the way {@link Collections#sort(java.util.List)}
 * works.
 * 
 * This class is subject to change and should not be used outside Vaadin core.
 */
public class ListSet<E> extends ArrayList<E> {
    private HashSet<E> itemSet = null;

    /**
     * Contains a map from an element to the number of duplicates it has. Used
     * to temporarily allow duplicates in the list.
     */
    private HashMap<E, Integer> duplicates = new HashMap<E, Integer>();

    public ListSet() {
        super();
        itemSet = new HashSet<E>();
    }

    public ListSet(Collection<? extends E> c) {
        super(c);
        itemSet = new HashSet<E>(c.size());
        itemSet.addAll(c);
    }

    public ListSet(int initialCapacity) {
        super(initialCapacity);
        itemSet = new HashSet<E>(initialCapacity);
    }

    // Delegate contains operations to the set
    @Override
    public boolean contains(Object o) {
        return itemSet.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return itemSet.containsAll(c);
    }

    // Methods for updating the set when the list is updated.
    @Override
    public boolean add(E e) {
        if (contains(e)) {
            // Duplicates are not allowed
            return false;
        }

        if (super.add(e)) {
            itemSet.add(e);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Works as java.util.ArrayList#add(int, java.lang.Object) but returns
     * immediately if the element is already in the ListSet.
     */
    @Override
    public void add(int index, E element) {
        if (contains(element)) {
            // Duplicates are not allowed
            return;
        }

        super.add(index, element);
        itemSet.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        Iterator<? extends E> i = c.iterator();
        while (i.hasNext()) {
            E e = i.next();
            if (contains(e)) {
                continue;
            }

            if (add(e)) {
                itemSet.add(e);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        ensureCapacity(size() + c.size());

        boolean modified = false;
        Iterator<? extends E> i = c.iterator();
        while (i.hasNext()) {
            E e = i.next();
            if (contains(e)) {
                continue;
            }

            add(index++, e);
            itemSet.add(e);
            modified = true;
        }

        return modified;
    }

    @Override
    public void clear() {
        super.clear();
        itemSet.clear();
    }

    @Override
    public int indexOf(Object o) {
        if (!contains(o)) {
            return -1;
        }

        return super.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!contains(o)) {
            return -1;
        }

        return super.lastIndexOf(o);
    }

    @Override
    public E remove(int index) {
        E e = super.remove(index);

        if (e != null) {
            itemSet.remove(e);
        }

        return e;
    }

    @Override
    public boolean remove(Object o) {
        if (super.remove(o)) {
            itemSet.remove(o);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        HashSet<E> toRemove = new HashSet<E>();
        for (int idx = fromIndex; idx < toIndex; idx++) {
            toRemove.add(get(idx));
        }
        super.removeRange(fromIndex, toIndex);
        itemSet.removeAll(toRemove);
    }

    @Override
    public E set(int index, E element) {
        if (contains(element)) {
            // Element already exist in the list
            if (get(index) == element) {
                // At the same position, nothing to be done
                return element;
            } else {
                // Adding at another position. We assume this is a sort
                // operation and temporarily allow it.

                // We could just remove (null) the old element and keep the list
                // unique. This would require finding the index of the old
                // element (indexOf(element)) which is not a fast operation in a
                // list. So we instead allow duplicates temporarily.
                addDuplicate(element);
            }
        }

        E old = super.set(index, element);
        removeFromSet(old);
        itemSet.add(element);

        return old;
    }

    /**
     * Removes "e" from the set if it no longer exists in the list.
     * 
     * @param e
     */
    private void removeFromSet(E e) {
        Integer dupl = duplicates.get(e);
        if (dupl != null) {
            // A duplicate was present so we only decrement the duplicate count
            // and continue
            if (dupl == 1) {
                // This is what always should happen. A sort sets the items one
                // by one, temporarily breaking the uniqueness requirement.
                duplicates.remove(e);
            } else {
                duplicates.put(e, dupl - 1);
            }
        } else {
            // The "old" value is no longer in the list.
            itemSet.remove(e);
        }

    }

    /**
     * Marks the "element" can be found more than once from the list. Allowed in
     * {@link #set(int, Object)} to make sorting work.
     * 
     * @param element
     */
    private void addDuplicate(E element) {
        Integer nr = duplicates.get(element);
        if (nr == null) {
            nr = 1;
        } else {
            nr++;
        }

        /*
         * Store the number of duplicates of this element so we know later on if
         * we should remove an element from the set or if it was a duplicate (in
         * removeFromSet)
         */
        duplicates.put(element, nr);

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        ListSet<E> v = (ListSet<E>) super.clone();
        v.itemSet = new HashSet<E>(itemSet);
        return v;
    }

}
