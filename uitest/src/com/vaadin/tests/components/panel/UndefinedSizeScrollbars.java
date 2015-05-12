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
package com.vaadin.tests.components.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class UndefinedSizeScrollbars extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setSizeFull();
        setContent(layout);

        GridLayout grid = new GridLayout();
        grid.setSpacing(true);

        TextField text1 = new TextField();
        text1.setCaption("Text1");
        text1.setRequired(true);

        TextField text2 = new TextField();
        text2.setCaption("Text2");
        text2.setRequired(true);

        ComboBox combo = new ComboBox();
        combo.setCaption("Combo1");

        CheckBox check = new CheckBox();
        check.setCaption("Check");

        grid.setColumns(2);
        grid.setRows(2);

        grid.addComponent(text1);
        grid.addComponent(text2);
        grid.addComponent(combo);
        grid.addComponent(check);

        grid.setSizeUndefined();

        Panel panel = new Panel();
        panel.setContent(grid);

        panel.setSizeUndefined();

        layout.addComponent(panel);
    }

}
