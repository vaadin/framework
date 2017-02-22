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
package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * Tests the layouting of Grid's details row when it contains a HorizontalLayout
 * with expand ratios.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class GridDetailsLayoutExpand extends SimpleGridUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = createGrid();
        grid.setSizeFull();

        addComponent(grid);

        grid.setDetailsGenerator(item -> {
            final HorizontalLayout detailsLayout = new HorizontalLayout();
            detailsLayout.setSpacing(false);
            detailsLayout.setSizeFull();
            detailsLayout.setHeightUndefined();

            // Label 1 first element of the detailsLayout, taking 200 pixels
            final Label lbl1 = new Label("test1");
            lbl1.setWidth("200px");
            detailsLayout.addComponent(lbl1);

            // layout2 second element of the detailsLayout, taking the rest
            // of the available space
            final HorizontalLayout layout2 = new HorizontalLayout();
            layout2.setSpacing(false);
            layout2.setSizeFull();
            layout2.setHeightUndefined();
            detailsLayout.addComponent(layout2);
            detailsLayout.setExpandRatio(layout2, 1);

            // 2 Labels added to the layout2
            final Label lbl2 = new Label("test2");
            lbl2.setWidth("100%");
            lbl2.setId("lbl2");
            layout2.addComponent(lbl2);

            final Label lbl3 = new Label("test3");
            lbl3.setWidth("100%");
            lbl3.setId("lbl3");
            layout2.addComponent(lbl3);

            return detailsLayout;
        });

        grid.addItemClickListener(event -> {
            final Person itemId = event.getItem();
            grid.setDetailsVisible(itemId, !grid.isDetailsVisible(itemId));
        });

    }

    @Override
    protected Integer getTicketNumber() {
        return 18821;
    }

    @Override
    protected String getTestDescription() {
        return "Details row must be the same after opening another details row";
    }
}
