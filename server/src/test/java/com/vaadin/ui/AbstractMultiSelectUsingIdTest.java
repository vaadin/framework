/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.ListDataProvider;

public class AbstractMultiSelectUsingIdTest {

    public TwinColSelect<ItemWithId> selectToTest;

    public static class ItemWithId {
        private int id;

        public ItemWithId() {
        }

        public ItemWithId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Before
    public void setUp() {
        selectToTest = new TwinColSelect<>();
        List<ItemWithId> items = new ArrayList<>();
        items.add(new ItemWithId(3));
        items.add(new ItemWithId(2));
        items.add(new ItemWithId(1));
        items.add(new ItemWithId(8));
        items.add(new ItemWithId(7));
        items.add(new ItemWithId(4));
        items.add(new ItemWithId(6));
        ListDataProvider<ItemWithId> dataProvider = new ListDataProvider<ItemWithId>(
                items) {
            @Override
            public Object getId(ItemWithId item) {
                return item.getId();
            }
        };
        selectToTest.setDataProvider(dataProvider);

    }

    @Test
    public void selectTwiceSelectsOnce() {
        selectToTest.select(new ItemWithId(1));
        assertSelectionOrder(1);
        selectToTest.select(new ItemWithId(1));
        assertSelectionOrder(1);
    }

    @Test
    public void deselectWorks() {
        selectToTest.select(new ItemWithId(1));
        selectToTest.deselect(new ItemWithId(1));
        assertSelectionOrder();
    }

    private void assertSelectionOrder(Integer... selectionOrder) {
        List<Integer> asList = Arrays.asList(selectionOrder);
        assertEquals(asList, selectToTest.getSelectedItems().stream()
                .map(ItemWithId::getId).collect(Collectors.toList()));
    }

}
