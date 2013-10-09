/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.grid.TestGrid;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;

/**
 * @since 7.2
 * @author Vaadin Ltd
 */
@Widgetset(TestingWidgetSet.NAME)
public class GridTest extends AbstractTestUI {
    @Override
    protected void setup(final VaadinRequest request) {
        final TestGrid grid = new TestGrid();
        addComponent(grid);

        final Layout insertRowsLayout = new HorizontalLayout();
        final TextField insertRowsOffset = new TextField();
        insertRowsLayout.addComponent(insertRowsOffset);
        final TextField insertRowsAmount = new TextField();
        insertRowsLayout.addComponent(insertRowsAmount);
        insertRowsLayout.addComponent(new Button("insert rows",
                new Button.ClickListener() {
                    @Override
                    @SuppressWarnings("boxing")
                    public void buttonClick(final ClickEvent event) {
                        int offset = Integer.valueOf(insertRowsOffset
                                .getValue());
                        int amount = Integer.valueOf(insertRowsAmount
                                .getValue());
                        grid.insertRows(offset, amount);
                    }
                }));
        addComponent(insertRowsLayout);

        final Layout removeRowsLayout = new HorizontalLayout();
        final TextField removeRowsOffset = new TextField();
        removeRowsLayout.addComponent(removeRowsOffset);
        final TextField removeRowsAmount = new TextField();
        removeRowsLayout.addComponent(removeRowsAmount);
        removeRowsLayout.addComponent(new Button("remove rows",
                new Button.ClickListener() {
                    @Override
                    @SuppressWarnings("boxing")
                    public void buttonClick(final ClickEvent event) {
                        int offset = Integer.valueOf(removeRowsOffset
                                .getValue());
                        int amount = Integer.valueOf(removeRowsAmount
                                .getValue());
                        grid.removeRows(offset, amount);
                    }
                }));
        addComponent(removeRowsLayout);

        final Layout insertColumnsLayout = new HorizontalLayout();
        final TextField insertColumnsOffset = new TextField();
        insertColumnsLayout.addComponent(insertColumnsOffset);
        final TextField insertColumnsAmount = new TextField();
        insertColumnsLayout.addComponent(insertColumnsAmount);
        insertColumnsLayout.addComponent(new Button("insert columns",
                new Button.ClickListener() {
                    @Override
                    @SuppressWarnings("boxing")
                    public void buttonClick(final ClickEvent event) {
                        int offset = Integer.valueOf(insertColumnsOffset
                                .getValue());
                        int amount = Integer.valueOf(insertColumnsAmount
                                .getValue());
                        grid.insertColumns(offset, amount);
                    }
                }));
        addComponent(insertColumnsLayout);

        final Layout removeColumnsLayout = new HorizontalLayout();
        final TextField removeColumnsOffset = new TextField();
        removeColumnsLayout.addComponent(removeColumnsOffset);
        final TextField removeColumnsAmount = new TextField();
        removeColumnsLayout.addComponent(removeColumnsAmount);
        removeColumnsLayout.addComponent(new Button("remove columns",
                new Button.ClickListener() {
                    @Override
                    @SuppressWarnings("boxing")
                    public void buttonClick(final ClickEvent event) {
                        int offset = Integer.valueOf(removeColumnsOffset
                                .getValue());
                        int amount = Integer.valueOf(removeColumnsAmount
                                .getValue());
                        grid.removeColumns(offset, amount);
                    }
                }));
        addComponent(removeColumnsLayout);
    }

    @Override
    protected String getTestDescription() {
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
