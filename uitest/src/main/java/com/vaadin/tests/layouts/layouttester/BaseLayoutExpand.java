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
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class BaseLayoutExpand extends BaseLayoutTestUI {

    public BaseLayoutExpand(Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        class ExpandButton extends Button {
            final private AbstractComponent c1;
            private AbstractComponent c2;
            private float expandComp1;
            private float expandComp2;

            public ExpandButton(final AbstractComponent c1,
                    final AbstractComponent c2, float e1, float e2) {
                super();
                this.c1 = c1;
                this.c2 = c2;
                expandComp1 = e1;
                expandComp2 = e2;
                setCaption("Expand ratio: " + e1 * 100 + " /" + e2 * 100);
                addClickListener(new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        l2.setExpandRatio(c1, expandComp1);
                        l2.setExpandRatio(c2, expandComp2);
                    }
                });
            }
        }
        Table t1 = getTestTable();
        Table t2 = getTestTable();
        t1.setSizeFull();
        t2.setSizeFull();
        l2.addComponent(t1);
        l2.addComponent(t2);

        l1.addComponent(new ExpandButton(t1, t2, 1.0f, 0.0f));
        l1.addComponent(new ExpandButton(t1, t2, 0.5f, 0.50f));
        l1.addComponent(new ExpandButton(t1, t2, .25f, 0.75f));
    }
}
