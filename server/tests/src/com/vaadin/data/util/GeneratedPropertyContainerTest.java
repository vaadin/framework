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

import org.junit.Test;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;

public class GeneratedPropertyContainerTest {

    @Test
    public void testSimpleGeneratedProperty() {
        GeneratedPropertyContainer container = new GeneratedPropertyContainer(
                createContainer());

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

    private Indexed createContainer() {
        return new IndexedContainer();
    }

}
