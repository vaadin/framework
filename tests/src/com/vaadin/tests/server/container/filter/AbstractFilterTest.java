package com.vaadin.tests.server.container.filter;

import junit.framework.TestCase;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

public abstract class AbstractFilterTest<FILTERTYPE extends Filter> extends
        TestCase {

    protected static final String PROPERTY1 = "property1";
    protected static final String PROPERTY2 = "property2";

    protected static class TestItem<T1, T2> extends PropertysetItem {

        public TestItem(T1 value1, T2 value2) {
            addItemProperty(PROPERTY1, new ObjectProperty<T1>(value1));
            addItemProperty(PROPERTY2, new ObjectProperty<T2>(value2));
        }
    }

    protected static class NullProperty implements Property {

        public Object getValue() {
            return null;
        }

        public void setValue(Object newValue) throws ReadOnlyException,
                ConversionException {
            throw new ReadOnlyException();
        }

        public Class<?> getType() {
            return String.class;
        }

        public boolean isReadOnly() {
            return true;
        }

        public void setReadOnly(boolean newStatus) {
            // do nothing
        }

    }

}
