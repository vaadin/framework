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

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

@Theme("tests-valo")
public class GridLayoutExpandWithManyRows extends UI {

    static final int POPULATED_ROWS = 20;
    static int ROW_COUNT = 58;

    public static class ColoredLabel extends Label {
        private static int colorNumber = 0;

        public ColoredLabel() {
            super();
            addStyleName("color-label");
            addStyleName("color-" + (colorNumber++) % 10);
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        for (int i = 0; i < 10; i++) {
            getPage().getStyles().add(".color-" + i + " {" //
                    + "background-color: hsl(" + (i * 90) + ", 60%, 70%);" //
                    + "}");
        }

        GridLayout gridLayout = new GridLayout(6, ROW_COUNT);
        for (int i = 0; i < ROW_COUNT; i++) {
            gridLayout.setRowExpandRatio(i, 1);
        }
        gridLayout.setSizeFull();
        for (int i = 0; i < POPULATED_ROWS; i++) {
            int upperLeftRow = i * 2;
            int upperLeftCol = 0;
            int lowerRightCol = 5;
            int lowerRightRow = upperLeftRow + 1;
            ColoredLabel coloredLabel = new ColoredLabel();
            coloredLabel.setSizeFull();
            gridLayout.addComponent(coloredLabel, upperLeftCol, upperLeftRow,
                    lowerRightCol, lowerRightRow);
        }

        gridLayout.setHeight("500%");
        Component root = new Panel(gridLayout);
        root.setSizeFull();
        setContent(root);

    }
}
