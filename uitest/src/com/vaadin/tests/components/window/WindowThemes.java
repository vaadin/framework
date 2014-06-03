/*
 * Copyright 2000-2014 Vaadin Ltd.
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

/**
 * 
 */
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class WindowThemes extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Window def = new Window("default", new Label("Some content"));
        def.setWidth("300px");
        def.setHeight("100%");
        addWindow(def);

        Window light = new Window("WINDOW_LIGHT", new Label("Some content"));
        light.setStyleName(Reindeer.WINDOW_LIGHT);
        light.setPositionX(300);
        light.setWidth("300px");
        light.setHeight("100%");
        addWindow(light);

        Window black = new Window("WINDOW_BLACK", new Label("Some content"));
        black.setStyleName(Reindeer.WINDOW_BLACK);
        black.setPositionX(600);
        black.setWidth("300px");
        black.setHeight("100%");
        addWindow(black);
    }

    @Override
    protected String getTestDescription() {
        return "Shows the different css themes of Window";
    }

    @Override
    protected Integer getTicketNumber() {
        // Not tied to any specific ticket
        return null;
    }
}
