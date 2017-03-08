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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

/**
 * HorizontalLayout and VerticalLayout should not leak caption elements via
 * listeners when removing components from a layout.
 *
 * @since 7.1.13
 * @author Vaadin Ltd
 */
public class CaptionLeak extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(false);
        root.setSpacing(false);

        HorizontalLayout layout = new HorizontalLayout();
        Panel parent = new Panel();
        Button setLeakyContent = makeButton("Set leaky content", parent,
                VerticalLayout.class);
        Button setNonLeakyContent = makeButton("Set non leaky content", parent,
                CssLayout.class);
        layout.addComponent(setLeakyContent);
        layout.addComponent(setNonLeakyContent);
        root.addComponent(layout);
        root.addComponent(parent);
        setContent(root);
    }

    private Button makeButton(String caption, final Panel parent,
            final Class<? extends ComponentContainer> targetClass) {
        Button btn = new Button(caption);
        btn.setId(caption);
        btn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    ComponentContainer target = targetClass.newInstance();
                    for (int i = 0; i < 61; i++) {
                        target.addComponent(new TextField("Test"));
                    }
                    parent.setContent(target);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return btn;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Open this UI with ?debug and count measured non-connector elements after setting leaky and non leaky content.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
