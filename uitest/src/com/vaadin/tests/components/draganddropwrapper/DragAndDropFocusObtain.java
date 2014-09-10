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
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for text area inside {@link DragAndDropWrapper}: text area should
 * obtain focus on click.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class DragAndDropFocusObtain extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout dndLayout = new VerticalLayout();
        TextArea area = new TextArea();
        area.setValue("text");
        dndLayout.addComponent(area);

        DragAndDropWrapper wrapper = new DragAndDropWrapper(dndLayout);
        wrapper.setDragStartMode(DragStartMode.COMPONENT);
        addComponent(wrapper);
    }

    @Override
    protected String getTestDescription() {
        return "Text fields/areas inside Drag and Drop Wrappers should get focus inside DnD wrapper on click.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12838;
    }
}