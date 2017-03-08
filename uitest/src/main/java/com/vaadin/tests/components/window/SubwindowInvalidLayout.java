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
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SubwindowInvalidLayout extends TestBase {

    @Override
    protected String getDescription() {
        return "The subwindow contains an invalid layout, which analyze layouts should detect.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3096;
    }

    @Override
    protected void setup() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window window = new Window("Sub window", layout);
        window.center();

        VerticalLayout vl = new VerticalLayout();
        vl.setWidth(null);
        Button b = new Button("A 100% wide button, invalid");
        b.setWidth("100%");
        vl.addComponent(b);
        layout.addComponent(vl);

        getMainWindow().addWindow(window);
    }

}
