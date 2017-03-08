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
package com.vaadin.v7.tests.server.component.table;

import org.junit.Test;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

public class TableGeneratorTest {
    public static Table createTableWithDefaultContainer(int properties,
            int items) {
        Table t = new Table();

        for (int i = 0; i < properties; i++) {
            t.addContainerProperty("Property " + i, String.class, null);
        }

        for (int j = 0; j < items; j++) {
            Item item = t.addItem("Item " + j);
            for (int i = 0; i < properties; i++) {
                item.getItemProperty("Property " + i)
                        .setValue("Item " + j + "/Property " + i);
            }
        }

        return t;
    }

    @Test
    public void testTableGenerator() {
        Table t = createTableWithDefaultContainer(1, 1);
        junit.framework.Assert.assertEquals(t.size(), 1);
        junit.framework.Assert.assertEquals(t.getContainerPropertyIds().size(),
                1);

        t = createTableWithDefaultContainer(100, 50);
        junit.framework.Assert.assertEquals(t.size(), 50);
        junit.framework.Assert.assertEquals(t.getContainerPropertyIds().size(),
                100);

    }

}
