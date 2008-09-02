package com.itmill.toolkit.tests.tickets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.data.util.PropertysetItem;

/**
 * @author Efecte R&D
 * @version $Revision$, $Date$
 */
public class Ticket1506_TestContainer implements Container {
    private Map items = new HashMap();
    public static final String ITEM_1_ID = "1";
    public static final String ITEM_2_ID = "2";
    public static final String PROPERTY_1_ID = "property 1";
    public static final String PROPERTY_2_ID = "property 2";

    private void loadItems() {
        final PropertysetItem item1 = new PropertysetItem();
        item1.addItemProperty(PROPERTY_1_ID, new ObjectProperty("value 1",
                String.class));
        item1.addItemProperty(PROPERTY_2_ID, new ObjectProperty("name 1",
                String.class));
        this.items.put(ITEM_1_ID, item1);

        final PropertysetItem item2 = new PropertysetItem();
        item2.addItemProperty(PROPERTY_1_ID, new ObjectProperty("value 2",
                String.class));
        item2.addItemProperty(PROPERTY_2_ID, new ObjectProperty("name 2",
                String.class));
        this.items.put(ITEM_2_ID, item2);
    }

    public Item getItem(Object itemId) {
        if (items.isEmpty()) {
            loadItems();
        }
        return (Item) items.get(itemId);
    }

    public Collection getContainerPropertyIds() {
        if (items.isEmpty()) {
            loadItems();
        }
        ArrayList a = new ArrayList();
        a.add(PROPERTY_1_ID);
        a.add(PROPERTY_2_ID);
        return a;
    }

    public Collection getItemIds() {
        if (items.isEmpty()) {
            loadItems();
        }
        ArrayList a = new ArrayList();
        a.add(ITEM_1_ID);
        a.add(ITEM_2_ID);
        return a;
    }

    public Property getContainerProperty(Object itemId, Object propertyId) {
        if (items.isEmpty()) {
            loadItems();
        }
        Item item = (Item) items.get(itemId);
        if (item != null) {
            return item.getItemProperty(propertyId);
        }
        return null;
    }

    public Class getType(Object propertyId) {
        if (items.isEmpty()) {
            loadItems();
        }
        return String.class;
    }

    public int size() {
        if (items.isEmpty()) {
            loadItems();
        }
        return items.size();
    }

    public boolean containsId(Object itemId) {
        if (items.isEmpty()) {
            loadItems();
        }
        return items.containsKey(itemId);
    }

    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean addContainerProperty(Object propertyId, Class type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
