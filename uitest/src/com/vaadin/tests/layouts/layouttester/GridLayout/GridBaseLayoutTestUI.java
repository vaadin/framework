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
package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.layouts.layouttester.BaseLayoutTestUI;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

/**
 *
 * @author Vaadin Ltd
 */
public abstract class GridBaseLayoutTestUI extends BaseLayoutTestUI {
    protected GridLayout layout = new GridLayout();

    /**
     * @param layoutClass
     */
    public GridBaseLayoutTestUI() {
        super(GridLayout.class);
        layout.setHideEmptyRowsAndColumns(true);
    }

    @Override
    protected void setup(VaadinRequest request) {
        layout.setMargin(true);
        layout.setSizeFull();
        getUI().setContent(layout);
    }

    @Override
    protected void getLayoutForLayoutSizing(final String compType) {

        layout.setSpacing(false);
        layout.setMargin(false);

        final AbstractComponent c1 = getTestTable();
        c1.setSizeFull();
        final AbstractComponent c2 = getTestTable();
        c2.setSizeFull();

        class SetSizeButton extends Button {
            SetSizeButton(final String size) {
                super();
                setCaption("Set size " + size);
                addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (compType == "layout") {
                            layout.setHeight(size);
                            layout.setWidth(size);
                        } else if (compType == "component") {
                            c2.setHeight(size);
                            c2.setWidth(size);
                        } else {
                        }

                    }
                });
            }

        }
        Button btn1 = new SetSizeButton("550px");
        Button btn2 = new SetSizeButton("-1px");
        Button btn3 = new SetSizeButton("75%");
        Button btn4 = new SetSizeButton("100%");

        layout.addComponent(btn1);
        layout.addComponent(btn2);
        layout.addComponent(btn3);
        layout.addComponent(btn4);
        layout.addComponent(c1);
        layout.addComponent(new Label(
                "<div style='height: 1px'></div><hr /><div style='height: 1px'></div>",
                ContentMode.HTML));
        layout.addComponent(c2);
        btn2.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Label newLabel = new Label("--- NEW LABEL ---");
                newLabel.setSizeUndefined();
                layout.addComponent(newLabel);
            }
        });
    }
}
