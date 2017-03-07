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
package com.vaadin.tests.dd;

import com.vaadin.annotations.Widgetset;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;

/**
 * Test for interrupting drag-and-drop.
 *
 * @author Vaadin Ltd
 */
@Widgetset(TestingWidgetSet.NAME)
public class DDInterrupt extends AbstractReindeerTestUI {

    private SpacebarPanner sp;

    @Override
    protected void setup(VaadinRequest vaadinRequest) {
        final CssLayout layout = new CssLayout();
        layout.setWidth("500px");
        layout.setHeight("500px");

        addButton("Click to interrupt next drag.", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                sp.interruptNext();
            }
        });

        final Label draggableLabel = new Label("Drag me");
        draggableLabel.setId("draggable");

        DragAndDropWrapper dndLayoutWrapper = new DragAndDropWrapper(layout);
        addComponent(dndLayoutWrapper);

        DragAndDropWrapper labelWrapper = new DragAndDropWrapper(
                draggableLabel);
        draggableLabel.setSizeUndefined();
        labelWrapper.setDragStartMode(DragStartMode.COMPONENT);
        labelWrapper.setSizeUndefined();

        layout.addComponent(labelWrapper);

        dndLayoutWrapper.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                TargetDetailsImpl targetDetails = (TargetDetailsImpl) event
                        .getTargetDetails();
                int x = targetDetails.getMouseEvent().getRelativeX();
                int y = targetDetails.getMouseEvent().getRelativeY();

                draggableLabel.setWidth(x, Unit.PIXELS);
                draggableLabel.setHeight(y, Unit.PIXELS);
            }
        });

        sp = SpacebarPanner.wrap(this);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17163;
    }

    @Override
    protected String getTestDescription() {
        return "Hold space while clicking and dragging the label, or click the button before draggin the label. There should be no client-side exception.";
    }
}
