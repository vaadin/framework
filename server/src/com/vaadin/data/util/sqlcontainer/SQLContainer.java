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
package com.vaadin.data.util.sqlcontainer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Container;
import com.vaadin.data.ContainerHelpers;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.MSSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.OracleGenerator;

public class SQLContainer implements Container, Container.Filterable,
        Container.Indexed, Container.Sortable, Container.ItemSetChangeNotifier {

    /** Query delegate */
    private QueryDelegate queryDelegate;
    /** Auto commit mode, default = false */
    private boolean autoCommit = false;

    /** Page length = number of items contained in one page */
    private int pageLength = DEFAULT_PAGE_LENGTH;
    public static final int DEFAULT_PAGE_LENGTH = 100;

    /** Number of items to cache = CACHE_RATIO x pageLength */
    public static final int CACHE_RATIO = 2;

    /** Amount of cache to overlap with previous page */
    private int cacheOverlap = pageLength;

    /** Item and index caches */
    private final Map<Integer, RowId> itemIndexes = new HashMap<Integer, RowId>();
    private final CacheMap<RowId, RowItem> cachedItems = new CacheMap<RowId, RowItem>();

    /** Container properties = column names, data types and statuses */
    private final List<String> propertyIds = new ArrayList<String>();
    private final Map<String, Class<?>> propertyTypes = new HashMap<String, Class<?>>();
    private final Map<String, Boolean> propertyReadOnly = new HashMap<String, Boolean>();
    private final Map<String, Boolean> propertyPersistable = new HashMap<String, Boolean>();
    private final Map<String, Boolean> propertyNullable = new HashMap<String, Boolean>();
    private final Map<String, Boolean> propertyPrimaryKey = new HashMap<String, Boolean>();

    /** Filters (WHERE) and sorters (ORDER BY) */
    private final List<Filter> filters = new ArrayList<Filter>();
    private final List<OrderBy> sorters = new ArrayList<OrderBy>();

    /**
     * Total number of items available in the data source using the current
     * query, filters and sorters.
     */
    private int size;

    /**
     * Size updating logic. Do not update size from data source if it has been
     * updated in the last sizeValidMilliSeconds milliseconds.
     */
    private final int sizeValidMilliSeconds = 10000;
    private boolean sizeDirty = true;
    private Date sizeUpdated = new Date();

    /** Starting row number of the currently fetched page */
    private int currentOffset;

    /** ItemSetChangeListeners */
    private LinkedList<Container.ItemSetChangeListener> itemSetChangeListeners;

    /** Temporary storage for modified items and items to be removed and added */
    private final Map<RowId, RowItem> removedItems = new HashMap<RowId, RowItem>();
    private final List<RowItem> addedItems = new ArrayList<RowItem>();
    private final List<RowItem> modifiedItems = new ArrayList<RowItem>();

    /** List of references to other SQLContainers */
    private final Map<SQLContainer, Reference> references = new HashMap<SQLContainer, Reference>();

    /** Cache flush notification system enabled. Disabled by default. */
    private boolean notificationsEnabled;

    /**
     * Prevent instantiation without a QueryDelegate.
     */
    @SuppressWarnings("unused")
    private SQLContainer() {
    }

    /**
     * Creates and initializes SQLContainer using the given QueryDelegate
     * 
     * @param delegate
     *            QueryDelegate implementation
     * @throws SQLException
     */
    public SQLContainer(QueryDelegate delegate) throws SQLException {
        if (delegate == null) {
            throw new IllegalArgumentException(
                    "QueryDelegate must not be null.");
        }
        queryDelegate = delegate;
        getPropertyIds();
        cachedItems.setCacheLimit(CACHE_RATIO * getPageLength() + cacheOverlap);
    }

    /**************************************/
    /** Methods from interface Container **/
    /**************************************/

    /**
     * Note! If auto commit mode is enabled, this method will still return the
     * temporary row ID assigned for the item. Implement
     * QueryDelegate.RowIdChangeListener to receive the actual Row ID value
     * after the addition has been committed.
     * 
     * {@inheritDoc}
     */

    @Override
    public Object addItem() throws UnsupportedOperationException {
        Object emptyKey[] = new Object[queryDelegate.getPrimaryKeyColumns()
                .size()];
        RowId itemId = new TemporaryRowId(emptyKey);
        // Create new empty column properties for the row item.
        List<ColumnProperty> itemProperties = new ArrayList<ColumnProperty>();
        for (String propertyId : propertyIds) {
            /* Default settings for new item properties. */
            ColumnProperty cp = new ColumnProperty(propertyId,
                    propertyReadOnly.get(propertyId),
                    propertyPersistable.get(propertyId),
                    propertyNullable.get(propertyId),
                    propertyPrimaryKey.get(propertyId), null,
                    getType(propertyId));

            itemProperties.add(cp);
        }
        RowItem newRowItem = new RowItem(this, itemId, itemProperties);

        if (autoCommit) {
            /* Add and commit instantly */
            try {
                if (queryDelegate instanceof TableQuery) {
                    itemId = ((TableQuery) queryDelegate)
                            .storeRowImmediately(newRowItem);
                } else {
                    queryDelegate.beginTransaction();
                    queryDelegate.storeRow(newRowItem);
                    queryDelegate.commit();
                }
                refresh();
                if (notificationsEnabled) {
                    CacheFlushNotifier.notifyOfCacheFlush(this);
                }
                getLogger().log(Level.FINER, "Row added to DB...");
                return itemId;
            } catch (SQLException e) {
                getLogger().log(Level.WARNING,
                        "Failed to add row to DB. Rolling back.", e);
                try {
                    queryDelegate.rollback();
                } catch (SQLException ee) {
                    getLogger().log(Level.SEVERE,
                            "Failed to roll back row addition", e);
                }
                return null;
            }
        } else {
            addedItems.add(newRowItem);
            fireContentsChange();
            return itemId;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#containsId(java.lang.Object)
     */

    @Override
    public boolean containsId(Object itemId) {
        if (itemId == null) {
            return false;
        }

        if (cachedItems.containsKey(itemId)) {
            return true;
        } else {
            for (RowItem item : addedItems) {
                if (item.getId().equals(itemId)) {
                    return itemPassesFilters(item);
                }
            }
        }
        if (removedItems.containsKey(itemId)) {
            return false;
        }

        if (itemId instanceof ReadOnlyRowId) {
            int rowNum = ((ReadOnlyRowId) itemId).getRowNum();
            return rowNum >= 0 && rowNum < size;
        }

        if (itemId instanceof RowId && !(itemId instanceof TemporaryRowId)) {
            try {
                return queryDelegate.containsRowWithKey(((RowId) itemId)
                        .getId());
            } catch (Exception e) {
                /* Query failed, just return false. */
                getLogger().log(Level.WARNING, "containsId query failed", e);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object,
     * java.lang.Object)
     */

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        Item item = getItem(itemId);
        if (item == null) {
            return null;
        }
        return item.getItemProperty(propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerPropertyIds()
     */

    @Override
    public Collection<?> getContainerPropertyIds() {
        return Collections.unmodifiableCollection(propertyIds);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */

    @Override
    public Item getItem(Object itemId) {
        if (!cachedItems.containsKey(itemId)) {
            int index = indexOfId(itemId);
            if (index >= size) {
                // The index is in the added items
                int offset = index - size;
                RowItem item = addedItems.get(offset);
                if (itemPassesFilters(item)) {
                    return item;
                } else {
                    return null;
                }
            } else {
                // load the item into cache
                updateOffsetAndCache(index);
            }
        }
        return cachedItems.get(itemId);
    }

    /**
     * Bypasses in-memory filtering to return items that are cached in memory.
     * <em>NOTE</em>: This does not bypass database-level filtering.
     * 
     * @param itemId
     *            the id of the item to retrieve.
     * @return the item represented by itemId.
     */
    public Item getItemUnfiltered(Object itemId) {
        if (!cachedItems.containsKey(itemId)) {
            for (RowItem item : addedItems) {
                if (item.getId().equals(itemId)) {
                    return item;
                }
            }
        }
        return cachedItems.get(itemId);
    }

    /**
     * NOTE! Do not use this method if in any way avoidable. This method doesn't
     * (and cannot) use lazy loading, which means that all rows in the database
     * will be loaded into memory.
     * 
     * {@inheritDoc}
     */

    @Override
    public Collection<?> getItemIds() {
        updateCount();
        ArrayList<RowId> ids = new ArrayList<RowId>();
        ResultSet rs = null;
        try {
            // Load ALL rows :(
            queryDelegate.beginTransaction();
            rs = queryDelegate.getResults(0, 0);
            List<String> pKeys = queryDelegate.getPrimaryKeyColumns();
            while (rs.next()) {
                RowId id = null;
                if (pKeys.isEmpty()) {
                    /* Create a read only itemId */
                    id = new ReadOnlyRowId(rs.getRow());
                } else {
                    /* Generate itemId for the row based on primary key(s) */
                    Object[] itemId = new Object[pKeys.size()];
                    for (int i = 0; i < pKeys.size(); i++) {
                        itemId[i] = rs.getObject(pKeys.get(i));
                    }
                    id = new RowId(itemId);
                }
                if (id != null && !removedItems.containsKey(id)) {
                    ids.add(id);
                }
            }
            rs.getStatement().close();
            rs.close();
            queryDelegate.commit();
        } catch (SQLException e) {
            getLogger().log(Level.WARNING,
                    "getItemIds() failed, rolling back.", e);
            try {
                queryDelegate.rollback();
            } catch (SQLException e1) {
                getLogger().log(Level.SEVERE, "Failed to roll back state", e1);
            }
            try {
                rs.getStatement().close();
                rs.close();
            } catch (SQLException e1) {
                getLogger().log(Level.WARNING, "Closing session failed", e1);
            }
            throw new RuntimeException("Failed to fetch item indexes.", e);
        }
        for (RowItem item : getFilteredAddedItems()) {
            ids.add(item.getId());
        }
        return Collections.unmodifiableCollection(ids);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */

    @Override
    public Class<?> getType(Object propertyId) {
        if (!propertyIds.contains(propertyId)) {
            return null;
        }
        return propertyTypes.get(propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#size()
     */

    @Override
    public int size() {
        updateCount();
        return size + sizeOfAddedItems() - removedItems.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
     */

    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        if (!containsId(itemId)) {
            return false;
        }
        for (RowItem item : addedItems) {
            if (item.getId().equals(itemId)) {
                addedItems.remove(item);
                fireContentsChange();
                return true;
            }
        }

        if (autoCommit) {
            /* Remove and commit instantly. */
            Item i = getItem(itemId);
            if (i == null) {
                return false;
            }
            try {
                queryDelegate.beginTransaction();
                boolean success = queryDelegate.removeRow((RowItem) i);
                queryDelegate.commit();
                refresh();
                if (notificationsEnabled) {
                    CacheFlushNotifier.notifyOfCacheFlush(this);
                }
                if (success) {
                    getLogger().log(Level.FINER, "Row removed from DB...");
                }
                return success;
            } catch (SQLException e) {
                getLogger().log(Level.WARNING,
                        "Failed to remove row, rolling back", e);
                try {
                    queryDelegate.rollback();
                } catch (SQLException ee) {
                    /* Nothing can be done here */
                    getLogger().log(Level.SEVERE,
                            "Failed to rollback row removal", ee);
                }
                return false;
            } catch (OptimisticLockException e) {
                getLogger().log(Level.WARNING,
                        "Failed to remove row, rolling back", e);
                try {
                    queryDelegate.rollback();
                } catch (SQLException ee) {
                    /* Nothing can be done here */
                    getLogger().log(Level.SEVERE,
                            "Failed to rollback row removal", ee);
                }
                throw e;
            }
        } else {
            removedItems.put((RowId) itemId, (RowItem) getItem(itemId));
            cachedItems.remove(itemId);
            refresh();
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeAllItems()
     */

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        if (autoCommit) {
            /* Remove and commit instantly. */
            try {
                queryDelegate.beginTransaction();
                boolean success = true;
                for (Object id : getItemIds()) {
                    if (!queryDelegate.removeRow((RowItem) getItem(id))) {
                        success = false;
                    }
                }
                if (success) {
                    queryDelegate.commit();
                    getLogger().log(Level.FINER, "All rows removed from DB...");
                    refresh();
                    if (notificationsEnabled) {
                        CacheFlushNotifier.notifyOfCacheFlush(this);
                    }
                } else {
                    queryDelegate.rollback();
                }
                return success;
            } catch (SQLException e) {
                getLogger().log(Level.WARNING,
                        "removeAllItems() failed, rolling back", e);
                try {
                    queryDelegate.rollback();
                } catch (SQLException ee) {
                    /* Nothing can be done here */
                    getLogger().log(Level.SEVERE, "Failed to roll back", ee);
                }
                return false;
            } catch (OptimisticLockException e) {
                getLogger().log(Level.WARNING,
                        "removeAllItems() failed, rolling back", e);
                try {
                    queryDelegate.rollback();
                } catch (SQLException ee) {
                    /* Nothing can be done here */
                    getLogger().log(Level.SEVERE, "Failed to roll back", ee);
                }
                throw e;
            }
        } else {
            for (Object id : getItemIds()) {
                removedItems.put((RowId) id, (RowItem) getItem(id));
                cachedItems.remove(id);
            }
            refresh();
            return true;
        }
    }

    /*************************************************/
    /** Methods from interface Container.Filterable **/
    /*************************************************/

    /**
     * {@inheritDoc}
     */

    @Override
    public void addContainerFilter(Filter filter)
            throws UnsupportedFilterException {
        // filter.setCaseSensitive(!ignoreCase);

        filters.add(filter);
        refresh();
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void removeContainerFilter(Filter filter) {
        filters.remove(filter);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void addContainerFilter(Object propertyId, String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {
        if (propertyId == null || !propertyIds.contains(propertyId)) {
            return;
        }

        /* Generate Filter -object */
        String likeStr = onlyMatchPrefix ? filterString + "%" : "%"
                + filterString + "%";
        Like like = new Like(propertyId.toString(), likeStr);
        like.setCaseSensitive(!ignoreCase);
        filters.add(like);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void removeContainerFilters(Object propertyId) {
        ArrayList<Filter> toRemove = new ArrayList<Filter>();
        for (Filter f : filters) {
            if (f.appliesToProperty(propertyId)) {
                toRemove.add(f);
            }
        }
        filters.removeAll(toRemove);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllContainerFilters() {
        filters.clear();
        refresh();
    }

    /**
     * Returns true if any filters have been applied to the container.
     * 
     * @return true if the container has filters applied, false otherwise
     * @since 7.1
     */
    public boolean hasContainerFilters() {
        return !getContainerFilters().isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Filterable#getContainerFilters()
     */
    @Override
    public Collection<Filter> getContainerFilters() {
        return Collections.unmodifiableCollection(filters);
    }

    /**********************************************/
    /** Methods from interface Container.Indexed **/
    /**********************************************/

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#indexOfId(java.lang.Object)
     */

    @Override
    public int indexOfId(Object itemId) {
        // First check if the id is in the added items
        for (int ix = 0; ix < addedItems.size(); ix++) {
            RowItem item = addedItems.get(ix);
            if (item.getId().equals(itemId)) {
                if (itemPassesFilters(item)) {
                    updateCount();
                    return size + ix;
                } else {
                    return -1;
                }
            }
        }

        if (!containsId(itemId)) {
            return -1;
        }
        if (cachedItems.isEmpty()) {
            getPage();
        }
        int size = size();
        // this protects against infinite looping
        int counter = 0;
        int oldIndex;
        while (counter < size) {
            if (itemIndexes.containsValue(itemId)) {
                for (Integer idx : itemIndexes.keySet()) {
                    if (itemIndexes.get(idx).equals(itemId)) {
                        return idx;
                    }
                }
            }
            oldIndex = currentOffset;
            // load in the next page.
            int nextIndex = currentOffset + pageLength * CACHE_RATIO
                    + cacheOverlap;
            if (nextIndex >= size) {
                // Container wrapped around, start from index 0.
                nextIndex = 0;
            }
            updateOffsetAndCache(nextIndex);

            // Update counter
            if (currentOffset > oldIndex) {
                counter += currentOffset - oldIndex;
            } else {
                counter += size - oldIndex;
            }
        }
        // safeguard in case item not found
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#getIdByIndex(int)
     */

    @Override
    public Object getIdByIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index is negative! index="
                    + index);
        }
        // make sure the size field is valid
        updateCount();
        if (index < size) {
            if (itemIndexes.keySet().contains(index)) {
                return itemIndexes.get(index);
            }
            updateOffsetAndCache(index);
            return itemIndexes.get(index);
        } else {
            // The index is in the added items
            int offset = index - size;
            // TODO this is very inefficient if looping - should improve
            // getItemIds(int, int)
            return getFilteredAddedItems().get(offset).getId();
        }
    }

    @Override
    public List<Object> getItemIds(int startIndex, int numberOfIds) {
        // TODO create a better implementation
        return (List<Object>) ContainerHelpers.getItemIdsUsingGetIdByIndex(
                startIndex, numberOfIds, this);
    }

    /**********************************************/
    /** Methods from interface Container.Ordered **/
    /**********************************************/

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
     */

    @Override
    public Object nextItemId(Object itemId) {
        int index = indexOfId(itemId) + 1;
        try {
            return getIdByIndex(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#prevItemId(java.lang.Object)
     */

    @Override
    public Object prevItemId(Object itemId) {
        int prevIndex = indexOfId(itemId) - 1;
        try {
            return getIdByIndex(prevIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#firstItemId()
     */

    @Override
    public Object firstItemId() {
        updateCount();
        if (size == 0) {
            if (addedItems.isEmpty()) {
                return null;
            } else {
                int ix = -1;
                do {
                    ix++;
                } while (!itemPassesFilters(addedItems.get(ix))
                        && ix < addedItems.size());
                if (ix < addedItems.size()) {
                    return addedItems.get(ix).getId();
                }
            }
        }
        if (!itemIndexes.containsKey(0)) {
            updateOffsetAndCache(0);
        }
        return itemIndexes.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#lastItemId()
     */

    @Override
    public Object lastItemId() {
        if (addedItems.isEmpty()) {
            int lastIx = size() - 1;
            if (!itemIndexes.containsKey(lastIx)) {
                updateOffsetAndCache(size - 1);
            }
            return itemIndexes.get(lastIx);
        } else {
            int ix = addedItems.size();
            do {
                ix--;
            } while (!itemPassesFilters(addedItems.get(ix)) && ix >= 0);
            if (ix >= 0) {
                return addedItems.get(ix).getId();
            } else {
                return null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#isFirstId(java.lang.Object)
     */

    @Override
    public boolean isFirstId(Object itemId) {
        return firstItemId().equals(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
     */

    @Override
    public boolean isLastId(Object itemId) {
        return lastItemId().equals(itemId);
    }

    /***********************************************/
    /** Methods from interface Container.Sortable **/
    /***********************************************/

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[],
     * boolean[])
     */

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {
        sorters.clear();
        if (propertyId == null || propertyId.length == 0) {
            refresh();
            return;
        }
        /* Generate OrderBy -objects */
        boolean asc = true;
        for (int i = 0; i < propertyId.length; i++) {
            /* Check that the property id is valid */
            if (propertyId[i] instanceof String
                    && propertyIds.contains(propertyId[i])) {
                try {
                    asc = ascending[i];
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "", e);
                }
                sorters.add(new OrderBy((String) propertyId[i], asc));
            }
        }
        refresh();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds()
     */

    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        return getContainerPropertyIds();
    }

    /**************************************/
    /** Methods specific to SQLContainer **/
    /**************************************/

    /**
     * Refreshes the container - clears all caches and resets size and offset.
     * Does NOT remove sorting or filtering rules!
     */
    public void refresh() {
        refresh(true);
    }

    /**
     * Refreshes the container. If <code>setSizeDirty</code> is
     * <code>false</code>, assumes that the current size is up to date. This is
     * used in {@link #updateCount()} to refresh the contents when we know the
     * size was just updated.
     * 
     * @param setSizeDirty
     */
    private void refresh(boolean setSizeDirty) {
        if (setSizeDirty) {
            sizeDirty = true;
        }
        currentOffset = 0;
        cachedItems.clear();
        itemIndexes.clear();
        fireContentsChange();
    }

    /**
     * Returns modify state of the container.
     * 
     * @return true if contents of this container have been modified
     */
    public boolean isModified() {
        return !removedItems.isEmpty() || !addedItems.isEmpty()
                || !modifiedItems.isEmpty();
    }

    /**
     * Set auto commit mode enabled or disabled. Auto commit mode means that all
     * changes made to items of this container will be immediately written to
     * the underlying data source.
     * 
     * @param autoCommitEnabled
     *            true to enable auto commit mode
     */
    public void setAutoCommit(boolean autoCommitEnabled) {
        autoCommit = autoCommitEnabled;
    }

    /**
     * Returns status of the auto commit mode.
     * 
     * @return true if auto commit mode is enabled
     */
    public boolean isAutoCommit() {
        return autoCommit;
    }

    /**
     * Returns the currently set page length.
     * 
     * @return current page length
     */
    public int getPageLength() {
        return pageLength;
    }

    /**
     * Sets the page length used in lazy fetching of items from the data source.
     * Also resets the cache size to match the new page length.
     * 
     * As a side effect the container will be refreshed.
     * 
     * @param pageLength
     *            new page length
     */
    public void setPageLength(int pageLength) {
        setPageLengthInternal(pageLength);
        refresh();
    }

    /**
     * Sets the page length internally, without refreshing the container.
     * 
     * @param pageLength
     *            the new page length
     */
    private void setPageLengthInternal(int pageLength) {
        this.pageLength = pageLength > 0 ? pageLength : DEFAULT_PAGE_LENGTH;
        cacheOverlap = getPageLength();
        cachedItems.setCacheLimit(CACHE_RATIO * getPageLength() + cacheOverlap);
    }

    /**
     * Adds the given OrderBy to this container and refreshes the container
     * contents with the new sorting rules.
     * 
     * Note that orderBy.getColumn() must return a column name that exists in
     * this container.
     * 
     * @param orderBy
     *            OrderBy to be added to the container sorting rules
     */
    public void addOrderBy(OrderBy orderBy) {
        if (orderBy == null) {
            return;
        }
        if (!propertyIds.contains(orderBy.getColumn())) {
            throw new IllegalArgumentException(
                    "The column given for sorting does not exist in this container.");
        }
        sorters.add(orderBy);
        refresh();
    }

    /**
     * Commits all the changes, additions and removals made to the items of this
     * container.
     * 
     * @throws UnsupportedOperationException
     * @throws SQLException
     */
    public void commit() throws UnsupportedOperationException, SQLException {
        try {
            getLogger().log(Level.FINER,
                    "Commiting changes through delegate...");
            queryDelegate.beginTransaction();
            /* Perform buffered deletions */
            for (RowItem item : removedItems.values()) {
                if (!queryDelegate.removeRow(item)) {
                    throw new SQLException("Removal failed for row with ID: "
                            + item.getId());
                }
            }
            /* Perform buffered modifications */
            for (RowItem item : modifiedItems) {
                if (!removedItems.containsKey(item.getId())) {
                    if (queryDelegate.storeRow(item) > 0) {
                        /*
                         * Also reset the modified state in the item in case it
                         * is reused e.g. in a form.
                         */
                        item.commit();
                    } else {
                        queryDelegate.rollback();
                        refresh();
                        throw new ConcurrentModificationException(
                                "Item with the ID '" + item.getId()
                                        + "' has been externally modified.");
                    }
                }
            }
            /* Perform buffered additions */
            for (RowItem item : addedItems) {
                queryDelegate.storeRow(item);
            }
            queryDelegate.commit();
            removedItems.clear();
            addedItems.clear();
            modifiedItems.clear();
            refresh();
            if (notificationsEnabled) {
                CacheFlushNotifier.notifyOfCacheFlush(this);
            }
        } catch (SQLException e) {
            queryDelegate.rollback();
            throw e;
        } catch (OptimisticLockException e) {
            queryDelegate.rollback();
            throw e;
        }
    }

    /**
     * Rolls back all the changes, additions and removals made to the items of
     * this container.
     * 
     * @throws UnsupportedOperationException
     * @throws SQLException
     */
    public void rollback() throws UnsupportedOperationException, SQLException {
        getLogger().log(Level.FINE, "Rolling back changes...");
        removedItems.clear();
        addedItems.clear();
        modifiedItems.clear();
        refresh();
    }

    /**
     * Notifies this container that a property in the given item has been
     * modified. The change will be buffered or made instantaneously depending
     * on auto commit mode.
     * 
     * @param changedItem
     *            item that has a modified property
     */
    void itemChangeNotification(RowItem changedItem) {
        if (autoCommit) {
            try {
                queryDelegate.beginTransaction();
                if (queryDelegate.storeRow(changedItem) == 0) {
                    queryDelegate.rollback();
                    refresh();
                    throw new ConcurrentModificationException(
                            "Item with the ID '" + changedItem.getId()
                                    + "' has been externally modified.");
                }
                queryDelegate.commit();
                if (notificationsEnabled) {
                    CacheFlushNotifier.notifyOfCacheFlush(this);
                }
                getLogger().log(Level.FINER, "Row updated to DB...");
            } catch (SQLException e) {
                getLogger().log(Level.WARNING,
                        "itemChangeNotification failed, rolling back...", e);
                try {
                    queryDelegate.rollback();
                } catch (SQLException ee) {
                    /* Nothing can be done here */
                    getLogger().log(Level.SEVERE, "Rollback failed", e);
                }
                throw new RuntimeException(e);
            }
        } else {
            if (!(changedItem.getId() instanceof TemporaryRowId)
                    && !modifiedItems.contains(changedItem)) {
                modifiedItems.add(changedItem);
            }
        }
    }

    /**
     * Determines a new offset for updating the row cache. The offset is
     * calculated from the given index, and will be fixed to match the start of
     * a page, based on the value of pageLength.
     * 
     * @param index
     *            Index of the item that was requested, but not found in cache
     */
    private void updateOffsetAndCache(int index) {

        int oldOffset = currentOffset;

        currentOffset = (index / pageLength) * pageLength - cacheOverlap;

        if (currentOffset < 0) {
            currentOffset = 0;
        }

        if (oldOffset == currentOffset && !cachedItems.isEmpty()) {
            return;
        }

        getPage();
    }

    /**
     * Fetches new count of rows from the data source, if needed.
     */
    private void updateCount() {
        if (!sizeDirty
                && new Date().getTime() < sizeUpdated.getTime()
                        + sizeValidMilliSeconds) {
            return;
        }
        try {
            try {
                queryDelegate.setFilters(filters);
            } catch (UnsupportedOperationException e) {
                getLogger().log(Level.FINE,
                        "The query delegate doesn't support filtering", e);
            }
            try {
                queryDelegate.setOrderBy(sorters);
            } catch (UnsupportedOperationException e) {
                getLogger().log(Level.FINE,
                        "The query delegate doesn't support sorting", e);
            }
            int newSize = queryDelegate.getCount();
            sizeUpdated = new Date();
            sizeDirty = false;
            if (newSize != size) {
                size = newSize;
                // Size is up to date so don't set it back to dirty in refresh()
                refresh(false);
            }
            getLogger().log(Level.FINER,
                    "Updated row count. New count is: {0}", size);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update item set size.", e);
        }
    }

    /**
     * Fetches property id's (column names and their types) from the data
     * source.
     * 
     * @throws SQLException
     */
    private void getPropertyIds() throws SQLException {
        propertyIds.clear();
        propertyTypes.clear();
        queryDelegate.setFilters(null);
        queryDelegate.setOrderBy(null);
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        try {
            queryDelegate.beginTransaction();
            rs = queryDelegate.getResults(0, 1);
            rsmd = rs.getMetaData();
            boolean resultExists = rs.next();
            Class<?> type = null;
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                if (!isColumnIdentifierValid(rsmd.getColumnLabel(i))) {
                    continue;
                }
                String colName = rsmd.getColumnLabel(i);
                /*
                 * Make sure not to add the same colName twice. This can easily
                 * happen if the SQL query joins many tables with an ID column.
                 */
                if (!propertyIds.contains(colName)) {
                    propertyIds.add(colName);
                }
                /* Try to determine the column's JDBC class by all means. */
                if (resultExists && rs.getObject(i) != null) {
                    type = rs.getObject(i).getClass();
                } else {
                    try {
                        type = Class.forName(rsmd.getColumnClassName(i));
                    } catch (Exception e) {
                        getLogger().log(Level.WARNING, "Class not found", e);
                        /* On failure revert to Object and hope for the best. */
                        type = Object.class;
                    }
                }
                /*
                 * Determine read only and nullability status of the column. A
                 * column is read only if it is reported as either read only or
                 * auto increment by the database, and also it is set as the
                 * version column in a TableQuery delegate.
                 */
                boolean readOnly = rsmd.isAutoIncrement(i)
                        || rsmd.isReadOnly(i);

                boolean persistable = !rsmd.isReadOnly(i);

                if (queryDelegate instanceof TableQuery) {
                    if (rsmd.getColumnLabel(i).equals(
                            ((TableQuery) queryDelegate).getVersionColumn())) {
                        readOnly = true;
                    }
                }

                propertyReadOnly.put(colName, readOnly);
                propertyPersistable.put(colName, persistable);
                propertyNullable.put(colName,
                        rsmd.isNullable(i) == ResultSetMetaData.columnNullable);
                propertyPrimaryKey.put(colName, queryDelegate
                        .getPrimaryKeyColumns()
                        .contains(rsmd.getColumnLabel(i)));
                propertyTypes.put(colName, type);
            }
            rs.getStatement().close();
            rs.close();
            queryDelegate.commit();
            getLogger().log(Level.FINER, "Property IDs fetched.");
        } catch (SQLException e) {
            getLogger().log(Level.WARNING,
                    "Failed to fetch property ids, rolling back", e);
            try {
                queryDelegate.rollback();
            } catch (SQLException e1) {
                getLogger().log(Level.SEVERE, "Failed to roll back", e1);
            }
            try {
                if (rs != null) {
                    if (rs.getStatement() != null) {
                        rs.getStatement().close();
                    }
                    rs.close();
                }
            } catch (SQLException e1) {
                getLogger().log(Level.WARNING, "Failed to close session", e1);
            }
            throw e;
        }
    }

    /**
     * Fetches a page from the data source based on the values of pageLenght and
     * currentOffset. Also updates the set of primary keys, used in
     * identification of RowItems.
     */
    private void getPage() {
        updateCount();
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        cachedItems.clear();
        itemIndexes.clear();
        try {
            try {
                queryDelegate.setOrderBy(sorters);
            } catch (UnsupportedOperationException e) {
                /* The query delegate doesn't support sorting. */
                /* No need to do anything. */
                getLogger().log(Level.FINE,
                        "The query delegate doesn't support sorting", e);
            }
            queryDelegate.beginTransaction();
            int fetchedRows = pageLength * CACHE_RATIO + cacheOverlap;
            rs = queryDelegate.getResults(currentOffset, fetchedRows);
            rsmd = rs.getMetaData();
            List<String> pKeys = queryDelegate.getPrimaryKeyColumns();
            // }
            /* Create new items and column properties */
            ColumnProperty cp = null;
            int rowCount = currentOffset;
            if (!queryDelegate.implementationRespectsPagingLimits()) {
                rowCount = currentOffset = 0;
                setPageLengthInternal(size);
            }
            while (rs.next()) {
                List<ColumnProperty> itemProperties = new ArrayList<ColumnProperty>();
                /* Generate row itemId based on primary key(s) */
                Object[] itemId = new Object[pKeys.size()];
                for (int i = 0; i < pKeys.size(); i++) {
                    itemId[i] = rs.getObject(pKeys.get(i));
                }
                RowId id = null;
                if (pKeys.isEmpty()) {
                    id = new ReadOnlyRowId(rs.getRow());
                } else {
                    id = new RowId(itemId);
                }
                List<String> propertiesToAdd = new ArrayList<String>(
                        propertyIds);
                if (!removedItems.containsKey(id)) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        if (!isColumnIdentifierValid(rsmd.getColumnLabel(i))) {
                            continue;
                        }
                        String colName = rsmd.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        Class<?> type = value != null ? value.getClass()
                                : Object.class;
                        if (value == null) {
                            for (String propName : propertyTypes.keySet()) {
                                if (propName.equals(rsmd.getColumnLabel(i))) {
                                    type = propertyTypes.get(propName);
                                    break;
                                }
                            }
                        }
                        /*
                         * In case there are more than one column with the same
                         * name, add only the first one. This can easily happen
                         * if you join many tables where each table has an ID
                         * column.
                         */
                        if (propertiesToAdd.contains(colName)) {

                            cp = new ColumnProperty(colName,
                                    propertyReadOnly.get(colName),
                                    propertyPersistable.get(colName),
                                    propertyNullable.get(colName),
                                    propertyPrimaryKey.get(colName), value,
                                    type);
                            itemProperties.add(cp);
                            propertiesToAdd.remove(colName);
                        }
                    }
                    /* Cache item */
                    itemIndexes.put(rowCount, id);

                    // if an item with the id is contained in the modified
                    // cache, then use this record and add it to the cached
                    // items. Otherwise create a new item
                    int modifiedIndex = indexInModifiedCache(id);
                    if (modifiedIndex != -1) {
                        cachedItems.put(id, modifiedItems.get(modifiedIndex));
                    } else {
                        cachedItems.put(id, new RowItem(this, id,
                                itemProperties));
                    }

                    rowCount++;
                }
            }
            rs.getStatement().close();
            rs.close();
            queryDelegate.commit();
            getLogger().log(Level.FINER, "Fetched {0} rows starting from {1}",
                    new Object[] { fetchedRows, currentOffset });
        } catch (SQLException e) {
            getLogger().log(Level.WARNING,
                    "Failed to fetch rows, rolling back", e);
            try {
                queryDelegate.rollback();
            } catch (SQLException e1) {
                getLogger().log(Level.SEVERE, "Failed to roll back", e1);
            }
            try {
                if (rs != null) {
                    if (rs.getStatement() != null) {
                        rs.getStatement().close();
                        rs.close();
                    }
                }
            } catch (SQLException e1) {
                getLogger().log(Level.WARNING, "Failed to close session", e1);
            }
            throw new RuntimeException("Failed to fetch page.", e);
        }
    }

    /**
     * Returns the index of the item with the given itemId for the modified
     * cache.
     * 
     * @param itemId
     * @return the index of the item with the itemId in the modified cache. Or
     *         -1 if not found.
     */
    private int indexInModifiedCache(Object itemId) {
        for (int ix = 0; ix < modifiedItems.size(); ix++) {
            RowItem item = modifiedItems.get(ix);
            if (item.getId().equals(itemId)) {
                return ix;
            }
        }
        return -1;
    }

    private int sizeOfAddedItems() {
        return getFilteredAddedItems().size();
    }

    private List<RowItem> getFilteredAddedItems() {
        ArrayList<RowItem> filtered = new ArrayList<RowItem>(addedItems);
        if (filters != null && !filters.isEmpty()) {
            for (RowItem item : addedItems) {
                if (!itemPassesFilters(item)) {
                    filtered.remove(item);
                }
            }
        }
        return filtered;
    }

    private boolean itemPassesFilters(RowItem item) {
        for (Filter filter : filters) {
            if (!filter.passesFilter(item.getId(), item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks is the given column identifier valid to be used with SQLContainer.
     * Currently the only non-valid identifier is "rownum" when MSSQL or Oracle
     * is used. This is due to the way the SELECT queries are constructed in
     * order to implement paging in these databases.
     * 
     * @param identifier
     *            Column identifier
     * @return true if the identifier is valid
     */
    private boolean isColumnIdentifierValid(String identifier) {
        if (identifier.equalsIgnoreCase("rownum")
                && queryDelegate instanceof TableQuery) {
            TableQuery tq = (TableQuery) queryDelegate;
            if (tq.getSqlGenerator() instanceof MSSQLGenerator
                    || tq.getSqlGenerator() instanceof OracleGenerator) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the QueryDelegate set for this SQLContainer.
     * 
     * @return current querydelegate
     */
    protected QueryDelegate getQueryDelegate() {
        return queryDelegate;
    }

    /************************************/
    /** UNSUPPORTED CONTAINER FEATURES **/
    /************************************/

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addContainerProperty(java.lang.Object,
     * java.lang.Class, java.lang.Object)
     */

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object)
     */

    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object,
     * java.lang.Object)
     */

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#addItemAt(int, java.lang.Object)
     */

    @Override
    public Item addItemAt(int index, Object newItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#addItemAt(int)
     */

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object)
     */

    @Override
    public Object addItemAfter(Object previousItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /******************************************/
    /** ITEMSETCHANGENOTIFIER IMPLEMENTATION **/
    /******************************************/

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.ItemSetChangeNotifier#addListener(com.vaadin
     * .data.Container.ItemSetChangeListener)
     */

    @Override
    public void addItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        if (itemSetChangeListeners == null) {
            itemSetChangeListeners = new LinkedList<Container.ItemSetChangeListener>();
        }
        itemSetChangeListeners.add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(Container.ItemSetChangeListener listener) {
        addItemSetChangeListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.ItemSetChangeNotifier#removeListener(com.vaadin
     * .data.Container.ItemSetChangeListener)
     */

    @Override
    public void removeItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        if (itemSetChangeListeners != null) {
            itemSetChangeListeners.remove(listener);
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(Container.ItemSetChangeListener listener) {
        removeItemSetChangeListener(listener);
    }

    protected void fireContentsChange() {
        if (itemSetChangeListeners != null) {
            final Object[] l = itemSetChangeListeners.toArray();
            final Container.ItemSetChangeEvent event = new SQLContainer.ItemSetChangeEvent(
                    this);
            for (int i = 0; i < l.length; i++) {
                ((Container.ItemSetChangeListener) l[i])
                        .containerItemSetChange(event);
            }
        }
    }

    /**
     * Simple ItemSetChangeEvent implementation.
     */
    @SuppressWarnings("serial")
    public static class ItemSetChangeEvent extends EventObject implements
            Container.ItemSetChangeEvent {

        private ItemSetChangeEvent(SQLContainer source) {
            super(source);
        }

        @Override
        public Container getContainer() {
            return (Container) getSource();
        }
    }

    /**************************************************/
    /** ROWIDCHANGELISTENER PASSING TO QUERYDELEGATE **/
    /**************************************************/

    /**
     * Adds a RowIdChangeListener to the QueryDelegate
     * 
     * @param listener
     */
    public void addRowIdChangeListener(RowIdChangeListener listener) {
        if (queryDelegate instanceof QueryDelegate.RowIdChangeNotifier) {
            ((QueryDelegate.RowIdChangeNotifier) queryDelegate)
                    .addListener(listener);
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addRowIdChangeListener(RowIdChangeListener)}
     **/
    @Deprecated
    public void addListener(RowIdChangeListener listener) {
        addRowIdChangeListener(listener);
    }

    /**
     * Removes a RowIdChangeListener from the QueryDelegate
     * 
     * @param listener
     */
    public void removeRowIdChangeListener(RowIdChangeListener listener) {
        if (queryDelegate instanceof QueryDelegate.RowIdChangeNotifier) {
            ((QueryDelegate.RowIdChangeNotifier) queryDelegate)
                    .removeListener(listener);
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeRowIdChangeListener(RowIdChangeListener)}
     **/
    @Deprecated
    public void removeListener(RowIdChangeListener listener) {
        removeRowIdChangeListener(listener);
    }

    /**
     * Calling this will enable this SQLContainer to send and receive cache
     * flush notifications for its lifetime.
     */
    public void enableCacheFlushNotifications() {
        if (!notificationsEnabled) {
            notificationsEnabled = true;
            CacheFlushNotifier.addInstance(this);
        }
    }

    /******************************************/
    /** Referencing mechanism implementation **/
    /******************************************/

    /**
     * Adds a new reference to the given SQLContainer. In addition to the
     * container you must provide the column (property) names used for the
     * reference in both this and the referenced SQLContainer.
     * 
     * Note that multiple references pointing to the same SQLContainer are not
     * supported.
     * 
     * @param refdCont
     *            Target SQLContainer of the new reference
     * @param refingCol
     *            Column (property) name in this container storing the (foreign
     *            key) reference
     * @param refdCol
     *            Column (property) name in the referenced container storing the
     *            referenced key
     */
    public void addReference(SQLContainer refdCont, String refingCol,
            String refdCol) {
        if (refdCont == null) {
            throw new IllegalArgumentException(
                    "Referenced SQLContainer can not be null.");
        }
        if (!getContainerPropertyIds().contains(refingCol)) {
            throw new IllegalArgumentException(
                    "Given referencing column name is invalid."
                            + " Please ensure that this container"
                            + " contains a property ID named: " + refingCol);
        }
        if (!refdCont.getContainerPropertyIds().contains(refdCol)) {
            throw new IllegalArgumentException(
                    "Given referenced column name is invalid."
                            + " Please ensure that the referenced container"
                            + " contains a property ID named: " + refdCol);
        }
        if (references.keySet().contains(refdCont)) {
            throw new IllegalArgumentException(
                    "An SQLContainer instance can only be referenced once.");
        }
        references.put(refdCont, new Reference(refdCont, refingCol, refdCol));
    }

    /**
     * Removes the reference pointing to the given SQLContainer.
     * 
     * @param refdCont
     *            Target SQLContainer of the reference
     * @return true if successful, false if the reference did not exist
     */
    public boolean removeReference(SQLContainer refdCont) {
        if (refdCont == null) {
            throw new IllegalArgumentException(
                    "Referenced SQLContainer can not be null.");
        }
        return references.remove(refdCont) == null ? false : true;
    }

    /**
     * Sets the referenced item. The referencing column of the item in this
     * container is updated accordingly.
     * 
     * @param itemId
     *            Item Id of the reference source (from this container)
     * @param refdItemId
     *            Item Id of the reference target (from referenced container)
     * @param refdCont
     *            Target SQLContainer of the reference
     * @return true if the referenced item was successfully set, false on
     *         failure
     */
    public boolean setReferencedItem(Object itemId, Object refdItemId,
            SQLContainer refdCont) {
        if (refdCont == null) {
            throw new IllegalArgumentException(
                    "Referenced SQLContainer can not be null.");
        }
        Reference r = references.get(refdCont);
        if (r == null) {
            throw new IllegalArgumentException(
                    "Reference to the given SQLContainer not defined.");
        }
        try {
            getContainerProperty(itemId, r.getReferencingColumn()).setValue(
                    refdCont.getContainerProperty(refdItemId,
                            r.getReferencedColumn()));
            return true;
        } catch (Exception e) {
            getLogger()
                    .log(Level.WARNING, "Setting referenced item failed.", e);
            return false;
        }
    }

    /**
     * Fetches the Item Id of the referenced item from the target SQLContainer.
     * 
     * @param itemId
     *            Item Id of the reference source (from this container)
     * @param refdCont
     *            Target SQLContainer of the reference
     * @return Item Id of the referenced item, or null if not found
     */
    public Object getReferencedItemId(Object itemId, SQLContainer refdCont) {
        if (refdCont == null) {
            throw new IllegalArgumentException(
                    "Referenced SQLContainer can not be null.");
        }
        Reference r = references.get(refdCont);
        if (r == null) {
            throw new IllegalArgumentException(
                    "Reference to the given SQLContainer not defined.");
        }
        Object refKey = getContainerProperty(itemId, r.getReferencingColumn())
                .getValue();

        refdCont.removeAllContainerFilters();
        refdCont.addContainerFilter(new Equal(r.getReferencedColumn(), refKey));
        Object toReturn = refdCont.firstItemId();
        refdCont.removeAllContainerFilters();
        return toReturn;
    }

    /**
     * Fetches the referenced item from the target SQLContainer.
     * 
     * @param itemId
     *            Item Id of the reference source (from this container)
     * @param refdCont
     *            Target SQLContainer of the reference
     * @return The referenced item, or null if not found
     */
    public Item getReferencedItem(Object itemId, SQLContainer refdCont) {
        return refdCont.getItem(getReferencedItemId(itemId, refdCont));
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        if (notificationsEnabled) {
            /*
             * Register instance with CacheFlushNotifier after de-serialization
             * if notifications are enabled
             */
            CacheFlushNotifier.addInstance(this);
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(SQLContainer.class.getName());
    }

}
