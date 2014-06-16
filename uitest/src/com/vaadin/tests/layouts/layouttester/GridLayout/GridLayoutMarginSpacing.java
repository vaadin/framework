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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class GridLayoutMarginSpacing extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        Table t1 = getTestTable();
        Table t2 = getTestTable();
        t1.setSizeFull();
        t2.setSizeFull();

        final Button btn1 = new Button("Toggle margin on/off");
        btn1.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                boolean margin = layout.getMargin().hasLeft();
                layout.setMargin(!margin);

            }
        });
        final Button btn2 = new Button("Toggle spacing on/off");
        btn2.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                layout.setSpacing(!layout.isSpacing());
            }
        });
        layout.addComponent(btn1);
        layout.addComponent(btn2);

        layout.addComponent(t1);
        layout.setMargin(false);
        layout.setSpacing(false);
        // Must add something around the hr to avoid the margins collapsing
        layout.addComponent(new Label(
                "<div style='height: 1px'></div><hr /><div style='height: 1px'></div>",
                ContentMode.HTML));
        layout.addComponent(t2);
    }
}
