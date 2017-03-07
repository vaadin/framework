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
package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CenteredWindowWithUndefinedSize extends TestBase {

    @Override
    protected String getDescription() {
        return "The centered sub-window with undefined height and a undefined high layout should be rendered in the center of the screen and not in the top-left corner.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2702;
    }

    @Override
    protected void setup() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window centered = new Window("A window", layout);
        centered.setSizeUndefined();
        layout.setSizeUndefined();
        centered.center();

        Label l = new Label("This window should be centered");
        l.setSizeUndefined();
        layout.addComponent(l);

        getMainWindow().addWindow(centered);

    }
}
