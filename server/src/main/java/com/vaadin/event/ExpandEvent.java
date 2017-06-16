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
package com.vaadin.event;

import java.lang.reflect.Method;

import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * An event that is fired when an item is expanded in a listing component that
 * displays hierarchical data.
 *
 * @author Vaadin Ltd
 * @since 8.1
 * @param <T>
 *            the expanded item's type
 */
public class ExpandEvent<T> extends Component.Event {

    private final T expandedItem;

    private final boolean userOriginated;

    /**
     * Construct an expand event.
     *
     * @param source
     *            the hierarchical component this event originated from
     * @param expandedItem
     *            the item that was expanded
     * @param userOriginated
     *            whether the expand was triggered by a user interaction or the
     *            server
     */
    public ExpandEvent(Component source, T expandedItem,
            boolean userOriginated) {
        super(source);
        this.expandedItem = expandedItem;
        this.userOriginated = userOriginated;
    }

    /**
     * Get the expanded item that triggered this event.
     *
     * @return the expanded item
     */
    public T getExpandedItem() {
        return expandedItem;
    }

    /**
     * Returns whether this event was triggered by user interaction, on the
     * client side, or programmatically, on the server side.
     *
     * @return {@code true} if this event originates from the client,
     *         {@code false} otherwise.
     */
    public boolean isUserOriginated() {
        return userOriginated;
    }

    /**
     * Item expand event listener.
     *
     * @param <T>
     *            the expanded item's type
     * @since 8.1
     */
    @FunctionalInterface
    public interface ExpandListener<T> extends SerializableEventListener {

        public static final Method EXPAND_METHOD = ReflectTools.findMethod(
                ExpandListener.class, "itemExpand", ExpandEvent.class);

        /**
         * Callback method for when an item has been expanded.
         *
         * @param event
         *            the expand event
         */
        public void itemExpand(ExpandEvent<T> event);
    }
}
