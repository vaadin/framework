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
package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;

/**
 * Test UI for DnD image element size
 * 
 * @author Vaadin Ltd
 */
public class DragAndDropRelativeWidth extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CssLayout layout = new CssLayout();
        layout.setWidth(300, Unit.PIXELS);

        Label label = new Label("drag source");
        label.addStyleName("drag-source");
        label.setWidth(100, Unit.PERCENTAGE);
        DragAndDropWrapper wrapper = new DragAndDropWrapper(label);
        wrapper.setWidth(100, Unit.PERCENTAGE);
        wrapper.setDragStartMode(DragStartMode.COMPONENT);

        layout.addComponent(wrapper);
        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Set explicit size for drag image element using calclulated size from the source";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14617;
    }

}
