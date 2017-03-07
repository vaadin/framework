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
package com.vaadin.v7.data.util.filter;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;

import junit.framework.TestCase;

public abstract class AbstractFilterTestBase<FILTERTYPE extends Filter>
        extends TestCase {

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
