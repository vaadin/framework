/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.table;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * Test UI for empty table: empty table (without any data) throws client side
 * exception if it's a target for DnD.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class DndEmptyTable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label source = new Label("label");
        DragAndDropWrapper wrapper = new DragAndDropWrapper(source);
        wrapper.setDragStartMode(DragStartMode.WRAPPER);
        addComponent(wrapper);

        Table target = new Table();
        target.setWidth(100, Unit.PERCENTAGE);
        addComponent(target);
        target.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Drag and drop into empty table should not throws client side exception.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13655;
    }

}
