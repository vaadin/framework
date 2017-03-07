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

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.TextField;

public class VerticalLayoutWidthCalculation extends AbstractTestCase {
    @Override
    public void init() {
        final LegacyWindow mainWindow = new LegacyWindow(
                "Vaadintest Application");
        mainWindow.addWindow(createSubWindow());
        setMainWindow(mainWindow);

    }

    private Window createSubWindow() {
        HorizontalLayout hl = new HorizontalLayout();

        VerticalLayout vlTF1 = new VerticalLayout();
        vlTF1.setSizeUndefined();
        final TextField tf1 = new TextField("Text1");
        tf1.setSizeUndefined();
        vlTF1.addComponent(tf1);
        hl.addComponent(vlTF1);

        VerticalLayout vlTF2 = new VerticalLayout();
        vlTF2.setSizeUndefined();
        final TextField tf2 = new TextField("Text2");
        tf2.setVisible(false);
        tf2.setSizeUndefined();
        vlTF2.addComponent(tf2);
        hl.addComponent(vlTF2);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeUndefined();
        Window wnd = new Window("Test", layout);
        layout.addComponent(hl);
        Button btn = new Button("Show/hide");
        btn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tf2.setVisible(!tf2.isVisible());
            }
        });
        layout.addComponent(btn);

        return wnd;
    }

    @Override
    protected String getDescription() {
        return "The second TextField is initially invisible. Make it visible and then hide it again. You should end up with the same result as initially.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7260;
    }

}
