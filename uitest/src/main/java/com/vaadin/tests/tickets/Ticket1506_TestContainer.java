package com.vaadin.tests.tickets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

/**
 * @author Efecte R&D
 * @version $Revision$, $Date$
 */
public class Ticket1506_TestContainer implements Container {
    private Map<String, PropertysetItem> items = new HashMap<String, PropertysetItem>();
    public static final String ITEM_1_ID = "1";
    public static final String ITEM_2_ID = "2";
    public static final String PROPERTY_1_ID = "property 1";
    public static final String PROPERTY_2_ID = "property 2";

    private void loadItems() {
        final PropertysetItem item1 = new PropertysetItem();
        item1.addItemProperty(PROPERTY_1_ID, new ObjectProperty<String>(
                "value 1", String.class));
        item1.addItemProperty(PROPERTY_2_ID, new ObjectProperty<String>(
                "name 1", String.class));
        items.put(ITEM_1_ID, item1);

        final PropertysetItem item2 = new PropertysetItem();
        item2.addItemProperty(PROPERTY_1_ID, new ObjectProperty<String>(
                "value 2", String.class));
        item2.addItemProperty(PROPERTY_2_ID, new ObjectProperty<String>(
                "name 2", String.class));
        items.put(ITEM_2_ID, item2);
    }

    @Override
    public Item getItem(Object itemId) {
        if (items.isEmpty()) {
            loadItems();
        }
        return items.get(itemId);
    }

    @Override
    public Collection<String> getContainerPropertyIds() {
        if (items.isEmpty()) {
            loadItems();
        }
        ArrayList<String> a = new ArrayList<String>();
        a.add(PROPERTY_1_ID);
        a.add(PROPERTY_2_ID);
        return a;
    }

    @Override
    public Collection<String> getItemIds() {
        if (items.isEmpty()) {
            loadItems();
        }
        ArrayList<String> a = new ArrayList<String>();
        a.add(ITEM_1_ID);
        a.add(ITEM_2_ID);
        return a;
    }

    @Override
    public Property<?> getContainerProperty(Object itemId, Object propertyId) {
        if (items.isEmpty()) {
            loadItems();
        }
        Item item = items.get(itemId);
        if (item != null) {
            return item.getItemProperty(propertyId);
        }
        return null;
    }

    @Override
    public Class<String> getType(Object propertyId) {
        if (items.isEmpty()) {
            loadItems();
        }
        return String.class;
    }

    @Override
    public int size() {
        if (items.isEmpty()) {
            loadItems();
        }
        return items.size();
    }

    @Override
    public boolean containsId(Object itemId) {
        if (items.isEmpty()) {
            loadItems();
        }
        return items.containsKey(itemId);
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
