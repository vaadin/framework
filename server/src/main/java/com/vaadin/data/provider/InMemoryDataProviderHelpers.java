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
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
class InMemoryDataProviderHelpers {

    /**
     *
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
     *
     * @param dataProvider
     * @param valueProvider
     * @param predicate
     * @return
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
     *
     * @param dataProvider
     * @param valueProvider
     * @param predicate
     * @param localeSupplier
     * @return
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
     *
     * @param valueProvider
     * @param sortDirection
     * @return
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
     *
     * @param sortDirection
     * @return
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
     *
     * @param valueProvider
     * @param valueFilter
     * @return
     */
    public static <T, V> SerializablePredicate<T> createValueProviderFilter(
            ValueProvider<T, V> valueProvider,
            SerializablePredicate<V> valueFilter) {
        return item -> valueFilter.test(valueProvider.apply(item));
    }

    /**
     *
     * @param valueProvider
     * @param requiredValue
     * @return
     */
    public static <T, V> SerializablePredicate<T> createEqualsFilter(
            ValueProvider<T, V> valueProvider, V requiredValue) {
        Objects.requireNonNull(valueProvider, "Value provider cannot be null");

        return item -> Objects.equals(valueProvider.apply(item), requiredValue);
    }
}
