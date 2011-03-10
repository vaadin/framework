package com.vaadin.data.util.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Simple string filter for matching items that start with or contain a
 * specified string. The matching can be case-sensitive or case-insensitive.
 * 
 * This filter also directly supports in-memory filtering. When performing
 * in-memory filtering, values of other types are converted using toString(),
 * but other (lazy container) implementations do not need to perform such
 * conversions and might not support values of different types.
 * 
 * Note that this filter is modeled after the pre-6.6 filtering mechanisms, and
 * might not be very efficient e.g. for database filtering.
 * 
 * TODO this might still change
 * 
 * @since 6.6
 */
public class SimpleStringFilter implements Filter {

    final Object propertyId;
    final String filterString;
    final boolean ignoreCase;
    final boolean onlyMatchPrefix;

    public SimpleStringFilter(Object propertyId, String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {
        this.propertyId = propertyId;
        this.filterString = ignoreCase ? filterString.toLowerCase()
                : filterString;
        this.ignoreCase = ignoreCase;
        this.onlyMatchPrefix = onlyMatchPrefix;
    }

    public boolean passesFilter(Item item) {
        final Property p = item.getItemProperty(propertyId);
        if (p == null || p.toString() == null) {
            return false;
        }
        final String value = ignoreCase ? p.toString().toLowerCase() : p
                .toString();
        if (onlyMatchPrefix) {
            if (!value.startsWith(filterString)) {
                return false;
            }
        } else {
            if (!value.contains(filterString)) {
                return false;
            }
        }
        return true;
    }

    public boolean appliesToProperty(Object propertyId) {
        return this.propertyId.equals(propertyId);
    }

    @Override
    public boolean equals(Object obj) {

        // Only ones of the objects of the same class can be equal
        if (!(obj instanceof SimpleStringFilter)) {
            return false;
        }
        final SimpleStringFilter o = (SimpleStringFilter) obj;

        // Checks the properties one by one
        if (propertyId != o.propertyId && o.propertyId != null
                && !o.propertyId.equals(propertyId)) {
            return false;
        }
        if (filterString != o.filterString && o.filterString != null
                && !o.filterString.equals(filterString)) {
            return false;
        }
        if (ignoreCase != o.ignoreCase) {
            return false;
        }
        if (onlyMatchPrefix != o.onlyMatchPrefix) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (propertyId != null ? propertyId.hashCode() : 0)
                ^ (filterString != null ? filterString.hashCode() : 0);
    }
}
