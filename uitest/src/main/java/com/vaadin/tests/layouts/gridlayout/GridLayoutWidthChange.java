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
package com.vaadin.tests.layouts.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

public class GridLayoutWidthChange extends AbstractReindeerTestUI {

    private GridLayout generateLayout() {
        VerticalLayout fields1 = new VerticalLayout();
        fields1.setMargin(false);
        fields1.setSpacing(false);

        NativeButton nb = new NativeButton("A button");
        nb.setHeight("300px");
        fields1.addComponent(nb);

        VerticalLayout fields3 = new VerticalLayout();
        fields3.setMargin(false);
        fields3.setSpacing(false);
        fields3.addComponent(new TextField("field14"));

        NativeButton b = new NativeButton("A big button");
        b.setWidth("200px");
        b.setHeight("200px");

        GridLayout layout = new GridLayout(3, 2);
        layout.setHideEmptyRowsAndColumns(true);
        layout.setWidth("100%");
        layout.addComponent(fields1, 0, 0, 0, 1);
        layout.addComponent(b, 2, 1);

        return layout;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final GridLayout layout1 = generateLayout();
        final CustomComponent cc = new CustomComponent(layout1);
        cc.setWidth("500px");
        addComponent(cc);

        Button testButton = new Button("Reduce GridLayout parent width",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        cc.setWidth((cc.getWidth() - 100) + "px");
                    }

                });
        addComponent(testButton);
    }

    @Override
    protected String getTestDescription() {
        return "A 100% wide GridLayout is wrapped inside a CustomComponent. When the width of the CustomComponent is reduced, the size of the GridLayout should be reduced accordingly. The Buttons should stay in place vertically and just move closer to each other horizontally.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }
}
