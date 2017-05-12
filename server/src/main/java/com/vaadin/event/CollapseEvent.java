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
 * An event that is fired when an item is collapsed in a listing component that
 * displays hierarchical data. Note that expanded subtrees of the collapsed item
 * will not trigger collapse events.
 *
 * @author Vaadin Ltd
 * @since 8.1
 * @param <T>
 *            collapsed item type
 */
public class CollapseEvent<T> extends Component.Event {

    private final T collapsedItem;

    private final boolean userOriginated;

    /**
     * Construct a collapse event.
     *
     * @param source
     *            the hierarchical component this event originated from
     * @param collapsedItem
     *            the item that was collapsed
     * @param userOriginated
     *            whether the collapse was triggered by a user interaction or
     *            the server
     */
    public CollapseEvent(Component source, T collapsedItem,
            boolean userOriginated) {
        super(source);
        this.collapsedItem = collapsedItem;
        this.userOriginated = userOriginated;
    }

    /**
     * Get the collapsed item that triggered this event.
     *
     * @return the collapsed item
     */
    public T getCollapsedItem() {
        return collapsedItem;
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
     * Item collapse event listener.
     *
     * @param <T>
     *            the collapsed item's type
     * @since 8.1
     */
    @FunctionalInterface
    public interface CollapseListener<T> extends SerializableEventListener {

        public static final Method COLLAPSE_METHOD = ReflectTools.findMethod(
                CollapseListener.class, "itemCollapse", CollapseEvent.class);

        /**
         * Callback method for when an item has been collapsed.
         *
         * @param event
         *            the collapse event
         */
        public void itemCollapse(CollapseEvent<T> event);
    }
}
