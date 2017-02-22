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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.UI;

/**
 * {@link DataProvider} wrapper for {@link Collection}s. This class does not
 * actually handle the {@link Query} parameters.
 *
 * @param <T>
 *            data type
 * @since 8.0
 */
public class ListDataProvider<T>
        extends AbstractDataProvider<T, SerializablePredicate<T>> implements
        ConfigurableFilterDataProvider<T, SerializablePredicate<T>, SerializablePredicate<T>> {

    private static final SerializableSupplier<Locale> CURRENT_LOCALE_SUPPLIER = () -> {
        UI currentUi = UI.getCurrent();
        if (currentUi != null) {
            return currentUi.getLocale();
        } else {
            return Locale.getDefault();
        }
    };

    private SerializableComparator<T> sortOrder = null;

    private SerializablePredicate<T> filter;

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

    /**
     * Returns the underlying data items.
     *
     * @return the underlying data items
     */
    public Collection<T> getItems() {
        return backend;
    }

    @Override
    public Stream<T> fetch(Query<T, SerializablePredicate<T>> query) {
        Stream<T> stream = getFilteredStream(query);

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
        return (int) getFilteredStream(query).count();
    }

    private Stream<T> getFilteredStream(
            Query<T, SerializablePredicate<T>> query) {
        Stream<T> stream = backend.stream();

        // Apply our own filters first so that query filters never see the items
        // that would already have been filtered out
        if (filter != null) {
            stream = stream.filter(filter);
        }

        stream = query.getFilter().map(stream::filter).orElse(stream);

        return stream;
    }

    /**
     * Sets the comparator to use as the default sorting for this data provider.
     * This overrides the sorting set by any other method that manipulates the
     * default sorting of this data provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @see #setSortOrder(ValueProvider, SortDirection)
     * @see #addSortComparator(SerializableComparator)
     *
     * @param comparator
     *            a comparator to use, or <code>null</code> to clear any
     *            previously set sort order
     */
    public void setSortComparator(SerializableComparator<T> comparator) {
        this.sortOrder = comparator;
        refreshAll();
    }

    /**
     * Sets the property and direction to use as the default sorting for this
     * data provider. This overrides the sorting set by any other method that
     * manipulates the default sorting of this data provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @see #setSortComparator(SerializableComparator)
     * @see #addSortOrder(ValueProvider, SortDirection)
     *
     * @param valueProvider
     *            the value provider that defines the property do sort by, not
     *            <code>null</code>
     * @param sortDirection
     *            the sort direction to use, not <code>null</code>
     */
    public <V extends Comparable<? super V>> void setSortOrder(
            ValueProvider<T, V> valueProvider, SortDirection sortDirection) {
        setSortComparator(propertyComparator(valueProvider, sortDirection));
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
     * @see #setSortComparator(SerializableComparator)
     * @see #addSortOrder(ValueProvider, SortDirection)
     *
     * @param comparator
     *            a comparator to add, not <code>null</code>
     */
    public void addSortComparator(SerializableComparator<T> comparator) {
        Objects.requireNonNull(comparator, "Sort order to add cannot be null");

        SerializableComparator<T> originalComparator = this.sortOrder;
        if (originalComparator == null) {
            setSortComparator(comparator);
        } else {
            setSortComparator((a, b) -> {
                int result = originalComparator.compare(a, b);
                if (result == 0) {
                    result = comparator.compare(a, b);
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
     * @see #setSortOrder(ValueProvider, SortDirection)
     * @see #addSortComparator(SerializableComparator)
     *
     * @param valueProvider
     *            the value provider that defines the property do sort by, not
     *            <code>null</code>
     * @param sortDirection
     *            the sort direction to use, not <code>null</code>
     */
    public <V extends Comparable<? super V>> void addSortOrder(
            ValueProvider<T, V> valueProvider, SortDirection sortDirection) {
        addSortComparator(propertyComparator(valueProvider, sortDirection));
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

    /**
     * Sets a filter to be applied to all queries. The filter replaces any
     * filter that has been set or added previously.
     *
     * @see #setFilter(ValueProvider, SerializablePredicate)
     * @see #setFilterByValue(ValueProvider, Object)
     * @see #addFilter(SerializablePredicate)
     *
     * @param filter
     *            the filter to set, or <code>null</code> to remove any set
     *            filters
     */
    @Override
    public void setFilter(SerializablePredicate<T> filter) {
        this.filter = filter;
        refreshAll();
    }

    /**
     * Adds a filter to be applied to all queries. The filter will be used in
     * addition to any filter that has been set or added previously.
     *
     * @see #addFilter(ValueProvider, SerializablePredicate)
     * @see #addFilterByValue(ValueProvider, Object)
     * @see #setFilter(SerializablePredicate)
     *
     * @param filter
     *            the filter to add, not <code>null</code>
     */
    public void addFilter(SerializablePredicate<T> filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");

        if (this.filter == null) {
            setFilter(filter);
        } else {
            SerializablePredicate<T> oldFilter = this.filter;
            setFilter(item -> oldFilter.test(item) && filter.test(item));
        }
    }

    /**
     * Removes any filter that has been set or added previously.
     *
     * @see #setFilter(SerializablePredicate)
     */
    public void clearFilters() {
        setFilter(null);
    }

    /**
     * Sets a filter for an item property. The filter replaces any filter that
     * has been set or added previously.
     *
     * @see #setFilter(SerializablePredicate)
     * @see #setFilterByValue(ValueProvider, Object)
     * @see #addFilter(ValueProvider, SerializablePredicate)
     *
     * @param valueProvider
     *            value provider that gets the property value, not
     *            <code>null</code>
     * @param valueFilter
     *            filter for testing the property value, not <code>null</code>
     */
    public <V> void setFilter(ValueProvider<T, V> valueProvider,
            SerializablePredicate<V> valueFilter) {
        setFilter(createValueProviderFilter(valueProvider, valueFilter));
    }

    /**
     * Adds a filter for an item property. The filter will be used in addition
     * to any filter that has been set or added previously.
     *
     * @see #addFilter(SerializablePredicate)
     * @see #addFilterByValue(ValueProvider, Object)
     * @see #setFilter(ValueProvider, SerializablePredicate)
     *
     * @param valueProvider
     *            value provider that gets the property value, not
     *            <code>null</code>
     * @param valueFilter
     *            filter for testing the property value, not <code>null</code>
     */
    public <V> void addFilter(ValueProvider<T, V> valueProvider,
            SerializablePredicate<V> valueFilter) {
        Objects.requireNonNull(valueProvider, "Value provider cannot be null");
        Objects.requireNonNull(valueFilter, "Value filter cannot be null");

        addFilter(createValueProviderFilter(valueProvider, valueFilter));
    }

    private static <T, V> SerializablePredicate<T> createValueProviderFilter(
            ValueProvider<T, V> valueProvider,
            SerializablePredicate<V> valueFilter) {
        return item -> valueFilter.test(valueProvider.apply(item));
    }

    /**
     * Sets a filter that requires an item property to have a specific value.
     * The property value and the provided value are compared using
     * {@link Object#equals(Object)}. The filter replaces any filter that has
     * been set or added previously.
     *
     * @see #setFilter(SerializablePredicate)
     * @see #setFilter(ValueProvider, SerializablePredicate)
     * @see #addFilterByValue(ValueProvider, Object)
     *
     * @param valueProvider
     *            value provider that gets the property value, not
     *            <code>null</code>
     * @param requiredValue
     *            the value that the property must have for the filter to pass
     */
    public <V> void setFilterByValue(ValueProvider<T, V> valueProvider,
            V requiredValue) {
        setFilter(createEqualsFilter(valueProvider, requiredValue));
    }

    /**
     * Adds a filter that requires an item property to have a specific value.
     * The property value and the provided value are compared using
     * {@link Object#equals(Object)}.The filter will be used in addition to any
     * filter that has been set or added previously.
     *
     * @see #setFilterByValue(ValueProvider, Object)
     * @see #addFilter(SerializablePredicate)
     * @see #addFilter(ValueProvider, SerializablePredicate)
     *
     * @param valueProvider
     *            value provider that gets the property value, not
     *            <code>null</code>
     * @param requiredValue
     *            the value that the property must have for the filter to pass
     */
    public <V> void addFilterByValue(ValueProvider<T, V> valueProvider,
            V requiredValue) {
        addFilter(createEqualsFilter(valueProvider, requiredValue));
    }

    private static <T, V> SerializablePredicate<T> createEqualsFilter(
            ValueProvider<T, V> valueProvider, V requiredValue) {
        Objects.requireNonNull(valueProvider, "Value provider cannot be null");

        return item -> Objects.equals(valueProvider.apply(item), requiredValue);
    }

    /**
     * Wraps this data provider to create a new data provider that is filtered
     * by comparing an item to the filter value provided in the query.
     * <p>
     * The predicate receives the item as the first parameter and the query
     * filter value as the second parameter, and should return <code>true</code>
     * if the corresponding item should be included. The query filter value is
     * never <code>null</code> – all items are included without running the
     * predicate if the query doesn't define any filter.
     *
     * @param predicate
     *            a predicate to use for comparing the item to the query filter,
     *            not <code>null</code>
     *
     * @return a data provider that filters accordingly, not <code>null</code>
     */
    public <Q> DataProvider<T, Q> filteringBy(
            SerializableBiPredicate<T, Q> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");

        return withConvertedFilter(
                filterValue -> item -> predicate.test(item, filterValue));
    }

    /**
     * Wraps this data provider to create a new data provider that is filtered
     * by comparing an item property value to the filter value provided in the
     * query.
     * <p>
     * The predicate receives the property value as the first parameter and the
     * query filter value as the second parameter, and should return
     * <code>true</code> if the corresponding item should be included. The query
     * filter value is never <code>null</code> – all items are included without
     * running either callback if the query doesn't define any filter.
     *
     * @param valueProvider
     *            a value provider that gets the property value, not
     *            <code>null</code>
     * @param predicate
     *            a predicate to use for comparing the property value to the
     *            query filter, not <code>null</code>
     *
     * @return a data provider that filters accordingly, not <code>null</code>
     */
    public <V, Q> DataProvider<T, Q> filteringBy(
            ValueProvider<T, V> valueProvider,
            SerializableBiPredicate<V, Q> predicate) {
        Objects.requireNonNull(valueProvider, "Value provider cannot be null");
        Objects.requireNonNull(predicate, "Predicate cannot be null");

        return filteringBy((item, filterValue) -> predicate
                .test(valueProvider.apply(item), filterValue));
    }

    /**
     * Wraps this data provider to create a new data provider that is filtered
     * by testing whether the value of a property is equals to the filter value
     * provided in the query. Equality is tested using
     * {@link Objects#equals(Object, Object)}.
     *
     * @param valueProvider
     *            a value provider that gets the property value, not
     *            <code>null</code>
     *
     * @return a data provider that filters accordingly, not <code>null</code>
     */
    public <V> DataProvider<T, V> filteringByEquals(
            ValueProvider<T, V> valueProvider) {
        return filteringBy(valueProvider, Objects::equals);
    }

    private <V, Q> DataProvider<T, Q> filteringByIgnoreNull(
            ValueProvider<T, V> valueProvider,
            SerializableBiPredicate<V, Q> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");

        return filteringBy(valueProvider,
                (itemValue, queryFilter) -> itemValue != null
                        && predicate.test(itemValue, queryFilter));
    }

    /**
     * Wraps this data provider to create a new data provider that is filtered
     * by a string by checking whether the lower case representation of the
     * filter value provided in the query is a substring of the lower case
     * representation of an item property value. The filter never passes if the
     * item property value is <code>null</code>.
     *
     * @param valueProvider
     *            a value provider that gets the string property value, not
     *            <code>null</code>
     * @param locale
     *            the locale to use for converting the strings to lower case,
     *            not <code>null</code>
     * @return a data provider that filters accordingly, not <code>null</code>
     */
    public DataProvider<T, String> filteringBySubstring(
            ValueProvider<T, String> valueProvider, Locale locale) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        return filteringByCaseInsensitiveString(valueProvider, String::contains,
                () -> locale);
    }

    /**
     * Wraps this data provider to create a new data provider that is filtered
     * by a string by checking whether the lower case representation of the
     * filter value provided in the query is a substring of the lower case
     * representation of an item property value. Conversion to lower case is
     * done using the locale of the {@link UI#getCurrent() current UI} if
     * available, or otherwise {@link Locale#getDefault() the default locale}.
     * The filter never passes if the item property value is <code>null</code>.
     *
     * @param valueProvider
     *            a value provider that gets the string property value, not
     *            <code>null</code>
     * @return a data provider that filters accordingly, not <code>null</code>
     */
    public DataProvider<T, String> filteringBySubstring(
            ValueProvider<T, String> valueProvider) {
        return filteringByCaseInsensitiveString(valueProvider, String::contains,
                CURRENT_LOCALE_SUPPLIER);
    }

    /**
     * Wraps this data provider to create a new data provider that is filtered
     * by a string by checking whether the lower case representation of an item
     * property value starts with the lower case representation of the filter
     * value provided in the query. The filter never passes if the item property
     * value is <code>null</code>.
     *
     * @param valueProvider
     *            a value provider that gets the string property value, not
     *            <code>null</code>
     * @param locale
     *            the locale to use for converting the strings to lower case,
     *            not <code>null</code>
     * @return a data provider that filters accordingly, not <code>null</code>
     */
    public DataProvider<T, String> filteringByPrefix(
            ValueProvider<T, String> valueProvider, Locale locale) {
        return filteringByCaseInsensitiveString(valueProvider,
                String::startsWith, () -> locale);
    }

    /**
     * Wraps this data provider to create a new data provider that is filtered
     * by a string by checking whether the lower case representation of an item
     * property value starts with the lower case representation of the filter
     * value provided in the query. Conversion to lower case is done using the
     * locale of the {@link UI#getCurrent() current UI} if available, or
     * otherwise {@link Locale#getDefault() the default locale}. The filter
     * never passes if the item property value is <code>null</code>.
     *
     * @param valueProvider
     *            a value provider that gets the string property value, not
     *            <code>null</code>
     * @return a data provider that filters accordingly, not <code>null</code>
     */
    public DataProvider<T, String> filteringByPrefix(
            ValueProvider<T, String> valueProvider) {
        return filteringByCaseInsensitiveString(valueProvider,
                String::startsWith, CURRENT_LOCALE_SUPPLIER);
    }

    private DataProvider<T, String> filteringByCaseInsensitiveString(
            ValueProvider<T, String> valueProvider,
            SerializableBiPredicate<String, String> predicate,
            SerializableSupplier<Locale> localeSupplier) {
        // Only assert since these are only passed from our own code
        assert predicate != null;
        assert localeSupplier != null;

        return filteringByIgnoreNull(valueProvider,
                (itemString, filterString) -> {
                    Locale locale = localeSupplier.get();
                    assert locale != null;

                    return predicate.test(itemString.toLowerCase(locale),
                            filterString.toLowerCase(locale));
                });
    }
}
