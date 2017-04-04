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
package com.vaadin.tests.elements.gridlayout;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridLayoutUI extends AbstractTestUI {

    public static final String ONE_ROW_ONE_COL = "oneRowOneCol";
    public static final String TEN_ROWS_TEN_COLS = "tenRowsTenCols";

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout oneRowZeroCols = new GridLayout(1, 1);
        oneRowZeroCols.setId(ONE_ROW_ONE_COL);
        addComponent(oneRowZeroCols);

        GridLayout tenTimesTen = new GridLayout(10, 10);
        tenTimesTen.addComponent(new Label("5-5"), 5, 5);
        tenTimesTen.addComponent(new Button("7-7 8-8"), 7, 7, 8, 8);
        tenTimesTen.setId(TEN_ROWS_TEN_COLS);
        addComponent(tenTimesTen);
    }

}
