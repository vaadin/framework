package com.vaadin.data.util.filter;

import junit.framework.TestCase;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

public abstract class AbstractFilterTestBase<FILTERTYPE extends Filter> extends
        TestCase {

    protected static final String PROPERTY1 = "property1";
    protected static final String PROPERTY2 = "property2";

    protected static class TestItem<T1, T2> extends PropertysetItem {

        public TestItem(T1 value1, T2 value2) {
            addItemProperty(PROPERTY1, new ObjectProperty<T1>(value1));
            addItemProperty(PROPERTY2, new ObjectProperty<T2>(value2));
        }
    }

    protected static class NullProperty implements Property<String> {

        @Override
        public String getValue() {
            return null;
        }

        @Override
        public void setValue(String newValue) throws ReadOnlyException {
            throw new ReadOnlyException();
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }

        @Override
        public void setReadOnly(boolean newStatus) {
            // do nothing
        }

    }

    public static class SameItemFilter implements Filter {

        private final Item item;
        private final Object propertyId;

        public SameItemFilter(Item item) {
            this(item, "");
        }

        public SameItemFilter(Item item, Object propertyId) {
            this.item = item;
            this.propertyId = propertyId;
        }

        @Override
        public boolean passesFilter(Object itemId, Item item)
                throws UnsupportedOperationException {
            return this.item == item;
        }

        @Override
        public boolean appliesToProperty(Object propertyId) {
            return this.propertyId != null ? this.propertyId.equals(propertyId)
                    : true;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !getClass().equals(obj.getClass())) {
                return false;
            }
            SameItemFilter other = (SameItemFilter) obj;
            return item == other.item
                    && (propertyId == null ? other.propertyId == null
                            : propertyId.equals(other.propertyId));
        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }
    }

}
