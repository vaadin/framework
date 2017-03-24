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

import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class DragAndDropCardShuffle extends AbstractTestUIWithLog {

    // Create cards
    private final Label ace = new Label("A");
    private final Label jack = new Label("J");
    private final Label queen = new Label("Q");
    private final Label king = new Label("K");

    // Create desk
    private final HorizontalLayout desk = new HorizontalLayout();

    @Override
    protected void setup(VaadinRequest request) {

        // Create UI and add extensions
        desk.addComponents(ace, jack, queen, king);

        ace.setStyleName("card");
        addDragSourceExtension(ace);
        addDropTargetExtension(ace);

        jack.setStyleName("card");
        addDragSourceExtension(jack);
        addDropTargetExtension(jack);

        queen.setStyleName("card");
        addDragSourceExtension(queen);
        addDropTargetExtension(queen);

        king.setStyleName("card");
        addDragSourceExtension(king);
        addDropTargetExtension(king);

        addComponent(desk);

        // Add styling
        setStyle();
    }

    private void addDragSourceExtension(Label source) {
        // Create and attach extension
        DragSourceExtension<Label> dragSource = new DragSourceExtension<>(
                source);

        // Add listeners
        dragSource.addDragStartListener(event -> {
            event.getComponent().addStyleName("dragged");
            log(event.getComponent().getValue() + " dragstart");
        });

        dragSource.addDragEndListener(event -> {
            event.getComponent().removeStyleName("dragged");
            log(event.getComponent().getValue() + " dragend, dropEffect="
                    + event.getDropEffect());
        });
    }

    private void addDropTargetExtension(Label target) {
        // Create and attach extension
        DropTargetExtension<Label> dropTarget = new DropTargetExtension<>(
                target);

        // Add listener
        dropTarget.addDropListener(event -> {
            event.getDragSourceExtension().ifPresent(dragSource -> {
                if (dragSource.getParent() instanceof Label) {
                    Label source = (Label) dragSource.getParent();

                    // Swap source and target components
                    desk.replaceComponent(target, source);

                    log(source.getValue() + " dropped onto " + event
                            .getComponent().getValue());
                }
            });
        });
    }

    private void setStyle() {
        Page.Styles styles = Page.getCurrent().getStyles();

        styles.add(".card {"
                + "width: 150px;"
                + "height: 200px;"
                + "border: 1px solid black;"
                + "border-radius: 7px;"
                + "padding-left: 10px;"
                + "color: red;"
                + "font-weight: bolder;"
                + "font-size: 25px;"
                + "background-color: gainsboro;"
                + "}");
        styles.add(".v-drag-over {border-style: dashed;}");
        styles.add(".dragged {opacity: .4;}");
    }

    @Override
    protected String getTestDescription() {
        return "Shuffle cards with pure HTML5 drag and drop";
    }
}
