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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;

public class IsNullFilterTest extends AbstractFilterTestBase<IsNull> {

    @Test
    public void testIsNull() {
        Item item1 = new PropertysetItem();
        item1.addItemProperty("a",
                new ObjectProperty<String>(null, String.class));
        item1.addItemProperty("b",
                new ObjectProperty<String>("b", String.class));
        Item item2 = new PropertysetItem();
        item2.addItemProperty("a",
                new ObjectProperty<String>("a", String.class));
        item2.addItemProperty("b",
                new ObjectProperty<String>(null, String.class));

        Filter filter1 = new IsNull("a");
        Filter filter2 = new IsNull("b");

        Assert.assertTrue(filter1.passesFilter(null, item1));
        Assert.assertFalse(filter1.passesFilter(null, item2));
        Assert.assertFalse(filter2.passesFilter(null, item1));
        Assert.assertTrue(filter2.passesFilter(null, item2));
    }

    @Test
    public void testIsNullAppliesToProperty() {
        Filter filterA = new IsNull("a");
        Filter filterB = new IsNull("b");

        Assert.assertTrue(filterA.appliesToProperty("a"));
        Assert.assertFalse(filterA.appliesToProperty("b"));
        Assert.assertFalse(filterB.appliesToProperty("a"));
        Assert.assertTrue(filterB.appliesToProperty("b"));
    }

    @Test
    public void testIsNullEqualsHashCode() {
        Filter filter1 = new IsNull("a");
        Filter filter1b = new IsNull("a");
        Filter filter2 = new IsNull("b");

        // equals()
        Assert.assertEquals(filter1, filter1b);
        Assert.assertFalse(filter1.equals(filter2));
        Assert.assertFalse(filter1.equals(new And()));

        // hashCode()
        Assert.assertEquals(filter1.hashCode(), filter1b.hashCode());
    }

}
