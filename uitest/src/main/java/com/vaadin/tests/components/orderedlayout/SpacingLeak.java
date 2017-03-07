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
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * HorizontalLayout and VerticalLayout should not leak .v-spacing elements via
 * listeners when removing components from a layout.
 *
 * @since 7.1.12
 * @author Vaadin Ltd
 */
public class SpacingLeak extends UI {

    private HorizontalLayout spacingLayout;

    @Override
    public void init(VaadinRequest req) {
        final VerticalLayout root = new VerticalLayout();
        setContent(root);
        root.setSizeUndefined();

        final Button spacingButton = new Button("Add layout with spacing");
        spacingButton.setId("addbutton");
        root.addComponent(spacingButton);
        spacingButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                spacingLayout = new HorizontalLayout();
                spacingLayout.setSpacing(true);
                spacingLayout.setWidth("100%");

                for (int i = 0; i < 100; ++i) {
                    spacingLayout.addComponent(new Button("" + i));
                }

                root.addComponent(spacingLayout);
            }
        });

        final Button removeButton = new Button("Remove layouts");
        removeButton.setId("removebutton");
        root.addComponent(removeButton);
        removeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                root.removeComponent(spacingLayout);
            }
        });
    }
}
