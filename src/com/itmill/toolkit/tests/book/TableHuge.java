/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import java.util.Collection;
import java.util.Vector;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Container.Indexed;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.data.util.PropertysetItem;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Table;

public class TableHuge extends CustomComponent {

    /**
     * This is a virtual container that generates the items on the fly when
     * requested.
     */
    public class HugeContainer implements Container,Indexed {
        int numberofitems;
        
        public HugeContainer(int numberofitems) {
            this.numberofitems = numberofitems;
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
        }

        public boolean containsId(Object itemId) {
            if (itemId instanceof Integer) {
                int pos = ((Integer) itemId).intValue();
                if (pos >= 0 && pos < numberofitems)
                    return true;
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
                if (pos >= 0 && pos < numberofitems) {
                    return new ObjectProperty("This is the item "+pos+" in the huge table");
                }
            }
            return null;
        }

        /** Table calls this to get the column names. */
        public Collection getContainerPropertyIds() {
            Vector ids = new Vector();
            ids.add("id");
            return ids;
        }

        public Item getItem(Object itemId) {
            if (itemId instanceof Integer) {
                int pos = ((Integer)itemId).intValue();
                if (pos >= 0 && pos < numberofitems) {
                    Item item = new PropertysetItem();
                    item.addItemProperty("id", new ObjectProperty("This is the item "+pos+" in the huge table"));
                    return item;
                }
            }
            return null;
        }

        public Collection getItemIds() {
            System.out.println("We can't do this.");
            return null;
        }

        public Class getType(Object propertyId) {
            return PropertysetItem.class;
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
            return numberofitems;
        }

        public Object addItemAt(int index) throws UnsupportedOperationException {
            // TODO Auto-generated method stub
            return null;
        }

        public Item addItemAt(int index, Object newItemId)
                throws UnsupportedOperationException {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getIdByIndex(int index) {
            return Integer.valueOf(index);
        }

        public int indexOfId(Object itemId) {
            return ((Integer) itemId).intValue();
        }

        public Object addItemAfter(Object previousItemId)
                throws UnsupportedOperationException {
            // TODO Auto-generated method stub
            return null;
        }

        public Item addItemAfter(Object previousItemId, Object newItemId)
                throws UnsupportedOperationException {
            // TODO Auto-generated method stub
            return null;
        }

        public Object firstItemId() {
            return new Integer(0);
        }

        public boolean isFirstId(Object itemId) {
            return ((Integer) itemId).intValue() == 0;
        }

        public boolean isLastId(Object itemId) {
            return ((Integer) itemId).intValue() == (numberofitems-1);
        }

        public Object lastItemId() {
            return new Integer(numberofitems-1);
        }

        public Object nextItemId(Object itemId) {
            int pos = indexOfId(itemId);
            if (pos >= numberofitems-1)
                return null;
            return getIdByIndex(pos+1);
        }

        public Object prevItemId(Object itemId) {
            int pos = indexOfId(itemId);
            if (pos <= 0)
                return null;
            return getIdByIndex(pos-1);
        }
    }

    public TableHuge() {
        Table table = new Table("HUGE table, REALLY HUGE");
        table.setContainerDataSource(new HugeContainer(500000));
        table.setPageLength(20);
        
        setCompositionRoot(table);
    }
}
