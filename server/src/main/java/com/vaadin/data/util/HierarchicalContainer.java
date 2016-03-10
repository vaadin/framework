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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * A specialized Container whose contents can be accessed like it was a
 * tree-like structure.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class HierarchicalContainer extends IndexedContainer implements
        Container.Hierarchical {

    /**
     * Set of IDs of those contained Items that can't have children.
     */
    private final HashSet<Object> noChildrenAllowed = new HashSet<Object>();

    /**
     * Mapping from Item ID to parent Item ID.
     */
    private final HashMap<Object, Object> parent = new HashMap<Object, Object>();

    /**
     * Mapping from Item ID to parent Item ID for items included in the filtered
     * container.
     */
    private HashMap<Object, Object> filteredParent = null;

    /**
     * Mapping from Item ID to a list of child IDs.
     */
    private final HashMap<Object, LinkedList<Object>> children = new HashMap<Object, LinkedList<Object>>();

    /**
     * Mapping from Item ID to a list of child IDs when filtered
     */
    private HashMap<Object, LinkedList<Object>> filteredChildren = null;

    /**
     * List that contains all root elements of the container.
     */
    private final LinkedList<Object> roots = new LinkedList<Object>();

    /**
     * List that contains all filtered root elements of the container.
     */
    private LinkedList<Object> filteredRoots = null;

    /**
     * Determines how filtering of the container is done.
     */
    private boolean includeParentsWhenFiltering = true;

    /**
     * Counts how many nested contents change disable calls are in progress.
     * 
     * Pending events are only fired when the counter reaches zero again.
     */
    private int contentChangedEventsDisabledCount = 0;

    private boolean contentsChangedEventPending;

    /*
     * Can the specified Item have any children? Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    @Override
    public boolean areChildrenAllowed(Object itemId) {
        if (noChildrenAllowed.contains(itemId)) {
            return false;
        }
        return containsId(itemId);
    }

    /*
     * Gets the IDs of the children of the specified Item. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Collection<?> getChildren(Object itemId) {
        LinkedList<Object> c;

        if (filteredChildren != null) {
            c = filteredChildren.get(itemId);
        } else {
            c = children.get(itemId);
        }

        if (c == null) {
            return null;
        }
        return Collections.unmodifiableCollection(c);
    }

    /*
     * Gets the ID of the parent of the specified Item. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Object getParent(Object itemId) {
        if (filteredParent != null) {
            return filteredParent.get(itemId);
        }
        return parent.get(itemId);
    }

    /*
     * Is the Item corresponding to the given ID a leaf node? Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean hasChildren(Object itemId) {
        if (filteredChildren != null) {
            return filteredChildren.containsKey(itemId);
        } else {
            return children.containsKey(itemId);
        }
    }

    /*
     * Is the Item corresponding to the given ID a root node? Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean isRoot(Object itemId) {
        // If the container is filtered the itemId must be among filteredRoots
        // to be a root.
        if (filteredRoots != null) {
            if (!filteredRoots.contains(itemId)) {
                return false;
            }
        } else {
            // Container is not filtered
            if (parent.containsKey(itemId)) {
                return false;
            }
        }

        return containsId(itemId);
    }

    /*
     * Gets the IDs of the root elements in the container. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Collection<?> rootItemIds() {
        if (filteredRoots != null) {
            return Collections.unmodifiableCollection(filteredRoots);
        } else {
            return Collections.unmodifiableCollection(roots);
        }
    }

    /**
     * <p>
     * Sets the given Item's capability to have children. If the Item identified
     * with the itemId already has children and the areChildrenAllowed is false
     * this method fails and <code>false</code> is returned; the children must
     * be first explicitly removed with
     * {@link #setParent(Object itemId, Object newParentId)} or
     * {@link com.vaadin.data.Container#removeItem(Object itemId)}.
     * </p>
     * 
     * @param itemId
     *            the ID of the Item in the container whose child capability is
     *            to be set.
     * @param childrenAllowed
     *            the boolean value specifying if the Item can have children or
     *            not.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    @Override
    public boolean setChildrenAllowed(Object itemId, boolean childrenAllowed) {

        // Checks that the item is in the container
        if (!containsId(itemId)) {
            return false;
        }

        // Updates status
        if (childrenAllowed) {
            noChildrenAllowed.remove(itemId);
        } else {
            noChildrenAllowed.add(itemId);
        }

        return true;
    }

    /**
     * <p>
     * Sets the parent of an Item. The new parent item must exist and be able to
     * have children. (<code>canHaveChildren(newParentId) == true</code>). It is
     * also possible to detach a node from the hierarchy (and thus make it root)
     * by setting the parent <code>null</code>.
     * </p>
     * 
     * @param itemId
     *            the ID of the item to be set as the child of the Item
     *            identified with newParentId.
     * @param newParentId
     *            the ID of the Item that's to be the new parent of the Item
     *            identified with itemId.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    @Override
    public boolean setParent(Object itemId, Object newParentId) {

        // Checks that the item is in the container
        if (!containsId(itemId)) {
            return false;
        }

        // Gets the old parent
        final Object oldParentId = parent.get(itemId);

        // Checks if no change is necessary
        if ((newParentId == null && oldParentId == null)
                || ((newParentId != null) && newParentId.equals(oldParentId))) {
            return true;
        }

        // Making root?
        if (newParentId == null) {
            // The itemId should become a root so we need to
            // - Remove it from the old parent's children list
            // - Add it as a root
            // - Remove it from the item -> parent list (parent is null for
            // roots)

            // Removes from old parents children list
            final LinkedList<Object> l = children.get(oldParentId);
            if (l != null) {
                l.remove(itemId);
                if (l.isEmpty()) {
                    children.remove(oldParentId);
                }

            }

            // Add to be a root
            roots.add(itemId);

            // Updates parent
            parent.remove(itemId);

            if (hasFilters()) {
                // Refilter the container if setParent is called when filters
                // are applied. Changing parent can change what is included in
                // the filtered version (if includeParentsWhenFiltering==true).
                doFilterContainer(hasFilters());
            }

            fireItemSetChange();

            return true;
        }

        // We get here when the item should not become a root and we need to
        // - Verify the new parent exists and can have children
        // - Check that the new parent is not a child of the selected itemId
        // - Updated the item -> parent mapping to point to the new parent
        // - Remove the item from the roots list if it was a root
        // - Remove the item from the old parent's children list if it was not a
        // root

        // Checks that the new parent exists in container and can have
        // children
        if (!containsId(newParentId) || noChildrenAllowed.contains(newParentId)) {
            return false;
        }

        // Checks that setting parent doesn't result to a loop
        Object o = newParentId;
        while (o != null && !o.equals(itemId)) {
            o = parent.get(o);
        }
        if (o != null) {
            return false;
        }

        // Updates parent
        parent.put(itemId, newParentId);
        LinkedList<Object> pcl = children.get(newParentId);
        if (pcl == null) {
            // Create an empty list for holding children if one were not
            // previously created
            pcl = new LinkedList<Object>();
            children.put(newParentId, pcl);
        }
        pcl.add(itemId);

        // Removes from old parent or root
        if (oldParentId == null) {
            roots.remove(itemId);
        } else {
            final LinkedList<Object> l = children.get(oldParentId);
            if (l != null) {
                l.remove(itemId);
                if (l.isEmpty()) {
                    children.remove(oldParentId);
                }
            }
        }

        if (hasFilters()) {
            // Refilter the container if setParent is called when filters
            // are applied. Changing parent can change what is included in
            // the filtered version (if includeParentsWhenFiltering==true).
            doFilterContainer(hasFilters());
        }

        fireItemSetChange();

        return true;
    }

    private boolean hasFilters() {
        return (filteredRoots != null);
    }

    /**
     * Moves a node (an Item) in the container immediately after a sibling node.
     * The two nodes must have the same parent in the container.
     * 
     * @param itemId
     *            the identifier of the moved node (Item)
     * @param siblingId
     *            the identifier of the reference node (Item), after which the
     *            other node will be located
     */
    public void moveAfterSibling(Object itemId, Object siblingId) {
        Object parent2 = getParent(itemId);
        LinkedList<Object> childrenList;
        if (parent2 == null) {
            childrenList = roots;
        } else {
            childrenList = children.get(parent2);
        }
        if (siblingId == null) {
            childrenList.remove(itemId);
            childrenList.addFirst(itemId);

        } else {
            int oldIndex = childrenList.indexOf(itemId);
            int indexOfSibling = childrenList.indexOf(siblingId);
            if (indexOfSibling != -1 && oldIndex != -1) {
                int newIndex;
                if (oldIndex > indexOfSibling) {
                    newIndex = indexOfSibling + 1;
                } else {
                    newIndex = indexOfSibling;
                }
                childrenList.remove(oldIndex);
                childrenList.add(newIndex, itemId);
            } else {
                throw new IllegalArgumentException(
                        "Given identifiers no not have the same parent.");
            }
        }
        fireItemSetChange();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.IndexedContainer#addItem()
     */
    @Override
    public Object addItem() {
        disableContentsChangeEvents();
        try {
            final Object itemId = super.addItem();
            if (itemId == null) {
                return null;
            }

            if (!roots.contains(itemId)) {
                roots.add(itemId);
                if (filteredRoots != null) {
                    if (passesFilters(itemId)) {
                        filteredRoots.add(itemId);
                    }
                }
            }
            return itemId;
        } finally {
            enableAndFireContentsChangeEvents();
        }
    }

    @Override
    protected void fireItemSetChange(
            com.vaadin.data.Container.ItemSetChangeEvent event) {
        if (contentsChangeEventsOn()) {
            super.fireItemSetChange(event);
        } else {
            contentsChangedEventPending = true;
        }
    }

    private boolean contentsChangeEventsOn() {
        return contentChangedEventsDisabledCount == 0;
    }

    private void disableContentsChangeEvents() {
        contentChangedEventsDisabledCount++;
    }

    private void enableAndFireContentsChangeEvents() {
        if (contentChangedEventsDisabledCount <= 0) {
            getLogger()
                    .log(Level.WARNING,
                            "Mismatched calls to disable and enable contents change events in HierarchicalContainer");
            contentChangedEventsDisabledCount = 0;
        } else {
            contentChangedEventsDisabledCount--;
        }
        if (contentChangedEventsDisabledCount == 0) {
            if (contentsChangedEventPending) {
                fireItemSetChange();
            }
            contentsChangedEventPending = false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.IndexedContainer#addItem(java.lang.Object)
     */
    @Override
    public Item addItem(Object itemId) {
        disableContentsChangeEvents();
        try {
            final Item item = super.addItem(itemId);
            if (item == null) {
                return null;
            }

            roots.add(itemId);

            if (filteredRoots != null) {
                if (passesFilters(itemId)) {
                    filteredRoots.add(itemId);
                }
            }
            return item;
        } finally {
            enableAndFireContentsChangeEvents();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.IndexedContainer#removeAllItems()
     */
    @Override
    public boolean removeAllItems() {
        disableContentsChangeEvents();
        try {
            final boolean success = super.removeAllItems();

            if (success) {
                roots.clear();
                parent.clear();
                children.clear();
                noChildrenAllowed.clear();
                if (filteredRoots != null) {
                    filteredRoots = null;
                }
                if (filteredChildren != null) {
                    filteredChildren = null;
                }
                if (filteredParent != null) {
                    filteredParent = null;
                }
            }
            return success;
        } finally {
            enableAndFireContentsChangeEvents();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.IndexedContainer#removeItem(java.lang.Object )
     */
    @Override
    public boolean removeItem(Object itemId) {
        disableContentsChangeEvents();
        try {
            final boolean success = super.removeItem(itemId);

            if (success) {
                // Remove from roots if this was a root
                if (roots.remove(itemId)) {

                    // If filtering is enabled we might need to remove it from
                    // the filtered list also
                    if (filteredRoots != null) {
                        filteredRoots.remove(itemId);
                    }
                }

                // Clear the children list. Old children will now become root
                // nodes
                LinkedList<Object> childNodeIds = children.remove(itemId);
                if (childNodeIds != null) {
                    if (filteredChildren != null) {
                        filteredChildren.remove(itemId);
                    }
                    for (Object childId : childNodeIds) {
                        setParent(childId, null);
                    }
                }

                // Parent of the item that we are removing will contain the item
                // id in its children list
                final Object parentItemId = parent.get(itemId);
                if (parentItemId != null) {
                    final LinkedList<Object> c = children.get(parentItemId);
                    if (c != null) {
                        c.remove(itemId);

                        if (c.isEmpty()) {
                            children.remove(parentItemId);
                        }

                        // Found in the children list so might also be in the
                        // filteredChildren list
                        if (filteredChildren != null) {
                            LinkedList<Object> f = filteredChildren
                                    .get(parentItemId);
                            if (f != null) {
                                f.remove(itemId);
                                if (f.isEmpty()) {
                                    filteredChildren.remove(parentItemId);
                                }
                            }
                        }
                    }
                }
                parent.remove(itemId);
                if (filteredParent != null) {
                    // Item id no longer has a parent as the item id is not in
                    // the container.
                    filteredParent.remove(itemId);
                }
                noChildrenAllowed.remove(itemId);
            }

            return success;
        } finally {
            enableAndFireContentsChangeEvents();
        }
    }

    /**
     * Removes the Item identified by given itemId and all its children.
     * 
     * @see #removeItem(Object)
     * @param itemId
     *            the identifier of the Item to be removed
     * @return true if the operation succeeded
     */
    public boolean removeItemRecursively(Object itemId) {
        disableContentsChangeEvents();
        try {
            boolean removeItemRecursively = removeItemRecursively(this, itemId);
            return removeItemRecursively;
        } finally {
            enableAndFireContentsChangeEvents();
        }
    }

    /**
     * Removes the Item identified by given itemId and all its children from the
     * given Container.
     * 
     * @param container
     *            the container where the item is to be removed
     * @param itemId
     *            the identifier of the Item to be removed
     * @return true if the operation succeeded
     */
    public static boolean removeItemRecursively(
            Container.Hierarchical container, Object itemId) {
        boolean success = true;
        Collection<?> children2 = container.getChildren(itemId);
        if (children2 != null) {
            Object[] array = children2.toArray();
            for (int i = 0; i < array.length; i++) {
                boolean removeItemRecursively = removeItemRecursively(
                        container, array[i]);
                if (!removeItemRecursively) {
                    success = false;
                }
            }
        }
        // remove the root of subtree if children where succesfully removed
        if (success) {
            success = container.removeItem(itemId);
        }
        return success;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.IndexedContainer#doSort()
     */
    @Override
    protected void doSort() {
        super.doSort();

        Collections.sort(roots, getItemSorter());
        for (LinkedList<Object> childList : children.values()) {
            Collections.sort(childList, getItemSorter());
        }
    }

    /**
     * Used to control how filtering works. @see
     * {@link #setIncludeParentsWhenFiltering(boolean)} for more information.
     * 
     * @return true if all parents for items that match the filter are included
     *         when filtering, false if only the matching items are included
     */
    public boolean isIncludeParentsWhenFiltering() {
        return includeParentsWhenFiltering;
    }

    /**
     * Controls how the filtering of the container works. Set this to true to
     * make filtering include parents for all matched items in addition to the
     * items themselves. Setting this to false causes the filtering to only
     * include the matching items and make items with excluded parents into root
     * items.
     * 
     * @param includeParentsWhenFiltering
     *            true to include all parents for items that match the filter,
     *            false to only include the matching items
     */
    public void setIncludeParentsWhenFiltering(
            boolean includeParentsWhenFiltering) {
        this.includeParentsWhenFiltering = includeParentsWhenFiltering;
        if (filteredRoots != null) {
            // Currently filtered so needs to be re-filtered
            doFilterContainer(true);
        }
    }

    /*
     * Overridden to provide filtering for root & children items.
     * 
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.IndexedContainer#updateContainerFiltering()
     */
    @Override
    protected boolean doFilterContainer(boolean hasFilters) {
        if (!hasFilters) {
            // All filters removed
            filteredRoots = null;
            filteredChildren = null;
            filteredParent = null;

            return super.doFilterContainer(hasFilters);
        }

        // Reset data structures
        filteredRoots = new LinkedList<Object>();
        filteredChildren = new HashMap<Object, LinkedList<Object>>();
        filteredParent = new HashMap<Object, Object>();

        if (includeParentsWhenFiltering) {
            // Filter so that parents for items that match the filter are also
            // included
            HashSet<Object> includedItems = new HashSet<Object>();
            for (Object rootId : roots) {
                if (filterIncludingParents(rootId, includedItems)) {
                    filteredRoots.add(rootId);
                    addFilteredChildrenRecursively(rootId, includedItems);
                }
            }
            // includedItemIds now contains all the item ids that should be
            // included. Filter IndexedContainer based on this
            filterOverride = includedItems;
            super.doFilterContainer(hasFilters);
            filterOverride = null;

            return true;
        } else {
            // Filter by including all items that pass the filter and make items
            // with no parent new root items

            // Filter IndexedContainer first so getItemIds return the items that
            // match
            super.doFilterContainer(hasFilters);

            LinkedHashSet<Object> filteredItemIds = new LinkedHashSet<Object>(
                    getItemIds());

            for (Object itemId : filteredItemIds) {
                Object itemParent = parent.get(itemId);
                if (itemParent == null || !filteredItemIds.contains(itemParent)) {
                    // Parent is not included or this was a root, in both cases
                    // this should be a filtered root
                    filteredRoots.add(itemId);
                } else {
                    // Parent is included. Add this to the children list (create
                    // it first if necessary)
                    addFilteredChild(itemParent, itemId);
                }
            }

            return true;
        }
    }

    /**
     * Adds the given childItemId as a filteredChildren for the parentItemId and
     * sets it filteredParent.
     * 
     * @param parentItemId
     * @param childItemId
     */
    private void addFilteredChild(Object parentItemId, Object childItemId) {
        LinkedList<Object> parentToChildrenList = filteredChildren
                .get(parentItemId);
        if (parentToChildrenList == null) {
            parentToChildrenList = new LinkedList<Object>();
            filteredChildren.put(parentItemId, parentToChildrenList);
        }
        filteredParent.put(childItemId, parentItemId);
        parentToChildrenList.add(childItemId);

    }

    /**
     * Recursively adds all items in the includedItems list to the
     * filteredChildren map in the same order as they are in the children map.
     * Starts from parentItemId and recurses down as long as child items that
     * should be included are found.
     * 
     * @param parentItemId
     *            The item id to start recurse from. Not added to a
     *            filteredChildren list
     * @param includedItems
     *            Set containing the item ids for the items that should be
     *            included in the filteredChildren map
     */
    private void addFilteredChildrenRecursively(Object parentItemId,
            HashSet<Object> includedItems) {
        LinkedList<Object> childList = children.get(parentItemId);
        if (childList == null) {
            return;
        }

        for (Object childItemId : childList) {
            if (includedItems.contains(childItemId)) {
                addFilteredChild(parentItemId, childItemId);
                addFilteredChildrenRecursively(childItemId, includedItems);
            }
        }
    }

    /**
     * Scans the itemId and all its children for which items should be included
     * when filtering. All items which passes the filters are included.
     * Additionally all items that have a child node that should be included are
     * also themselves included.
     * 
     * @param itemId
     * @param includedItems
     * @return true if the itemId should be included in the filtered container.
     */
    private boolean filterIncludingParents(Object itemId,
            HashSet<Object> includedItems) {
        boolean toBeIncluded = passesFilters(itemId);

        LinkedList<Object> childList = children.get(itemId);
        if (childList != null) {
            for (Object childItemId : children.get(itemId)) {
                toBeIncluded |= filterIncludingParents(childItemId,
                        includedItems);
            }
        }

        if (toBeIncluded) {
            includedItems.add(itemId);
        }
        return toBeIncluded;
    }

    private Set<Object> filterOverride = null;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.IndexedContainer#passesFilters(java.lang.Object)
     */
    @Override
    protected boolean passesFilters(Object itemId) {
        if (filterOverride != null) {
            return filterOverride.contains(itemId);
        } else {
            return super.passesFilters(itemId);
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(HierarchicalContainer.class.getName());
    }
}
