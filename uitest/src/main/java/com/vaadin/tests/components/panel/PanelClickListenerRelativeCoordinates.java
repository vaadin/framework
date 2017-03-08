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
package com.vaadin.tests.components.panel;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelClickListenerRelativeCoordinates extends TestBase {

    @Override
    protected void setup() {
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel panel = new Panel("Panel's caption", panelLayout);
        panel.addClickListener(new ClickListener() {

            @Override
            public void click(ClickEvent event) {
                getMainWindow().showNotification("" + event.getRelativeX()
                        + ", " + event.getRelativeY());
            }
        });
        addComponent(panel);

    }

    @Override
    protected String getDescription() {
        return "Click the panel to get coordinates relative to the top-left corder of the panel.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
