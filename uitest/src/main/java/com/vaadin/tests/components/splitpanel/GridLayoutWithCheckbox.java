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
package com.vaadin.tests.components.splitpanel;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@Theme("reindeer")
public class GridLayoutWithCheckbox extends UI {

    @Override
    protected void init(VaadinRequest request) {
        GridLayout grid = new GridLayout(2, 3);
        grid.setWidth(500, Unit.PIXELS);

        Label l = new Label("Textfield 1:");
        grid.addComponent(l, 0, 0);
        TextField textfield = new TextField();
        textfield.addValueChangeListener(listener -> {
        });
        textfield.setValueChangeMode(ValueChangeMode.EAGER);
        grid.addComponent(textfield, 1, 0);

        l = new Label("CheckBox:");
        grid.addComponent(l, 0, 1);
        CheckBox checkBox = new CheckBox();
        grid.addComponent(checkBox, 1, 2);
        Window window = new Window();
        window.setWidth(300.0f, Unit.PIXELS);
        window.setContent(grid);
        window.setResizable(false);
        window.setWidth(550, Unit.PIXELS);

        // grid.setColumnExpandRatio(1, 1);
        addWindow(window);
    }
}
