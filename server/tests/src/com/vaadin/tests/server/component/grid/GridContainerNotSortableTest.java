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

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.ui.Grid;

public class GridContainerNotSortableTest {

    @Test
    public void testGridWithNotSortableContainer() {
        new Grid(new AbstractInMemoryContainer<Object, Object, Item>() {

            @Override
            public Collection<?> getContainerPropertyIds() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public Property getContainerProperty(Object itemId,
                    Object propertyId) {
                return null;
            }

            @Override
            public Class<?> getType(Object propertyId) {
                return null;
            }

            @Override
            protected Item getUnfilteredItem(Object itemId) {
                return null;
            }
        });
    }
}
