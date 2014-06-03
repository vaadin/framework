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
package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Window;

/**
 * 
 * @author Vaadin Ltd
 */
public class MoveToTop extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Window window = new Window("one");
        window.addStyleName("first-window");
        window.setWidth(200, Unit.PIXELS);
        window.setHeight(100, Unit.PIXELS);
        window.setPositionX(100);
        window.setPositionY(100);
        addWindow(window);

        window = new Window("two");
        window.setWidth(200, Unit.PIXELS);
        window.setHeight(100, Unit.PIXELS);
        window.setPositionX(150);
        window.setPositionY(150);
        window.addStyleName("second-window");
        addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Bring to front window on click it's header";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13445;
    }

}
