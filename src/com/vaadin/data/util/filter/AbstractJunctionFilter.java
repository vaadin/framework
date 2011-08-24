/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.vaadin.data.Container.Filter;

/**
 * Abstract base class for filters that are composed of multiple sub-filters.
 * 
 * The method {@link #appliesToProperty(Object)} is provided to help
 * implementing {@link Filter} for in-memory filters.
 * 
 * @since 6.6
 */
public abstract class AbstractJunctionFilter implements Filter {

    protected final Collection<Filter> filters;

    public AbstractJunctionFilter(Filter... filters) {
        this.filters = Collections.unmodifiableCollection(Arrays
                .asList(filters));
    }

    /**
     * Returns an unmodifiable collection of the sub-filters of this composite
     * filter.
     * 
     * @return
     */
    public Collection<Filter> getFilters() {
        return filters;
    }

    /**
     * Returns true if a change in the named property may affect the filtering
     * result. If some of the sub-filters are not in-memory filters, true is
     * returned.
     * 
     * By default, all sub-filters are iterated to check if any of them applies.
     * If there are no sub-filters, false is returned - override in subclasses
     * to change this behavior.
     */
    public boolean appliesToProperty(Object propertyId) {
        for (Filter filter : getFilters()) {
            if (filter.appliesToProperty(propertyId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        AbstractJunctionFilter other = (AbstractJunctionFilter) obj;
        // contents comparison with equals()
        return Arrays.equals(filters.toArray(), other.filters.toArray());
    }

    @Override
    public int hashCode() {
        int hash = getFilters().size();
        for (Filter filter : filters) {
            hash = (hash << 1) ^ filter.hashCode();
        }
        return hash;
    }
}