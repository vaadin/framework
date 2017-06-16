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
package com.vaadin.tests.dnd;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DraggableButton extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {

        Button draggableButton = new Button("Draggable Button",
                event -> log("clicked draggable button"));
        DragSourceExtension<Button> dragSourceExtension = new DragSourceExtension<>(
                draggableButton);
        dragSourceExtension.setDataTransferText(
                "If you see this, the drop was successful");

        Label dropTarget = new Label("Drop Here");
        dropTarget.addStyleName("drop-target");
        DropTargetExtension<Label> dropTargetExtension = new DropTargetExtension<>(
                dropTarget);
        dropTargetExtension
                .addDropListener(event -> log(event.getDataTransferText()));

        Layout layout = new HorizontalLayout();
        layout.addComponents(draggableButton, dropTarget, new Button(
                "another button", event -> log("click on another button")));
        addComponent(layout);

        // Add styling
        setStyle();
    }

    private void setStyle() {
        Page.Styles styles = Page.getCurrent().getStyles();

        styles.add(".drop-target {" + "width: 150px;" + "height: 100px;"
                + "border: 1px solid black;" + "border-radius: 4px;"
                + "text-align: center;" + "}");
        styles.add(".v-label-drag-center {" + "border-style: dashed;" + "}");
    }

    @Override
    protected String getTestDescription() {
        return "Test if Button is draggable, and it won't steal all other clicks";
    }
}
