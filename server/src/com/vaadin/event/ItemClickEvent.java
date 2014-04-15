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
package com.vaadin.event;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;

/**
 * 
 * Click event fired by a {@link Component} implementing
 * {@link com.vaadin.data.Container} interface. ItemClickEvents happens on an
 * {@link Item} rendered somehow on terminal. Event may also contain a specific
 * {@link Property} on which the click event happened.
 * 
 * @since 5.3
 * 
 */
@SuppressWarnings("serial")
public class ItemClickEvent extends ClickEvent implements Serializable {
    private Item item;
    private Object itemId;
    private Object propertyId;

    public ItemClickEvent(Component source, Item item, Object itemId,
            Object propertyId, MouseEventDetails details) {
        super(source, details);
        this.item = item;
        this.itemId = itemId;
        this.propertyId = propertyId;
    }

    /**
     * Gets the item on which the click event occurred.
     * 
     * @return item which was clicked
     */
    public Item getItem() {
        return item;
    }

    /**
     * Gets a possible identifier in source for clicked Item
     * 
     * @return
     */
    public Object getItemId() {
        return itemId;
    }

    /**
     * Returns property on which click event occurred. Returns null if source
     * cannot be resolved at property leve. For example if clicked a cell in
     * table, the "column id" is returned.
     * 
     * @return a property id of clicked property or null if click didn't occur
     *         on any distinct property.
     */
    public Object getPropertyId() {
        return propertyId;
    }

    public static final Method ITEM_CLICK_METHOD;

    static {
        try {
            ITEM_CLICK_METHOD = ItemClickListener.class.getDeclaredMethod(
                    "itemClick", new Class[] { ItemClickEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException();
        }
    }

    public interface ItemClickListener extends Serializable {
        public void itemClick(ItemClickEvent event);
    }

    /**
     * The interface for adding and removing <code>ItemClickEvent</code>
     * listeners. By implementing this interface a class explicitly announces
     * that it will generate an <code>ItemClickEvent</code> when one of its
     * items is clicked.
     * <p>
     * Note: The general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
     * 
     * @since 6.5
     * @see ItemClickListener
     * @see ItemClickEvent
     */
    public interface ItemClickNotifier extends Serializable {
        /**
         * Register a listener to handle {@link ItemClickEvent}s.
         * 
         * @param listener
         *            ItemClickListener to be registered
         */
        public void addItemClickListener(ItemClickListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #addItemClickListener(ItemClickListener)}
         **/
        @Deprecated
        public void addListener(ItemClickListener listener);

        /**
         * Removes an ItemClickListener.
         * 
         * @param listener
         *            ItemClickListener to be removed
         */
        public void removeItemClickListener(ItemClickListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #removeItemClickListener(ItemClickListener)}
         **/
        @Deprecated
        public void removeListener(ItemClickListener listener);
    }

}
