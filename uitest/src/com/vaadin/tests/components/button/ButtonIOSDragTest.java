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

package com.vaadin.tests.components.button;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class ButtonIOSDragTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();

        Button offset = new Button("Drag me");
        offset.addListener(new ClickListener() {
            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                Notification.show("Button clicked!");
            }
        });
        DragAndDropWrapper dragMe = new DragAndDropWrapper(offset);
        dragMe.setDragStartMode(DragStartMode.WRAPPER);
        layout.addComponent(dragMe);
        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Test dragging of Button in iOS - dragging from the inside of the button to the outside and releasing should not cause a ClickEvent to be fired.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7690;
    }

}
