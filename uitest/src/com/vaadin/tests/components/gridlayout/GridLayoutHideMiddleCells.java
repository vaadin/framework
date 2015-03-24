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

import java.util.Random;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridLayoutHideMiddleCells extends AbstractTestUI {
    GridLayout gridLayout;
    GridLayout gridLayout2;

    @Override
    protected void setup(VaadinRequest request) {
        final int ROWS = 5;
        final int COLS = 5;

        final Label[][] labels = new Label[ROWS][COLS];
        VerticalLayout mainLayout = new VerticalLayout();
        HorizontalLayout horLayout = new HorizontalLayout();
        gridLayout = new GridLayout(ROWS, COLS);
        gridLayout.setHideEmptyRowsAndColumns(true);

        gridLayout2 = new GridLayout(4, 4);
        gridLayout2.setHideEmptyRowsAndColumns(true);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Label label = new Label("Slot " + i + " " + j);
                labels[i][j] = label;
                gridLayout.addComponent(label);
                if (!(i == 2 || j == 2)) {
                    Label label2 = new Label("Slot " + i + " " + j);
                    gridLayout2.addComponent(label2);
                }
            }
        }
        setContent(mainLayout);
        gridLayout.setHeight("500px");
        gridLayout.setWidth("500px");
        gridLayout.setSpacing(true);

        addComponent(gridLayout);
        addComponent(gridLayout2);
        mainLayout.addComponent(horLayout);
        gridLayout2.setHeight("500px");
        gridLayout2.setWidth("500px");
        gridLayout2.setSpacing(true);
        horLayout.addComponent(gridLayout);
        horLayout.addComponent(gridLayout2);

        mainLayout.addComponent(new Button(
                "Hide/show both middle Column and row",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        for (int i = 0; i < ROWS; i++) {
                            for (int j = 0; j < COLS; j++) {
                                if (j == 2 || i == 2) {
                                    if (labels[i][j].isVisible()) {
                                        labels[i][j].setVisible(false);
                                    } else {
                                        labels[i][j].setVisible(true);
                                    }
                                }
                            }
                        }
                    }
                }));
        mainLayout.addComponent(new Button("Hide/show middle Column",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        for (int i = 0; i < ROWS; i++) {
                            if (labels[i][2].isVisible()) {
                                labels[i][2].setVisible(false);
                            } else {
                                labels[i][2].setVisible(true);
                            }
                        }
                    }
                }));
        mainLayout.addComponent(new Button("Hide/show middle Row",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        for (int j = 0; j < COLS; j++) {
                            if (labels[2][j].isVisible()) {
                                labels[2][j].setVisible(false);
                            } else {
                                labels[2][j].setVisible(true);
                            }
                        }
                    }

                }));
        mainLayout.addComponent(new Button("Hide Random button",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        // TODO Auto-generated method stub
                        Random rand = new Random();
                        int i = rand.nextInt(ROWS);
                        int j = rand.nextInt(COLS);
                        if (labels[i][j].isVisible()) {
                            labels[i][j].setVisible(false);
                        } else {
                            labels[i][j].setVisible(true);
                        }
                    }
                }));
    }

    @Override
    protected Integer getTicketNumber() {
        return 8855;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Changing the number of visible fields a GridLayout with spacing should not cause additional empty space on the place of invisible fields";
    }

}
