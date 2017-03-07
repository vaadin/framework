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
package com.vaadin.tests.components.caption;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class LargeCaptionIcon extends TestBase {

    @Override
    protected String getDescription() {
        return "The icon should be completely visible on both initial load and after subsequent refreshes.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2902;
    }

    @Override
    protected void setup() {
        GridLayout gl = new GridLayout();
        gl.setWidth("100%");

        Label l = new Label("This is a label");
        l.setCaption("This is its caption, it also has a large icon");
        l.setIcon(new ThemeResource("../runo/icons/64/ok.png"));
        gl.addComponent(l);
        addComponent(gl);
    }
}
