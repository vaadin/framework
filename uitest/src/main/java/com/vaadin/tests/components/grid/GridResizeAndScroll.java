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
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.VerticalLayout;

public class GridResizeAndScroll extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        addComponent(content);

        final Grid g = new Grid();
        content.setHeight("500px");
        content.addComponent(g);

        IndexedContainer cont = new IndexedContainer();
        for (int j = 0; j < 3; j++) {
            cont.addContainerProperty("" + j, String.class, "");
        }

        for (int k = 0; k < 50; k++) {
            Item addItem = cont.addItem(k);
            for (int j = 0; j < 3; j++) {
                addItem.getItemProperty("" + j).setValue("cell " + k + " " + j);
            }
        }
        g.setContainerDataSource(cont);
        g.setSizeFull();

        g.setSelectionMode(SelectionMode.MULTI);

        g.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
                if (g.getSelectedRows().isEmpty()) {
                    g.setHeight("100%");
                } else {
                    g.setHeight("50%");
                }
            }
        });
    }

}
