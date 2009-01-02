/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import java.util.Collection;
import java.util.Vector;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.ui.AbstractField;
import com.itmill.toolkit.ui.BaseFieldFactory;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;

/**
 * Shows how to bind a bean to a table and make it editable.
 */
public class TableEditableBean extends CustomComponent {
    /**
     * Let's have a simple example bean.
     */
    public class MyBean {
        boolean selected;
        String text;

        public MyBean() {
            selected = false;
            text = "";
        }

        public boolean isSelected() {
            System.out.println("isSelected() called: " + selected);
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            System.out.println("setSelected1(" + selected + ") called.");
        }

        public String getText() {
            System.out.println("getText() called: " + text);
            return text;
        }

        public void setText(String text) {
            this.text = text;
            System.out.println("setText(" + text + ") called.");
        }
    };

    /**
     * Custom field factory that sets the fields as immediate for debugging
     * purposes. This is not normally necessary, unless you want to have some
     * interaction that requires it.
     */
    public class MyFieldFactory extends BaseFieldFactory {
        @Override
        public Field createField(Class type, Component uiContext) {
            // Let the BaseFieldFactory create the fields
            Field field = super.createField(type, uiContext);

            // ...and just set them as immediate
            ((AbstractField) field).setImmediate(true);

            return field;
        }
    }

    /**
     * This is a custom container that allows adding BeanItems inside it. The
     * BeanItem objects must be bound to a MyBean object. The item ID is an
     * Integer from 0 to 99.
     * 
     * Most of the interface methods are implemented with just dummy
     * implementations, as they are not needed in this example.
     */
    public class MyContainer implements Container {
        Item[] items;
        int current = 0;

        public MyContainer() {
            items = new Item[100]; // Yeah this is just a test
        }

        public boolean addContainerProperty(Object propertyId, Class type,
                Object defaultValue) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public Item addItem(Object itemId) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public Object addItem() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * This addItem method is specific for this container and allows adding
         * BeanItem objects. The BeanItems must be bound to MyBean objects.
         */
        public void addItem(BeanItem item) throws UnsupportedOperationException {
            items[current++] = item;
        }

        public boolean containsId(Object itemId) {
            if (itemId instanceof Integer) {
                int pos = ((Integer) itemId).intValue();
                if (pos >= 0 && pos < 100) {
                    return items[pos] != null;
                }
            }
            return false;
        }

        /**
         * The Table will call this method to get the property objects for the
         * columns. It uses the property objects to determine the data types of
         * the columns.
         */
        public Property getContainerProperty(Object itemId, Object propertyId) {
            if (itemId instanceof Integer) {
                int pos = ((Integer) itemId).intValue();
                if (pos >= 0 && pos < 100) {
                    Item item = items[pos];

                    // The BeanItem provides the property objects for the items.
                    return item.getItemProperty(propertyId);
                }
            }
            return null;
        }

        /** Table calls this to get the column names. */
        public Collection getContainerPropertyIds() {
            // This container can contain only BeanItems bound to MyBeans.
            Item item = new BeanItem(new MyBean());

            // The BeanItem knows how to get the property names from the bean.
            return item.getItemPropertyIds();
        }

        public Item getItem(Object itemId) {
            if (itemId instanceof Integer) {
                int pos = ((Integer) itemId).intValue();
                if (pos >= 0 && pos < 100) {
                    return items[pos];
                }
            }
            return null;
        }

        public Collection getItemIds() {
            Vector ids = new Vector();
            for (int i = 0; i < 100; i++) {
                ids.add(Integer.valueOf(i));
            }
            return ids;
        }

        public Class getType(Object propertyId) {
            return BeanItem.class;
        }

        public boolean removeAllItems() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public boolean removeContainerProperty(Object propertyId)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public boolean removeItem(Object itemId)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return current;
        }

    }

    TableEditableBean() {
        /* A layout needed for the example. */
        OrderedLayout layout = new OrderedLayout(
                OrderedLayout.ORIENTATION_VERTICAL);
        setCompositionRoot(layout);

        // Create a table. It is by default not editable.
        final Table table = new Table();
        layout.addComponent(table);
        table.setPageLength(8);

        // Use the custom container as the data source
        MyContainer myContainer = new MyContainer();
        table.setContainerDataSource(myContainer);

        // Add a few items in the table.
        for (int i = 0; i < 5; i++) {
            // Create the bean
            MyBean item = new MyBean();
            item.setText("MyBean " + i);

            // Have an Item that is bound to the bean
            BeanItem bitem = new BeanItem(item);

            // Add the item directly to the container using the custom addItem()
            // method. We could otherwise add it to the Table as well, but
            // the Container interface of Table does not allow adding items
            // as such, just item IDs.
            myContainer.addItem(bitem);
        }

        // Use custom field factory that sets the checkboxes in immediate mode.
        // This is just for debugging purposes and is not normally necessary.
        table.setFieldFactory(new MyFieldFactory());

        // Have a check box to switch the table between normal and editable
        // mode.
        final CheckBox switchEditable = new CheckBox("Editable");
        switchEditable.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                table.setEditable(((Boolean) event.getProperty().getValue())
                        .booleanValue());
            }
        });
        switchEditable.setImmediate(true);
        layout.addComponent(switchEditable);
    }
}
