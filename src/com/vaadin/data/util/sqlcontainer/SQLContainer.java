package com.vaadin.data.util.sqlcontainer;

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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.data.util.sqlcontainer.query.generator.MSSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.OracleGenerator;

public class SQLContainer implements Container, Container.Filterable,
        Container.Indexed, Container.Sortable, Container.ItemSetChangeNotifier {
    private static final long serialVersionUID = -3863564310693712511L;

    /** Query delegate */
    private QueryDelegate delegate;
    /** Auto commit mode, default = false */
    private boolean autoCommit = false;

    /** Page length = number of items contained in one page */
    private int pageLength = DEFAULT_PAGE_LENGTH;
    public static final int DEFAULT_PAGE_LENGTH = 100;

    /** Number of items to cache = CACHE_RATIO x pageLength */
    public static final int CACHE_RATIO = 2;

    /** Item and index caches */
    private final Map<Integer, RowId> itemIndexes = new HashMap<Integer, RowId>();
    private final CacheMap<RowId, RowItem> cachedItems = new CacheMap<RowId, RowItem>();

    /** Container properties = column names, data types and statuses */
    private final List<String> propertyIds = new ArrayList<String>();
    private final Map<String, Class<?>> propertyTypes = new HashMap<String, Class<?>>();
    private final Map<String, Boolean> propertyReadOnly = new HashMap<String, Boolean>();
    private final Map<String, Boolean> propertyNullable = new HashMap<String, Boolean>();

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

    /** Enable to output possible stack traces and diagnostic information */
    private boolean debugMode;

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
        this.delegate = delegate;
        getPropertyIds();
        cachedItems.setCacheLimit(CACHE_RATIO * getPageLength());
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
    public Object addItem() throws UnsupportedOperationException {
        Object emptyKey[] = new Object[delegate.getPrimaryKeyColumns().size()];
        RowId itemId = new TemporaryRowId(emptyKey);
        // Create new empty column properties for the row item.
        List<ColumnProperty> itemProperties = new ArrayList<ColumnProperty>();
        for (String propertyId : propertyIds) {
            /* Default settings for new item properties. */
            itemProperties
                    .add(new ColumnProperty(propertyId, propertyReadOnly
                            .get(propertyId),
                            !propertyReadOnly.get(propertyId), propertyNullable
                                    .get(propertyId), null, getType(propertyId)));
        }
        RowItem newRowItem = new RowItem(this, itemId, itemProperties);

        if (autoCommit) {
            /* Add and commit instantly */
            try {
                if (delegate instanceof TableQuery) {
                    itemId = ((TableQuery) delegate)
                            .storeRowImmediately(newRowItem);
                } else {
                    delegate.beginTransaction();
                    delegate.storeRow(newRowItem);
                    delegate.commit();
                }
                refresh();
                if (notificationsEnabled) {
                    CacheFlushNotifier.notifyOfCacheFlush(this);
                }
                debug(null, "Row added to DB...");
                return itemId;
            } catch (SQLException e) {
                debug(e, null);
                try {
                    delegate.rollback();
                } catch (SQLException ee) {
                    debug(ee, null);
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

        if (!(itemId instanceof TemporaryRowId)) {
            try {
                return delegate.containsRowWithKey(((RowId) itemId).getId());
            } catch (Exception e) {
                /* Query failed, just return false. */
                debug(e, null);
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
    public Collection<?> getContainerPropertyIds() {
        return Collections.unmodifiableCollection(propertyIds);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
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
    public Collection<?> getItemIds() {
        updateCount();
        ArrayList<RowId> ids = new ArrayList<RowId>();
        ResultSet rs = null;
        try {
            // Load ALL rows :(
            delegate.beginTransaction();
            rs = delegate.getResults(0, 0);
            List<String> pKeys = delegate.getPrimaryKeyColumns();
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
            delegate.commit();
        } catch (SQLException e) {
            debug(e, null);
            try {
                delegate.rollback();
            } catch (SQLException e1) {
                debug(e1, null);
            }
            try {
                rs.getStatement().close();
                rs.close();
            } catch (SQLException e1) {
                debug(e1, null);
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
    public int size() {
        updateCount();
        return size + sizeOfAddedItems() - removedItems.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
     */
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
                delegate.beginTransaction();
                boolean success = delegate.removeRow((RowItem) i);
                delegate.commit();
                refresh();
                if (notificationsEnabled) {
                    CacheFlushNotifier.notifyOfCacheFlush(this);
                }
                if (success) {
                    debug(null, "Row removed from DB...");
                }
                return success;
            } catch (SQLException e) {
                debug(e, null);
                try {
                    delegate.rollback();
                } catch (SQLException ee) {
                    /* Nothing can be done here */
                    debug(ee, null);
                }
                return false;
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
    public boolean removeAllItems() throws UnsupportedOperationException {
        if (autoCommit) {
            /* Remove and commit instantly. */
            try {
                delegate.beginTransaction();
                boolean success = true;
                for (Object id : getItemIds()) {
                    if (!delegate.removeRow((RowItem) getItem(id))) {
                        success = false;
                    }
                }
                if (success) {
                    delegate.commit();
                    debug(null, "All rows removed from DB...");
                    refresh();
                    if (notificationsEnabled) {
                        CacheFlushNotifier.notifyOfCacheFlush(this);
                    }
                } else {
                    delegate.rollback();
                }
                return success;
            } catch (SQLException e) {
                debug(e, null);
                try {
                    delegate.rollback();
                } catch (SQLException ee) {
                    /* Nothing can be done here */
                    debug(ee, null);
                }
                return false;
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
    public void addContainerFilter(Filter filter)
            throws UnsupportedFilterException {
        // filter.setCaseSensitive(!ignoreCase);

        filters.add(filter);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void removeContainerFilter(Filter filter) {
        filters.remove(filter);
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
    public void removeAllContainerFilters() {
        filters.clear();
        refresh();
    }

    /**********************************************/
    /** Methods from interface Container.Indexed **/
    /**********************************************/

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#indexOfId(java.lang.Object)
     */
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
        boolean wrappedAround = false;
        while (!wrappedAround) {
            for (Integer i : itemIndexes.keySet()) {
                if (itemIndexes.get(i).equals(itemId)) {
                    return i;
                }
            }
            // load in the next page.
            int nextIndex = (currentOffset / (pageLength * CACHE_RATIO) + 1)
                    * (pageLength * CACHE_RATIO);
            if (nextIndex >= size) {
                // Container wrapped around, start from index 0.
                wrappedAround = true;
                nextIndex = 0;
            }
            updateOffsetAndCache(nextIndex);
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#getIdByIndex(int)
     */
    public Object getIdByIndex(int index) {
        if (index < 0 || index > size() - 1) {
            return null;
        }
        if (index < size) {
            if (itemIndexes.keySet().contains(index)) {
                return itemIndexes.get(index);
            }
            updateOffsetAndCache(index);
            return itemIndexes.get(index);
        } else {
            // The index is in the added items
            int offset = index - size;
            return addedItems.get(offset).getId();
        }
    }

    /**********************************************/
    /** Methods from interface Container.Ordered **/
    /**********************************************/

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
     */
    public Object nextItemId(Object itemId) {
        return getIdByIndex(indexOfId(itemId) + 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#prevItemId(java.lang.Object)
     */
    public Object prevItemId(Object itemId) {
        return getIdByIndex(indexOfId(itemId) - 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#firstItemId()
     */
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
    public boolean isFirstId(Object itemId) {
        return firstItemId().equals(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
     */
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
                    debug(e, null);
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
        sizeDirty = true;
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
        cachedItems.setCacheLimit(CACHE_RATIO * getPageLength());
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
            debug(null, "Commiting changes through delegate...");
            delegate.beginTransaction();
            /* Perform buffered deletions */
            for (RowItem item : removedItems.values()) {
                if (!delegate.removeRow(item)) {
                    throw new SQLException("Removal failed for row with ID: "
                            + item.getId());
                }
            }
            /* Perform buffered modifications */
            for (RowItem item : modifiedItems) {
                if (delegate.storeRow(item) > 0) {
                    /*
                     * Also reset the modified state in the item in case it is
                     * reused e.g. in a form.
                     */
                    item.commit();
                } else {
                    delegate.rollback();
                    refresh();
                    throw new ConcurrentModificationException(
                            "Item with the ID '" + item.getId()
                                    + "' has been externally modified.");
                }
            }
            /* Perform buffered additions */
            for (RowItem item : addedItems) {
                delegate.storeRow(item);
            }
            delegate.commit();
            removedItems.clear();
            addedItems.clear();
            modifiedItems.clear();
            refresh();
            if (notificationsEnabled) {
                CacheFlushNotifier.notifyOfCacheFlush(this);
            }
        } catch (SQLException e) {
            delegate.rollback();
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
        debug(null, "Rolling back changes...");
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
                delegate.beginTransaction();
                if (delegate.storeRow(changedItem) == 0) {
                    delegate.rollback();
                    refresh();
                    throw new ConcurrentModificationException(
                            "Item with the ID '" + changedItem.getId()
                                    + "' has been externally modified.");
                }
                delegate.commit();
                if (notificationsEnabled) {
                    CacheFlushNotifier.notifyOfCacheFlush(this);
                }
                debug(null, "Row updated to DB...");
            } catch (SQLException e) {
                debug(e, null);
                try {
                    delegate.rollback();
                } catch (SQLException ee) {
                    /* Nothing can be done here */
                    debug(e, null);
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
        if (itemIndexes.containsKey(index)) {
            return;
        }
        currentOffset = (index / (pageLength * CACHE_RATIO))
                * (pageLength * CACHE_RATIO);
        if (currentOffset < 0) {
            currentOffset = 0;
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
                delegate.setFilters(filters);
            } catch (UnsupportedOperationException e) {
                /* The query delegate doesn't support filtering. */
                debug(e, null);
            }
            try {
                delegate.setOrderBy(sorters);
            } catch (UnsupportedOperationException e) {
                /* The query delegate doesn't support filtering. */
                debug(e, null);
            }
            int newSize = delegate.getCount();
            if (newSize != size) {
                size = newSize;
                refresh();
            }
            sizeUpdated = new Date();
            sizeDirty = false;
            debug(null, "Updated row count. New count is: " + size);
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
        delegate.setFilters(null);
        delegate.setOrderBy(null);
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        try {
            delegate.beginTransaction();
            rs = delegate.getResults(0, 1);
            boolean resultExists = rs.next();
            rsmd = rs.getMetaData();
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
                        debug(e, null);
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
                if (delegate instanceof TableQuery
                        && rsmd.getColumnLabel(i).equals(
                                ((TableQuery) delegate).getVersionColumn())) {
                    readOnly = true;
                }
                propertyReadOnly.put(colName, readOnly);
                propertyNullable.put(colName,
                        rsmd.isNullable(i) == ResultSetMetaData.columnNullable);
                propertyTypes.put(colName, type);
            }
            rs.getStatement().close();
            rs.close();
            delegate.commit();
            debug(null, "Property IDs fetched.");
        } catch (SQLException e) {
            debug(e, null);
            try {
                delegate.rollback();
            } catch (SQLException e1) {
                debug(e1, null);
            }
            try {
                if (rs != null) {
                    if (rs.getStatement() != null) {
                        rs.getStatement().close();
                    }
                    rs.close();
                }
            } catch (SQLException e1) {
                debug(e1, null);
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
                delegate.setOrderBy(sorters);
            } catch (UnsupportedOperationException e) {
                /* The query delegate doesn't support sorting. */
                /* No need to do anything. */
                debug(e, null);
            }
            delegate.beginTransaction();
            rs = delegate.getResults(currentOffset, pageLength * CACHE_RATIO);
            rsmd = rs.getMetaData();
            List<String> pKeys = delegate.getPrimaryKeyColumns();
            // }
            /* Create new items and column properties */
            ColumnProperty cp = null;
            int rowCount = currentOffset;
            if (!delegate.implementationRespectsPagingLimits()) {
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
                                    !propertyReadOnly.get(colName),
                                    propertyNullable.get(colName), value, type);
                            itemProperties.add(cp);
                            propertiesToAdd.remove(colName);
                        }
                    }
                    /* Cache item */
                    itemIndexes.put(rowCount, id);
                    cachedItems.put(id, new RowItem(this, id, itemProperties));
                    rowCount++;
                }
            }
            rs.getStatement().close();
            rs.close();
            delegate.commit();
            debug(null, "Fetched " + pageLength * CACHE_RATIO
                    + " rows starting from " + currentOffset);
        } catch (SQLException e) {
            debug(e, null);
            try {
                delegate.rollback();
            } catch (SQLException e1) {
                debug(e1, null);
            }
            try {
                if (rs != null) {
                    if (rs.getStatement() != null) {
                        rs.getStatement().close();
                        rs.close();
                    }
                }
            } catch (SQLException e1) {
                debug(e1, null);
            }
            throw new RuntimeException("Failed to fetch page.", e);
        }
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
                && delegate instanceof TableQuery) {
            TableQuery tq = (TableQuery) delegate;
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
        return delegate;
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
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object)
     */
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object,
     * java.lang.Object)
     */
    public Item addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#addItemAt(int, java.lang.Object)
     */
    public Item addItemAt(int index, Object newItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#addItemAt(int)
     */
    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object)
     */
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
    public void addListener(Container.ItemSetChangeListener listener) {
        if (itemSetChangeListeners == null) {
            itemSetChangeListeners = new LinkedList<Container.ItemSetChangeListener>();
        }
        itemSetChangeListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.ItemSetChangeNotifier#removeListener(com.vaadin
     * .data.Container.ItemSetChangeListener)
     */
    public void removeListener(Container.ItemSetChangeListener listener) {
        if (itemSetChangeListeners != null) {
            itemSetChangeListeners.remove(listener);
        }
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
    public class ItemSetChangeEvent extends EventObject implements
            Container.ItemSetChangeEvent {

        private ItemSetChangeEvent(SQLContainer source) {
            super(source);
        }

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
    public void addListener(RowIdChangeListener listener) {
        if (delegate instanceof QueryDelegate.RowIdChangeNotifier) {
            ((QueryDelegate.RowIdChangeNotifier) delegate)
                    .addListener(listener);
        }
    }

    /**
     * Removes a RowIdChangeListener from the QueryDelegate
     * 
     * @param listener
     */
    public void removeListener(RowIdChangeListener listener) {
        if (delegate instanceof QueryDelegate.RowIdChangeNotifier) {
            ((QueryDelegate.RowIdChangeNotifier) delegate)
                    .removeListener(listener);
        }
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Output a debug message or a stack trace of an exception
     * 
     * @param message
     */
    private void debug(Exception e, String message) {
        if (debugMode) {
            // TODO: Replace with the common Vaadin logging system once it is
            // available.
            if (message != null) {
                System.err.println(message);
            }
            if (e != null) {
                e.printStackTrace();
            }
        }
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
            debug(e, "Setting referenced item failed.");
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

}