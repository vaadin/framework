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
package com.vaadin.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Item caption generator class for declarative support.
 * <p>
 * Provides a straightforward mapping between an item and its caption.
 *
 * @param <T>
 *            item type
 */
class DeclarativeCaptionGenerator<T> implements ItemCaptionGenerator<T> {

    private ItemCaptionGenerator<T> fallback;
    private Map<T, String> captions = new HashMap<>();

    public DeclarativeCaptionGenerator(ItemCaptionGenerator<T> fallback) {
        this.fallback = fallback;
    }

    @Override
    public String apply(T item) {
        return captions.containsKey(item) ? captions.get(item)
                : fallback.apply(item);
    }

    /**
     * Sets a {@code caption} for the {@code item}.
     *
     * @param item
     *            a data item
     * @param caption
     *            a caption for the {@code item}
     */
    protected void setCaption(T item, String caption) {
        captions.put(item, caption);
    }

}
