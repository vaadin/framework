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
package com.vaadin.client.widget.grid.datasources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.widget.grid.events.SelectAllEvent;
import com.vaadin.client.widget.grid.events.SelectAllHandler;
import com.vaadin.shared.util.SharedUtil;

/**
 * A simple list based on an in-memory data source for simply adding a list of
 * row pojos to the grid. Based on a wrapped list instance which supports adding
 * and removing of items.
 * 
 * <p>
 * Usage:
 * 
 * <pre>
 * ListDataSource&lt;Integer&gt; ds = new ListDataSource&lt;Integer&gt;(1, 2, 3, 4);
 * 
 * // Add item to the data source
 * ds.asList().add(5);
 * 
 * // Remove item from the data source
 * ds.asList().remove(3);
 * 
 * // Add multiple items
 * ds.asList().addAll(Arrays.asList(5, 6, 7));
 * </pre>
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ListDataSource<T> implements DataSource<T> {

    private class RowHandleImpl extends RowHandle<T> {

        private final T row;

        public RowHandleImpl(T row) {
            this.row = row;
        }

        @Override
        public T getRow() {
            /*
             * We'll cheat here and don't throw an IllegalStateException even if
             * this isn't pinned, because we know that the reference never gets
             * stale.
             */
            return row;
        }

        @Override
        public void pin() {
            // NOOP, really
        }

        @Override
        public void unpin() throws IllegalStateException {
            /*
             * Just to make things easier for everyone, we won't throw the
             * exception, even in illegal situations.
             */
        }

        @Override
        protected boolean equalsExplicit(Object obj) {
            if (obj instanceof ListDataSource.RowHandleImpl) {
                /*
                 * Java prefers AbstractRemoteDataSource<?>.RowHandleImpl. I
                 * like the @SuppressWarnings more (keeps the line length in
                 * check.)
                 */
                @SuppressWarnings("unchecked")
                RowHandleImpl rhi = (RowHandleImpl) obj;
                return SharedUtil.equals(row, rhi.row);
            } else {
                return false;
            }
        }

        @Override
        protected int hashCodeExplicit() {
            return row.hashCode();
        }

        @Override
        public void updateRow() {
            if (changeHandler != null) {
                changeHandler.dataUpdated(ds.indexOf(getRow()), 1);
            }
        }
    }

    /**
     * Wraps the datasource list and notifies the change handler of changing to
     * the list
     */
    private class ListWrapper implements List<T> {

        @Override
        public int size() {
            return ds.size();
        }

        @Override
        public boolean isEmpty() {
            return ds.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return new ListWrapperIterator(ds.iterator());
        }

        @Override
        public Object[] toArray() {
            return ds.toArray();
        }

        @Override
        @SuppressWarnings("hiding")
        public <T> T[] toArray(T[] a) {
            return toArray(a);
        }

        @Override
        public boolean add(T e) {
            if (ds.add(e)) {
                if (changeHandler != null) {
                    changeHandler.dataAdded(ds.size() - 1, 1);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            int index = ds.indexOf(o);
            if (ds.remove(o)) {
                if (changeHandler != null) {
                    changeHandler.dataRemoved(index, 1);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return ds.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            int idx = ds.size();
            if (ds.addAll(c)) {
                if (changeHandler != null) {
                    changeHandler.dataAdded(idx, c.size());
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            if (ds.addAll(index, c)) {
                if (changeHandler != null) {
                    changeHandler.dataAdded(index, c.size());
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if (ds.removeAll(c)) {
                if (changeHandler != null) {
                    // Have to update the whole list as the removal does not
                    // have to be a continuous range
                    changeHandler.dataUpdated(0, ds.size());
                    changeHandler.dataAvailable(0, ds.size());
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            if (ds.retainAll(c)) {
                if (changeHandler != null) {
                    // Have to update the whole list as the retain does not
                    // have to be a continuous range
                    changeHandler.dataUpdated(0, ds.size());
                    changeHandler.dataAvailable(0, ds.size());
                }
                return true;
            }
            return false;
        }

        @Override
        public void clear() {
            int size = ds.size();
            ds.clear();
            if (changeHandler != null) {
                changeHandler.dataRemoved(0, size);
            }
        }

        @Override
        public T get(int index) {
            return ds.get(index);
        }

        @Override
        public T set(int index, T element) {
            T prev = ds.set(index, element);
            if (changeHandler != null) {
                changeHandler.dataUpdated(index, 1);
            }
            return prev;
        }

        @Override
        public void add(int index, T element) {
            ds.add(index, element);
            if (changeHandler != null) {
                changeHandler.dataAdded(index, 1);
            }
        }

        @Override
        public T remove(int index) {
            T removed = ds.remove(index);
            if (changeHandler != null) {
                changeHandler.dataRemoved(index, 1);
            }
            return removed;
        }

        @Override
        public int indexOf(Object o) {
            return ds.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return ds.lastIndexOf(o);
        }

        @Override
        public ListIterator<T> listIterator() {
            // TODO could be implemented by a custom iterator.
            throw new UnsupportedOperationException(
                    "List iterators not supported at this time.");
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            // TODO could be implemented by a custom iterator.
            throw new UnsupportedOperationException(
                    "List iterators not supported at this time.");
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException("Sub lists not supported.");
        }
    }

    /**
     * Iterator returned by {@link ListWrapper}
     */
    private class ListWrapperIterator implements Iterator<T> {

        private final Iterator<T> iterator;

        /**
         * Constructs a new iterator
         */
        public ListWrapperIterator(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "Iterator.remove() is not supported by this iterator.");
        }
    }

    /**
     * Datasource for providing row pojo's
     */
    private final List<T> ds;

    /**
     * Wrapper that wraps the data source
     */
    private final ListWrapper wrapper;

    /**
     * Handler for listening to changes in the underlying list.
     */
    private DataChangeHandler changeHandler;

    /**
     * Constructs a new list data source.
     * <p>
     * Note: Modifications to the original list will not be reflected in the
     * data source after the data source has been constructed. To add or remove
     * items to the data source after it has been constructed use
     * {@link ListDataSource#asList()}.
     * 
     * 
     * @param datasource
     *            The list to use for providing the data to the grid
     */
    public ListDataSource(List<T> datasource) {
        if (datasource == null) {
            throw new IllegalArgumentException("datasource cannot be null");
        }
        ds = new ArrayList<T>(datasource);
        wrapper = new ListWrapper();
    }

    /**
     * Constructs a data source with a set of rows. You can dynamically add and
     * remove rows from the data source via the list you get from
     * {@link ListDataSource#asList()}
     * 
     * @param rows
     *            The rows to initially add to the data source
     */
    public ListDataSource(T... rows) {
        if (rows == null) {
            ds = new ArrayList<T>();
        } else {
            ds = new ArrayList<T>(Arrays.asList(rows));
        }
        wrapper = new ListWrapper();
    }

    @Override
    public void ensureAvailability(int firstRowIndex, int numberOfRows) {
        if (firstRowIndex >= ds.size()) {
            throw new IllegalStateException(
                    "Trying to fetch rows outside of array");
        }

        if (changeHandler != null) {
            changeHandler.dataAvailable(firstRowIndex, numberOfRows);
        }
    }

    @Override
    public T getRow(int rowIndex) {
        return ds.get(rowIndex);
    }

    @Override
    public int size() {
        return ds.size();
    }

    @Override
    public void setDataChangeHandler(DataChangeHandler dataChangeHandler) {
        this.changeHandler = dataChangeHandler;
    }

    /**
     * Gets the list that backs this datasource. Any changes made to this list
     * will be reflected in the datasource.
     * <p>
     * Note: The list is not the same list as passed into the data source via
     * the constructor.
     * 
     * @return Returns a list implementation that wraps the real list that backs
     *         the data source and provides events for the data source
     *         listeners.
     */
    public List<T> asList() {
        return wrapper;
    }

    @Override
    public RowHandle<T> getHandle(T row) throws IllegalStateException {
        assert ds.contains(row) : "This data source doesn't contain the row "
                + row;
        return new RowHandleImpl(row);
    }

    /**
     * Sort entire container according to a {@link Comparator}.
     * 
     * @param comparator
     *            a comparator object, which compares two data source entries
     *            (beans/pojos)
     */
    public void sort(Comparator<T> comparator) {
        Collections.sort(ds, comparator);
        if (changeHandler != null) {
            changeHandler.dataUpdated(0, ds.size());
        }
    }

    /**
     * Retrieves the index for given row object.
     * <p>
     * <em>Note:</em> This method does not verify that the given row object
     * exists at all in this DataSource.
     * 
     * @param row
     *            the row object
     * @return index of the row; or <code>-1</code> if row is not available
     */
    public int indexOf(T row) {
        return ds.indexOf(row);
    }

    /**
     * Returns a {@link SelectAllHandler} for this ListDataSource.
     * 
     * @return select all handler
     */
    public SelectAllHandler<T> getSelectAllHandler() {
        return new SelectAllHandler<T>() {
            @Override
            public void onSelectAll(SelectAllEvent<T> event) {
                event.getSelectionModel().select(asList());
            }
        };
    }
}
