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
import com.vaadin.v7.data.util.BeanItem;

public class NotFilterTest extends AbstractFilterTestBase<Not> {

    protected Item item1 = new BeanItem<Integer>(1);
    protected Item item2 = new BeanItem<Integer>(2);

    @Test
    public void testNot() {
        Filter origFilter = new SameItemFilter(item1);
        Filter filter = new Not(origFilter);

        Assert.assertTrue(origFilter.passesFilter(null, item1));
        Assert.assertFalse(origFilter.passesFilter(null, item2));
        Assert.assertFalse(filter.passesFilter(null, item1));
        Assert.assertTrue(filter.passesFilter(null, item2));
    }

    @Test
    public void testANotAppliesToProperty() {
        Filter filterA = new Not(new SameItemFilter(item1, "a"));
        Filter filterB = new Not(new SameItemFilter(item1, "b"));

        Assert.assertTrue(filterA.appliesToProperty("a"));
        Assert.assertFalse(filterA.appliesToProperty("b"));
        Assert.assertFalse(filterB.appliesToProperty("a"));
        Assert.assertTrue(filterB.appliesToProperty("b"));
    }

    @Test
    public void testNotEqualsHashCode() {
        Filter origFilter = new SameItemFilter(item1);
        Filter filter1 = new Not(origFilter);
        Filter filter1b = new Not(new SameItemFilter(item1));
        Filter filter2 = new Not(new SameItemFilter(item2));

        // equals()
        Assert.assertEquals(filter1, filter1b);
        Assert.assertFalse(filter1.equals(filter2));
        Assert.assertFalse(filter1.equals(origFilter));
        Assert.assertFalse(filter1.equals(new And()));

        // hashCode()
        Assert.assertEquals(filter1.hashCode(), filter1b.hashCode());
    }

}
