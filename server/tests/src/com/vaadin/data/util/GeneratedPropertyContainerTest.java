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
package com.vaadin.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.ui.components.grid.sort.SortOrder;

public class GeneratedPropertyContainerTest {

    GeneratedPropertyContainer container;

    @Before
    public void setUp() {
        container = new GeneratedPropertyContainer(createContainer());
    }

    @Test
    public void testSimpleGeneratedProperty() {
        container.addGeneratedProperty("hello",
                new PropertyValueGenerator<String>() {

                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return "Hello World!";
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        Object itemId = container.addItem();
        assertEquals("Expected value not in item.", container.getItem(itemId)
                .getItemProperty("hello").getValue(), "Hello World!");
    }

    @Test
    public void testSortableProperties() {
        container.addGeneratedProperty("baz",
                new PropertyValueGenerator<String>() {

                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return item.getItemProperty("foo").getValue() + " "
                                + item.getItemProperty("bar").getValue();
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }

                    @Override
                    public SortOrder[] getSortProperties(SortOrder order) {
                        SortOrder[] sortOrder = new SortOrder[1];
                        sortOrder[0] = new SortOrder("bar", order
                                .getDirection());
                        return sortOrder;
                    }
                });

        container.sort(new Object[] { "baz" }, new boolean[] { true });
        assertEquals("foo 0", container.getItem(container.getIdByIndex(0))
                .getItemProperty("baz").getValue());

        container.sort(new Object[] { "baz" }, new boolean[] { false });
        assertEquals("foo 9", container.getItem(container.getIdByIndex(0))
                .getItemProperty("baz").getValue());
    }

    @Test
    public void testOverrideSortableProperties() {

        assertTrue(container.getSortableContainerPropertyIds().contains("bar"));

        container.addGeneratedProperty("bar",
                new PropertyValueGenerator<String>() {

                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return item.getItemProperty("foo").getValue() + " "
                                + item.getItemProperty("bar").getValue();
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        assertFalse(container.getSortableContainerPropertyIds().contains("bar"));
    }

    private Indexed createContainer() {
        Indexed container = new IndexedContainer();
        container.addContainerProperty("foo", String.class, "foo");
        container.addContainerProperty("bar", Integer.class, 0);

        for (int i = 0; i < 10; ++i) {
            Object itemId = container.addItem();
            Item item = container.getItem(itemId);
            item.getItemProperty("foo").setValue("foo");
            item.getItemProperty("bar").setValue(i);
        }

        return container;
    }

}
