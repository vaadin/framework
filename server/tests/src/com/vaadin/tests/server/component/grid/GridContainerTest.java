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

import org.junit.Test;

import com.vaadin.data.util.IndexedContainer;

public class GridContainerTest {

    @Test
    public void testSetContainerTwice() throws Exception {

        TestGrid grid = new TestGrid();

        grid.setContainerDataSource(createContainer());

        // Simulate initial response to ensure "lazy" state changes are done
        // before resetting the datasource
        grid.beforeClientResponse(true);
        grid.getDataProvider().beforeClientResponse(true);

        grid.setContainerDataSource(createContainer());
    }

    @SuppressWarnings("unchecked")
    private IndexedContainer createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("x", String.class, null);
        container.addItem(0).getItemProperty("x").setValue("y");
        return container;
    }
}
