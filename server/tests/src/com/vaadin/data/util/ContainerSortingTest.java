package com.vaadin.data.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.tests.util.TestUtil;

public class ContainerSortingTest extends TestCase {

    private static final String ITEM_DATA_MINUS2_NULL = "Data -2 null";
    private static final String ITEM_DATA_MINUS2 = "Data -2";
    private static final String ITEM_DATA_MINUS1 = "Data -1";
    private static final String ITEM_DATA_MINUS1_NULL = "Data -1 null";
    private static final String ITEM_ANOTHER_NULL = "Another null";
    private static final String ITEM_STRING_2 = "String 2";
    private static final String ITEM_STRING_NULL2 = "String null";
    private static final String ITEM_STRING_1 = "String 1";

    private static final String PROPERTY_INTEGER_NULL2 = "integer-null";
    private static final String PROPERTY_INTEGER_NOT_NULL = "integer-not-null";
    private static final String PROPERTY_STRING_NULL = "string-null";
    private static final String PROPERTY_STRING_ID = "string-not-null";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testEmptyFilteredIndexedContainer() {
        IndexedContainer ic = new IndexedContainer();

        addProperties(ic);
        populate(ic);

        ic.addContainerFilter(PROPERTY_STRING_ID, "aasdfasdfasdf", true, false);
        ic.sort(new Object[] { PROPERTY_STRING_ID }, new boolean[] { true });

    }

    public void testFilteredIndexedContainer() {
        IndexedContainer ic = new IndexedContainer();

        addProperties(ic);
        populate(ic);

        ic.addContainerFilter(PROPERTY_STRING_ID, "a", true, false);
        ic.sort(new Object[] { PROPERTY_STRING_ID }, new boolean[] { true });
        verifyOrder(ic,
                new String[] { ITEM_ANOTHER_NULL, ITEM_DATA_MINUS1,
                        ITEM_DATA_MINUS1_NULL, ITEM_DATA_MINUS2,
                        ITEM_DATA_MINUS2_NULL, });
    }

    public void testIndexedContainer() {
        IndexedContainer ic = new IndexedContainer();

        addProperties(ic);
        populate(ic);

        ic.sort(new Object[] { PROPERTY_STRING_ID }, new boolean[] { true });
        verifyOrder(ic, new String[] { ITEM_ANOTHER_NULL, ITEM_DATA_MINUS1,
                ITEM_DATA_MINUS1_NULL, ITEM_DATA_MINUS2, ITEM_DATA_MINUS2_NULL,
                ITEM_STRING_1, ITEM_STRING_2, ITEM_STRING_NULL2 });

        ic.sort(new Object[] { PROPERTY_INTEGER_NOT_NULL,
                PROPERTY_INTEGER_NULL2, PROPERTY_STRING_ID }, new boolean[] {
                true, false, true });
        verifyOrder(ic, new String[] { ITEM_DATA_MINUS2, ITEM_DATA_MINUS2_NULL,
                ITEM_DATA_MINUS1, ITEM_DATA_MINUS1_NULL, ITEM_ANOTHER_NULL,
                ITEM_STRING_NULL2, ITEM_STRING_1, ITEM_STRING_2 });

        ic.sort(new Object[] { PROPERTY_INTEGER_NOT_NULL,
                PROPERTY_INTEGER_NULL2, PROPERTY_STRING_ID }, new boolean[] {
                true, true, true });
        verifyOrder(ic, new String[] { ITEM_DATA_MINUS2_NULL, ITEM_DATA_MINUS2,
                ITEM_DATA_MINUS1_NULL, ITEM_DATA_MINUS1, ITEM_ANOTHER_NULL,
                ITEM_STRING_NULL2, ITEM_STRING_1, ITEM_STRING_2 });

    }

    public void testHierarchicalContainer() {
        HierarchicalContainer hc = new HierarchicalContainer();
        populateContainer(hc);
        hc.sort(new Object[] { "name" }, new boolean[] { true });
        verifyOrder(hc, new String[] { "Audi", "C++", "Call of Duty", "Cars",
                "English", "Fallout", "Finnish", "Ford", "Games", "Java",
                "Might and Magic", "Natural languages", "PHP",
                "Programming languages", "Python", "Red Alert", "Swedish",
                "Toyota", "Volvo" });
        TestUtil.assertArrays(
                hc.rootItemIds().toArray(),
                new Integer[] { nameToId.get("Cars"), nameToId.get("Games"),
                        nameToId.get("Natural languages"),
                        nameToId.get("Programming languages") });
        TestUtil.assertArrays(
                hc.getChildren(nameToId.get("Games")).toArray(),
                new Integer[] { nameToId.get("Call of Duty"),
                        nameToId.get("Fallout"),
                        nameToId.get("Might and Magic"),
                        nameToId.get("Red Alert") });
    }

