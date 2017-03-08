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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class SubwindowDraggability extends TestBase {

    @Override
    protected void setup() {
        final Window draggableSubWindow = new Window("Draggable sub window");
        draggableSubWindow.setHeight("300px");
        final Window fixedSubWindow = new Window("Fixed sub window");
        fixedSubWindow.setHeight("200px");
        fixedSubWindow.setDraggable(false);

        fixedSubWindow.center();
        getMainWindow().addWindow(draggableSubWindow);
        getMainWindow().addWindow(fixedSubWindow);

        Button b = new Button("Swap", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                boolean b = draggableSubWindow.isDraggable();

                draggableSubWindow.setDraggable(!b);
                fixedSubWindow.setDraggable(b);

            }

        });
        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Two sub windows. One is draggable, the other one is fixed and cannot be moved by the user";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3133;
    }

}
