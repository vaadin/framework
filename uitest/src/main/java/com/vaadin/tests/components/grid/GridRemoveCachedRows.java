/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

@SuppressWarnings("serial")
public class GridRemoveCachedRows extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        List<String> columnIds = Arrays.asList("Hello", "this", "are",
                "multiple", "columns", "plus", "these", "resemble", "a",
                "group", "here", "no", "more");

        final Grid grid = new Grid(new CustomIndexedContainer());
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(20);
        for (String columnId : columnIds) {
            ((CustomIndexedContainer) grid.getContainerDataSource())
                    .addContainerProperty(columnId, String.class, "");
        }

        // add a lot of rows to make sure that at least some of them are cached
        for (int i = 0; i < 400; i++) {
            grid.addRow(columnIds.toArray());
        }

        Button removeRowsButton = new Button("Remove a cached row");
        removeRowsButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // remove a range of items starting from the visible items and
                // ending into the cached items
                ((CustomIndexedContainer) grid.getContainerDataSource())
                        .removeItemRange(10, 300);
            }
        });

        addComponents(grid, removeRowsButton);
    }

    @Override
    protected String getTestDescription() {
        return "Test what happens when item range (starting from visible and ending to cached items) is removed from Grid.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8840;
    }

    /**
     * Customised IndexedContainer for removing item range
     */
    public class CustomIndexedContainer extends IndexedContainer {
        public void removeItemRange(int startIndex, int count) {
            Object firstItem = null;

            for (int index = startIndex; index < startIndex + count; index++) {
                Object itemId = getIdByIndex(startIndex);
                getAllItemIds().remove(itemId);
                if (firstItem == null) {
                    firstItem = itemId;
                }
            }
            fireItemsRemoved(startIndex, firstItem, count);
        }
    }
}
