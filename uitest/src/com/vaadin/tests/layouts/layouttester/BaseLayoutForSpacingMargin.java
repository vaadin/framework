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
package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 *
 * @author Vaadin Ltd
 */
public class BaseLayoutForSpacingMargin extends BaseLayoutTestUI {
    /**
     * @param layoutClass
     */
    public BaseLayoutForSpacingMargin(
            Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        Table t1 = getTestTable();
        Table t2 = getTestTable();
        t1.setSizeFull();
        t2.setSizeFull();
        l2.addComponent(t1);
        l2.setMargin(false);
        l2.setSpacing(false);
        // Must add something around the hr to avoid the margins collapsing
        l2.addComponent(new Label(
                "<div style='height: 1px'></div><hr /><div style='height: 1px'></div>",
                ContentMode.HTML));
        l2.addComponent(t2);
        final Button btn1 = new Button("Toggle margin on/off");
        btn1.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                boolean margin = l2.getMargin().hasLeft();
                l2.setMargin(!margin);

            }
        });
        final Button btn2 = new Button("Toggle spacing on/off");
        btn2.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                l2.setSpacing(!l2.isSpacing());
            }
        });
        l1.addComponent(btn1);
        l1.addComponent(btn2);
    }
}
