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
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ExtraLargeSubWindow extends TestBase {

    @Override
    protected void setup() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        Window w = new Window("full sized window", layout);
        w.setWidth("2000px");
        w.setHeight("2000px");
        NativeButton b = new NativeButton("A large button");
        b.setSizeFull();
        layout.addComponent(b);

        getMainWindow().addWindow(w);
    }

    @Override
    protected String getDescription() {
        return "A 100%x100% sub window should not produce scrollbars in the main view or in the sub window. The button inside the sub window is 100%x100%, as is the layout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3407;
    }

}
