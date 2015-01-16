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
package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.data.RpcDataProviderExtension.DataProviderKeyMapper;
import com.vaadin.data.util.IndexedContainer;

public class DataProviderExtension {
    private RpcDataProviderExtension dataProvider;
    private DataProviderKeyMapper keyMapper;
    private Container.Indexed container;

    private static final Object ITEM_ID1 = "itemid1";
    private static final Object ITEM_ID2 = "itemid2";
    private static final Object ITEM_ID3 = "itemid3";

    private static final Object PROPERTY_ID1_STRING = "property1";

    @Before
    public void setup() {
        container = new IndexedContainer();
        populate(container);

        dataProvider = new RpcDataProviderExtension(container);
        keyMapper = dataProvider.getKeyMapper();
    }

    private static void populate(Indexed container) {
        container.addContainerProperty(PROPERTY_ID1_STRING, String.class, "");
        for (Object itemId : Arrays.asList(ITEM_ID1, ITEM_ID2, ITEM_ID3)) {
            final Item item = container.addItem(itemId);
            @SuppressWarnings("unchecked")
            final Property<String> stringProperty = item
                    .getItemProperty(PROPERTY_ID1_STRING);
            stringProperty.setValue(itemId.toString());
        }
    }

    @Test
    public void pinBasics() {
        assertFalse("itemId1 should not start as pinned",
                keyMapper.isPinned(ITEM_ID2));

        keyMapper.pin(ITEM_ID1);
        assertTrue("itemId1 should now be pinned", keyMapper.isPinned(ITEM_ID1));

        keyMapper.unpin(ITEM_ID1);
        assertFalse("itemId1 should not be pinned anymore",
                keyMapper.isPinned(ITEM_ID2));
    }

    @Test(expected = IllegalStateException.class)
    public void doublePinning() {
        keyMapper.pin(ITEM_ID1);
        keyMapper.pin(ITEM_ID1);
    }

    @Test(expected = IllegalStateException.class)
    public void nonexistentUnpin() {
        keyMapper.unpin(ITEM_ID1);
    }
}
