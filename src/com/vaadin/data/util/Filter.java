/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util;

import java.io.Serializable;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * A default filter that can be used to implement
 * {@link com.vaadin.data.Container.Filterable}.
 * 
 * @since 5.4
 */
@SuppressWarnings("serial")
public class Filter implements AbstractInMemoryContainer.ItemFilter,
        Serializable {
    final Object propertyId;
    final String filterString;
    final boolean ignoreCase;
    final boolean onlyMatchPrefix;

    Filter(Object propertyId, String filterString, boolean ignoreCase,
            boolean onlyMatchPrefix) {
        this.propertyId = propertyId;
        ;
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
        if (!(obj instanceof Filter)) {
            return false;
        }
        final Filter o = (Filter) obj;

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