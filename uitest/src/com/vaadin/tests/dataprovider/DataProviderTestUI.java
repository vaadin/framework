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
package com.vaadin.tests.dataprovider;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.communication.data.DataGenerator;
import com.vaadin.server.communication.data.RpcDataProviderExtension;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.AbstractComponent;

import elemental.json.JsonObject;

@Widgetset(TestingWidgetSet.NAME)
public class DataProviderTestUI extends AbstractTestUIWithLog {

    public static class DataProviderTester extends AbstractComponent {

        private RpcDataProviderExtension dataProvider;

        public DataProviderTester(Container c) {
            dataProvider = new RpcDataProviderExtension(c);
            dataProvider.extend(this);

            /* dataProvider.addDataGenerator(new DataGenerator() {

                @Override
                public void generateData(Object itemId, Item item,
                        JsonObject rowData) {
                    rowData.put("key", itemId.toString());
                }

                @Override
                public void destroyData(Object itemId) {
                }
            }); */
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Container c = new AbstractContainer() {

            Map<Object, Item> items = new LinkedHashMap<Object, Item>();

            @Override
            public Item getItem(Object itemId) {
                return items.get(itemId);
            }

            @Override
            public Collection<?> getContainerPropertyIds() {
                return Collections.emptySet();
            }

            @Override
            public Collection<?> getItemIds() {
                return items.keySet();
            }

            @Override
            public Property getContainerProperty(Object itemId,
                    Object propertyId) {
                throw new UnsupportedOperationException(
                        "No properties in this container.");
            }

            @Override
            public Class<?> getType(Object propertyId) {
                throw new UnsupportedOperationException(
                        "No properties in this container.");
            }

            @Override
            public int size() {
                return items.size();
            }

            @Override
            public boolean containsId(Object itemId) {
                return items.containsKey(itemId);
            }

            @Override
            public Item addItem(Object itemId)
                    throws UnsupportedOperationException {
                final Item value = new Item() {

                    @Override
                    public Property getItemProperty(Object id) {
                        throw new UnsupportedOperationException(
                                "No properties in this container.");
                    }

                    @Override
                    public Collection<?> getItemPropertyIds() {
                        return Collections.emptySet();
                    }

                    @Override
                    public boolean addItemProperty(Object id, Property property)
                            throws UnsupportedOperationException {
                        throw new UnsupportedOperationException(
                                "No properties in this container.");
                    }

                    @Override
                    public boolean removeItemProperty(Object id)
                            throws UnsupportedOperationException {
                        throw new UnsupportedOperationException(
                                "No properties in this container.");
                    }
                };

                items.put(itemId, value);
                fireItemSetChange();
                return value;
            }

            @Override
            public Object addItem() throws UnsupportedOperationException {
                throw new UnsupportedOperationException(
                        "No id generation in this container.");
            }

            @Override
            public boolean removeItem(Object itemId)
                    throws UnsupportedOperationException {
                throw new UnsupportedOperationException(
                        "No removing from this container.");
            }

            @Override
            public boolean addContainerProperty(Object propertyId,
                    Class<?> type, Object defaultValue)
                    throws UnsupportedOperationException {
                throw new UnsupportedOperationException(
                        "No properties in this container.");
            }

            @Override
            public boolean removeContainerProperty(Object propertyId)
                    throws UnsupportedOperationException {
                throw new UnsupportedOperationException(
                        "No properties in this container.");
            }

            @Override
            public boolean removeAllItems()
                    throws UnsupportedOperationException {
                throw new UnsupportedOperationException(
                        "No removing from this container.");
            }
        };

        final DataProviderTester tester = new DataProviderTester(c);
        tester.setSizeFull();
        addComponent(tester);

        for (Integer i = 0; i < 1000; ++i) {
            c.addItem(i);
        }
    }
}
