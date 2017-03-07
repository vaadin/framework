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
package com.vaadin.tests.components.button;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;

public class ButtonsAndIcons extends TestBase {

    @Override
    protected String getDescription() {
        return "The first button has text and an icon, the second only text and the third only an icon.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3031;
    }

    @Override
    protected void setup() {
        Button b = new Button("Text and icon");
        b.setIcon(new ThemeResource("../runo/icons/16/ok.png"));

        addComponent(b);

        b = new Button("Only text");

        addComponent(b);
        b = new Button((String) null);
        b.setIcon(new ThemeResource("../runo/icons/16/ok.png"));

        addComponent(b);

        b = new NativeButton("Text and icon");
        b.setIcon(new ThemeResource("../runo/icons/16/ok.png"));

        addComponent(b);

        b = new NativeButton("Only text");

        addComponent(b);
        b = new NativeButton(null);
        b.setIcon(new ThemeResource("../runo/icons/16/ok.png"));

        addComponent(b);
    }

}
