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
package com.vaadin.tests.components.grid;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class GridScrolling extends AbstractTestUI {

    private Grid grid;

    private IndexedContainer ds;

    @Override
    @SuppressWarnings("unchecked")
    protected void setup(VaadinRequest request) {
        // Build data source
        ds = new IndexedContainer();

        for (int col = 0; col < 5; col++) {
            ds.addContainerProperty("col" + col, String.class, "");
        }

        for (int row = 0; row < 65536; row++) {
            Item item = ds.addItem(Integer.valueOf(row));
            for (int col = 0; col < 5; col++) {
                item.getItemProperty("col" + col).setValue(
                        "(" + row + ", " + col + ")");
            }
        }

        grid = new Grid(ds);

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(grid);
        hl.setMargin(true);
        hl.setSpacing(true);

        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);

        // Add scroll buttons
        Button scrollUpButton = new Button("Top", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                grid.scrollToStart();
            }
        });
        scrollUpButton.setSizeFull();
        vl.addComponent(scrollUpButton);

        for (int i = 1; i < 7; ++i) {
            final int row = (ds.size() / 7) * i;
            Button scrollButton = new Button("Scroll to row " + row,
                    new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            grid.scrollTo(Integer.valueOf(row),
                                    ScrollDestination.MIDDLE);
                        }
                    });
            scrollButton.setSizeFull();
            vl.addComponent(scrollButton);
        }

        Button scrollDownButton = new Button("Bottom", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                grid.scrollToEnd();
            }
        });
        scrollDownButton.setSizeFull();
        vl.addComponent(scrollDownButton);

        hl.addComponent(vl);
        addComponent(hl);
    }

    @Override
    protected String getTestDescription() {
        return "Test Grid programmatic scrolling features";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13327;
    }

}
