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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class GridLayoutWithNonIntegerWidth extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel p1 = new Panel("Panel with GridLayout");
        GridLayout grid = new GridLayout(1, 1, new Label("A"));
        grid.setWidth(100, Unit.PERCENTAGE);
        p1.setContent(grid);
        p1.setWidth("354.390625px");

        Panel p2 = new Panel("Panel with HorizontalLayout");
        HorizontalLayout hl = new HorizontalLayout(new Label("A"));
        hl.setWidth(100, Unit.PERCENTAGE);
        p2.setContent(hl);
        p2.setWidth("354.390625px");

        setContent(new VerticalLayout(p1, p2));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Neither of the panels should contain scrollbars";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 11775;
    }
}
