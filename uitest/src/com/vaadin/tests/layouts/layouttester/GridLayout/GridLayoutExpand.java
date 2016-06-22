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
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

/**
 *
 * @author Vaadin Ltd
 */
public class GridLayoutExpand extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        class ExpandButton extends Button {

            public ExpandButton(final int i1, final int i2, final float e1,
                    final float e2) {
                super();
                setCaption("Expand ratio: " + e1 * 100 + " /" + e2 * 100);
                addClickListener(new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        layout.setColumnExpandRatio(i1, e1);
                        layout.setColumnExpandRatio(i2, e2);
                    }
                });
            }
        }
        Table t1 = getTestTable();
        Table t2 = getTestTable();
        t1.setSizeFull();
        t2.setSizeFull();
        layout.setColumns(4);
        layout.setRows(4);
        layout.addComponent(new ExpandButton(1, 2, 1.0f, 0.0f), 0, 0);
        layout.addComponent(new ExpandButton(1, 2, 0.5f, 0.50f), 0, 1);
        layout.addComponent(new ExpandButton(1, 2, .25f, 0.75f), 0, 2);

        layout.addComponent(t1, 1, 1);
        layout.addComponent(t2, 2, 1);
    }
}
