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
package com.vaadin.tests.layouts.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridSpanEmptyColumns extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout gridLayout = new GridLayout(3, 1);
        gridLayout.setWidth("1000px");

        Label bigCell = new Label("big cell");
        bigCell.setId("bigCell");
        Label smallCell = new Label("small cell");
        smallCell.setId("smallCell");
        gridLayout.addComponent(bigCell, 0, 0, 1, 0); // spans first two columns
        gridLayout.addComponent(smallCell, 2, 0, 2, 0); // last column only

        addComponent(gridLayout);
    }

    @Override
    protected String getTestDescription() {
        return "A 3x1 grid has a spanned component on the first two cells and a component on the last cell. The two components should occupy 2/3 and 1/3 of the available space respectively, instead of 1/2 each.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14335;
    }
}
