/*
 * Copyright 2000-2015 Vaadin Ltd.
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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * Test to demonstrate that tooltips are shown for both Window header and
 * content
 * 
 * @author Vaadin Ltd
 */
public class ToolTipInWindow extends AbstractTestUI {

    Window window;

    @Override
    protected void setup(VaadinRequest request) {

        window = new Window("Caption", new Label("A label content"));
        window.setPositionX(300);
        window.setPositionY(200);
        window.setWidth("200px");
        window.setHeight("200px");
        window.setDescription("Tooltip");
        addWindow(window);

    }

}
