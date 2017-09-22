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

import java.util.Locale;
import java.util.Objects;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.UI;

/**
 * A mixin interface for in-memory data providers. Contains methods for
 * configuring sorting and filtering.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            data type
 */
public interface InMemoryDataProvider<T> extends
        ConfigurableFilterDataProvider<T, SerializablePredicate<T>, SerializablePredicate<T>> {

    @Override
    public default boolean isInMemory() {
        return true;
    }

    /**
     * Gets the current filter of this data provider.
     *
     * @return the filter of this data provider
     */
    public SerializablePredicate<T> getFilter();

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
    public void setFilter(SerializablePredicate<T> filter);

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
    public default <V> void setFilter(ValueProvider<T, V> valueProvider,
            SerializablePredicate<V> valueFilter) {
        setFilter(InMemoryDataProviderHelpers
                .createValueProviderFilter(valueProvider, valueFilter));
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
    public default void addFilter(SerializablePredicate<T> filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");

        if (getFilter() == null) {
            setFilter(filter);
        } else {
            SerializablePredicate<T> oldFilter = getFilter();
            setFilter(item -> oldFilter.test(item) && filter.test(item));
        }
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
    public default <V> void addFilter(ValueProvider<T, V> valueProvider,
            SerializablePredicate<V> valueFilter) {
        Objects.requireNonNull(valueProvider, "Value provider cannot be null");
        Objects.requireNonNull(valueFilter, "Value filter cannot be null");

        addFilter(InMemoryDataProviderHelpers
                .createValueProviderFilter(valueProvider, valueFilter));
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
    public default <V> void setFilterByValue(ValueProvider<T, V> valueProvider,
            V requiredValue) {
        setFilter(InMemoryDataProviderHelpers.createEqualsFilter(valueProvider,
                requiredValue));
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
    public default <V> void addFilterByValue(ValueProvider<T, V> valueProvider,
            V requiredValue) {
        addFilter(InMemoryDataProviderHelpers.createEqualsFilter(valueProvider,
                requiredValue));
    }

    /**
     * Removes any filter that has been set or added previously.
     *
     * @see #setFilter(SerializablePredicate)
     */
    public default void clearFilters() {
        setFilter(null);
    }

    /**
     * Gets the current sort comparator of this data provider.
     *
     * @return the sort comparator of this data provider
     */
    public SerializableComparator<T> getSortComparator();

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
    public void setSortComparator(SerializableComparator<T> comparator);

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
    public default void addSortComparator(
            SerializableComparator<T> comparator) {
        Objects.requireNonNull(comparator, "Comparator to add cannot be null");
        SerializableComparator<T> originalComparator = getSortComparator();
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
    public default <V extends Comparable<? super V>> void setSortOrder(
            ValueProvider<T, V> valueProvider, SortDirection sortDirection) {
        setSortComparator(InMemoryDataProviderHelpers
                .propertyComparator(valueProvider, sortDirection));
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
    public default <V extends Comparable<? super V>> void addSortOrder(
            ValueProvider<T, V> valueProvider, SortDirection sortDirection) {
        addSortComparator(InMemoryDataProviderHelpers
                .propertyComparator(valueProvider, sortDirection));
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
    public default <Q> DataProvider<T, Q> filteringBy(
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
    public default <V, Q> DataProvider<T, Q> filteringBy(
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
    public default <V> DataProvider<T, V> filteringByEquals(
            ValueProvider<T, V> valueProvider) {
        return filteringBy(valueProvider, Objects::equals);
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
    public default DataProvider<T, String> filteringBySubstring(
            ValueProvider<T, String> valueProvider, Locale locale) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        return InMemoryDataProviderHelpers.filteringByCaseInsensitiveString(
                this, valueProvider,
                String::contains, () -> locale);
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
    public default DataProvider<T, String> filteringBySubstring(
            ValueProvider<T, String> valueProvider) {
        return InMemoryDataProviderHelpers.filteringByCaseInsensitiveString(
                this, valueProvider, String::contains,
                InMemoryDataProviderHelpers.CURRENT_LOCALE_SUPPLIER);
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
    public default DataProvider<T, String> filteringByPrefix(
            ValueProvider<T, String> valueProvider, Locale locale) {
        return InMemoryDataProviderHelpers.filteringByCaseInsensitiveString(this, valueProvider,
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
    public default DataProvider<T, String> filteringByPrefix(
            ValueProvider<T, String> valueProvider) {
        return InMemoryDataProviderHelpers.filteringByCaseInsensitiveString(this, valueProvider,
                String::startsWith, InMemoryDataProviderHelpers.CURRENT_LOCALE_SUPPLIER);
    }
}
