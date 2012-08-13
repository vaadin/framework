/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CacheMap extends LinkedHashMap, adding the possibility to adjust maximum
 * number of items. In SQLContainer this is used for RowItem -cache. Cache size
 * will be two times the page length parameter of the container.
 */
class CacheMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 679999766473555231L;
    private int cacheLimit = SQLContainer.CACHE_RATIO
            * SQLContainer.DEFAULT_PAGE_LENGTH;

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > cacheLimit;
    }

    void setCacheLimit(int limit) {
        cacheLimit = limit > 0 ? limit : SQLContainer.DEFAULT_PAGE_LENGTH;
    }

    int getCacheLimit() {
        return cacheLimit;
    }
}