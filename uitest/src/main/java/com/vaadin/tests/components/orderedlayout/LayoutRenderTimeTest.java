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

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.themes.Reindeer;

public class LayoutRenderTimeTest extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow();
        setMainWindow(main);

        VerticalLayout root = new VerticalLayout();
        root.setWidth("100%");
        main.setContent(root);

        for (int i = 1; i <= 100; i++) {
            root.addComponent(getRow(i));
        }
    }

    private HorizontalLayout getRow(int i) {
        HorizontalLayout row = new HorizontalLayout();
        // row.setWidth("100%");
        // row.setSpacing(true);

        Embedded icon = new Embedded(null,
                new ThemeResource("../runo/icons/32/document.png"));
        // row.addComponent(icon);
        // row.setComponentAlignment(icon, Alignment.MIDDLE_LEFT);

        Label text = new Label("Row content #" + i
                + ". In pellentesque faucibus vestibulum. Nulla at nulla justo, eget luctus tortor. Nulla facilisi. Duis aliquet.");
        // row.addComponent(text);
        // row.setExpandRatio(text, 1);

        Button button = new Button("Edit");
        button.addStyleName(Reindeer.BUTTON_SMALL);
        row.addComponent(button);
        // row.setComponentAlignment(button, Alignment.MIDDLE_LEFT);

        button = new Button("Delete");
        button.addStyleName(Reindeer.BUTTON_SMALL);
        row.addComponent(button);
        // row.setComponentAlignment(button, Alignment.MIDDLE_LEFT);

        return row;
    }
}
