/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.ui;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class AbstractSelectTest {

    @Test
    public void addItemsStrings() {
        NativeSelect ns = new NativeSelect();
        ns.addItems("Foo", "bar", "baz");
        Assert.assertEquals(3, ns.size());
        Assert.assertArrayEquals(new Object[] { "Foo", "bar", "baz" }, ns
                .getItemIds().toArray());
    }

    @Test
    public void addItemsObjects() {
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();

        NativeSelect ns = new NativeSelect();
        ns.addItems(o1, o2, o3);
        Assert.assertEquals(3, ns.size());
        Assert.assertArrayEquals(new Object[] { o1, o2, o3 }, ns.getItemIds()
                .toArray());
    }

    @Test
    public void addItemsStringList() {
        ArrayList<String> itemIds = new ArrayList<String>();
        itemIds.add("foo");
        itemIds.add("bar");
        itemIds.add("baz");
        NativeSelect ns = new NativeSelect();
        ns.addItems(itemIds);
        Assert.assertEquals(3, ns.size());
        Assert.assertArrayEquals(new Object[] { "foo", "bar", "baz" }, ns
                .getItemIds().toArray());
    }

    @Test
    public void addItemsObjectList() {
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        ArrayList<Object> itemIds = new ArrayList<Object>();
        itemIds.add(o1);
        itemIds.add(o2);
        itemIds.add(o3);
        NativeSelect ns = new NativeSelect();
        ns.addItems(itemIds);
        Assert.assertEquals(3, ns.size());
        Assert.assertArrayEquals(new Object[] { o1, o2, o3 }, ns.getItemIds()
                .toArray());

    }
}
