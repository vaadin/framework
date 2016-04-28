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
package com.vaadin.tests.layouts;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.extensions.LayoutMemoryUsageIE8Extension;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class IE8MeasuredSizeMemoryLeak extends AbstractTestUI {

    private boolean state = false;

    private HasComponents component1 = new VerticalLayout() {
        {
            for (int i = 1; i <= 200; i++) {
                String idText = "ID:" + i;
                Label c = new Label(idText);
                c.setId(idText);
                addComponent(c);
            }
        }
    };

    private HasComponents component2 = new VerticalLayout() {
        {
            for (int i = 201; i <= 400; i++) {
                String idText = "ID:" + i;
                Label c = new Label(idText);
                c.setId(idText);
                addComponent(c);
            }
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        new LayoutMemoryUsageIE8Extension().extend(this);

        VerticalLayout layout = new VerticalLayout();
        setContent(layout);

        final VerticalLayout contentLayout = new VerticalLayout();

        Button button = new Button("Toggle");
        button.setId("toggle");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                contentLayout.removeAllComponents();
                if (state) {
                    contentLayout.addComponent(component1);
                } else {
                    contentLayout.addComponent(component2);
                }
                state = !state;
            }

        });

        layout.addComponent(button);
        layout.addComponent(contentLayout);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "IE8 leaks memory when components are added and removed";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12688;
    }
}