    private static void populateContainer(HierarchicalContainer container) {
        container.addContainerProperty("name", String.class, null);

        addItem(container, "Games", null);
        addItem(container, "Call of Duty", "Games");
        addItem(container, "Might and Magic", "Games");
        addItem(container, "Fallout", "Games");
        addItem(container, "Red Alert", "Games");

        addItem(container, "Cars", null);
        addItem(container, "Toyota", "Cars");
        addItem(container, "Volvo", "Cars");
        addItem(container, "Audi", "Cars");
        addItem(container, "Ford", "Cars");

        addItem(container, "Natural languages", null);
        addItem(container, "Swedish", "Natural languages");
        addItem(container, "English", "Natural languages");
        addItem(container, "Finnish", "Natural languages");

        addItem(container, "Programming languages", null);
        addItem(container, "C++", "Programming languages");
        addItem(container, "PHP", "Programming languages");
        addItem(container, "Java", "Programming languages");
        addItem(container, "Python", "Programming languages");

    }

    private static int index = 0;
    private static Map<String, Integer> nameToId = new HashMap<String, Integer>();
    private static Map<Integer, String> idToName = new HashMap<Integer, String>();

    public static void addItem(IndexedContainer container, String string,
            String parent) {
        nameToId.put(string, index);
        idToName.put(index, string);

        Item item = container.addItem(index);
        item.getItemProperty("name").setValue(string);

        if (parent != null && container instanceof HierarchicalContainer) {
            ((HierarchicalContainer) container).setParent(index,
                    nameToId.get(parent));
        }

        index++;
    }

    private void verifyOrder(Container.Sortable ic, Object[] idOrder) {
        int size = ic.size();
        Object[] actual = new Object[size];
        Iterator<?> i = ic.getItemIds().iterator();
        int index = 0;
        while (i.hasNext()) {
            Object o = i.next();
            if (o.getClass() == Integer.class
                    && idOrder[index].getClass() == String.class) {
                o = idToName.get(o);
            }
            actual[index++] = o;
        }

        TestUtil.assertArrays(actual, idOrder);

    }

    private void populate(IndexedContainer ic) {
        addItem(ic, ITEM_STRING_1, ITEM_STRING_1, 1, 1);
        addItem(ic, ITEM_STRING_NULL2, null, 0, null);
        addItem(ic, ITEM_STRING_2, ITEM_STRING_2, 2, 2);
        addItem(ic, ITEM_ANOTHER_NULL, null, 0, null);
        addItem(ic, ITEM_DATA_MINUS1, ITEM_DATA_MINUS1, -1, -1);
        addItem(ic, ITEM_DATA_MINUS1_NULL, null, -1, null);
        addItem(ic, ITEM_DATA_MINUS2, ITEM_DATA_MINUS2, -2, -2);
        addItem(ic, ITEM_DATA_MINUS2_NULL, null, -2, null);
    }

    private Item addItem(Container ic, String id, String string_null,
            int integer, Integer integer_null) {
        Item i = ic.addItem(id);
        i.getItemProperty(PROPERTY_STRING_ID).setValue(id);
        i.getItemProperty(PROPERTY_STRING_NULL).setValue(string_null);
        i.getItemProperty(PROPERTY_INTEGER_NOT_NULL).setValue(integer);
        i.getItemProperty(PROPERTY_INTEGER_NULL2).setValue(integer_null);

        return i;
    }

    private void addProperties(IndexedContainer ic) {
        ic.addContainerProperty("id", String.class, null);
        ic.addContainerProperty(PROPERTY_STRING_ID, String.class, "");
        ic.addContainerProperty(PROPERTY_STRING_NULL, String.class, null);
        ic.addContainerProperty(PROPERTY_INTEGER_NULL2, Integer.class, null);
        ic.addContainerProperty(PROPERTY_INTEGER_NOT_NULL, Integer.class, 0);
        ic.addContainerProperty("comparable-null", Integer.class, 0);
    }

    public class MyObject implements Comparable<MyObject> {
        private String data;

        @Override
        public int compareTo(MyObject o) {
            if (o == null) {
                return 1;
            }

            if (o.data == null) {
                return data == null ? 0 : 1;
            } else if (data == null) {
                return -1;
            } else {
                return data.compareTo(o.data);
            }
        }
    }

}
