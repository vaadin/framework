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

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.SerializableSupplier;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.UI;

/**
 * A class containing a number of static helper methods for implementing
 * {@link InMemoryDataProvider}s.
 * <p>
 * This class is intended primarily for internal use.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class InMemoryDataProviderHelpers {

    /**
     * Supplier that attempts to resolve a locale from the current UI. Returns
     * the system's default locale as a fallback.
     */
    public static final SerializableSupplier<Locale> CURRENT_LOCALE_SUPPLIER = () -> {
        UI currentUi = UI.getCurrent();
        if (currentUi != null) {
            return currentUi.getLocale();
        } else {
            return Locale.getDefault();
        }
    };

    /**
     * Wraps a given data provider so that its filter ignores null items
     * returned by the given value provider.
     *
     * @param dataProvider
     *            the data provider to wrap
     * @param valueProvider
     *            the value provider for providing values to filter
     * @param predicate
     *            the predicate to combine null filtering with
     * @return the wrapped data provider
     */
    public static <T, V, Q> DataProvider<T, Q> filteringByIgnoreNull(
            InMemoryDataProvider<T> dataProvider,
            ValueProvider<T, V> valueProvider,
            SerializableBiPredicate<V, Q> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");

        return dataProvider.filteringBy(valueProvider,
                (itemValue, queryFilter) -> itemValue != null
                        && predicate.test(itemValue, queryFilter));
    }

    /**
     * Wraps a given data provider so that its filter tests the given predicate
     * with the lower case string provided by the given value provider.
     *
     * @param dataProvider
     *            the data provider to wrap
     * @param valueProvider
     *            the value provider for providing string values to filter
     * @param predicate
     *            the predicate to use for comparing the resulting lower case
     *            strings
     * @param localeSupplier
     *            the locale to use when converting strings to lower case
     * @return the wrapped data provider
     */
    public static <T> DataProvider<T, String> filteringByCaseInsensitiveString(
            InMemoryDataProvider<T> dataProvider,
            ValueProvider<T, String> valueProvider,
            SerializableBiPredicate<String, String> predicate,
            SerializableSupplier<Locale> localeSupplier) {
        // Only assert since these are only passed from our own code
        assert predicate != null;
        assert localeSupplier != null;

        return filteringByIgnoreNull(dataProvider, valueProvider,
                (itemString, filterString) -> {
                    Locale locale = localeSupplier.get();
                    assert locale != null;

                    return predicate.test(itemString.toLowerCase(locale),
                            filterString.toLowerCase(locale));
                });
    }

    /**
     * Creates a comparator for the return type of the given
     * {@link ValueProvider}, sorted in the direction specified by the given
     * {@link SortDirection}.
     *
     * @param valueProvider
     *            the value provider to use
     * @param sortDirection
     *            the sort direction to use
     * @return the created comparator
     */
    public static <V extends Comparable<? super V>, T> SerializableComparator<T> propertyComparator(
            ValueProvider<T, V> valueProvider, SortDirection sortDirection) {
        Objects.requireNonNull(valueProvider, "Value provider cannot be null");
        Objects.requireNonNull(sortDirection, "Sort direction cannot be null");

        Comparator<V> comparator = getNaturalSortComparator(sortDirection);

        return (a, b) -> comparator.compare(valueProvider.apply(a),
                valueProvider.apply(b));
    }

    /**
     * Gets the natural order comparator for the type argument, or the natural
     * order comparator reversed if the given sorting direction is
     * {@link SortDirection#DESCENDING}.
     *
     * @param sortDirection
     *            the sort direction to use
     * @return the natural comparator, with ordering defined by the given sort
     *         direction
     */
    public static <V extends Comparable<? super V>> Comparator<V> getNaturalSortComparator(
            SortDirection sortDirection) {
        Comparator<V> comparator = Comparator.naturalOrder();
        if (sortDirection == SortDirection.DESCENDING) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    /**
     * Creates a new predicate from the given predicate and value provider. This
     * allows using a predicate of the value providers return type with objects
     * of the value providers type.
     *
     * @param valueProvider
     *            the value provider to use
     * @param valueFilter
     *            the original predicate
     * @return the created predicate
     */
    public static <T, V> SerializablePredicate<T> createValueProviderFilter(
            ValueProvider<T, V> valueProvider,
            SerializablePredicate<V> valueFilter) {
        return item -> valueFilter.test(valueProvider.apply(item));
    }

    /**
     * Creates a predicate that compares equality of the given required value to
     * the value the given value provider obtains.
     *
     * @param valueProvider
     *            the value provider to use
     * @param requiredValue
     *            the required value
     * @return the created predicate
     */
    public static <T, V> SerializablePredicate<T> createEqualsFilter(
            ValueProvider<T, V> valueProvider, V requiredValue) {
        Objects.requireNonNull(valueProvider, "Value provider cannot be null");

        return item -> Objects.equals(valueProvider.apply(item), requiredValue);
    }
}
