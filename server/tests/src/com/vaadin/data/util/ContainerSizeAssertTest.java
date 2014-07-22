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

import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class ContainerSizeAssertTest {

    @Test(expected = AssertionError.class)
    public void testNegativeSizeAssert() {
        Table table = createAttachedTable();

        table.setContainerDataSource(createNegativeSizeContainer());
    }

    @Test
    public void testZeroSizeNoAssert() {
        Table table = createAttachedTable();

        table.setContainerDataSource(new IndexedContainer());
    }

    private Container createNegativeSizeContainer() {
        return new IndexedContainer() {
            @Override
            public int size() {
                return -1;
            }
        };
    }

    private Table createAttachedTable() {
        return new Table() {
            private boolean initialized = true;

            @Override
            public boolean isAttached() {
                // This returns false until the super constructor has finished
                return initialized;
            }
        };
    }
}
