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

package com.vaadin.data.util.filter;

import org.junit.Assert;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

public class LikeFilterTest extends AbstractFilterTest<Like> {

    protected Item item1 = new PropertysetItem();
    protected Item item2 = new PropertysetItem();
    protected Item item3 = new PropertysetItem();

    public void testLikeWithNulls() {

        Like filter = new Like("value", "a");

        item1.addItemProperty("value", new ObjectProperty<String>("a"));
        item2.addItemProperty("value", new ObjectProperty<String>("b"));
        item3.addItemProperty("value", new ObjectProperty<String>(null,
                String.class));

        Assert.assertTrue(filter.passesFilter(null, item1));
        Assert.assertFalse(filter.passesFilter(null, item2));
        Assert.assertFalse(filter.passesFilter(null, item3));

    }

}
