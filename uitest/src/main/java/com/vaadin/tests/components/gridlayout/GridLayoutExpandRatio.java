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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class GridLayoutExpandRatio extends AbstractTestUI {
    HorizontalLayout layout;
    GridLayout gridLayout;
    GridLayout gridLayout2;
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private Label[][] labels;

    @Override
    protected void setup(VaadinRequest request) {

        labels = new Label[ROWS][COLS];
        layout = new HorizontalLayout();
        gridLayout = new GridLayout(ROWS, COLS);
        gridLayout.setHideEmptyRowsAndColumns(true);

        layout.setImmediate(true);
        gridLayout.setImmediate(true);
        gridLayout2 = new GridLayout(4, 4);
        gridLayout2.setHideEmptyRowsAndColumns(true);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Label label = new Label("Slot " + i + " " + j);
                label.setImmediate(true);
                labels[i][j] = label;
                gridLayout.addComponent(label, j, i);
                if (!(i == 2 || j == 2)) {
                    Label label2 = new Label("Slot " + i + " " + j);
                    gridLayout2.addComponent(label2);
                }
            }
        }
        gridLayout.setHeight("500px");
        gridLayout.setWidth("500px");
        gridLayout.setSpacing(true);

        gridLayout2.setHeight("500px");
        gridLayout2.setWidth("500px");
        gridLayout2.setSpacing(true);
        addComponent(layout);
        HorizontalLayout space = new HorizontalLayout();
        space.setWidth("100px");
        layout.addComponent(gridLayout);
        layout.addComponent(space);
        layout.addComponent(gridLayout2);

        setExpandRatio();
        addComponent(new Button("Hide/show both middle Column and row",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        hideComponetns();
                    }
                }));
    }

    private void hideComponetns() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (i == 2 || j == 2) {
                    if (labels[i][j].isVisible()) {
                        labels[i][j].setVisible(false);
                    } else {
                        labels[i][j].setVisible(true);
                    }
                }
            }
        }
    }

    private void setExpandRatio() {
        gridLayout.setRowExpandRatio(2, 5);
        gridLayout2.setRowExpandRatio(1, 5);
    }

    @Override
    protected Integer getTicketNumber() {
        return 8855;
    }

    @Override
    protected String getTestDescription() {
        return "If row/column doesn't have elements but have an expand ratio set, it should be shown as a empty row/column";
    }

}
