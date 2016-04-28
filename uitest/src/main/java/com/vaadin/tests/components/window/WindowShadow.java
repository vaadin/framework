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
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

//Tests that  invisible divs don't overlap windows and don't block mouse events
public class WindowShadow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Window wnd = createWindow();
        wnd.setId("topwindow");
        Window wnd2 = createWindow();
        wnd2.setId("botwindow");
        wnd.setPositionX(100);
        wnd.setPositionY(100);
        wnd2.setPositionX(100);
        // Pick ycoord, that the top div of the Window overlaps with its footer
        int yCoord = (int) (wnd.getPositionX() + wnd.getHeight() - 5);
        wnd2.setPositionY(yCoord);
        UI.getCurrent().addWindow(wnd);
        UI.getCurrent().addWindow(wnd2);
    }

    private Window createWindow() {
        Window wnd = new Window();
        wnd.setHeight("200");
        wnd.setWidth("200");
        return wnd;
    }

    @Override
    protected String getTestDescription() {
        return "Popup window has shadow div elemetns, which overlaps other elements and blocks mouse events";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13885;
    }

}
