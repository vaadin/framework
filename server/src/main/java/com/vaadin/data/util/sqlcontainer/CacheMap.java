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
