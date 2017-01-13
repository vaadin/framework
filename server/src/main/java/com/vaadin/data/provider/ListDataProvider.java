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
package com.vaadin.data.provider;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.data.sort.SortDirection;

/**
 * {@link DataProvider} wrapper for {@link Collection}s. This class does not
 * actually handle the {@link Query} parameters.
 *
 * @param <T>
 *            data type
 */
public class ListDataProvider<T>
        extends AbstractDataProvider<T, SerializablePredicate<T>>
        implements AppendableFilterDataProvider<T, SerializablePredicate<T>> {

    private SerializableComparator<T> sortOrder = null;
    private final Collection<T> backend;

    /**
     * Constructs a new ListDataProvider.
     * <p>
     * No protective copy is made of the list, and changes in the provided
     * backing Collection will be visible via this data provider. The caller
     * should copy the list if necessary.
     *
     * @param items
     *            the initial data, not null
     */
    public ListDataProvider(Collection<T> items) {
        Objects.requireNonNull(items, "items cannot be null");
        backend = items;
        sortOrder = null;
    }

    @Override
    public Stream<T> fetch(Query<T, SerializablePredicate<T>> query) {
        Stream<T> stream = backend.stream()
                .filter(t -> query.getFilter().orElse(p -> true).test(t));

        Optional<Comparator<T>> comparing = Stream
                .of(query.getInMemorySorting(), sortOrder)
                .filter(c -> c != null)
                .reduce((c1, c2) -> c1.thenComparing(c2));

        if (comparing.isPresent()) {
            stream = stream.sorted(comparing.get());
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int size(Query<T, SerializablePredicate<T>> query) {
        return (int) backend.stream()
                .filter(t -> query.getFilter().orElse(p -> true).test(t))
                .count();
    }

    /**
     * Sets the comparator to use as the default sorting for this data provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @param sortOrder
     *            a comparator to use, or <code>null</code> to clear any
     *            previously set sort order
     */
    public void setSortOrder(SerializableComparator<T> sortOrder) {
        this.sortOrder = sortOrder;
        refreshAll();
    }

    /**
     * Sets the property and direction to use as the default sorting for this
     * data provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @param valueProvider
     *            the value provider that defines the property do sort by, not
     *            <code>null</code>
     * @param sortDirection
     *            the sort direction to use, not <code>null</code>
     */
    public <V extends Comparable<? super V>> void setSortOrder(
            ValueProvider<T, V> valueProvider, SortDirection sortDirection) {
        setSortOrder(propertyComparator(valueProvider, sortDirection));
    }

    /**
     * Adds a comparator to the default sorting for this data provider. If no
     * default sorting has been defined, then the provided comparator will be
     * used as the default sorting. If a default sorting has been defined, then
     * the provided comparator will be used to determine the ordering of items
     * that are considered equal by the previously defined default sorting.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @param sortOrder
     *            a comparator to add, not <code>null</code>
     */
    public void addSortOrder(SerializableComparator<T> sortOrder) {
        Objects.requireNonNull(sortOrder, "Sort order to add cannot be null");

        SerializableComparator<T> originalComparator = this.sortOrder;
        if (originalComparator == null) {
            setSortOrder(sortOrder);
        } else {
            setSortOrder((a, b) -> {
                int result = originalComparator.compare(a, b);
                if (result == 0) {
                    result = sortOrder.compare(a, b);
                }
                return result;
            });
        }
    }

    /**
     * Adds a property and direction to the default sorting for this data
     * provider. If no default sorting has been defined, then the provided sort
     * order will be used as the default sorting. If a default sorting has been
     * defined, then the provided sort order will be used to determine the
     * ordering of items that are considered equal by the previously defined
     * default sorting.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @param valueProvider
     *            the value provider that defines the property do sort by, not
     *            <code>null</code>
     * @param sortDirection
     *            the sort direction to use, not <code>null</code>
     */
    public <V extends Comparable<? super V>> void addSortOrder(
            ValueProvider<T, V> valueProvider, SortDirection sortDirection) {
        addSortOrder(propertyComparator(valueProvider, sortDirection));
    }

    private static <V extends Comparable<? super V>, T> SerializableComparator<T> propertyComparator(
            ValueProvider<T, V> valueProvider, SortDirection sortDirection) {
        Objects.requireNonNull(valueProvider, "Value provider cannot be null");
        Objects.requireNonNull(sortDirection, "Sort direction cannot be null");

        Comparator<V> comparator = getNaturalSortComparator(sortDirection);

        return (a, b) -> comparator.compare(valueProvider.apply(a),
                valueProvider.apply(b));
    }

    private static <V extends Comparable<? super V>> Comparator<V> getNaturalSortComparator(
            SortDirection sortDirection) {
        Comparator<V> comparator = Comparator.naturalOrder();
        if (sortDirection == SortDirection.DESCENDING) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    @Override
    public SerializablePredicate<T> combineFilters(
            SerializablePredicate<T> filter1,
            SerializablePredicate<T> filter2) {
        return t -> filter1.test(t) && filter2.test(t);
    }
}
