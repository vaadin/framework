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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;

public class GridInWindow extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();

        grid.addColumn("Hidable column").setHidable(true);
        grid.addRow("Close and reopen and it vanishes");

        Button popupButton = new Button("Open PopUp",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        Window subWindow = new Window("Sub-window");
                        subWindow.setContent(grid);
                        subWindow.setWidth(600, Unit.PIXELS);
                        subWindow.setWidth(400, Unit.PIXELS);
                        getUI().addWindow(subWindow);
                    }
                });

        addComponent(popupButton);

    }

}
